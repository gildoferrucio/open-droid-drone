package com.example.gildo.quadcoptercontroller.models.entities.threads.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.activities.ManualControlActivity;
import com.example.gildo.quadcoptercontroller.models.devices.actuators.ESC;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Tachometer;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.models.entities.IOIOControllerLooper;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;
import com.example.gildo.quadcoptercontroller.models.entities.PIDControl;
import com.example.gildo.quadcoptercontroller.models.entities.Rotor;
import com.example.gildo.quadcoptercontroller.models.constants.RotorId;
import com.example.gildo.quadcoptercontroller.services.SensoryInterpretationService;
import com.example.gildo.quadcoptercontroller.services.communication.UDPConnectionService;
import com.example.gildo.quadcoptercontroller.services.control.AltitudeControlService;
import com.example.gildo.quadcoptercontroller.services.control.AttitudeControlService;
import com.example.gildo.quadcoptercontroller.services.control.HorizontalRotationControlService;
import com.example.gildo.quadcoptercontroller.services.control.RotorSpeedControlService;

import java.util.Hashtable;

/**
 * Created by gildo on 07/12/16.
 */

public class RotationSpeedControlThread extends ControlThread {
    private PIDControl speedControlRotor1;
    private PIDControl speedControlRotor2;
    private PIDControl speedControlRotor3;
    private PIDControl speedControlRotor4;

    private boolean emergencyStop;
    private boolean rotorsStarted;

    private int alcComponent;
    private int atcComponent;
    private int hrcComponent;
    private int activeControlsDivider;

    private int desiredSpeedAlcRotor1_pulseWidthMicrosecs;
    private int desiredSpeedAlcRotor2_pulseWidthMicrosecs;
    private int desiredSpeedAlcRotor3_pulseWidthMicrosecs;
    private int desiredSpeedAlcRotor4_pulseWidthMicrosecs;

    private int desiredSpeedAtcRotor1_pulseWidthMicrosecs;
    private int desiredSpeedAtcRotor2_pulseWidthMicrosecs;
    private int desiredSpeedAtcRotor3_pulseWidthMicrosecs;
    private int desiredSpeedAtcRotor4_pulseWidthMicrosecs;

    private int desiredSpeedHrcRotor1_pulseWidthMicrosecs;
    private int desiredSpeedHrcRotor2_pulseWidthMicrosecs;
    private int desiredSpeedHrcRotor3_pulseWidthMicrosecs;
    private int desiredSpeedHrcRotor4_pulseWidthMicrosecs;

    private int controlVariableRotor1_pulseWidthMicrosecs; //control variable
    private int controlVariableRotor2_pulseWidthMicrosecs; //control variable
    private int controlVariableRotor3_pulseWidthMicrosecs; //control variable
    private int controlVariableRotor4_pulseWidthMicrosecs; //control variable

    private int correctedSpeedRotor1_pulseWidthMicrosecs;
    private int correctedSpeedRotor2_pulseWidthMicrosecs;
    private int correctedSpeedRotor3_pulseWidthMicrosecs;
    private int correctedSpeedRotor4_pulseWidthMicrosecs;

    private int throttleBaseValue_pulseWidthMicrosecs;

    private DevicesDynamicsHandler devicesDynamicsHandler;

    public RotationSpeedControlThread(Context context) {
        float kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient;
        SharedPreferences sharedPreferences;

        this.context = context;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        kProportionalCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_rsc_proportional_coefficient), 10000) / 1000000f;
        kIntegrativeCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_rsc_integrative_coefficient), 10000) / 1000000f;
        kDerivativeCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_rsc_derivative_coefficient), 10000) / 1000000f;

        this.pidControlEnabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_rsc_enable), true);
        this.logEnabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_enable_rsc_logs), false);
        if (sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_alc_enable), false)) {
            this.alcComponent = 1;
        } else {
            this.alcComponent = 0;
        }
        if (sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_atc_enable), false)) {
            this.atcComponent = 1;
        } else {
            this.atcComponent = 0;
        }
        if (sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_hrc_enable), false)) {
            this.hrcComponent = 1;
        } else {
            this.hrcComponent = 0;
        }
        this.activeControlsDivider = this.alcComponent + this.atcComponent + this.hrcComponent;

        this.rotorsStarted = false;

        this.minimumOutput_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_control_min_throttle_perTenThousand), 1000) / 10000f);
        this.maximumOutput_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_control_max_throttle_perTenThousand), 2150) / 10000f);

        this.throttleBaseValue_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_rsc_throttle_base_value_perTenThousand), 500) / 10000f);
        if (this.throttleBaseValue_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.throttleBaseValue_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.throttleBaseValue_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.throttleBaseValue_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }

        this.speedControlRotor1 = new PIDControl(kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient,
//                ESC.MINIMUM_PULSE_WIDTH_MICROSECS - this.throttleBaseValue_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                -this.maximumOutput_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
        this.speedControlRotor2 = new PIDControl(kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient,
//                ESC.MINIMUM_PULSE_WIDTH_MICROSECS - this.throttleBaseValue_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                -this.maximumOutput_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
        this.speedControlRotor3 = new PIDControl(kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient,
//                ESC.MINIMUM_PULSE_WIDTH_MICROSECS - this.throttleBaseValue_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                -this.maximumOutput_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
        this.speedControlRotor4 = new PIDControl(kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient,
//                ESC.MINIMUM_PULSE_WIDTH_MICROSECS - this.throttleBaseValue_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                -this.maximumOutput_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);

        this.broadcastIntent = new Intent(RotorSpeedControlService.INTENT_FILTER_ROTOR_SPEED_CONTROL_SERVICE);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);

        this.devicesDynamicsHandler = DevicesDynamicsHandler.getInstance(this.context);

        this.logHandler = LogHandler.getInstance();

        this.hasInputToProcess = false;

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(AltitudeControlService.INTENT_FILTER_ALTITUDE_CONTROL_SERVICE)) {
                    desiredSpeedAlcRotor1_pulseWidthMicrosecs = intent.getIntExtra(
                            AltitudeControlService.ALC_OUTPUT_ROTOR1_PW_MICROSECS_KEY, minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedAlcRotor2_pulseWidthMicrosecs = intent.getIntExtra(
                            AltitudeControlService.ALC_OUTPUT_ROTOR2_PW_MICROSECS_KEY, minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedAlcRotor3_pulseWidthMicrosecs = intent.getIntExtra(
                            AltitudeControlService.ALC_OUTPUT_ROTOR3_PW_MICROSECS_KEY, minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedAlcRotor4_pulseWidthMicrosecs = intent.getIntExtra(
                            AltitudeControlService.ALC_OUTPUT_ROTOR4_PW_MICROSECS_KEY, minimumOutput_pulseWidthMicrosecs);
                    currentSamplePeriod_millisecs = intent.getLongExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY,
                            IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
                } else if (action.equals(AttitudeControlService.INTENT_FILTER_ATTITUDE_CONTROL_SERVICE)) {
                    desiredSpeedAtcRotor1_pulseWidthMicrosecs = intent.getIntExtra(
                            AttitudeControlService.ATC_OUTPUT_ROTOR1_PW_MICROSECS_KEY, minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedAtcRotor2_pulseWidthMicrosecs = intent.getIntExtra(
                            AttitudeControlService.ATC_OUTPUT_ROTOR2_PW_MICROSECS_KEY, minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedAtcRotor3_pulseWidthMicrosecs = intent.getIntExtra(
                            AttitudeControlService.ATC_OUTPUT_ROTOR3_PW_MICROSECS_KEY, minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedAtcRotor4_pulseWidthMicrosecs = intent.getIntExtra(
                            AttitudeControlService.ATC_OUTPUT_ROTOR4_PW_MICROSECS_KEY, minimumOutput_pulseWidthMicrosecs);
                    currentSamplePeriod_millisecs = intent.getLongExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY,
                            IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
                } else if (action.equals(HorizontalRotationControlService.INTENT_FILTER_HORIZONTAL_ROTATION_CONTROL_SERVICE)) {
                    desiredSpeedHrcRotor1_pulseWidthMicrosecs = intent.getIntExtra(
                            HorizontalRotationControlService.HRC_OUTPUT_ROTOR1_PW_MICROSECS_KEY,
                            minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedHrcRotor2_pulseWidthMicrosecs = intent.getIntExtra(
                            HorizontalRotationControlService.HRC_OUTPUT_ROTOR2_PW_MICROSECS_KEY,
                            minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedHrcRotor3_pulseWidthMicrosecs = intent.getIntExtra(
                            HorizontalRotationControlService.HRC_OUTPUT_ROTOR3_PW_MICROSECS_KEY,
                            minimumOutput_pulseWidthMicrosecs);
                    desiredSpeedHrcRotor4_pulseWidthMicrosecs = intent.getIntExtra(
                            HorizontalRotationControlService.HRC_OUTPUT_ROTOR4_PW_MICROSECS_KEY,
                            minimumOutput_pulseWidthMicrosecs);
                    currentSamplePeriod_millisecs = intent.getLongExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY,
                            IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
                } else if (action.equals(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE)) {
                    // get rotor's speed inputs from sensors
                    devicesDynamicsHandler.updateCurrentRotorRotationSpeed(RotorId.ONE, intent.getFloatExtra(
                            Tachometer.REVOLUTIONS_ROTOR1_RPM_KEY, 0));
                    devicesDynamicsHandler.updateCurrentRotorRotationSpeed(RotorId.TWO, intent.getFloatExtra(
                            Tachometer.REVOLUTIONS_ROTOR2_RPM_KEY, 0));
                    devicesDynamicsHandler.updateCurrentRotorRotationSpeed(RotorId.THREE, intent.getFloatExtra(
                            Tachometer.REVOLUTIONS_ROTOR3_RPM_KEY, 0));
                    devicesDynamicsHandler.updateCurrentRotorRotationSpeed(RotorId.FOUR, intent.getFloatExtra(
                            Tachometer.REVOLUTIONS_ROTOR4_RPM_KEY, 0));
                } else if (action.equals(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE)) {
                    emergencyStop = intent.getBooleanExtra(ManualControlActivity.EXECUTE_EMERGENCY_STOP_MANOUVER_KEY,
                            false);
                    rotorsStarted = intent.getBooleanExtra(ManualControlActivity.START_STOP_ROTORS_KEY,
                            false);
                }

                hasInputToProcess = true;
            }
        };

        this.setName("RotationSpeedControlThread");
    }

    @Override
    public void run() {
        IntentFilter intentFilter;

        intentFilter = new IntentFilter();
        intentFilter.addAction(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE);
        intentFilter.addAction(AltitudeControlService.INTENT_FILTER_ALTITUDE_CONTROL_SERVICE);
        intentFilter.addAction(AttitudeControlService.INTENT_FILTER_ATTITUDE_CONTROL_SERVICE);
        intentFilter.addAction(HorizontalRotationControlService.INTENT_FILTER_HORIZONTAL_ROTATION_CONTROL_SERVICE);
        intentFilter.addAction(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE);
        this.localBroadcastManager.registerReceiver(this.broadcastReceiver, intentFilter);

        this.running = true;

        while (this.running) {
            if (this.hasInputToProcess) {
                calculateRotorsDesiredSpeed();

                if (this.pidControlEnabled && this.rotorsStarted) {
                    calculateControlVariable();
                } else {
                    processRotorsPulseWidthWithoutControl();
                }

                if (this.logEnabled && this.pidControlEnabled) {
                    logInputsAndOutputs();
                }

                sendLocalBroadcast();

                this.hasInputToProcess = false;
            }
        }
    }

    private void calculateRotorsDesiredSpeed() {
        int desiredSpeedRotor1_pulseWidthMicrosecs, desiredSpeedRotor2_pulseWidthMicrosecs,
                desiredSpeedRotor3_pulseWidthMicrosecs, desiredSpeedRotor4_pulseWidthMicrosecs;

        if (this.emergencyStop || !this.rotorsStarted) {
            desiredSpeedRotor1_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
            desiredSpeedRotor2_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
            desiredSpeedRotor3_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
            desiredSpeedRotor4_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
        } else if (this.activeControlsDivider > 0) {
            desiredSpeedRotor1_pulseWidthMicrosecs = (
                    (this.alcComponent * this.desiredSpeedAlcRotor1_pulseWidthMicrosecs) +
                            (this.atcComponent * this.desiredSpeedAtcRotor1_pulseWidthMicrosecs) +
                            (this.hrcComponent * this.desiredSpeedHrcRotor1_pulseWidthMicrosecs))
                    / this.activeControlsDivider;
            desiredSpeedRotor2_pulseWidthMicrosecs = (
                    (this.alcComponent * this.desiredSpeedAlcRotor2_pulseWidthMicrosecs) +
                            (this.atcComponent * this.desiredSpeedAtcRotor2_pulseWidthMicrosecs) +
                            (this.hrcComponent * this.desiredSpeedHrcRotor2_pulseWidthMicrosecs))
                    / this.activeControlsDivider;
            desiredSpeedRotor3_pulseWidthMicrosecs = (
                    (this.alcComponent * this.desiredSpeedAlcRotor3_pulseWidthMicrosecs) +
                            (this.atcComponent * this.desiredSpeedAtcRotor3_pulseWidthMicrosecs) +
                            (this.hrcComponent * this.desiredSpeedHrcRotor3_pulseWidthMicrosecs))
                    / this.activeControlsDivider;
            desiredSpeedRotor4_pulseWidthMicrosecs = (
                    (this.alcComponent * this.desiredSpeedAlcRotor4_pulseWidthMicrosecs) +
                            (this.atcComponent * this.desiredSpeedAtcRotor4_pulseWidthMicrosecs) +
                            (this.hrcComponent * this.desiredSpeedHrcRotor4_pulseWidthMicrosecs))
                    / this.activeControlsDivider;
        } else {
            desiredSpeedRotor1_pulseWidthMicrosecs = (this.desiredSpeedAlcRotor1_pulseWidthMicrosecs +
                    this.desiredSpeedAtcRotor1_pulseWidthMicrosecs + this.desiredSpeedHrcRotor1_pulseWidthMicrosecs) / 3;
            desiredSpeedRotor2_pulseWidthMicrosecs = (this.desiredSpeedAlcRotor2_pulseWidthMicrosecs +
                    this.desiredSpeedAtcRotor2_pulseWidthMicrosecs + this.desiredSpeedHrcRotor2_pulseWidthMicrosecs) / 3;
            desiredSpeedRotor3_pulseWidthMicrosecs = (this.desiredSpeedAlcRotor3_pulseWidthMicrosecs +
                    this.desiredSpeedAtcRotor3_pulseWidthMicrosecs + this.desiredSpeedHrcRotor3_pulseWidthMicrosecs) / 3;
            desiredSpeedRotor4_pulseWidthMicrosecs = (this.desiredSpeedAlcRotor4_pulseWidthMicrosecs +
                    this.desiredSpeedAtcRotor4_pulseWidthMicrosecs + this.desiredSpeedHrcRotor4_pulseWidthMicrosecs) / 3;
        }

        this.devicesDynamicsHandler.updateDesiredRotorRotationSpeed(RotorId.ONE, desiredSpeedRotor1_pulseWidthMicrosecs);
        this.devicesDynamicsHandler.updateDesiredRotorRotationSpeed(RotorId.TWO, desiredSpeedRotor2_pulseWidthMicrosecs);
        this.devicesDynamicsHandler.updateDesiredRotorRotationSpeed(RotorId.THREE, desiredSpeedRotor3_pulseWidthMicrosecs);
        this.devicesDynamicsHandler.updateDesiredRotorRotationSpeed(RotorId.FOUR, desiredSpeedRotor4_pulseWidthMicrosecs);
    }

    @Override
    protected void calculateControlVariable() {
        Hashtable<Integer, Rotor> rotors = this.devicesDynamicsHandler.getRotors();
        Rotor currentRotor;
        float currentRotorDesiredSpeed_rpm;

        currentRotor = rotors.get(RotorId.ONE);
        currentRotorDesiredSpeed_rpm = currentRotor.getDesiredSpeed_rpm();
        this.controlVariableRotor1_pulseWidthMicrosecs = (int) this.speedControlRotor1.calculateControlVariable(
                currentRotorDesiredSpeed_rpm, currentRotor.getCurrentSpeed_rpm(),
                this.currentSamplePeriod_millisecs);
        this.correctedSpeedRotor1_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs +
                this.controlVariableRotor1_pulseWidthMicrosecs;
        if (this.correctedSpeedRotor1_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.correctedSpeedRotor1_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.correctedSpeedRotor1_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.correctedSpeedRotor1_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }

        currentRotor = rotors.get(RotorId.TWO);
        currentRotorDesiredSpeed_rpm = currentRotor.getDesiredSpeed_rpm();
        this.controlVariableRotor2_pulseWidthMicrosecs = (int) this.speedControlRotor2.calculateControlVariable(
                currentRotorDesiredSpeed_rpm, currentRotor.getCurrentSpeed_rpm(),
                this.currentSamplePeriod_millisecs);
        this.correctedSpeedRotor2_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs +
                this.controlVariableRotor2_pulseWidthMicrosecs;
        if (this.correctedSpeedRotor2_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.correctedSpeedRotor2_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.correctedSpeedRotor2_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.correctedSpeedRotor2_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }

        currentRotor = rotors.get(RotorId.THREE);
        currentRotorDesiredSpeed_rpm = currentRotor.getDesiredSpeed_rpm();
        this.controlVariableRotor3_pulseWidthMicrosecs = (int) this.speedControlRotor3.calculateControlVariable(
                currentRotorDesiredSpeed_rpm, currentRotor.getCurrentSpeed_rpm(),
                this.currentSamplePeriod_millisecs);
        this.correctedSpeedRotor3_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs +
                this.controlVariableRotor3_pulseWidthMicrosecs;
        if (this.correctedSpeedRotor3_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.correctedSpeedRotor3_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.correctedSpeedRotor3_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.correctedSpeedRotor3_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }

        currentRotor = rotors.get(RotorId.FOUR);
        currentRotorDesiredSpeed_rpm = currentRotor.getDesiredSpeed_rpm();
        this.controlVariableRotor4_pulseWidthMicrosecs = (int) this.speedControlRotor4.calculateControlVariable(
                currentRotorDesiredSpeed_rpm, currentRotor.getCurrentSpeed_rpm(),
                this.currentSamplePeriod_millisecs);
        this.correctedSpeedRotor4_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs +
                this.controlVariableRotor4_pulseWidthMicrosecs;
        if (this.correctedSpeedRotor4_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.correctedSpeedRotor4_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.correctedSpeedRotor4_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.correctedSpeedRotor4_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }
    }

    @Override
    protected void processRotorsPulseWidthWithoutControl() {
        Hashtable<Integer, Rotor> rotors = this.devicesDynamicsHandler.getRotors();

        this.correctedSpeedRotor1_pulseWidthMicrosecs = rotors.get(RotorId.ONE).getDesiredSpeed_pulseWidthMicrosecs();
        this.correctedSpeedRotor2_pulseWidthMicrosecs = rotors.get(RotorId.TWO).getDesiredSpeed_pulseWidthMicrosecs();
        this.correctedSpeedRotor3_pulseWidthMicrosecs = rotors.get(RotorId.THREE).getDesiredSpeed_pulseWidthMicrosecs();
        this.correctedSpeedRotor4_pulseWidthMicrosecs = rotors.get(RotorId.FOUR).getDesiredSpeed_pulseWidthMicrosecs();
    }

    @Override
    protected void distributeSystemOutputThroughRotors() {
        //Actually, there's no need to implement this method in this class. Maybe it was a design flaw.
    }

    @Override
    protected void logInputsAndOutputs() {
        Hashtable<Integer, Rotor> rotors = this.devicesDynamicsHandler.getRotors();
        Rotor currentRotor;

        if (!this.logHeaderCreated) {
            createLogHeader();
        }

        if (this.stringBuilderLogValues == null) {
            this.stringBuilderLogValues = new StringBuilder();
        } else {
            this.stringBuilderLogValues.delete(0, this.stringBuilderLogValues.length());
        }

        //Log values
        currentRotor = rotors.get(RotorId.ONE);
        this.stringBuilderLogValues.append(currentRotor.getDesiredSpeed_rpm()).append(",");
        this.stringBuilderLogValues.append(currentRotor.getCurrentSpeed_rpm()).append(",");
        this.stringBuilderLogValues.append(this.controlVariableRotor1_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.correctedSpeedRotor1_pulseWidthMicrosecs).append(",");

        currentRotor = rotors.get(RotorId.TWO);
        this.stringBuilderLogValues.append(currentRotor.getDesiredSpeed_rpm()).append(",");
        this.stringBuilderLogValues.append(currentRotor.getCurrentSpeed_rpm()).append(",");
        this.stringBuilderLogValues.append(this.controlVariableRotor2_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.correctedSpeedRotor2_pulseWidthMicrosecs).append(",");

        currentRotor = rotors.get(RotorId.THREE);
        this.stringBuilderLogValues.append(currentRotor.getDesiredSpeed_rpm()).append(",");
        this.stringBuilderLogValues.append(currentRotor.getCurrentSpeed_rpm()).append(",");
        this.stringBuilderLogValues.append(this.controlVariableRotor3_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.correctedSpeedRotor3_pulseWidthMicrosecs).append(",");

        currentRotor = rotors.get(RotorId.FOUR);
        this.stringBuilderLogValues.append(currentRotor.getDesiredSpeed_rpm()).append(",");
        this.stringBuilderLogValues.append(currentRotor.getCurrentSpeed_rpm()).append(",");
        this.stringBuilderLogValues.append(this.controlVariableRotor4_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.correctedSpeedRotor4_pulseWidthMicrosecs);

        this.logHandler.appendDataToLog(R.string.pref_key_enable_rsc_logs, this.stringBuilderLogValues);
    }

    @Override
    protected void createLogHeader() {
        if (this.stringBuilderLogTags == null) {
            this.stringBuilderLogTags = new StringBuilder();
        } else {
            this.stringBuilderLogTags.delete(0, this.stringBuilderLogTags.length());
        }
        //Log tags
//        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_controlService_rsc));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_input_rotor1_desiredRotationSpeed_rpm)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_input_rotor1_currentRotationSpeed_rpm)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_output_rotor1_controlVariable_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor1_pulseWidth_microsecs)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_input_rotor2_desiredRotationSpeed_rpm)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_input_rotor2_currentRotationSpeed_rpm)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_output_rotor2_controlVariable_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor2_pulseWidth_microsecs)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_input_rotor3_desiredRotationSpeed_rpm)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_input_rotor3_currentRotationSpeed_rpm)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_output_rotor3_controlVariable_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor3_pulseWidth_microsecs)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_input_rotor4_desiredRotationSpeed_rpm)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_input_rotor4_currentRotationSpeed_rpm)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_rsc_output_rotor4_controlVariable_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor4_pulseWidth_microsecs));


        this.logHandler.appendDataToLog(R.string.pref_key_enable_rsc_logs, this.stringBuilderLogTags);

        this.logHeaderCreated = true;
    }

    @Override
    protected void sendLocalBroadcast() {
        this.broadcastIntent.putExtra(RotorSpeedControlService.RSC_OUTPUT_ROTOR1_PW_MICROSECS_KEY,
                this.correctedSpeedRotor1_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(RotorSpeedControlService.RSC_OUTPUT_ROTOR2_PW_MICROSECS_KEY,
                this.correctedSpeedRotor2_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(RotorSpeedControlService.RSC_OUTPUT_ROTOR3_PW_MICROSECS_KEY,
                this.correctedSpeedRotor3_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(RotorSpeedControlService.RSC_OUTPUT_ROTOR4_PW_MICROSECS_KEY,
                this.correctedSpeedRotor4_pulseWidthMicrosecs);

        this.localBroadcastManager.sendBroadcast(this.broadcastIntent);
    }
}
