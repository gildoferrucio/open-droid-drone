package com.example.gildo.quadcoptercontroller.models.entities;

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
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Accelerometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Barometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Gyroscope;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Magnetometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Sonar;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Tachometer;
import com.example.gildo.quadcoptercontroller.models.constants.RotorId;
import com.example.gildo.quadcoptercontroller.models.constants.SonarId;
import com.example.gildo.quadcoptercontroller.models.entities.threads.devices.BarometerThread;
import com.example.gildo.quadcoptercontroller.models.entities.threads.devices.DeviceThread;
import com.example.gildo.quadcoptercontroller.models.entities.threads.devices.IMUThread;
import com.example.gildo.quadcoptercontroller.models.entities.threads.devices.SonarThread;
import com.example.gildo.quadcoptercontroller.models.entities.threads.devices.TachometerThread;
import com.example.gildo.quadcoptercontroller.services.control.RotorSpeedControlService;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

/**
 * Created by gildo on 31/03/16.
 */
public class IOIOControllerLooper extends BaseIOIOLooper {
    public static final String INTENT_FILTER_SENSOR_READINGS = "sensorsReadings";

    public static final String SAMPLING_PERIOD_MILLISECS_KEY = "currentSamplePeriod_millisecs";

    public static final int STANDARD_SAMPLE_PERIOD_MILLISECS = 14;

    private DigitalOutput led;

    private BarometerThread barometerThread;
    private IMUThread imuThread;
    private TachometerThread tachometerThread1;
    private TachometerThread tachometerThread2;
    private TachometerThread tachometerThread3;
    private TachometerThread tachometerThread4;
    private SonarThread sonarEThread;
    private SonarThread sonarNEThread;
    private SonarThread sonarNThread;
    private SonarThread sonarNWThread;
    private SonarThread sonarWThread;
    private SonarThread sonarSWThread;
    private SonarThread sonarSThread;
    private SonarThread sonarSEThread;
    private SonarThread sonarUpThread;
    private SonarThread sonarDownThread;

    private int[] acceleration;
    private int[] angularSpeed;
    private int[] magneticField;

    private int[] calibrationRegistersBarometer;
    private int temperatureBarometer;
    private int airPressure;

    private float rotorRevolutions1;
    private float rotorRevolutions2;
    private float rotorRevolutions3;
    private float rotorRevolutions4;

    private float distanceEast;
    private float distanceNortheast;
    private float distanceNorth;
    private float distanceNorthwest;
    private float distanceWest;
    private float distanceSouthwest;
    private float distanceSouth;
    private float distanceSoutheast;
    private float distanceUp;
    private float distanceDown;

    private int rotor1_pulseWidthMicrosecs;
    private int rotor2_pulseWidthMicrosecs;
    private int rotor3_pulseWidthMicrosecs;
    private int rotor4_pulseWidthMicrosecs;

    private ESC esc1;
    private ESC esc2;
    private ESC esc3;
    private ESC esc4;

    private int delay;

    private Context context;

    private Intent broadcastIntent;

    private LocalBroadcastManager localBroadcastManager;

    private boolean successfulInitialization;
    private boolean firstSample;
    private boolean ledState;

    private boolean isTachometer1Enabled;
    private boolean isTachometer2Enabled;
    private boolean isTachometer3Enabled;
    private boolean isTachometer4Enabled;
    private boolean isEsc1Enabled;
    private boolean isEsc2Enabled;
    private boolean isEsc3Enabled;
    private boolean isEsc4Enabled;
    private boolean iSonarE_enabled;
    private boolean isSonarNE_enabled;
    private boolean isSonarN_enabled;
    private boolean isSonarNW_enabled;
    private boolean isSonarW_enabled;
    private boolean isSonarSW_enabled;
    private boolean isSonarS_enabled;
    private boolean isSonarSE_enabled;
    private boolean isSonarUpEnabled;
    private boolean isSonarDownEnabled;
    private boolean isBarometerEnabled;
    private boolean isAccelerometerEnabled;
    private boolean isGyroscopeEnabled;
    private boolean isMagnetometerEnabled;

    private BroadcastReceiver broadcastReceiver;

    private long previousSampleTimestamp;

    public IOIOControllerLooper(Context context) {
        super();
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void setup() throws ConnectionLostException, InterruptedException {
        //Implement ports openings
        this.successfulInitialization = true;
        this.firstSample = true;

        setEnabledDevices();
        initializeDevices();

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        this.broadcastIntent = new Intent(IOIOControllerLooper.INTENT_FILTER_SENSOR_READINGS);

        if (this.successfulInitialization) {
            this.broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    if (action.equals(TestsExecutorActivity.INTENT_FILTER_TEST_EXECUTOR_ACTIVITY)) {
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
                    } else if (action.equals(IMUThread.INTENT_FILTER_IMU_THREAD)) {
                        acceleration[0] = intent.getIntExtra(Accelerometer.ACCELERATION_X_RAW_KEY, 0);
                        acceleration[1] = intent.getIntExtra(Accelerometer.ACCELERATION_Y_RAW_KEY, 0);
                        acceleration[2] = intent.getIntExtra(Accelerometer.ACCELERATION_Z_RAW_KEY, 0);

                        angularSpeed[0] = intent.getIntExtra(Gyroscope.ANGULAR_SPEED_X_RAW_KEY, 0);
                        angularSpeed[1] = intent.getIntExtra(Gyroscope.ANGULAR_SPEED_Y_RAW_KEY, 0);
                        angularSpeed[2] = intent.getIntExtra(Gyroscope.ANGULAR_SPEED_Z_RAW_KEY, 0);

                        magneticField[0] = intent.getIntExtra(Magnetometer.MAGNETIC_FIELD_X_RAW_KEY, 0);
                        magneticField[1] = intent.getIntExtra(Magnetometer.MAGNETIC_FIELD_Y_RAW_KEY, 0);
                        magneticField[2] = intent.getIntExtra(Magnetometer.MAGNETIC_FIELD_Z_RAW_KEY, 0);
                    } else if (action.equals(BarometerThread.INTENT_FILTER_BAROMETER_THREAD)) {
                        calibrationRegistersBarometer = intent.getIntArrayExtra(Barometer.CALIBRATION_REGISTERS_KEY);
                        temperatureBarometer = intent.getIntExtra(Barometer.TEMPERATURE_RAW_KEY, 0);
                        airPressure = intent.getIntExtra(Barometer.AIR_PRESSURE_RAW_KEY, 0);
                    } else if (action.equals(TachometerThread.INTENT_FILTER_TACHOMETER1_THREAD)) {
                        rotorRevolutions1 = intent.getFloatExtra(Tachometer.REVOLUTIONS_ROTOR1_HZ_KEY, 0f);
                    } else if (action.equals(TachometerThread.INTENT_FILTER_TACHOMETER2_THREAD)) {
                        rotorRevolutions2 = intent.getFloatExtra(Tachometer.REVOLUTIONS_ROTOR2_HZ_KEY, 0f);
                    } else if (action.equals(TachometerThread.INTENT_FILTER_TACHOMETER3_THREAD)) {
                        rotorRevolutions3 = intent.getFloatExtra(Tachometer.REVOLUTIONS_ROTOR3_HZ_KEY, 0f);
                    } else if (action.equals(TachometerThread.INTENT_FILTER_TACHOMETER4_THREAD)) {
                        rotorRevolutions4 = intent.getFloatExtra(Tachometer.REVOLUTIONS_ROTOR4_HZ_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_DOWN_THREAD)) {
                        distanceDown = intent.getFloatExtra(Sonar.DISTANCE_DOWN_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_UP_THREAD)) {
                        distanceUp = intent.getFloatExtra(Sonar.DISTANCE_UP_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_EAST_THREAD)) {
                        distanceEast = intent.getFloatExtra(Sonar.DISTANCE_EAST_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_NORTHEAST_THREAD)) {
                        distanceNortheast = intent.getFloatExtra(Sonar.DISTANCE_NORTHEAST_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_NORTH_THREAD)) {
                        distanceNorth = intent.getFloatExtra(Sonar.DISTANCE_NORTH_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_NORTHWEST_THREAD)) {
                        distanceNorthwest = intent.getFloatExtra(Sonar.DISTANCE_NORTHWEST_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_WEST_THREAD)) {
                        distanceWest = intent.getFloatExtra(Sonar.DISTANCE_WEST_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_SOUTHWEST_THREAD)) {
                        distanceSouthwest = intent.getFloatExtra(Sonar.DISTANCE_SOUTHWEST_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_SOUTH_THREAD)) {
                        distanceSouth = intent.getFloatExtra(Sonar.DISTANCE_SOUTH_RAW_KEY, 0f);
                    } else if (action.equals(SonarThread.INTENT_FILTER_SONAR_SOUTHEAST_THREAD)) {
                        distanceSoutheast = intent.getFloatExtra(Sonar.DISTANCE_SOUTHEAST_RAW_KEY, 0f);
                    }
                }
            };

            if (this.broadcastReceiver != null) {
                IntentFilter intentFilter = new IntentFilter();
                //register intentFilter from TestsExecutorActivity
                intentFilter.addAction(TestsExecutorActivity.INTENT_FILTER_TEST_EXECUTOR_ACTIVITY);
                //register intentFilter from Rotor Speed Control
                intentFilter.addAction(RotorSpeedControlService.INTENT_FILTER_ROTOR_SPEED_CONTROL_SERVICE);
                //register intentFilter from IMUThread
                intentFilter.addAction(IMUThread.INTENT_FILTER_IMU_THREAD);
                //register intentFilter from BarometerThread
                intentFilter.addAction(BarometerThread.INTENT_FILTER_BAROMETER_THREAD);
                //register intentFilter from TachometerThread 1,2,3,4
                intentFilter.addAction(TachometerThread.INTENT_FILTER_TACHOMETER1_THREAD);
                intentFilter.addAction(TachometerThread.INTENT_FILTER_TACHOMETER2_THREAD);
                intentFilter.addAction(TachometerThread.INTENT_FILTER_TACHOMETER3_THREAD);
                intentFilter.addAction(TachometerThread.INTENT_FILTER_TACHOMETER4_THREAD);
                //register intentFilter from SonarThread E,NE,N,NW,W,SW,S,SE,Up,Down
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_EAST_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_NORTHEAST_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_NORTH_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_NORTHWEST_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_WEST_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_SOUTHWEST_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_SOUTH_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_SOUTHEAST_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_UP_THREAD);
                intentFilter.addAction(SonarThread.INTENT_FILTER_SONAR_DOWN_THREAD);

                this.localBroadcastManager.registerReceiver(this.broadcastReceiver, intentFilter);
            }
        } else {
            this.delay = 1500;
        }

        this.led = this.ioio_.openDigitalOutput(IOIO.LED_PIN);
        this.ledState = true;
        //Delay inserted to make sure that all devices had been initialized
        Thread.sleep(DeviceThread.FIRST_WAIT_TIME_MILLISECS * 2);
    }

    private void setEnabledDevices() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.isTachometer1Enabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_tachometer1_enable), true);
        this.isTachometer2Enabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_tachometer2_enable), true);
        this.isTachometer3Enabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_tachometer3_enable), true);
        this.isTachometer4Enabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_tachometer4_enable), true);
        this.isEsc1Enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_esc1_enable), true);
        this.isEsc2Enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_esc2_enable), true);
        this.isEsc3Enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_esc3_enable), true);
        this.isEsc4Enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_esc4_enable), true);
        this.iSonarE_enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarE_enable), false);
        this.isSonarNE_enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarNE_enable), false);
        this.isSonarN_enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarN_enable), false);
        this.isSonarNW_enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarNW_enable), false);
        this.isSonarW_enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarW_enable), false);
        this.isSonarSW_enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarSW_enable), false);
        this.isSonarS_enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarS_enable), false);
        this.isSonarSE_enabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarSE_enable), false);
        this.isSonarUpEnabled = sharedPreferences.getBoolean(this.context.getString(R.string.pref_key_sonarUp_enable), false);
        this.isSonarDownEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_sonarDown_enable), true);
        this.isBarometerEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_barometer_enable), true);
        this.isAccelerometerEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_accelerometer_enable), true);
        this.isGyroscopeEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_gyroscope_enable), true);
        this.isMagnetometerEnabled = sharedPreferences.getBoolean(this.context.getString(
                R.string.pref_key_magnetometer_enable), true);
    }

    private void initializeDevices() throws ConnectionLostException, InterruptedException {
        //ESCs initialization
        if (this.isEsc1Enabled) {
            this.rotor1_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
            this.esc1 = new ESC(this.ioio_, 6);
        }
        if (this.isEsc2Enabled) {
            this.rotor2_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
            this.esc2 = new ESC(this.ioio_, 7);
        }
        if (this.isEsc3Enabled) {
            this.rotor3_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
            this.esc3 = new ESC(this.ioio_, 27);
        }
        if (this.isEsc4Enabled) {
            this.rotor4_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
            this.esc4 = new ESC(this.ioio_, 28);
        }

        /* The PIC microprocessor used in IOIO has 9 single precision counters. That can be combined to double precision
           counters. The standard IOIOLibCore downloaded via Gradle sets 3 single precision counters and 3 double
           precision. To change this code it's needed to modify the IOIOLibCore package.
           Reference: https://groups.google.com/forum/#!topic/ioio-users/hlQwYpGfbMQ
         */
        //Tachometers initialization
        if (this.isTachometer1Enabled) {
            this.tachometerThread1 = new TachometerThread(this.ioio_, 11, RotorId.ONE, false, this.context);
            this.tachometerThread1.start();
        }
        if (this.isTachometer2Enabled) {
            this.tachometerThread2 = new TachometerThread(this.ioio_, 12, RotorId.TWO, false, this.context);
            this.tachometerThread2.start();
        }
        if (this.isTachometer3Enabled) {
            this.tachometerThread3 = new TachometerThread(this.ioio_, 13, RotorId.THREE, false, this.context);
            this.tachometerThread3.start();
        }
        if (this.isTachometer4Enabled) {
            this.tachometerThread4 = new TachometerThread(this.ioio_, 14, RotorId.FOUR, true, this.context);
            this.tachometerThread4.start();
        }

        //Sonars initialization
        if (this.iSonarE_enabled) {
            this.sonarEThread = new SonarThread(this.ioio_, 32, SonarId.EAST, true, this.context);
            this.sonarEThread.start();
        }
        if (this.isSonarNE_enabled) {
            this.sonarNEThread = new SonarThread(this.ioio_, 34, SonarId.NORTHEAST, true, this.context);
            this.sonarNEThread.start();
        }
        if (this.isSonarN_enabled) {
            this.sonarNThread = new SonarThread(this.ioio_, 35, SonarId.NORTH, true, this.context);
            this.sonarNThread.start();
        }
        if (this.isSonarNW_enabled) {
            this.sonarNWThread = new SonarThread(this.ioio_, 36, SonarId.NORTHWEST, true, this.context);
            this.sonarNWThread.start();
        }
        if (this.isSonarW_enabled) {
            this.sonarWThread = new SonarThread(this.ioio_, 37, SonarId.WEST, true, this.context);
            this.sonarWThread.start();
        }
        if (this.isSonarSW_enabled) {
            this.sonarSWThread = new SonarThread(this.ioio_, 38, SonarId.SOUTHWEST, true, this.context);
            this.sonarSWThread.start();
        }
        if (this.isSonarS_enabled) {
            this.sonarSThread = new SonarThread(this.ioio_, 39, SonarId.SOUTH, true, this.context);
            this.sonarSThread.start();
        }
        if (this.isSonarSE_enabled) {
            this.sonarSEThread = new SonarThread(this.ioio_, 40, SonarId.SOUTHEAST, true, this.context);
            this.sonarSEThread.start();
        }
        if (this.isSonarUpEnabled) {
            this.sonarUpThread = new SonarThread(this.ioio_, 46, SonarId.UP, true, this.context);
            this.sonarUpThread.start();
        }
        if (this.isSonarDownEnabled) {
            this.sonarDownThread = new SonarThread(this.ioio_, 45, SonarId.DOWN, true, this.context);
            this.sonarDownThread.start();
        }

        //Barometer initialization
        if (this.isBarometerEnabled) {
            this.calibrationRegistersBarometer = new int[11];
            this.barometerThread = new BarometerThread(this.ioio_, this.context);
            this.successfulInitialization = this.barometerThread.isSuccessfulInitialization();
            this.barometerThread.start();
        }

        //IMU(Accelerometer, Gyroscope, Magnetometer) initialization
        if (this.isAccelerometerEnabled || this.isGyroscopeEnabled || this.isMagnetometerEnabled) {
            this.acceleration = new int[3];
            this.angularSpeed = new int[3];
            this.magneticField = new int[3];
            this.imuThread = new IMUThread(this.ioio_, this.context, this.isAccelerometerEnabled, this.isGyroscopeEnabled,
                    this.isMagnetometerEnabled);
            this.successfulInitialization = this.imuThread.isSuccessfulInitialization();
            this.imuThread.start();
        }
    }

    @Override
    public void loop() throws ConnectionLostException, InterruptedException {
        long timeStampBefore, timeStampAfter, timeStampResult, currentSampleTimestamp, samplesPeriodTimestamp;

        //Implement sensors readings and actuators activation
        if (this.successfulInitialization) {
            timeStampBefore = System.currentTimeMillis();
            currentSampleTimestamp = timeStampBefore;
            if (this.firstSample) {
                samplesPeriodTimestamp = IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS;
            } else {
                samplesPeriodTimestamp = currentSampleTimestamp - this.previousSampleTimestamp;
            }
            this.previousSampleTimestamp = currentSampleTimestamp;
            this.broadcastIntent.putExtra(IOIOControllerLooper.SAMPLING_PERIOD_MILLISECS_KEY, samplesPeriodTimestamp);

            if (this.isAccelerometerEnabled) {
                this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_X_RAW_KEY, this.acceleration[0]);
                this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_Y_RAW_KEY, this.acceleration[1]);
                this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_Z_RAW_KEY, this.acceleration[2]);
            }

            if (this.isGyroscopeEnabled) {
                this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_X_RAW_KEY, this.angularSpeed[0]);
                this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_Y_RAW_KEY, this.angularSpeed[1]);
                this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_Z_RAW_KEY, this.angularSpeed[2]);
            }

            if (this.isMagnetometerEnabled) {
                this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_X_RAW_KEY, this.magneticField[0]);
                this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_Y_RAW_KEY, this.magneticField[1]);
                this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_Z_RAW_KEY, this.magneticField[2]);
            }

            if (this.isBarometerEnabled) {
                if (this.firstSample && this.calibrationRegistersBarometer != null) {
                    //Barometer calibration registers send
                    this.broadcastIntent.putExtra(Barometer.CALIBRATION_REGISTERS_KEY, this.calibrationRegistersBarometer);
                    this.firstSample = false;
                }
                this.broadcastIntent.putExtra(Barometer.TEMPERATURE_RAW_KEY, this.temperatureBarometer);
                this.broadcastIntent.putExtra(Barometer.AIR_PRESSURE_RAW_KEY, this.airPressure);
            }

            if (this.iSonarE_enabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_EAST_RAW_KEY, this.distanceEast);
            }
            if (this.isSonarNE_enabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTHEAST_RAW_KEY, this.distanceNortheast);
            }
            if (this.isSonarN_enabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTH_RAW_KEY, this.distanceNorth);
            }
            if (this.isSonarNW_enabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTHWEST_RAW_KEY, this.distanceNorthwest);
            }
            if (this.isSonarW_enabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_WEST_RAW_KEY, this.distanceWest);
            }
            if (this.isSonarSW_enabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTHWEST_RAW_KEY, this.distanceSouthwest);
            }
            if (this.isSonarS_enabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTH_RAW_KEY, this.distanceSouth);
            }
            if (this.isSonarSE_enabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTHEAST_RAW_KEY, this.distanceSoutheast);
            }
            if (this.isSonarUpEnabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_UP_RAW_KEY, this.distanceUp);
            }
            if (this.isSonarDownEnabled) {
                this.broadcastIntent.putExtra(Sonar.DISTANCE_DOWN_RAW_KEY, this.distanceDown);
            }

            if (this.isTachometer1Enabled) {
                this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR1_HZ_KEY, this.rotorRevolutions1);
            }
            if (this.isTachometer2Enabled) {
                this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR2_HZ_KEY, this.rotorRevolutions2);
            }
            if (this.isTachometer3Enabled) {
                this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR3_HZ_KEY, this.rotorRevolutions3);
            }
            if (this.isTachometer4Enabled) {
                this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR4_HZ_KEY, this.rotorRevolutions4);
            }


            if (this.isEsc1Enabled) {
                this.esc1.setPulseWidth_Microsecs(this.rotor1_pulseWidthMicrosecs);
                this.esc1.setNewPulseWidthToESC();
            }
            if (this.isEsc2Enabled) {
                this.esc2.setPulseWidth_Microsecs(this.rotor2_pulseWidthMicrosecs);
                this.esc2.setNewPulseWidthToESC();
            }
            if (this.isEsc3Enabled) {
                this.esc3.setPulseWidth_Microsecs(this.rotor3_pulseWidthMicrosecs);
                this.esc3.setNewPulseWidthToESC();
            }
            if (this.isEsc4Enabled) {
                this.esc4.setPulseWidth_Microsecs(this.rotor4_pulseWidthMicrosecs);
                this.esc4.setNewPulseWidthToESC();
            }

            this.localBroadcastManager.sendBroadcast(this.broadcastIntent);

            timeStampResult = System.currentTimeMillis() - timeStampBefore;
            if (timeStampResult < IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS) {
                Thread.sleep(IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS - timeStampResult);
            }
//                System.gc();
        } else {
            Thread.sleep(this.delay);
        }
        this.led.write(this.ledState);
        this.ledState = !this.ledState;
    }
}
