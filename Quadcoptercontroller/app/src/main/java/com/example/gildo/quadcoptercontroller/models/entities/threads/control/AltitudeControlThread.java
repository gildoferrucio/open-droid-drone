package com.example.gildo.quadcoptercontroller.models.entities.threads.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.devices.actuators.ESC;
import com.example.gildo.quadcoptercontroller.models.devices.fusedSensors.AltimeterSensor;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.models.entities.IOIOControllerLooper;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;
import com.example.gildo.quadcoptercontroller.models.entities.PIDControl;
import com.example.gildo.quadcoptercontroller.services.SensoryInterpretationService;
import com.example.gildo.quadcoptercontroller.services.communication.UDPConnectionService;
import com.example.gildo.quadcoptercontroller.services.control.AltitudeControlService;

/**
 * Created by gildo on 07/12/16.
 */

public class AltitudeControlThread extends ControlThread {
    private PIDControl altitudeControl;

    private int controlVariable; //control variable
    private float desiredAbsoluteAltitude_meters; //target
    private float desiredRelativeAltitude_meters; //target
    private float currentAbsoluteAltitude_meters; //input
    private float currentRelativeAltitude_meters; //input
    private float throttle_percent;
    private float adjustedThrottle_percent;

    private int rotor1_pulseWidthMicrosecs; //control output
    private int rotor2_pulseWidthMicrosecs; //control output
    private int rotor3_pulseWidthMicrosecs; //control output
    private int rotor4_pulseWidthMicrosecs; //control output

    private int throttleBaseValue_pulseWidthMicrosecs;

    private float minThrottle_percent;
    private float maxThrottle_percent;

    public AltitudeControlThread(Context context) {
        super();
        float kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient;
        SharedPreferences sharedPreferences;

        this.context = context;

        this.logHandler = LogHandler.getInstance();

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        kProportionalCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_alc_proportional_coefficient), 10000) / 1000000f;
        kIntegrativeCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_alc_integrative_coefficient), 10000) / 1000000f;
        kDerivativeCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_alc_derivative_coefficient), 10000) / 1000000f;

        this.minThrottle_percent = sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_control_min_throttle_perTenThousand), 1000) / 10000f;
        this.maxThrottle_percent = sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_control_max_throttle_perTenThousand), 2150) / 10000f;

        this.minimumOutput_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.minThrottle_percent);
        this.maximumOutput_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.maxThrottle_percent);

        this.altitudeControl = new PIDControl(kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient,
                this.minimumOutput_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);

        this.pidControlEnabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_alc_enable), true);
        this.logEnabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_enable_alc_logs),
                false);

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        this.broadcastIntent = new Intent(AltitudeControlService.INTENT_FILTER_ALTITUDE_CONTROL_SERVICE);

        this.hasInputToProcess = false;

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE)) {
                    // get altitude inputs from remote controller
                    throttle_percent = intent.getFloatExtra(DevicesDynamicsHandler.DESIRED_THROTTLE_PERCENT_KEY, 0);
                    //TODO: when receiving inputs from swarm it's gonna be altitude itself not throttle!
                    desiredRelativeAltitude_meters = adjustedThrottle_percent * maximumOutput_pulseWidthMicrosecs;
//                    if (desiredRelativeAltitude_meters < minimumOutput_pulseWidthMicrosecs) {
//                        desiredRelativeAltitude_meters = minimumOutput_pulseWidthMicrosecs;
//                    }
                } else if (action.equals(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE)) {
                    // get altitude inputs from sensors
                    currentRelativeAltitude_meters = intent.getFloatExtra(AltimeterSensor.RELATIVE_ALTITUDE_METERS_KEY,
                            0);
                    currentSamplePeriod_millisecs = intent.getLongExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY,
                            IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
                }

                hasInputToProcess = true;
            }
        };

        this.setName("AltitudeControlThread");
    }

    @Override
    public void run() {
        IntentFilter intentFilter;

        intentFilter = new IntentFilter();
        intentFilter.addAction(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE);
        intentFilter.addAction(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE);
        this.localBroadcastManager.registerReceiver(this.broadcastReceiver, intentFilter);

        this.running = true;

        while (this.running) {
            if (this.hasInputToProcess) {
                if (this.pidControlEnabled) {
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

    @Override
    protected void calculateControlVariable() {
        int correctedValue_pulseWidthMicrosecs;
        this.adjustedThrottle_percent = this.throttle_percent * this.maxThrottle_percent;
        if(this.adjustedThrottle_percent < this.minThrottle_percent){
            this.adjustedThrottle_percent = this.minThrottle_percent;
        }
        this.throttleBaseValue_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent);

        this.controlVariable = (int) this.altitudeControl.calculateControlVariable(this.desiredRelativeAltitude_meters,
                this.currentRelativeAltitude_meters, this.currentSamplePeriod_millisecs);
        correctedValue_pulseWidthMicrosecs = throttleBaseValue_pulseWidthMicrosecs + this.controlVariable;
        if (correctedValue_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            correctedValue_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (correctedValue_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            correctedValue_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }

        this.rotor1_pulseWidthMicrosecs = this.rotor2_pulseWidthMicrosecs = this.rotor3_pulseWidthMicrosecs =
                this.rotor4_pulseWidthMicrosecs = correctedValue_pulseWidthMicrosecs;
    }

    @Override
    protected void processRotorsPulseWidthWithoutControl() {
        this.adjustedThrottle_percent = this.throttle_percent * this.maxThrottle_percent;
        if(this.adjustedThrottle_percent < this.minThrottle_percent){
            this.adjustedThrottle_percent = this.minThrottle_percent;
        }
        this.throttleBaseValue_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent);

        this.rotor1_pulseWidthMicrosecs = this.rotor2_pulseWidthMicrosecs = this.rotor3_pulseWidthMicrosecs =
                this.rotor4_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs;
    }

    @Override
    protected void distributeSystemOutputThroughRotors() {
        //There's no need to implement this method on AltitudeControlThread
    }

    @Override
    protected void logInputsAndOutputs() {
        if (!this.logHeaderCreated) {
            createLogHeader();
        }

        if (this.stringBuilderLogValues == null) {
            this.stringBuilderLogValues = new StringBuilder();
        } else {
            this.stringBuilderLogValues.delete(0, this.stringBuilderLogValues.length());
        }

        //Log values
        this.stringBuilderLogValues.append(this.adjustedThrottle_percent).append(",");
        this.stringBuilderLogValues.append(this.desiredRelativeAltitude_meters).append(",");
        this.stringBuilderLogValues.append(this.currentRelativeAltitude_meters).append(",");
        this.stringBuilderLogValues.append(this.controlVariable).append(",");
        this.stringBuilderLogValues.append(this.rotor1_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor2_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor3_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor4_pulseWidthMicrosecs);

        this.logHandler.appendDataToLog(R.string.pref_key_enable_alc_logs, this.stringBuilderLogValues);
    }

    @Override
    protected void createLogHeader() {
        if (this.stringBuilderLogTags == null) {
            this.stringBuilderLogTags = new StringBuilder();
        } else {
            this.stringBuilderLogTags.delete(0, this.stringBuilderLogTags.length());
        }

        //Log tags
//        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_controlService_alc));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_alc_input_throttle_percent)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_alc_input_desiredRelativeAltitude_meters)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_alc_input_currentRelativeAltitude_meters)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_alc_output_controlVariable_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_alc_output_rotor1_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_alc_output_rotor2_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_alc_output_rotor3_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_alc_output_rotor4_pulseWidth_microsecs));

        this.logHandler.appendDataToLog(R.string.pref_key_enable_alc_logs, this.stringBuilderLogTags);

        this.logHeaderCreated = true;
    }

    @Override
    protected void sendLocalBroadcast() {
        this.broadcastIntent.putExtra(AltitudeControlService.ALC_OUTPUT_ROTOR1_PW_MICROSECS_KEY,
                this.rotor1_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(AltitudeControlService.ALC_OUTPUT_ROTOR2_PW_MICROSECS_KEY,
                this.rotor2_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(AltitudeControlService.ALC_OUTPUT_ROTOR3_PW_MICROSECS_KEY,
                this.rotor3_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(AltitudeControlService.ALC_OUTPUT_ROTOR4_PW_MICROSECS_KEY,
                this.rotor4_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY, this.currentSamplePeriod_millisecs);

        this.localBroadcastManager.sendBroadcast(this.broadcastIntent);
    }
}
