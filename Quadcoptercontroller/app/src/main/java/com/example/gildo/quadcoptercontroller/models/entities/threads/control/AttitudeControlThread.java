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
import com.example.gildo.quadcoptercontroller.models.devices.fusedSensors.AttitudeSensor;
import com.example.gildo.quadcoptercontroller.models.entities.EquilibriumAxis;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.models.entities.IOIOControllerLooper;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;
import com.example.gildo.quadcoptercontroller.models.entities.PIDControl;
import com.example.gildo.quadcoptercontroller.services.SensoryInterpretationService;
import com.example.gildo.quadcoptercontroller.services.communication.UDPConnectionService;
import com.example.gildo.quadcoptercontroller.services.control.AttitudeControlService;

/**
 * Created by gildo on 07/12/16.
 */

public class AttitudeControlThread extends ControlThread {
    private PIDControl pitchControl; //lateral axis - x axis rotation
    private PIDControl rollControl; //longitudinal axis - y axis rotation

    private int controlVariableX_pulseWidthMicrosecs; //control variable
    private int controlVariableY_pulseWidthMicrosecs; //control variable
    private float desiredAxisRotationX_degrees; //target or set point
    private float desiredAxisRotationY_degrees; //target or set point
    private float currentAxisRotationX_degrees; //input or process variable
    private float currentAxisRotationY_degrees; //input or process variable
    private float maximumAxisRotation_degrees;

    private float adjustedThrottle_percent;
    private float throttle_percent;

    private int xPositiveRegionRotors_pulseWidthMicrosecs; //control output or manipulated variable
    private int xNegativeRegionRotors_pulseWidthMicrosecs; //control output or manipulated variable
    private int yPositiveRegionRotors_pulseWidthMicrosecs; //control output or manipulated variable
    private int yNegativeRegionRotors_pulseWidthMicrosecs; //control output or manipulated variable

    private int rotor1_pulseWidthMicrosecs; //attitude control output
    private int rotor2_pulseWidthMicrosecs; //attitude control output
    private int rotor3_pulseWidthMicrosecs; //attitude control output
    private int rotor4_pulseWidthMicrosecs; //attitude control output

    private int throttleBaseValue_pulseWidthMicrosecs;

    private float minThrottle_percent;
    private float maxThrottle_percent;

    public AttitudeControlThread(Context context) {
        super();
        float kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient;
        SharedPreferences sharedPreferences;

        this.context = context;

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        kProportionalCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_atc_proportional_coefficient), 10000) / 1000000f;
        kIntegrativeCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_atc_integrative_coefficient), 10000) / 1000000f;
        kDerivativeCoefficient = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_atc_derivative_coefficient), 10000) / 1000000f;

        this.pidControlEnabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_atc_enable), true);
        this.logEnabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_enable_atc_logs), false);

        this.maximumAxisRotation_degrees = sharedPreferences.getInt(this.context.getString(R.string.pref_key_maximum_inclination), 5);

        this.minThrottle_percent = sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_control_min_throttle_perTenThousand), 1000) / 10000f;
        this.maxThrottle_percent = sharedPreferences.getInt(
                this.context.getString(R.string.pref_key_control_max_throttle_perTenThousand), 2150) / 10000f;

        this.minimumOutput_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.minThrottle_percent);
        this.maximumOutput_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.maxThrottle_percent);

        this.pitchControl = new PIDControl(kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient,
                this.minimumOutput_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
        this.rollControl = new PIDControl(kProportionalCoefficient, kIntegrativeCoefficient, kDerivativeCoefficient,
                this.minimumOutput_pulseWidthMicrosecs, this.maximumOutput_pulseWidthMicrosecs,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);

        this.broadcastIntent = new Intent(AttitudeControlService.INTENT_FILTER_ATTITUDE_CONTROL_SERVICE);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);

        this.logHandler = LogHandler.getInstance();

        this.hasInputToProcess = false;

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE)) {
                    //adjustedThrottle_percent will serve as parameter of maximum rotation speed for each motor;
                    throttle_percent = intent.getFloatExtra(DevicesDynamicsHandler.DESIRED_THROTTLE_PERCENT_KEY, 0);
                    // get inclination inputs from remote controller
                    desiredAxisRotationX_degrees = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_X_DEGREES_KEY, 0);
                    desiredAxisRotationY_degrees = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_Y_DEGREES_KEY, 0);
                } else if (action.equals(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE)) {
                    // get inclination inputs from sensors
                    currentAxisRotationX_degrees = intent.getFloatExtra(AttitudeSensor.ROTATION_ANGLE_X_DEGREES_KEY, 0);
                    currentAxisRotationY_degrees = intent.getFloatExtra(AttitudeSensor.ROTATION_ANGLE_Y_DEGREES_KEY, 0);
                    currentSamplePeriod_millisecs = intent.getLongExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY,
                            IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);
                }

                hasInputToProcess = true;
            }
        };

        this.setName("AttitudeControlThread");
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

        this.controlVariableX_pulseWidthMicrosecs = (int) this.rollControl.calculateControlVariable(this.desiredAxisRotationX_degrees,
                this.currentAxisRotationX_degrees, this.currentSamplePeriod_millisecs);

        this.xPositiveRegionRotors_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs + this.controlVariableX_pulseWidthMicrosecs;
        if (this.xPositiveRegionRotors_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.xPositiveRegionRotors_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.xPositiveRegionRotors_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.xPositiveRegionRotors_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }
        this.xNegativeRegionRotors_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs - this.controlVariableX_pulseWidthMicrosecs;
        if (this.xNegativeRegionRotors_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.xNegativeRegionRotors_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.xNegativeRegionRotors_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.xNegativeRegionRotors_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }

        this.controlVariableY_pulseWidthMicrosecs = (int) this.pitchControl.calculateControlVariable(this.desiredAxisRotationY_degrees,
                this.currentAxisRotationY_degrees, this.currentSamplePeriod_millisecs);

        this.yPositiveRegionRotors_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs + this.controlVariableY_pulseWidthMicrosecs;
        if (this.yPositiveRegionRotors_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.yPositiveRegionRotors_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.yPositiveRegionRotors_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.yPositiveRegionRotors_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }
        this.yNegativeRegionRotors_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs - this.controlVariableY_pulseWidthMicrosecs;
        if (this.yNegativeRegionRotors_pulseWidthMicrosecs < this.minimumOutput_pulseWidthMicrosecs) {
            this.yNegativeRegionRotors_pulseWidthMicrosecs = this.minimumOutput_pulseWidthMicrosecs;
        } else if (this.yNegativeRegionRotors_pulseWidthMicrosecs > this.maximumOutput_pulseWidthMicrosecs) {
            this.yNegativeRegionRotors_pulseWidthMicrosecs = this.maximumOutput_pulseWidthMicrosecs;
        }

        distributeSystemOutputThroughRotors();
    }

    @Override
    protected void processRotorsPulseWidthWithoutControl() {
        float xRotationRatio = this.desiredAxisRotationX_degrees / this.maximumAxisRotation_degrees;
        float yRotationRatio = this.desiredAxisRotationY_degrees / this.maximumAxisRotation_degrees;

        this.adjustedThrottle_percent = this.throttle_percent * this.maxThrottle_percent;
        if(this.adjustedThrottle_percent < this.minThrottle_percent){
            this.adjustedThrottle_percent = this.minThrottle_percent;
        }
        this.throttleBaseValue_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent);

//        if (xRotationRatio < 0) {
//            this.xPositiveRegionRotors_pulseWidthMicrosecs = this.throttleBaseValue_pulseWidthMicrosecs;
//            this.xNegativeRegionRotors_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent *
//                    (1 + xRotationRatio));
//        } else if (xRotationRatio > 0) {
//            this.xPositiveRegionRotors_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent *
//                    (1 - xRotationRatio));
//            this.xNegativeRegionRotors_pulseWidthMicrosecs = throttleBaseValue_pulseWidthMicrosecs;
        if (xRotationRatio != 0) {
            this.xPositiveRegionRotors_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(
                    this.adjustedThrottle_percent * this.maxThrottle_percent * (1 - xRotationRatio));
            this.xNegativeRegionRotors_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(
                    this.adjustedThrottle_percent * this.maxThrottle_percent * (1 + xRotationRatio));
        } else {
            this.xPositiveRegionRotors_pulseWidthMicrosecs = this.xNegativeRegionRotors_pulseWidthMicrosecs =
                    this.throttleBaseValue_pulseWidthMicrosecs;
        }

//        if (yRotationRatio < 0) {
//            this.yPositiveRegionRotors_pulseWidthMicrosecs = throttleBaseValue_pulseWidth;
//            this.yNegativeRegionRotors_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent *
//                    (1 + yRotationRatio));
//        } else if (yRotationRatio > 0) {
//            this.yPositiveRegionRotors_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(this.adjustedThrottle_percent *
//                    (1 - yRotationRatio));
//            this.yNegativeRegionRotors_pulseWidthMicrosecs = throttleBaseValue_pulseWidth;
        if (yRotationRatio != 0) {
            this.yPositiveRegionRotors_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(
                    this.adjustedThrottle_percent * this.maxThrottle_percent * (1 - yRotationRatio));
            this.yNegativeRegionRotors_pulseWidthMicrosecs = ESC.percentToPulseWidth_microsecs(
                    this.adjustedThrottle_percent * this.maxThrottle_percent * (1 + yRotationRatio));
        } else {
            this.yPositiveRegionRotors_pulseWidthMicrosecs = this.yNegativeRegionRotors_pulseWidthMicrosecs =
                    this.throttleBaseValue_pulseWidthMicrosecs;
        }

        distributeSystemOutputThroughRotors();
    }

    @Override
    protected void distributeSystemOutputThroughRotors() {
        this.rotor1_pulseWidthMicrosecs = (this.xPositiveRegionRotors_pulseWidthMicrosecs +
                this.yPositiveRegionRotors_pulseWidthMicrosecs) / 2;
        this.rotor2_pulseWidthMicrosecs = (this.xPositiveRegionRotors_pulseWidthMicrosecs +
                this.yNegativeRegionRotors_pulseWidthMicrosecs) / 2;
        this.rotor3_pulseWidthMicrosecs = (this.xNegativeRegionRotors_pulseWidthMicrosecs +
                this.yNegativeRegionRotors_pulseWidthMicrosecs) / 2;
        this.rotor4_pulseWidthMicrosecs = (this.xNegativeRegionRotors_pulseWidthMicrosecs +
                this.yPositiveRegionRotors_pulseWidthMicrosecs) / 2;
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
        this.stringBuilderLogValues.append(this.desiredAxisRotationX_degrees).append(",");
        this.stringBuilderLogValues.append(this.currentAxisRotationX_degrees).append(",");
        this.stringBuilderLogValues.append(this.controlVariableX_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.xPositiveRegionRotors_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.xNegativeRegionRotors_pulseWidthMicrosecs).append(",");

        this.stringBuilderLogValues.append(this.desiredAxisRotationY_degrees).append(",");
        this.stringBuilderLogValues.append(this.currentAxisRotationY_degrees).append(",");
        this.stringBuilderLogValues.append(this.controlVariableY_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.yPositiveRegionRotors_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.yNegativeRegionRotors_pulseWidthMicrosecs).append(",");

        this.stringBuilderLogValues.append(this.rotor1_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor2_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor3_pulseWidthMicrosecs).append(",");
        this.stringBuilderLogValues.append(this.rotor4_pulseWidthMicrosecs);

        this.logHandler.appendDataToLog(R.string.pref_key_enable_atc_logs, this.stringBuilderLogValues);
    }

    @Override
    protected void createLogHeader() {
        if (this.stringBuilderLogTags == null) {
            this.stringBuilderLogTags = new StringBuilder();
        } else {
            this.stringBuilderLogTags.delete(0, this.stringBuilderLogTags.length());
        }

        //Log tags
//        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_controlService_atc));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_input_throttle_percent)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_input_desiredAxisRotationX_degrees)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_input_currentAxisRotationX_degrees)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_controlVariableX_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_xPositiveRegion_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_xNegativeRegion_pulseWidth_microsecs)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_input_desiredAxisRotationY_degrees)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_input_currentAxisRotationY_degrees)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_controlVariableY_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_yPositiveRegion_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_yNegativeRegion_pulseWidth_microsecs)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor1_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor2_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor3_pulseWidth_microsecs)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_atc_output_rotor4_pulseWidth_microsecs));

        this.logHandler.appendDataToLog(R.string.pref_key_enable_atc_logs, this.stringBuilderLogTags);

        this.logHeaderCreated = true;
    }

    @Override
    protected void sendLocalBroadcast() {
        this.broadcastIntent.putExtra(AttitudeControlService.ATC_OUTPUT_ROTOR1_PW_MICROSECS_KEY,
                this.rotor1_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(AttitudeControlService.ATC_OUTPUT_ROTOR2_PW_MICROSECS_KEY,
                this.rotor2_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(AttitudeControlService.ATC_OUTPUT_ROTOR3_PW_MICROSECS_KEY,
                this.rotor3_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(AttitudeControlService.ATC_OUTPUT_ROTOR4_PW_MICROSECS_KEY,
                this.rotor4_pulseWidthMicrosecs);
        this.broadcastIntent.putExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY, this.currentSamplePeriod_millisecs);

        this.localBroadcastManager.sendBroadcast(this.broadcastIntent);
    }
}
