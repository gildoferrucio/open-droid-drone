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
import com.example.gildo.quadcoptercontroller.models.devices.fusedSensors.HorizontalRotationSensor;
import com.example.gildo.quadcoptercontroller.models.entities.EquilibriumAxis;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.models.entities.IOIOControllerLooper;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;
import com.example.gildo.quadcoptercontroller.models.entities.PIDControl;
import com.example.gildo.quadcoptercontroller.services.SensoryInterpretationService;
import com.example.gildo.quadcoptercontroller.services.communication.UDPConnectionService;
import com.example.gildo.quadcoptercontroller.services.control.HorizontalRotationControlService;

/**
 * Created by gildo on 07/12/16.
 */

public class HorizontalRotationControlThread extends ControlThread {
    private PIDControl yawControl;

    private int controlVariable; //control variable
    private float desiredRotationAngle_degrees; //target
    private float currentRotationAngle_degrees; //input
    private float maximumRotationAngle_degrees;

    private float throttle_percent;
    private float adjustedThrottle_percent;

    private int rotor1_pulseWidthMicrosecs; //control output
    private int rotor2_pulseWidthMicrosecs; //control output
    private int rotor3_pulseWidthMicrosecs; //control output
    private int rotor4_pulseWidthMicrosecs; //control output

    private int clockwiseRotation_pulseWidthMicrosecs;
    private int counterClockwiseRotation_pulseWidthMicrosecs;

    private int throttleBaseValue_pulseWidthMicrosecs;

    private float minThrottle_percent;
    private float maxThrottle_percent;

    public HorizontalRotationControlThread(Context context) {
        float kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient;
        SharedPreferences sharedPreferences;
        this.context = context;

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        kProportionalCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_hrc_proportional_coefficient), 10000) / 1000000f;
        kIntegrativeCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_hrc_integrative_coefficient), 10000) / 1000000f;
        kDerivativeCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_hrc_derivative_coefficient), 10000) / 1000000f;

        this.pidControlEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_hrc_enable), true);
        this.logEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_enable_hrc_logs), false);

        this.maximumRotationAngle_degrees = 90f;

        this.minThrottle_percent = sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_control_min_throttle_perTenThousand), 1000) / 10000f;
        this.maxThrottle_percent = sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_control_max_throttle_perTenThousand), 2150) / 10000f;

        this.minimumOutput_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.minThrottle_percent);
        this.maximumOutput_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.maxThrottle_percent);

        this.yawControl = new PIDControl(kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient,
                this.minimumOutput_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);

        this.broadcastIntent = new Intent(HorizontalRotationControlService.INTENT_FILTER_HORIZONTAL_ROTATION_CONTROL_SERVICE);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);

        this.logHandler = LogHandler.getInstance();

        this.hasInputToProcess = false;

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE)) {
                    // get altitude inputs from remote controller
                    throttle_percent = intent.getFloatExtra(DevicesDynamicsHandler.DESIRED_THROTTLE_PERCENT_KEY, 0);
                    // get horizontal rotation inputs from remote controller
                    desiredRotationAngle_degrees = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_Z_DEGREES_KEY, 0);
                } else if (action.equals(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE)) {
                    // get horizontal rotation inputs from sensors
                    currentRotationAngle_degrees = intent.getFloatExtra(
                            HorizontalRotationSensor.HORIZONTAL_ROTATION_DEGREES_KEY, 0);
                    currentSamplePeriod_millisecs = intent.getLongExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY,
                            IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
                }

                hasInputToProcess = true;
            }
        };

        this.setName("HorizontalRotationControlThread");
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
        this.adjustedThrottle_percent = this.throttle_percent * this.maxThrottle_percent;
        if(this.adjustedThrottle_percent < this.minThrottle_percent){
            this.adjustedThrottle_percent = this.minThrottle_percent;
        }
        this.throttleBaseValue_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent);

        this.controlVariable = (int) this.yawControl.calculateControlVariable(this.desiredRotationAngle_degrees,
                this.currentRotationAngle_degrees, this.currentSamplePeriod_millisecs);

        this.clockwiseRotation_pulseWidthMicrosecs = throttleBaseValue_pulseWidthMicrosecs + this.controlVariable;
        if (this.clockwiseRotation_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.clockwiseRotation_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.clockwiseRotation_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.clockwiseRotation_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }
        this.counterClockwiseRotation_pulseWidthMicrosecs = throttleBaseValue_pulseWidthMicrosecs - this.controlVariable;
        if (this.counterClockwiseRotation_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.counterClockwiseRotation_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.counterClockwiseRotation_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.counterClockwiseRotation_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }

        distributeSystemOutputThroughRotors();
    }

    @Override
    protected void processRotorsPulseWidthWithoutControl() {
        float zRotationRatio = this.desiredRotationAngle_degrees / this.maximumRotationAngle_degrees;
        this.adjustedThrottle_percent = this.throttle_percent * this.maxThrottle_percent;
        if(this.adjustedThrottle_percent < this.minThrottle_percent){
            this.adjustedThrottle_percent = this.minThrottle_percent;
        }
        this.throttleBaseValue_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent);

        if (zRotationRatio < 0) {
            //Quadrotor clockwise horizontal rotation
            this.clockwiseRotation_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs;
            this.counterClockwiseRotation_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent *
                    this.maxThrottle_percent * (1 + zRotationRatio));
        } else if (zRotationRatio > 0) {
            //Quadrotor counter clockwise horizontal rotation
            this.clockwiseRotation_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent *
                    this.maxThrottle_percent * (1 - zRotationRatio));
            this.counterClockwiseRotation_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs;
        } else {
            this.clockwiseRotation_pulseWidthMicrosecs = this.counterClockwiseRotation_pulseWidthMicrosecs =
                    this.throttleBaseValue_pulseWidthMicrosecs;
        }

        distributeSystemOutputThroughRotors();
    }

    @Override
    protected void distributeSystemOutputThroughRotors() {
        this.rotor1_pulseWidthMicrosecs = this.rotor3_pulseWidthMicrosecs = this.clockwiseRotation_pulseWidthMicrosecs;
        this.rotor2_pulseWidthMicrosecs = this.rotor4_pulseWidthMicrosecs = this.counterClockwiseRotation_pulseWidthMicrosecs;
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
        this.stringBuilderLogValues.append(this.desiredRotationAngle_degrees).append(",");
        this.stringBuilderLogValues.append(this.currentRotationAngle_degrees).append(",");
        this.stringBuilderLogValues.append(this.controlVariable).append(",");
        this.stringBuilderLogValues.append(this.clockwiseRotation_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.counterClockwiseRotation_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor1_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor2_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor3_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor4_pulseWidthMicrosecs);

        this.logHandler.appendDataToLog(R.string.pref_key_enable_hrc_logs, this.stringBuilderLogValues);
    }

    @Override
    protected void createLogHeader() {
        if (this.stringBuilderLogTags == null) {
            this.stringBuilderLogTags = new StringBuilder();
        } else {
            this.stringBuilderLogTags.delete(0, this.stringBuilderLogTags.length());
        }

        //Log tags
//        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_controlService_hrc));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_input_throttle_percent)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_hrc_input_desiredRotationAngle_degrees)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_hrc_input_currentRotationAngle_degrees)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_hrc_output_controlVariable_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_hrc_output_clockwiseRotation_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_hrc_output_counterClockwiseRotation_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor1_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor2_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor3_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor4_pulseWidth_microsecs));

        this.logHandler.appendDataToLog(R.string.pref_key_enable_hrc_logs, this.stringBuilderLogTags);

        this.logHeaderCreated = true;
    }

    @Override
    protected void sendLocalBroadcast() {
        this.broadcastIntent.putExtra(HorizontalRotationControlService.HRC_OUTPUT_ROTOR1_PW_MICROSECS_KEY,
                this.rotor1_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(HorizontalRotationControlService.HRC_OUTPUT_ROTOR2_PW_MICROSECS_KEY,
                this.rotor2_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(HorizontalRotationControlService.HRC_OUTPUT_ROTOR3_PW_MICROSECS_KEY,
                this.rotor3_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(HorizontalRotationControlService.HRC_OUTPUT_ROTOR4_PW_MICROSECS_KEY,
                this.rotor4_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY, this.currentSamplePeriod_millisecs);

        this.localBroadcastManager.sendBroadcast(this.broadcastIntent);
    }
}
