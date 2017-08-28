package com.example.gildo.quadcoptercontroller.models.entities.threads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.activities.TestsExecutorActivity;
import com.example.gildo.quadcoptercontroller.models.devices.actuators.ESC;
import com.example.gildo.quadcoptercontroller.models.devices.fusedSensors.AltimeterSensor;
import com.example.gildo.quadcoptercontroller.models.devices.fusedSensors.AttitudeSensor;
import com.example.gildo.quadcoptercontroller.models.devices.fusedSensors.HorizontalRotationSensor;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.AccelerometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.BarometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.GyroscopeInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.MagnetometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.SonarInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.TachometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Accelerometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Barometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Gyroscope;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Magnetometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Sonar;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Tachometer;
import com.example.gildo.quadcoptercontroller.models.entities.IOIOControllerLooper;
import com.example.gildo.quadcoptercontroller.models.constants.AxisName;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;
import com.example.gildo.quadcoptercontroller.services.SensoryInterpretationService;
import com.example.gildo.quadcoptercontroller.services.control.RotorSpeedControlService;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gildo on 08/12/16.
 */

public class SensoryInterpretationThread extends Thread {
    private AccelerometerInterpreter accelerometerInterpreter;

    private BarometerInterpreter barometerInterpreter;

    private GyroscopeInterpreter gyroscopeInterpreter;

    private MagnetometerInterpreter magnetometerInterpreter;

    private TachometerInterpreter tachometerInterpreter1;
    private TachometerInterpreter tachometerInterpreter2;
    private TachometerInterpreter tachometerInterpreter3;
    private TachometerInterpreter tachometerInterpreter4;

    private SonarInterpreter sonarInterpreterN;
    private SonarInterpreter sonarInterpreterNE;
    private SonarInterpreter sonarInterpreterE;
    private SonarInterpreter sonarInterpreterSE;
    private SonarInterpreter sonarInterpreterS;
    private SonarInterpreter sonarInterpreterSW;
    private SonarInterpreter sonarInterpreterW;
    private SonarInterpreter sonarInterpreterNW;
    private SonarInterpreter sonarInterpreterUp;
    private SonarInterpreter sonarInterpreterDown;

    //Fused sensors
    private AltimeterSensor altimeterSensor;
    private AttitudeSensor attitudeSensor;
    private HorizontalRotationSensor horizontalRotationSensor;

    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver broadcastReceiver;

    private Context context;

    private boolean firstSample;
    private boolean logHeaderCreated;
    private boolean running;
    private boolean hasInputToProcess;
    private boolean hasReadBarometerCalibrationValues;

    private int[] barometerCalibrationValues;

    private boolean tachometer1Enabled;
    private boolean tachometer2Enabled;
    private boolean tachometer3Enabled;
    private boolean tachometer4Enabled;
    private boolean sonarEastEnabled;
    private boolean sonarNorthEastEnabled;
    private boolean sonarNorthEnabled;
    private boolean sonarNorthWestEnabled;
    private boolean sonarWestEnabled;
    private boolean sonarSouthWestEnabled;
    private boolean sonarSouthenabled;
    private boolean sonarSouthEastenabled;
    private boolean sonarUpEnabled;
    private boolean sonarDownEnabled;
    private boolean barometerEnabled;
    private boolean accelerometerEnabled;
    private boolean gyroscopeEnabled;
    private boolean gyroscopeRemoveOffsetEnabled;
    private boolean magnetometerEnabled;
    private boolean magnetometerRemoveOffsetEnabled;

    private boolean logEnabledSensory;
    private boolean lpfAccelerometerEnabled;
    private boolean hpfGyroscopeEnabled;
    private boolean lpfMagnetometerEnabled;
//    private boolean mafTachometerEnable;

    private float attitudeSensorAlpha;
    private float horizontalRotationSensorAlpha;
    private float lpfAccelerometerAlpha;
    private float hpfGyroscopeAlpha;
    private float lpfMagnetometerAlpha;

    private int rotor1_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
    private int rotor2_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
    private int rotor3_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
    private int rotor4_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;

    private long currentSamplePeriod_millisecs;

    private StringBuilder stringBuilderLogTags;
    private StringBuilder stringBuilderLogValues;

    private DevicesDynamicsHandler devicesDynamicsHandler;

    private LogHandler logHandler;

    private Intent broadcastIntent;

    public SensoryInterpretationThread(Context context) {
        SharedPreferences sharedPreferences;

        this.firstSample = true;
        this.hasReadBarometerCalibrationValues = false;

        this.context = context;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);

        this.broadcastIntent = new Intent(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.attitudeSensorAlpha = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_alpha_attitude_sensor), 9800) / 10000f;
        this.horizontalRotationSensorAlpha = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_alpha_horizontal_rotation_sensor), 9800) / 10000f;
        this.lpfAccelerometerAlpha = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_alpha_lpf_accelerometer), 5000) / 10000f;
        this.hpfGyroscopeAlpha = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_alpha_hpf_gyroscope), 5000) / 10000f;
        this.lpfMagnetometerAlpha = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_alpha_lpf_magnetometer), 5000) / 10000f;

        this.logEnabledSensory = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_enable_sensory_logs), true);
        this.lpfAccelerometerEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_lpf_accelerometer_enable), false);
        this.hpfGyroscopeEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_hpf_gyroscope_enable), false);
        this.lpfMagnetometerEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_lpf_magnetometer_enable), false);
//        this.mafTachometerEnable = sharedPreferences.getBoolean(this.context.getString(
//                R.string.pref_key_maf_tachometer_enable), true);

        this.tachometer1Enabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_tachometer1_enable), true);
        this.tachometer2Enabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_tachometer2_enable), true);
        this.tachometer3Enabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_tachometer3_enable), true);
        this.tachometer4Enabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_tachometer4_enable), true);
        this.sonarEastEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarE_enable), true);
        this.sonarNorthEastEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarNE_enable), true);
        this.sonarNorthEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarN_enable), true);
        this.sonarNorthWestEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarNW_enable), true);
        this.sonarWestEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarW_enable), true);
        this.sonarSouthWestEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarSW_enable), true);
        this.sonarSouthenabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarS_enable), true);
        this.sonarSouthEastenabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarSE_enable), true);
        this.sonarUpEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarUp_enable), true);
        this.sonarDownEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarDown_enable), true);
        this.barometerEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_barometer_enable), true);
        this.accelerometerEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_accelerometer_enable), true);
        this.gyroscopeEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_gyroscope_enable), true);
        this.gyroscopeRemoveOffsetEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_gyroscope_remove_offset_enable), true);
        this.magnetometerEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_magnetometer_enable), true);
        this.magnetometerRemoveOffsetEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_magnetometer_remove_offset_enable), true);

        if (this.accelerometerEnabled) {
            this.accelerometerInterpreter = new AccelerometerInterpreter();
        }
        if (this.barometerEnabled) {
            this.barometerInterpreter = new BarometerInterpreter();
        }
        if (this.gyroscopeEnabled) {
            this.gyroscopeInterpreter = new GyroscopeInterpreter();
            if (this.gyroscopeRemoveOffsetEnabled) {
                Hashtable<Integer, ArrayList<Float>> uncalibratedReadings;

                //We need to get a reference to DevicesDynamicsHandler instance
                this.devicesDynamicsHandler = DevicesDynamicsHandler.getInstance(this.context);
                uncalibratedReadings = this.devicesDynamicsHandler.getUncalibratedGyroscopeReadings();
                this.gyroscopeInterpreter.calculateOffset(uncalibratedReadings.get(AxisName.X),
                        uncalibratedReadings.get(AxisName.Y), uncalibratedReadings.get(AxisName.Z));
            }
        }
        if (this.magnetometerEnabled) {
            this.magnetometerInterpreter = new MagnetometerInterpreter();
            if(this.magnetometerRemoveOffsetEnabled){
                Hashtable<Integer, ArrayList<Float>> uncalibratedReadings;

                //We need to get a reference to DevicesDynamicsHandler instance
                this.devicesDynamicsHandler = DevicesDynamicsHandler.getInstance(this.context);
                uncalibratedReadings = this.devicesDynamicsHandler.getUncalibratedMagnetometerReadings();
                this.magnetometerInterpreter.calculateOffset(uncalibratedReadings.get(AxisName.X),
                        uncalibratedReadings.get(AxisName.Y));
            }
        }
        if (this.tachometer1Enabled) {
            this.tachometerInterpreter1 = new TachometerInterpreter();
        }
        if (this.tachometer2Enabled) {
            this.tachometerInterpreter2 = new TachometerInterpreter();
        }
        if (this.tachometer3Enabled) {
            this.tachometerInterpreter3 = new TachometerInterpreter();
        }
        if (this.tachometer4Enabled) {
            this.tachometerInterpreter4 = new TachometerInterpreter();
        }
        if (this.sonarNorthEnabled) {
            this.sonarInterpreterN = new SonarInterpreter();
        }
        if (this.sonarNorthEastEnabled) {
            this.sonarInterpreterNE = new SonarInterpreter();
        }
        if (this.sonarEastEnabled) {
            this.sonarInterpreterE = new SonarInterpreter();
        }
        if (this.sonarSouthEastenabled) {
            this.sonarInterpreterSE = new SonarInterpreter();
        }
        if (this.sonarSouthenabled) {
            this.sonarInterpreterS = new SonarInterpreter();
        }
        if (this.sonarSouthWestEnabled) {
            this.sonarInterpreterSW = new SonarInterpreter();
        }
        if (this.sonarWestEnabled) {
            this.sonarInterpreterW = new SonarInterpreter();
        }
        if (this.sonarNorthWestEnabled) {
            this.sonarInterpreterNW = new SonarInterpreter();
        }
        if (this.sonarUpEnabled) {
            this.sonarInterpreterUp = new SonarInterpreter();
        }
        if (this.sonarDownEnabled) {
            this.sonarInterpreterDown = new SonarInterpreter();
        }

        this.hasInputToProcess = false;

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(IOIOControllerLooper.INTENT_FILTER_SENSOR_READINGS)) {
                    getSensorReadingsFromBroadcast(intent);

                    hasInputToProcess = true;
                } else if (action.equals(TestsExecutorActivity.INTENT_FILTER_TEST_EXECUTOR_ACTIVITY)) {
                    rotor1_pulseWidthMicrosecs = intent.getIntExtra(ESC.ESC1_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    rotor2_pulseWidthMicrosecs = intent.getIntExtra(ESC.ESC2_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    rotor3_pulseWidthMicrosecs = intent.getIntExtra(ESC.ESC3_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    rotor4_pulseWidthMicrosecs = intent.getIntExtra(ESC.ESC4_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                } else if (action.equals(RotorSpeedControlService.INTENT_FILTER_ROTOR_SPEED_CONTROL_SERVICE)) {
                    rotor1_pulseWidthMicrosecs = intent.getIntExtra(
                            RotorSpeedControlService.RSC_OUTPUT_ROTOR1_PW_MICROSECS_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    rotor2_pulseWidthMicrosecs = intent.getIntExtra(
                            RotorSpeedControlService.RSC_OUTPUT_ROTOR2_PW_MICROSECS_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    rotor3_pulseWidthMicrosecs = intent.getIntExtra(
                            RotorSpeedControlService.RSC_OUTPUT_ROTOR3_PW_MICROSECS_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    rotor4_pulseWidthMicrosecs = intent.getIntExtra(
                            RotorSpeedControlService.RSC_OUTPUT_ROTOR4_PW_MICROSECS_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                }
            }
        };

        this.setName("SensoryInterpretationThread");

        this.logHandler = LogHandler.getInstance();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
//        IntentFilter intentFilter = new IntentFilter(IOIOControllerLooper.INTENT_FILTER_SENSOR_READINGS);
        IntentFilter intentFilter = new IntentFilter();
        //register receiver from IOIOControllerLooper
        intentFilter.addAction(IOIOControllerLooper.INTENT_FILTER_SENSOR_READINGS);
        //register receiver from TestsExecutorActivity
        intentFilter.addAction(TestsExecutorActivity.INTENT_FILTER_TEST_EXECUTOR_ACTIVITY);
        //register from Rotor Speed Control
        intentFilter.addAction(RotorSpeedControlService.INTENT_FILTER_ROTOR_SPEED_CONTROL_SERVICE);

        this.localBroadcastManager.registerReceiver(this.broadcastReceiver, intentFilter);

        this.running = true;

        while (this.running) {
            if (this.hasInputToProcess) {
                putReadingsToScale();

                filterSignals();

                fuseSensorsReadings();

                logAndSendInterpretedReadings();

                this.hasInputToProcess = false;
            }
        }
    }

    private void getSensorReadingsFromBroadcast(Intent intent) {
        this.currentSamplePeriod_millisecs = intent.getLongExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY,
                IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS);

        if (this.accelerometerEnabled) {
            this.accelerometerInterpreter.setAccelerationX_raw(intent.getIntExtra(Accelerometer.ACCELERATION_X_RAW_KEY, 0));
            this.accelerometerInterpreter.setAccelerationY_raw(intent.getIntExtra(Accelerometer.ACCELERATION_Y_RAW_KEY, 0));
            this.accelerometerInterpreter.setAccelerationZ_raw(intent.getIntExtra(Accelerometer.ACCELERATION_Z_RAW_KEY, 0));
        }

        if (this.barometerEnabled) {
//            if (this.firstSample) {
            this.barometerCalibrationValues = intent.getIntArrayExtra(Barometer.CALIBRATION_REGISTERS_KEY);
            if ((!this.hasReadBarometerCalibrationValues) && (this.barometerCalibrationValues != null)) {
                this.barometerInterpreter.setCalibrationValues(this.barometerCalibrationValues);
                this.hasReadBarometerCalibrationValues = true;
            }
            this.barometerInterpreter.setTemperature_raw(intent.getIntExtra(Barometer.TEMPERATURE_RAW_KEY, 0));
            this.barometerInterpreter.setPressure_raw(intent.getIntExtra(Barometer.AIR_PRESSURE_RAW_KEY, 0));
        }

        if (this.gyroscopeEnabled) {
            this.gyroscopeInterpreter.setAngularSpeedX_raw(intent.getIntExtra(Gyroscope.ANGULAR_SPEED_X_RAW_KEY, 0));
            this.gyroscopeInterpreter.setAngularSpeedY_raw(intent.getIntExtra(Gyroscope.ANGULAR_SPEED_Y_RAW_KEY, 0));
            this.gyroscopeInterpreter.setAngularSpeedZ_raw(intent.getIntExtra(Gyroscope.ANGULAR_SPEED_Z_RAW_KEY, 0));
        }

        if (this.magnetometerEnabled) {
            this.magnetometerInterpreter.setMagneticFieldX_raw(intent.getIntExtra(Magnetometer.MAGNETIC_FIELD_X_RAW_KEY, 0));
            this.magnetometerInterpreter.setMagneticFieldY_raw(intent.getIntExtra(Magnetometer.MAGNETIC_FIELD_Y_RAW_KEY, 0));
            this.magnetometerInterpreter.setMagneticFieldZ_raw(intent.getIntExtra(Magnetometer.MAGNETIC_FIELD_Z_RAW_KEY, 0));
        }

        if (this.tachometer1Enabled) {
            this.tachometerInterpreter1.setRevolutionsFrequency_hz(intent.getFloatExtra(
                    Tachometer.REVOLUTIONS_ROTOR1_HZ_KEY, 0f));
        }
        if (this.tachometer2Enabled) {
            this.tachometerInterpreter2.setRevolutionsFrequency_hz(intent.getFloatExtra(
                    Tachometer.REVOLUTIONS_ROTOR2_HZ_KEY, 0f));
        }
        if (this.tachometer3Enabled) {
            this.tachometerInterpreter3.setRevolutionsFrequency_hz(intent.getFloatExtra(
                    Tachometer.REVOLUTIONS_ROTOR3_HZ_KEY, 0f));
        }
        if (this.tachometer4Enabled) {
            this.tachometerInterpreter4.setRevolutionsFrequency_hz(intent.getFloatExtra(
                    Tachometer.REVOLUTIONS_ROTOR4_HZ_KEY, 0f));
        }

        if (this.sonarEastEnabled) {
            this.sonarInterpreterE.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_EAST_RAW_KEY, 0f));
        }
        if (this.sonarNorthEastEnabled) {
            this.sonarInterpreterNE.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_NORTHEAST_RAW_KEY, 0f));
        }
        if (this.sonarNorthEnabled) {
            this.sonarInterpreterN.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_NORTH_RAW_KEY, 0f));
        }
        if (this.sonarNorthWestEnabled) {
            this.sonarInterpreterNW.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_NORTHWEST_RAW_KEY, 0f));
        }
        if (this.sonarWestEnabled) {
            this.sonarInterpreterW.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_WEST_RAW_KEY, 0f));
        }
        if (this.sonarSouthWestEnabled) {
            this.sonarInterpreterSW.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_SOUTHWEST_RAW_KEY, 0f));
        }
        if (this.sonarSouthenabled) {
            this.sonarInterpreterS.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_SOUTH_RAW_KEY, 0f));
        }
        if (this.sonarSouthEastenabled) {
            this.sonarInterpreterSE.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_SOUTHEAST_RAW_KEY, 0f));
        }
        if (this.sonarUpEnabled) {
            this.sonarInterpreterUp.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_UP_RAW_KEY, 0f));
        }
        if (this.sonarDownEnabled) {
            this.sonarInterpreterDown.setDistance_raw(intent.getFloatExtra(Sonar.DISTANCE_DOWN_RAW_KEY, 0f));
        }
    }

    private void putReadingsToScale() {
        if (this.accelerometerEnabled) {
            this.accelerometerInterpreter.setAccelerationX_metersPerSecSquared(
                    this.accelerometerInterpreter.convertReadingToMetersPerSecSquared(
                            this.accelerometerInterpreter.getAccelerationX_raw()));
            this.accelerometerInterpreter.setAccelerationY_metersPerSecSquared(
                    this.accelerometerInterpreter.convertReadingToMetersPerSecSquared(
                            this.accelerometerInterpreter.getAccelerationY_raw()));
            this.accelerometerInterpreter.setAccelerationZ_metersPerSecSquared(
                    this.accelerometerInterpreter.convertReadingToMetersPerSecSquared(
                            this.accelerometerInterpreter.getAccelerationZ_raw()));
        }

        if (this.gyroscopeEnabled) {
            this.gyroscopeInterpreter.setAngularSpeedX_degreesPerSec(this.gyroscopeInterpreter.removeOffset(AxisName.X,
                    this.gyroscopeInterpreter.convertReadingToDegreesPerSec(this.gyroscopeInterpreter.getAngularSpeedX_raw())));
            this.gyroscopeInterpreter.setAngularSpeedY_degreesPerSec(this.gyroscopeInterpreter.removeOffset(AxisName.Y,
                    this.gyroscopeInterpreter.convertReadingToDegreesPerSec(this.gyroscopeInterpreter.getAngularSpeedY_raw())));
            this.gyroscopeInterpreter.setAngularSpeedZ_degreesPerSec(this.gyroscopeInterpreter.removeOffset(AxisName.Z,
                    this.gyroscopeInterpreter.convertReadingToDegreesPerSec(this.gyroscopeInterpreter.getAngularSpeedZ_raw())));
        }

        if (this.magnetometerEnabled) {
            this.magnetometerInterpreter.setMagneticFieldX_microTeslas(this.magnetometerInterpreter.removeOffset(AxisName.X,
                    this.magnetometerInterpreter.convertReadingToMicroTeslas(
                            this.magnetometerInterpreter.getMagneticFieldX_raw())));
            this.magnetometerInterpreter.setMagneticFieldY_microTeslas(this.magnetometerInterpreter.removeOffset(AxisName.Y,
                    this.magnetometerInterpreter.convertReadingToMicroTeslas(
                            this.magnetometerInterpreter.getMagneticFieldY_raw())));
            this.magnetometerInterpreter.setMagneticFieldZ_microTeslas(
                    this.magnetometerInterpreter.convertReadingToMicroTeslas(
                            this.magnetometerInterpreter.getMagneticFieldZ_raw()));
        }

        if (this.tachometer1Enabled) {
            this.tachometerInterpreter1.setRevolutionsFrequency_rpm(
                    this.tachometerInterpreter1.convertReadingToRevolutionsPerMinute(
                            this.tachometerInterpreter1.getRevolutionsFrequency_hz()));
        }
        if (this.tachometer2Enabled) {
            this.tachometerInterpreter2.setRevolutionsFrequency_rpm(
                    this.tachometerInterpreter2.convertReadingToRevolutionsPerMinute(
                            this.tachometerInterpreter2.getRevolutionsFrequency_hz()));
        }
        if (this.tachometer3Enabled) {
            this.tachometerInterpreter3.setRevolutionsFrequency_rpm(
                    this.tachometerInterpreter3.convertReadingToRevolutionsPerMinute(
                            this.tachometerInterpreter3.getRevolutionsFrequency_hz()));
        }
        if (this.tachometer4Enabled) {
            this.tachometerInterpreter4.setRevolutionsFrequency_rpm(
                    this.tachometerInterpreter4.convertReadingToRevolutionsPerMinute(
                            this.tachometerInterpreter4.getRevolutionsFrequency_hz()));
        }

        if (this.barometerEnabled) {
            this.barometerInterpreter.convertReadingsToDeciCelsius();
            this.barometerInterpreter.convertReadingsToPascals();
            this.barometerInterpreter.setTemperature_celsius(this.barometerInterpreter.convertTemperatureToCelsius());
            this.barometerInterpreter.setPressure_hectoPascals(this.barometerInterpreter.convertPressureToHectoPascals());
            this.barometerInterpreter.setAbsoluteAltitude_meters(this.barometerInterpreter.convertReadingsToAltitudeMeters());
        }

        if (this.sonarEastEnabled) {
            this.sonarInterpreterE.setDistance_centimeters(this.sonarInterpreterE.convertReadingToCentimeters(
                    this.sonarInterpreterE.getDistance_raw()));
        }
        if (this.sonarNorthEastEnabled) {
            this.sonarInterpreterNE.setDistance_centimeters(this.sonarInterpreterNE.convertReadingToCentimeters(
                    this.sonarInterpreterNE.getDistance_raw()));
        }
        if (this.sonarNorthEnabled) {
            this.sonarInterpreterN.setDistance_centimeters(this.sonarInterpreterN.convertReadingToCentimeters(
                    this.sonarInterpreterN.getDistance_raw()));
        }
        if (this.sonarNorthWestEnabled) {
            this.sonarInterpreterNW.setDistance_centimeters(this.sonarInterpreterNW.convertReadingToCentimeters(
                    this.sonarInterpreterNW.getDistance_raw()));
        }
        if (this.sonarWestEnabled) {
            this.sonarInterpreterW.setDistance_centimeters(this.sonarInterpreterW.convertReadingToCentimeters(
                    this.sonarInterpreterW.getDistance_raw()));
        }
        if (this.sonarSouthWestEnabled) {
            this.sonarInterpreterSW.setDistance_centimeters(this.sonarInterpreterSW.convertReadingToCentimeters(
                    this.sonarInterpreterSW.getDistance_raw()));
        }
        if (this.sonarSouthenabled) {
            this.sonarInterpreterS.setDistance_centimeters(this.sonarInterpreterS.convertReadingToCentimeters(
                    this.sonarInterpreterS.getDistance_raw()));
        }
        if (this.sonarSouthEastenabled) {
            this.sonarInterpreterSE.setDistance_centimeters(this.sonarInterpreterSE.convertReadingToCentimeters(
                    this.sonarInterpreterSE.getDistance_raw()));
        }
        if (this.sonarUpEnabled) {
            this.sonarInterpreterUp.setDistance_centimeters(this.sonarInterpreterUp.convertReadingToCentimeters(
                    this.sonarInterpreterUp.getDistance_raw()));
        }
        if (this.sonarDownEnabled) {
            this.sonarInterpreterDown.setDistance_centimeters(this.sonarInterpreterDown.convertReadingToCentimeters(
                    this.sonarInterpreterDown.getDistance_raw()));
        }
    }

    private void filterSignals() {
        if (this.accelerometerEnabled && this.lpfAccelerometerEnabled) {
            this.accelerometerInterpreter.applyLowPassFilterRC(this.lpfAccelerometerAlpha,
                    this.accelerometerInterpreter.getAccelerationX_metersPerSecSquared(),
                    this.accelerometerInterpreter.getAccelerationY_metersPerSecSquared(),
                    this.accelerometerInterpreter.getAccelerationZ_metersPerSecSquared(), this.firstSample);
        }

        if (this.gyroscopeEnabled && this.hpfGyroscopeEnabled) {
//            this.gyroscopeInterpreter.applyLowPassFilterRC(this.hpfGyroscopeAlpha,
            this.gyroscopeInterpreter.applyHighPassFilterRC(this.hpfGyroscopeAlpha,
                    this.gyroscopeInterpreter.getAngularSpeedX_degreesPerSec(),
                    this.gyroscopeInterpreter.getAngularSpeedY_degreesPerSec(),
                    this.gyroscopeInterpreter.getAngularSpeedZ_degreesPerSec(), this.firstSample);
        }

        if (this.magnetometerEnabled) {
            if (this.lpfMagnetometerEnabled) {
                this.magnetometerInterpreter.applyLowPassFilterRC(this.lpfMagnetometerAlpha,
                        this.magnetometerInterpreter.getMagneticFieldX_microTeslas(),
                        this.magnetometerInterpreter.getMagneticFieldY_microTeslas(),
                        this.magnetometerInterpreter.getMagneticFieldZ_microTeslas(), this.firstSample);
            }
            //Update compass heading
            this.magnetometerInterpreter.calculateCompassHeadingDegrees(AxisName.Z, this.context, this.lpfMagnetometerEnabled);
        }

//        if (this.mafTachometerEnable) {
//            if (this.tachometer1Enabled) {
//                this.tachometerInterpreter1.applyMovingAverageFilterIfLastValueIsZero();
//            }
//            if (this.tachometer2Enabled) {
//                this.tachometerInterpreter2.applyMovingAverageFilterIfLastValueIsZero();
//            }
//            if (this.tachometer3Enabled) {
//                this.tachometerInterpreter3.applyMovingAverageFilterIfLastValueIsZero();
//            }
//            if (this.tachometer4Enabled) {
//                this.tachometerInterpreter4.applyMovingAverageFilterIfLastValueIsZero();
//            }
//        }
    }

    private void fuseSensorsReadings() {
        float xAccelerationToFuse_metersPerSecSquared, yAccelerationToFuse_metersPerSecSquared,
                zAccelerationToFuse_metersPerSecSquared, xAngularSpeedToFuse_degreesPerSec,
                yAngularSpeedToFuse_degreesPerSec, zAngularSpeedToFuse_degreesPerSec;

        xAccelerationToFuse_metersPerSecSquared = yAccelerationToFuse_metersPerSecSquared =
                zAccelerationToFuse_metersPerSecSquared = xAngularSpeedToFuse_degreesPerSec =
                        yAngularSpeedToFuse_degreesPerSec = zAngularSpeedToFuse_degreesPerSec = 0;

        if (this.firstSample) {
            this.firstSample = false;
            this.logHeaderCreated = false;
            this.altimeterSensor = new AltimeterSensor();
            this.attitudeSensor = new AttitudeSensor(this.attitudeSensorAlpha);
            this.horizontalRotationSensor = new HorizontalRotationSensor(this.horizontalRotationSensorAlpha);
        }

        if (this.barometerEnabled && this.sonarDownEnabled) {
            this.altimeterSensor.updateAltitude(this.barometerInterpreter.getAbsoluteAltitude_meters(),
                    this.sonarInterpreterDown.getDistance_centimeters());
        }

        if (this.accelerometerEnabled) {
            if (this.lpfAccelerometerEnabled) {
                xAccelerationToFuse_metersPerSecSquared = this.accelerometerInterpreter.getCurrentFilteredValues_scaled().get(AxisName.X);
                yAccelerationToFuse_metersPerSecSquared = this.accelerometerInterpreter.getCurrentFilteredValues_scaled().get(AxisName.Y);
                zAccelerationToFuse_metersPerSecSquared = this.accelerometerInterpreter.getCurrentFilteredValues_scaled().get(AxisName.Z);
            } else {
                xAccelerationToFuse_metersPerSecSquared = this.accelerometerInterpreter.getAccelerationX_metersPerSecSquared();
                yAccelerationToFuse_metersPerSecSquared = this.accelerometerInterpreter.getAccelerationY_metersPerSecSquared();
                zAccelerationToFuse_metersPerSecSquared = this.accelerometerInterpreter.getAccelerationZ_metersPerSecSquared();
            }
        }

        if (this.gyroscopeEnabled) {
            if (this.hpfGyroscopeEnabled) {
                xAngularSpeedToFuse_degreesPerSec = this.gyroscopeInterpreter.getCurrentFilteredValues_scaled().get(AxisName.X);
                yAngularSpeedToFuse_degreesPerSec = this.gyroscopeInterpreter.getCurrentFilteredValues_scaled().get(AxisName.Y);
                zAngularSpeedToFuse_degreesPerSec = this.gyroscopeInterpreter.getCurrentFilteredValues_scaled().get(AxisName.Z);
            } else {
                xAngularSpeedToFuse_degreesPerSec = this.gyroscopeInterpreter.getAngularSpeedX_degreesPerSec();
                yAngularSpeedToFuse_degreesPerSec = this.gyroscopeInterpreter.getAngularSpeedY_degreesPerSec();
                zAngularSpeedToFuse_degreesPerSec = this.gyroscopeInterpreter.getAngularSpeedZ_degreesPerSec();
            }
        }

        if (this.accelerometerEnabled && this.gyroscopeEnabled) {
            this.attitudeSensor.updateAttitude(xAccelerationToFuse_metersPerSecSquared,
                    yAccelerationToFuse_metersPerSecSquared, zAccelerationToFuse_metersPerSecSquared,
                    xAngularSpeedToFuse_degreesPerSec, yAngularSpeedToFuse_degreesPerSec);
        }
        if (this.magnetometerEnabled && this.gyroscopeEnabled) {
            this.horizontalRotationSensor.updateHorizontalRotation(this.magnetometerInterpreter.getCompassHeading_degrees(),
                    zAngularSpeedToFuse_degreesPerSec);
        }
    }

    private void logAndSendInterpretedReadings() {
        float accelerationMetersPerSecSquaredX, accelerationMetersPerSecSquaredY, accelerationMetersPerSecSquaredZ,
                angularSpeedDegreesPerSecX, angularSpeedDegreesPerSecY, angularSpeedDegreesPerSecZ,
                magneticFieldMicroTeslaX, magneticFieldMicroTeslaY, magneticFieldMicroTeslaZ, compassHeading,
                speedRpmRotor1, speedRpmRotor2, speedRpmRotor3, speedRpmRotor4,
                pressureHectoPascals, temperatureCelsius, barometerAbsoluteAltitudeMeters, distanceDownCentimeters,
                altimeterAbsoluteAltitudeMeters, altimeterRelativeAltitudeMeters,
                rotationAngleFusedXDegrees, rotationAngleFusedYDegrees, rotationAngleFusedZDegrees,
                rotationAngleAccelerometerXDegrees, rotationAngleAccelerometerYDegrees, rotationAngleMagnetometerZDegrees,
                rotationAngleGyroscopeXDegrees, rotationAngleGyroscopeYDegrees, rotationAngleGyroscopeZDegrees;

        accelerationMetersPerSecSquaredX = accelerationMetersPerSecSquaredY = accelerationMetersPerSecSquaredZ = 0;
        angularSpeedDegreesPerSecX = angularSpeedDegreesPerSecY = angularSpeedDegreesPerSecZ = 0;
        magneticFieldMicroTeslaX = magneticFieldMicroTeslaY = magneticFieldMicroTeslaZ = compassHeading = 0;
        speedRpmRotor1 = speedRpmRotor2 = speedRpmRotor3 = speedRpmRotor4 = 0;
        pressureHectoPascals = temperatureCelsius = barometerAbsoluteAltitudeMeters = 0;
        distanceDownCentimeters = 0;

        if (this.accelerometerEnabled) {
            if (this.lpfAccelerometerEnabled) {
                // Filtered values are already scaled, thus there's no need to convert them to some measurement unit
                accelerationMetersPerSecSquaredX = this.accelerometerInterpreter.getCurrentFilteredValues_scaled().
                        get(AxisName.X);
                accelerationMetersPerSecSquaredY = this.accelerometerInterpreter.getCurrentFilteredValues_scaled().
                        get(AxisName.Y);
                accelerationMetersPerSecSquaredZ = this.accelerometerInterpreter.getCurrentFilteredValues_scaled().
                        get(AxisName.Z);
            } else {
                accelerationMetersPerSecSquaredX = this.accelerometerInterpreter.getAccelerationX_metersPerSecSquared();
                accelerationMetersPerSecSquaredY = this.accelerometerInterpreter.getAccelerationY_metersPerSecSquared();
                accelerationMetersPerSecSquaredZ = this.accelerometerInterpreter.getAccelerationZ_metersPerSecSquared();
            }

            this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_X_METERS_PER_SEC_SQUARED_KEY,
                    accelerationMetersPerSecSquaredX);
            this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_Y_METERS_PER_SEC_SQUARED_KEY,
                    accelerationMetersPerSecSquaredY);
            this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_Z_METERS_PER_SEC_SQUARED_KEY,
                    accelerationMetersPerSecSquaredZ);
        }

        if (this.gyroscopeEnabled) {
            if (this.hpfGyroscopeEnabled) {
                // Filtered values are already scaled, thus there's no need to convert them to some measurement unit
                angularSpeedDegreesPerSecX = this.gyroscopeInterpreter.getCurrentFilteredValues_scaled().get(AxisName.X);
                angularSpeedDegreesPerSecY = this.gyroscopeInterpreter.getCurrentFilteredValues_scaled().get(AxisName.Y);
                angularSpeedDegreesPerSecZ = this.gyroscopeInterpreter.getCurrentFilteredValues_scaled().get(AxisName.Z);
            } else {
                angularSpeedDegreesPerSecX = this.gyroscopeInterpreter.getAngularSpeedX_degreesPerSec();
                angularSpeedDegreesPerSecY = this.gyroscopeInterpreter.getAngularSpeedY_degreesPerSec();
                angularSpeedDegreesPerSecZ = this.gyroscopeInterpreter.getAngularSpeedZ_degreesPerSec();
            }

            this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_X_DEGREES_PER_SEC_KEY, angularSpeedDegreesPerSecX);
            this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_Y_DEGREES_PER_SEC_KEY, angularSpeedDegreesPerSecY);
            this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_Z_DEGREES_PER_SEC_KEY, angularSpeedDegreesPerSecZ);
        }

        if (this.magnetometerEnabled) {
            if (this.lpfMagnetometerEnabled) {
                // Filtered values are already scaled, thus there's no need to convert them to some measurement unit
                magneticFieldMicroTeslaX = this.magnetometerInterpreter.getCurrentFilteredValues_scaled().get(AxisName.X);
                magneticFieldMicroTeslaY = this.magnetometerInterpreter.getCurrentFilteredValues_scaled().get(AxisName.Y);
                magneticFieldMicroTeslaZ = this.magnetometerInterpreter.getCurrentFilteredValues_scaled().get(AxisName.Z);
            } else {
                magneticFieldMicroTeslaX = this.magnetometerInterpreter.getMagneticFieldX_microTeslas();
                magneticFieldMicroTeslaY = this.magnetometerInterpreter.getMagneticFieldY_microTeslas();
                magneticFieldMicroTeslaZ = this.magnetometerInterpreter.getMagneticFieldZ_microTeslas();
            }
            //Get compass heading
            compassHeading = this.magnetometerInterpreter.getCompassHeading_degrees();

            this.broadcastIntent.putExtra(Magnetometer.COMPASS_HEADING_KEY, compassHeading);
            this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_X_MICROTESLAS_KEY, magneticFieldMicroTeslaX);
            this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_Y_MICROTESLAS_KEY, magneticFieldMicroTeslaY);
            this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_Z_MICROTESLAS_KEY, magneticFieldMicroTeslaZ);
        }

        if (this.tachometer1Enabled) {
            speedRpmRotor1 = this.tachometerInterpreter1.getRevolutionsFrequency_rpm();

            this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR1_RPM_KEY, speedRpmRotor1);
        }
        if (this.tachometer2Enabled) {
            speedRpmRotor2 = this.tachometerInterpreter2.getRevolutionsFrequency_rpm();

            this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR2_RPM_KEY, speedRpmRotor2);
        }
        if (this.tachometer3Enabled) {
            speedRpmRotor3 = this.tachometerInterpreter3.getRevolutionsFrequency_rpm();

            this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR3_RPM_KEY, speedRpmRotor3);
        }
        if (this.tachometer4Enabled) {
            speedRpmRotor4 = this.tachometerInterpreter4.getRevolutionsFrequency_rpm();

            this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR4_RPM_KEY, speedRpmRotor4);
        }

        if (this.barometerEnabled) {
            temperatureCelsius = this.barometerInterpreter.getTemperature_celsius();
            pressureHectoPascals = this.barometerInterpreter.getPressure_hectoPascals();
            barometerAbsoluteAltitudeMeters = this.barometerInterpreter.getAbsoluteAltitude_meters();

            this.broadcastIntent.putExtra(Barometer.TEMPERATURE_CELSIUS_KEY, temperatureCelsius);
            this.broadcastIntent.putExtra(Barometer.AIR_PRESSURE_HECTOPASCALS_KEY, pressureHectoPascals);
            this.broadcastIntent.putExtra(Barometer.ABSOLUTE_ALTITUDE_METERS_KEY, barometerAbsoluteAltitudeMeters);
        }

        if (this.sonarEastEnabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_EAST_CENTIMETERS_KEY,
                    this.sonarInterpreterE.getDistance_centimeters());
        }
        if (this.sonarNorthEastEnabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTHEAST_CENTIMETERS_KEY,
                    this.sonarInterpreterNE.getDistance_centimeters());
        }
        if (this.sonarNorthEnabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTH_CENTIMETERS_KEY,
                    this.sonarInterpreterN.getDistance_centimeters());
        }
        if (this.sonarNorthWestEnabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTHWEST_CENTIMETERS_KEY,
                    this.sonarInterpreterNW.getDistance_centimeters());
        }
        if (this.sonarWestEnabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_WEST_CENTIMETERS_KEY,
                    this.sonarInterpreterW.getDistance_centimeters());
        }
        if (this.sonarSouthWestEnabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTHWEST_CENTIMETERS_KEY,
                    this.sonarInterpreterSW.getDistance_centimeters());
        }
        if (this.sonarSouthenabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTH_CENTIMETERS_KEY,
                    this.sonarInterpreterS.getDistance_centimeters());
        }
        if (this.sonarSouthEastenabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTHEAST_CENTIMETERS_KEY,
                    this.sonarInterpreterSE.getDistance_centimeters());
        }
        if (this.sonarUpEnabled) {
            this.broadcastIntent.putExtra(Sonar.DISTANCE_UP_CENTIMETERS_KEY,
                    this.sonarInterpreterUp.getDistance_centimeters());
        }
        if (this.sonarDownEnabled) {
            distanceDownCentimeters = this.sonarInterpreterDown.getDistance_centimeters();

            this.broadcastIntent.putExtra(Sonar.DISTANCE_DOWN_CENTIMETERS_KEY, distanceDownCentimeters);
        }

        // send fused sensory readings
        altimeterAbsoluteAltitudeMeters = this.altimeterSensor.getAbsoluteAltitude_meters();
        altimeterRelativeAltitudeMeters = this.altimeterSensor.getRelativeAltitude_meters();

        this.broadcastIntent.putExtra(AltimeterSensor.ABSOLUTE_ALTITUDE_METERS_KEY, altimeterAbsoluteAltitudeMeters);
        this.broadcastIntent.putExtra(AltimeterSensor.RELATIVE_ALTITUDE_METERS_KEY, altimeterRelativeAltitudeMeters);

        rotationAngleFusedXDegrees = this.attitudeSensor.getRotationAngleFusedX_degrees();
        rotationAngleFusedYDegrees = this.attitudeSensor.getRotationAngleFusedY_degrees();
        rotationAngleFusedZDegrees = this.horizontalRotationSensor.getHorizontalRotation_degrees();

        this.broadcastIntent.putExtra(AttitudeSensor.ROTATION_ANGLE_X_DEGREES_KEY, rotationAngleFusedXDegrees);
        this.broadcastIntent.putExtra(AttitudeSensor.ROTATION_ANGLE_Y_DEGREES_KEY, rotationAngleFusedYDegrees);
        this.broadcastIntent.putExtra(HorizontalRotationSensor.HORIZONTAL_ROTATION_DEGREES_KEY, rotationAngleFusedZDegrees);

        rotationAngleAccelerometerXDegrees = this.attitudeSensor.getRotationAngleFromAccelerometerX_degrees();
        rotationAngleAccelerometerYDegrees = this.attitudeSensor.getRotationAngleFromAccelerometerY_degrees();

        rotationAngleGyroscopeXDegrees = this.attitudeSensor.getRotationAngleAccumulatorFromGyroscopeX_degrees();
        rotationAngleGyroscopeYDegrees = this.attitudeSensor.getRotationAngleAccumulatorFromGyroscopeY_degrees();
        rotationAngleGyroscopeZDegrees = this.horizontalRotationSensor.getRotationAccumulatorFromGyroscopeZ_degrees();

        rotationAngleMagnetometerZDegrees = this.horizontalRotationSensor.getRotationAccumulatorFromMagnetometer_degrees();

        if (this.logEnabledSensory) {
            if (!this.logHeaderCreated) {
                createLogHeader();
            }

            if (this.stringBuilderLogValues == null) {
                this.stringBuilderLogValues = new StringBuilder();
            } else {
                this.stringBuilderLogValues.delete(0, this.stringBuilderLogValues.length());
            }

            this.stringBuilderLogValues.append(accelerationMetersPerSecSquaredX).append(",");
            this.stringBuilderLogValues.append(accelerationMetersPerSecSquaredY).append(",");
            this.stringBuilderLogValues.append(accelerationMetersPerSecSquaredZ).append(",");

            this.stringBuilderLogValues.append(angularSpeedDegreesPerSecX).append(",");
            this.stringBuilderLogValues.append(angularSpeedDegreesPerSecY).append(",");
            this.stringBuilderLogValues.append(angularSpeedDegreesPerSecZ).append(",");

            this.stringBuilderLogValues.append(magneticFieldMicroTeslaX).append(",");
            this.stringBuilderLogValues.append(magneticFieldMicroTeslaY).append(",");
            this.stringBuilderLogValues.append(magneticFieldMicroTeslaZ).append(",");
            this.stringBuilderLogValues.append(compassHeading).append(",");

            this.stringBuilderLogValues.append(this.rotor1_pulseWidthMicrosecs).append(",");
            this.stringBuilderLogValues.append(this.rotor2_pulseWidthMicrosecs).append(",");
            this.stringBuilderLogValues.append(this.rotor3_pulseWidthMicrosecs).append(",");
            this.stringBuilderLogValues.append(this.rotor4_pulseWidthMicrosecs).append(",");

            this.stringBuilderLogValues.append(speedRpmRotor1).append(",");
            this.stringBuilderLogValues.append(speedRpmRotor2).append(",");
            this.stringBuilderLogValues.append(speedRpmRotor3).append(",");
            this.stringBuilderLogValues.append(speedRpmRotor4).append(",");

            this.stringBuilderLogValues.append(pressureHectoPascals).append(",");
            this.stringBuilderLogValues.append(temperatureCelsius).append(",");
            this.stringBuilderLogValues.append(barometerAbsoluteAltitudeMeters).append(",");

            this.stringBuilderLogValues.append(distanceDownCentimeters).append(",");

            this.stringBuilderLogValues.append(altimeterAbsoluteAltitudeMeters).append(",");
            this.stringBuilderLogValues.append(altimeterRelativeAltitudeMeters).append(",");

            this.stringBuilderLogValues.append(rotationAngleFusedXDegrees).append(",");
            this.stringBuilderLogValues.append(rotationAngleFusedYDegrees).append(",");
            this.stringBuilderLogValues.append(rotationAngleFusedZDegrees).append(",");

            this.stringBuilderLogValues.append(rotationAngleAccelerometerXDegrees).append(",");
            this.stringBuilderLogValues.append(rotationAngleAccelerometerYDegrees).append(",");

            this.stringBuilderLogValues.append(rotationAngleGyroscopeXDegrees).append(",");
            this.stringBuilderLogValues.append(rotationAngleGyroscopeYDegrees).append(",");
            this.stringBuilderLogValues.append(rotationAngleGyroscopeZDegrees).append(",");

            this.stringBuilderLogValues.append(rotationAngleMagnetometerZDegrees);

            this.logHandler.appendDataToLog(R.string.pref_key_enable_sensory_logs, this.stringBuilderLogValues);
        }

        //send current sample period
        this.broadcastIntent.putExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY, this.currentSamplePeriod_millisecs);

        this.localBroadcastManager.sendBroadcast(this.broadcastIntent);
    }

    private void createLogHeader() {
        if (this.stringBuilderLogTags == null) {
            this.stringBuilderLogTags = new StringBuilder();
        } else {
            this.stringBuilderLogTags.delete(0, this.stringBuilderLogTags.length());
        }

        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_x));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_acceleration_metersPerSecSquared_compact)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_y));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_acceleration_metersPerSecSquared_compact)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_z));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_acceleration_metersPerSecSquared_compact)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_x));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_angularSpeed_degreesPerSec_compact)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_y));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_angularSpeed_degreesPerSec_compact)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_z));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_angularSpeed_degreesPerSec_compact)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_x));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_magneticField_microTeslas_compact)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_y));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_magneticField_microTeslas_compact)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_z));
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.measurementUnit_magneticField_microTeslas_compact)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_compass_heading));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_rotations_pulseWidthMicrosecs_rotor1)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_rotations_pulseWidthMicrosecs_rotor2)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_rotations_pulseWidthMicrosecs_rotor3)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_rotations_pulseWidthMicrosecs_rotor4)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(
                R.string.logTag_rotations_rotationsPerMinute_rotor1)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.logTag_rotations_rotationsPerMinute_rotor2)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.logTag_rotations_rotationsPerMinute_rotor3)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(
                R.string.logTag_rotations_rotationsPerMinute_rotor4)).append(",");

        this.stringBuilderLogTags.append("Baro").append(this.context.getString(
                R.string.measurementUnit_pressure_hectoPascals_compact)).append(",");
        this.stringBuilderLogTags.append("Baro").append(this.context.getString(
                R.string.measurementUnit_temperature_celsius_compact)).append(",");
        this.stringBuilderLogTags.append("Baro").append(this.context.getString(
                R.string.measurementUnit_altitude_meters_compact)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_distance_centimeters_down)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_absolute_altitude));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_altitude_meters_compact)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_relative_altitude));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_altitude_meters_compact)).append(",");

        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_x)).append(" ");
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_y)).append(" ");
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");
        this.stringBuilderLogTags.append(this.context.getString(R.string.logTag_axis_z)).append(" ");
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");

        this.stringBuilderLogTags.append("Acce ").append(this.context.getString(R.string.logTag_axis_x));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");
        this.stringBuilderLogTags.append("Acce ").append(this.context.getString(R.string.logTag_axis_y));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");

        this.stringBuilderLogTags.append("Gyro ").append(this.context.getString(R.string.logTag_axis_x));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");
        this.stringBuilderLogTags.append("Gyro ").append(this.context.getString(R.string.logTag_axis_y));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");
        this.stringBuilderLogTags.append("Gyro ").append(this.context.getString(R.string.logTag_axis_z));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees)).append(",");

        this.stringBuilderLogTags.append("Mag ").append(this.context.getString(R.string.logTag_axis_z));
        this.stringBuilderLogTags.append(this.context.getString(R.string.measurementUnit_axisRotation_degrees));

        this.logHandler.appendDataToLog(R.string.pref_key_enable_sensory_logs, this.stringBuilderLogTags);

        this.logHeaderCreated = true;
    }
}
