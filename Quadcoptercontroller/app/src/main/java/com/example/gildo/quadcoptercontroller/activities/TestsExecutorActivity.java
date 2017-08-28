package com.example.gildo.quadcoptercontroller.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.devices.actuators.ESC;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.AccelerometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.BarometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.GyroscopeInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.MagnetometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.SensorInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.SonarInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.TachometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Accelerometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Barometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Gyroscope;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Magnetometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Sonar;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Tachometer;
import com.example.gildo.quadcoptercontroller.models.entities.Rotor;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;
import com.example.gildo.quadcoptercontroller.models.constants.PeripheralKind;
import com.example.gildo.quadcoptercontroller.models.constants.RotorId;
import com.example.gildo.quadcoptercontroller.models.gui.adapters.Peripheral;
import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;
import com.example.gildo.quadcoptercontroller.services.SensoryInterpretationService;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.model.AbstractChartData;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.AbstractChartView;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class TestsExecutorActivity extends AppCompatActivity implements View.OnLongClickListener,
        CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {
    // used a component for plotting graphics from https://android-arsenal.com/details/1/1068

    public static final String INTENT_FILTER_TEST_EXECUTOR_ACTIVITY = "testExecutorActivity";

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 10;
    private static final int DATA_UPDATE_COUNTS_TO_UPDATE_GRAPHS = 3;

    private Spinner spinner_deviceSelector;
    private Spinner spinner_activeTestTypeSelector;

    private LinearLayout linearLayout_passiveTest;
    private LinearLayout linearLayout_activeTest;
    private LinearLayout linearLayout_graph1;
    private LinearLayout linearLayout_graph2;
    private LinearLayout linearLayout_graph3;

    private Switch aSwitch_passiveTest;
    private Switch aSwitch_activeTest;

    private Peripheral peripheral;

    private boolean rollingPassiveTest;
    private boolean rollingActiveTest;
    private boolean logEnabledDeviceTests;

    private String selectedDevice;
    private String selectedActiveTestType;

    private AbstractChartView abstractChartView1;
    private AbstractChartView abstractChartView2;
    private AbstractChartView abstractChartView3;

    private AbstractChartData abstractChartData1;
    private AbstractChartData abstractChartData2;
    private AbstractChartData abstractChartData3;

    private BroadcastReceiver broadcastReceiver;

    private SensorInterpreter sensorInterpreter;

    private LogHandler logHandler;

    private Intent broadcastIntent;

    private LocalBroadcastManager localBroadcastManager;

    private VerticalSeekBar verticalSeekBar_throttle;

    private TextView textView_sensorReading;
    private TextView textView_actuatorTarget;

    private StringBuilder stringBuilderTextView;
    private StringBuilder stringBuilderLogTags;
    private StringBuilder stringBuilderLogValues;
    private StringBuilder stringBuilderActuatorSetPoint;

    private int dataUpdateCounter;

    private Context context;

    private DevicesDynamicsHandler devicesDynamicsHandler;

    private Timer timer;
    private int stepCounter;
    private Handler viewModifierHandler;

    private int rotorCurrentPulseWidth_microsecs;
    private float rotorCurrentRotorRotationSpeed_rpm;

    private float rotorMinThrottleActiveTests_percent;
    private float rotorMaxThrottleActiveTests_percent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_executor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();
        this.peripheral = intent.getParcelableExtra(Peripheral.PARCELABLE_EXTRA_KEY);

        this.rollingPassiveTest = false;
        this.rollingActiveTest = false;

        //altera o título da toolbar a partir do tipo do dispositivo recebido pelo intent
        toolbar.setTitle(this.peripheral.getName());

        setSupportActionBar(toolbar);

        this.spinner_deviceSelector = (Spinner) findViewById(R.id.spinner_deviceSelect);
        this.spinner_deviceSelector.setOnItemSelectedListener(this);

        this.spinner_activeTestTypeSelector = (Spinner) findViewById(R.id.spinner_activeTestType);
        this.spinner_activeTestTypeSelector.setOnItemSelectedListener(this);

        this.linearLayout_passiveTest = (LinearLayout) findViewById(R.id.passiveTest_envelope);
        this.linearLayout_activeTest = (LinearLayout) findViewById(R.id.activeTest_envelope);

        this.linearLayout_graph1 = (LinearLayout) findViewById(R.id.graph_envelope1);
        this.linearLayout_graph2 = (LinearLayout) findViewById(R.id.graph_envelope2);
        this.linearLayout_graph3 = (LinearLayout) findViewById(R.id.graph_envelope3);

        this.verticalSeekBar_throttle = (VerticalSeekBar) findViewById(R.id.verticalSeekBar_throttle_testsExecutor);

        this.textView_sensorReading = (TextView) findViewById(R.id.textView_sensorReading);
        this.textView_actuatorTarget = (TextView) findViewById(R.id.textView_actuatorTarget);

        this.aSwitch_passiveTest = (Switch) findViewById(R.id.switch_passiveTest);
        this.aSwitch_activeTest = (Switch) findViewById(R.id.switch_activeTest);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateSpinners();

        enableComponents();

        createGraphAccordingToPeripheral();

        this.logHandler = LogHandler.getInstance();

        this.context = this.getApplicationContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.logEnabledDeviceTests = sharedPreferences.getBoolean(getString(R.string.pref_key_enable_device_tests_logs),
                false);
        this.rotorMinThrottleActiveTests_percent = sharedPreferences.getInt(getString(R.string.pref_key_control_min_throttle_perTenThousand), 1000) / 10000f;
        this.rotorMaxThrottleActiveTests_percent = sharedPreferences.getInt(getString(R.string.pref_key_control_max_throttle_perTenThousand), 2500) / 10000f;

        this.devicesDynamicsHandler = DevicesDynamicsHandler.getInstance(this.context);
    }

    private void registerBroadcastReceiverAccordingToPeripheralPassiveTest() {
        int peripheralKind = this.peripheral.getPeripheralKind();

        switch (peripheralKind) {
            case PeripheralKind.ACCELEROMETER:
                this.sensorInterpreter = new AccelerometerInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        AccelerometerInterpreter interpreter = (AccelerometerInterpreter) sensorInterpreter;

                        interpreter.setAccelerationX_metersPerSecSquared(intent.getFloatExtra(
                                Accelerometer.ACCELERATION_X_METERS_PER_SEC_SQUARED_KEY, 0f));
                        interpreter.setAccelerationY_metersPerSecSquared(intent.getFloatExtra(
                                Accelerometer.ACCELERATION_Y_METERS_PER_SEC_SQUARED_KEY, 0f));
                        interpreter.setAccelerationZ_metersPerSecSquared(intent.getFloatExtra(
                                Accelerometer.ACCELERATION_Z_METERS_PER_SEC_SQUARED_KEY, 0f));

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
            case PeripheralKind.GYROSCOPE:
                this.sensorInterpreter = new GyroscopeInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        GyroscopeInterpreter interpreter = (GyroscopeInterpreter) sensorInterpreter;

                        interpreter.setAngularSpeedX_degreesPerSec(intent.getFloatExtra(
                                Gyroscope.ANGULAR_SPEED_X_DEGREES_PER_SEC_KEY, 0f));
                        interpreter.setAngularSpeedY_degreesPerSec(intent.getFloatExtra(
                                Gyroscope.ANGULAR_SPEED_Y_DEGREES_PER_SEC_KEY, 0f));
                        interpreter.setAngularSpeedZ_degreesPerSec(intent.getFloatExtra(
                                Gyroscope.ANGULAR_SPEED_Z_DEGREES_PER_SEC_KEY, 0f));

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
            case PeripheralKind.MAGNETOMETER:
                this.sensorInterpreter = new MagnetometerInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        MagnetometerInterpreter interpreter = (MagnetometerInterpreter) sensorInterpreter;

                        interpreter.setMagneticFieldX_microTeslas(intent.getFloatExtra(
                                Magnetometer.MAGNETIC_FIELD_X_MICROTESLAS_KEY, 0f));
                        interpreter.setMagneticFieldY_microTeslas(intent.getFloatExtra(
                                Magnetometer.MAGNETIC_FIELD_Y_MICROTESLAS_KEY, 0f));
                        interpreter.setMagneticFieldZ_microTeslas(intent.getFloatExtra(
                                Magnetometer.MAGNETIC_FIELD_Z_MICROTESLAS_KEY, 0f));
                        interpreter.setCompassHeading_degrees(intent.getFloatExtra(
                                Magnetometer.COMPASS_HEADING_KEY, 0f));

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
            case PeripheralKind.TACHOMETER:
                this.sensorInterpreter = new TachometerInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        TachometerInterpreter interpreter = (TachometerInterpreter) sensorInterpreter;
                        String[] tachometersList = getResources().getStringArray(
                                R.array.peripheral_tachometer_device_list);

                        if (selectedDevice.equals(tachometersList[0])) {
                            interpreter.setRevolutionsFrequency_rpm(intent.getFloatExtra(Tachometer
                                    .REVOLUTIONS_ROTOR1_RPM_KEY, 0f));
                        } else if (selectedDevice.equals(tachometersList[1])) {
                            interpreter.setRevolutionsFrequency_rpm(intent.getFloatExtra(Tachometer
                                    .REVOLUTIONS_ROTOR2_RPM_KEY, 0f));
                        } else if (selectedDevice.equals(tachometersList[2])) {
                            interpreter.setRevolutionsFrequency_rpm(intent.getFloatExtra(Tachometer
                                    .REVOLUTIONS_ROTOR3_RPM_KEY, 0f));
                        } else {
                            interpreter.setRevolutionsFrequency_rpm(intent.getFloatExtra(Tachometer
                                    .REVOLUTIONS_ROTOR4_RPM_KEY, 0f));
                        }

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
            case PeripheralKind.BAROMETER:
                this.sensorInterpreter = new BarometerInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        BarometerInterpreter interpreter = (BarometerInterpreter) sensorInterpreter;

                        interpreter.setTemperature_celsius(intent.getFloatExtra(
                                Barometer.TEMPERATURE_CELSIUS_KEY, 0f));
                        interpreter.setPressure_hectoPascals(intent.getFloatExtra(
                                Barometer.AIR_PRESSURE_HECTOPASCALS_KEY, 0f));
                        interpreter.setAbsoluteAltitude_meters(intent.getFloatExtra(
                                Barometer.ABSOLUTE_ALTITUDE_METERS_KEY, 0f));

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
            case PeripheralKind.SONAR:
                this.sensorInterpreter = new SonarInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        SonarInterpreter interpreter = (SonarInterpreter) sensorInterpreter;
                        String[] sonarsList = getResources().getStringArray(R.array.peripheral_sonar_device_list);

                        if (selectedDevice.equals(sonarsList[0])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_EAST_CENTIMETERS_KEY, 1000f));
                        } else if (selectedDevice.equals(sonarsList[1])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_NORTHEAST_CENTIMETERS_KEY, 1000f));
                        } else if (selectedDevice.equals(sonarsList[2])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_NORTH_CENTIMETERS_KEY, 1000f));
                        } else if (selectedDevice.equals(sonarsList[3])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_NORTHWEST_CENTIMETERS_KEY, 1000f));
                        } else if (selectedDevice.equals(sonarsList[4])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_WEST_CENTIMETERS_KEY, 1000f));
                        } else if (selectedDevice.equals(sonarsList[5])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_SOUTHWEST_CENTIMETERS_KEY, 1000f));
                        } else if (selectedDevice.equals(sonarsList[6])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_SOUTH_CENTIMETERS_KEY, 1000f));
                        } else if (selectedDevice.equals(sonarsList[7])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_SOUTHEAST_CENTIMETERS_KEY, 1000f));
                        } else if (selectedDevice.equals(sonarsList[8])) {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_UP_CENTIMETERS_KEY, 1000f));
                        } else {
                            interpreter.setDistance_centimeters(intent.getFloatExtra(
                                    Sonar.DISTANCE_DOWN_CENTIMETERS_KEY, 1000f));
                        }

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
            case PeripheralKind.LONG_RANGE_COMMUNICATOR:
                break;
        }

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        if (this.broadcastReceiver != null) {
            this.localBroadcastManager.registerReceiver(this.broadcastReceiver,
                    new IntentFilter(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE));
        }
    }

    private void registerBroadcastReceiverAccordingToPeripheralActiveTest() {
        int peripheralKind = this.peripheral.getPeripheralKind();

        switch (peripheralKind) {
            case PeripheralKind.TACHOMETER:
            case PeripheralKind.ESC:
                this.sensorInterpreter = new TachometerInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        TachometerInterpreter interpreter = (TachometerInterpreter) sensorInterpreter;
                        String[] escsList = getResources().getStringArray(R.array.peripheral_esc_device_list);

                        if (selectedDevice.equals(escsList[0])) {
                            interpreter.setRevolutionsFrequency_rpm(intent.getFloatExtra(
                                    Tachometer.REVOLUTIONS_ROTOR1_RPM_KEY, 0f));
                        } else if (selectedDevice.equals(escsList[1])) {
                            interpreter.setRevolutionsFrequency_rpm(intent.getFloatExtra(
                                    Tachometer.REVOLUTIONS_ROTOR2_RPM_KEY, 0f));
                        } else if (selectedDevice.equals(escsList[2])) {
                            interpreter.setRevolutionsFrequency_rpm(intent.getFloatExtra(
                                    Tachometer.REVOLUTIONS_ROTOR3_RPM_KEY, 0f));
                        } else {
                            interpreter.setRevolutionsFrequency_rpm(intent.getFloatExtra(
                                    Tachometer.REVOLUTIONS_ROTOR4_RPM_KEY, 0f));
                        }

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
            case PeripheralKind.GYROSCOPE:
                this.sensorInterpreter = new GyroscopeInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        GyroscopeInterpreter interpreter = (GyroscopeInterpreter) sensorInterpreter;

                        interpreter.setAngularSpeedX_degreesPerSec(intent.getFloatExtra(
                                Gyroscope.ANGULAR_SPEED_X_DEGREES_PER_SEC_KEY, 0f));
                        interpreter.setAngularSpeedY_degreesPerSec(intent.getFloatExtra(
                                Gyroscope.ANGULAR_SPEED_Y_DEGREES_PER_SEC_KEY, 0f));
                        interpreter.setAngularSpeedZ_degreesPerSec(intent.getFloatExtra(
                                Gyroscope.ANGULAR_SPEED_Z_DEGREES_PER_SEC_KEY, 0f));

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
            case PeripheralKind.MAGNETOMETER:
                this.sensorInterpreter = new MagnetometerInterpreter();

                this.broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        MagnetometerInterpreter interpreter = (MagnetometerInterpreter) sensorInterpreter;

                        interpreter.setMagneticFieldX_microTeslas(intent.getFloatExtra(
                                Magnetometer.MAGNETIC_FIELD_X_MICROTESLAS_KEY, 0f));
                        interpreter.setMagneticFieldY_microTeslas(intent.getFloatExtra(
                                Magnetometer.MAGNETIC_FIELD_Y_MICROTESLAS_KEY, 0f));
                        interpreter.setMagneticFieldZ_microTeslas(intent.getFloatExtra(
                                Magnetometer.MAGNETIC_FIELD_Z_MICROTESLAS_KEY, 0f));
                        interpreter.setCompassHeading_degrees(intent.getFloatExtra(
                                Magnetometer.COMPASS_HEADING_KEY, 0f));

                        updateDataAccordingToPeripheral();
                    }
                };
                break;
        }

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.context);
        if (this.broadcastReceiver != null) {
            this.localBroadcastManager.registerReceiver(this.broadcastReceiver,
                    new IntentFilter(SensoryInterpretationService.INTENT_FILTER_SENSORS_INTERPRETERS_SERVICE));
        }
    }

    private void createGraphAccordingToPeripheral() {
        int peripheralKind = this.peripheral.getPeripheralKind();

        switch (peripheralKind) {
            case PeripheralKind.ACCELEROMETER:
                createThreeGraphsColumnChart();
                this.abstractChartData1.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_acceleration_metersPerSecSquared));
                this.abstractChartData2.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_acceleration_metersPerSecSquared));
                this.abstractChartData3.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_acceleration_metersPerSecSquared));
                break;
            case PeripheralKind.GYROSCOPE:
                createThreeGraphsColumnChart();
                this.abstractChartData1.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_angularSpeed_degreesPerSec));
                this.abstractChartData2.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_angularSpeed_degreesPerSec));
                this.abstractChartData3.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_angularSpeed_degreesPerSec));
                break;
            case PeripheralKind.MAGNETOMETER:
                createThreeGraphsColumnChart();
                this.abstractChartData1.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_magneticField_microTeslas));
                this.abstractChartData2.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_magneticField_microTeslas));
                this.abstractChartData3.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_magneticField_microTeslas));
                break;
            case PeripheralKind.BAROMETER:
                createOneLineGraphLineChart();
                this.abstractChartData1.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_pressure_hectoPascals));
                break;
            case PeripheralKind.SONAR:
                createOneLineGraphLineChart();
                this.abstractChartData1.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_distance_centimeters));
                break;
            case PeripheralKind.TACHOMETER:
            case PeripheralKind.ESC:
                this.broadcastIntent = new Intent(TestsExecutorActivity.INTENT_FILTER_TEST_EXECUTOR_ACTIVITY);
                createOneLineGraphLineChart();
                this.abstractChartData1.getAxisYLeft().setName(getString(
                        R.string.measurementUnit_rotations_rotationsPerMinute));
                break;
            case PeripheralKind.LONG_RANGE_COMMUNICATOR:
                createTwoLineGraphLineChart();
                break;
        }
    }

    /**
     * To animate values you have to change targets values and then call {@link
     * AbstractChartView#startDataAnimation()} * method(don't confuse with View.animate()).
     */
    private void updateDataAccordingToPeripheral() {
        int peripheralKind = this.peripheral.getPeripheralKind();
        boolean updateGraphics;

        if (this.stringBuilderTextView == null) {
            this.stringBuilderTextView = new StringBuilder(getString(R.string.sensorReading));
        } else {
            this.stringBuilderTextView.delete(0, this.stringBuilderTextView.length());
            this.stringBuilderTextView.append(getString(R.string.sensorReading));
        }

        if (this.stringBuilderLogTags == null) {
            this.stringBuilderLogTags = new StringBuilder();
        } else {
            this.stringBuilderLogTags.delete(0, this.stringBuilderLogTags.length());
        }

        if (this.stringBuilderLogValues == null) {
            this.stringBuilderLogValues = new StringBuilder();
        } else {
            this.stringBuilderLogValues.delete(0, this.stringBuilderLogValues.length());
        }

        if (this.dataUpdateCounter >= DATA_UPDATE_COUNTS_TO_UPDATE_GRAPHS) {
            updateGraphics = true;
            this.dataUpdateCounter = 0;
        } else {
            updateGraphics = false;
            this.dataUpdateCounter++;
        }

        switch (peripheralKind) {
            case PeripheralKind.ACCELEROMETER: {
                //Three ColumnGraphs
                AccelerometerInterpreter interpreter = (AccelerometerInterpreter) this.sensorInterpreter;
                float accelerationMetersPerSecSquaredX, accelerationMetersPerSecSquaredY, accelerationMetersPerSecSquaredZ;

                accelerationMetersPerSecSquaredX = interpreter.getAccelerationX_metersPerSecSquared();
                accelerationMetersPerSecSquaredY = interpreter.getAccelerationY_metersPerSecSquared();
                accelerationMetersPerSecSquaredZ = interpreter.getAccelerationZ_metersPerSecSquared();

                if (updateGraphics) {
                    ((ColumnChartData) this.abstractChartData1).getColumns().get(0).getValues().get(0).setTarget(
                            accelerationMetersPerSecSquaredX);
                    ((ColumnChartData) this.abstractChartData2).getColumns().get(0).getValues().get(0).setTarget(
                            accelerationMetersPerSecSquaredY);
                    ((ColumnChartData) this.abstractChartData3).getColumns().get(0).getValues().get(0).setTarget(
                            accelerationMetersPerSecSquaredZ);

                    this.abstractChartView1.startDataAnimation();
                    this.abstractChartView2.startDataAnimation();
                    this.abstractChartView3.startDataAnimation();
                }

                if (this.logEnabledDeviceTests) {
                    this.stringBuilderLogTags.append(getString(R.string.measurementUnit_acceleration_metersPerSecSquared_compact));
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_x)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_y)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_z));

                    this.stringBuilderLogValues.append(accelerationMetersPerSecSquaredX).append(",");
                    this.stringBuilderLogValues.append(accelerationMetersPerSecSquaredY).append(",");
                    this.stringBuilderLogValues.append(accelerationMetersPerSecSquaredZ);
                    this.logHandler.appendValueToLog(R.string.pref_key_enable_device_tests_logs,
                            this.stringBuilderLogTags, this.stringBuilderLogValues);
                }

                this.stringBuilderTextView.append(": ").append(getString(R.string
                        .measurementUnit_acceleration_metersPerSecSquared_compact));
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_x)).append(" ").
                        append(accelerationMetersPerSecSquaredX);
                this.stringBuilderTextView.append(", ");
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_y)).append(" ").
                        append(accelerationMetersPerSecSquaredY);
                this.stringBuilderTextView.append(", ");
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_z)).append(" ").
                        append(accelerationMetersPerSecSquaredZ);
                this.textView_sensorReading.setText(this.stringBuilderTextView);
            }
            break;
            case PeripheralKind.GYROSCOPE: {
                //Three ColumnGraphs
                GyroscopeInterpreter interpreter = (GyroscopeInterpreter) this.sensorInterpreter;
                float angularSpeedDegreesPerSecX, angularSpeedDegreesPerSecY, angularSpeedDegreesPerSecZ;

                angularSpeedDegreesPerSecX = interpreter.getAngularSpeedX_degreesPerSec();
                angularSpeedDegreesPerSecY = interpreter.getAngularSpeedY_degreesPerSec();
                angularSpeedDegreesPerSecZ = interpreter.getAngularSpeedZ_degreesPerSec();

                if (updateGraphics) {
                    ((ColumnChartData) this.abstractChartData1).getColumns().get(0).getValues().get(0).setTarget(
                            angularSpeedDegreesPerSecX);
                    ((ColumnChartData) this.abstractChartData2).getColumns().get(0).getValues().get(0).setTarget(
                            angularSpeedDegreesPerSecY);
                    ((ColumnChartData) this.abstractChartData3).getColumns().get(0).getValues().get(0).setTarget(
                            angularSpeedDegreesPerSecZ);

                    this.abstractChartView1.startDataAnimation();
                    this.abstractChartView2.startDataAnimation();
                    this.abstractChartView3.startDataAnimation();
                }

                if (this.logEnabledDeviceTests) {
                    this.stringBuilderLogTags.append(getString(R.string.measurementUnit_angularSpeed_degreesPerSec_compact));
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_x)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_y)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_z));

                    this.stringBuilderLogValues.append(angularSpeedDegreesPerSecX).append(",");
                    this.stringBuilderLogValues.append(angularSpeedDegreesPerSecY).append(",");
                    this.stringBuilderLogValues.append(angularSpeedDegreesPerSecZ);
                    this.logHandler.appendValueToLog(R.string.pref_key_enable_device_tests_logs,
                            this.stringBuilderLogTags, this.stringBuilderLogValues);
                }

                this.stringBuilderTextView.append(": ").append(getString(R.string
                        .measurementUnit_angularSpeed_degreesPerSec_compact));
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_x)).append(" ").append(angularSpeedDegreesPerSecX);
                this.stringBuilderTextView.append(", ");
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_y)).append(" ").append(angularSpeedDegreesPerSecY);
                this.stringBuilderTextView.append(", ");
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_z)).append(" ").append(angularSpeedDegreesPerSecZ);
                this.textView_sensorReading.setText(this.stringBuilderTextView);

                if (this.aSwitch_activeTest.isChecked()) {
                    //Update gyrsocope calibration
                    this.devicesDynamicsHandler.addGyroscopeUncalibratedReadings(angularSpeedDegreesPerSecX,
                            angularSpeedDegreesPerSecY, angularSpeedDegreesPerSecZ);
                }
            }
            break;
            case PeripheralKind.MAGNETOMETER: {
                //Three ColumnGraphs
                MagnetometerInterpreter interpreter = (MagnetometerInterpreter) this.sensorInterpreter;
                float magneticFieldMicroTeslaX, magneticFieldMicroTeslaY, magneticFieldMicroTeslaZ, compassHeading;
                String compassHeadingDirectionName;

                magneticFieldMicroTeslaX = interpreter.getMagneticFieldX_microTeslas();
                magneticFieldMicroTeslaY = interpreter.getMagneticFieldY_microTeslas();
                magneticFieldMicroTeslaZ = interpreter.getMagneticFieldZ_microTeslas();
                compassHeading = interpreter.getCompassHeading_degrees();
                compassHeadingDirectionName = getString(interpreter.getCompassHeadingCardinalNameResourceId(
                        compassHeading));

                if (updateGraphics) {
                    ((ColumnChartData) this.abstractChartData1).getColumns().get(0).getValues().get(0).setTarget(
                            magneticFieldMicroTeslaX);
                    ((ColumnChartData) this.abstractChartData2).getColumns().get(0).getValues().get(0).setTarget(
                            magneticFieldMicroTeslaY);
                    ((ColumnChartData) this.abstractChartData3).getColumns().get(0).getValues().get(0).setTarget(
                            magneticFieldMicroTeslaZ);

                    this.abstractChartView1.startDataAnimation();
                    this.abstractChartView2.startDataAnimation();
                    this.abstractChartView3.startDataAnimation();
                }

                if (this.logEnabledDeviceTests) {
                    this.stringBuilderLogTags.append(getString(R.string.measurementUnit_magneticField_microTeslas_compact));
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_x)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_y)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.logTag_axis_z)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.logTag_compass_heading));

                    this.stringBuilderLogValues.append(magneticFieldMicroTeslaX).append(",");
                    this.stringBuilderLogValues.append(magneticFieldMicroTeslaY).append(",");
                    this.stringBuilderLogValues.append(magneticFieldMicroTeslaZ).append(",");
                    this.stringBuilderLogValues.append(compassHeading);
                    this.logHandler.appendValueToLog(R.string.pref_key_enable_device_tests_logs,
                            this.stringBuilderLogTags, this.stringBuilderLogValues);
                }

                this.stringBuilderTextView.append(": ").append(getString(R.string
                        .measurementUnit_magneticField_microTeslas_compact));
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_x)).append(" ").append(magneticFieldMicroTeslaX);
                this.stringBuilderTextView.append(",");
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_y)).append(" ").append(magneticFieldMicroTeslaY);
                this.stringBuilderTextView.append(",");
                this.stringBuilderTextView.append(getString(R.string.logTag_axis_z)).append(" ").append(magneticFieldMicroTeslaZ);
                this.stringBuilderTextView.append("\n").append("Direção da bússola: ").append(compassHeadingDirectionName);
                this.stringBuilderTextView.append(" ").append(compassHeading);
                this.textView_sensorReading.setText(this.stringBuilderTextView);

                if (this.aSwitch_activeTest.isChecked()) {
                    //Update magnetometer calibration
                    this.devicesDynamicsHandler.addMagnetometerUncalibratedReadings(magneticFieldMicroTeslaX,
                            magneticFieldMicroTeslaY);
                }
            }
//                for (Column column : ((ColumnChartData) this.abstractChartData1).getColumns()) {
//                    for (SubcolumnValue value : column.getValues()) {
//                        value.setTarget((float) Math.random() * 100);
//                    }
//                }
//                for (Column column : ((ColumnChartData) this.abstractChartData2).getColumns()) {
//                    for (SubcolumnValue value : column.getValues()) {
//                        value.setTarget((float) Math.random() * 100);
//                    }
//                }
//                for (Column column : ((ColumnChartData) this.abstractChartData3).getColumns()) {
//                    for (SubcolumnValue value : column.getValues()) {
//                        value.setTarget((float) Math.random() * 100);
//                    }
//                }
            break;
            case PeripheralKind.ESC: {
                //One LineGraph
                TachometerInterpreter interpreter = (TachometerInterpreter) this.sensorInterpreter;
                String[] escsList = getResources().getStringArray(R.array.peripheral_esc_device_list);
                this.rotorCurrentRotorRotationSpeed_rpm = interpreter.getRevolutionsFrequency_rpm();
                String selectedDeviceLogTag;
                Rotor rotor;
                List<PointValue> pointValueList;

                if (updateGraphics) {
                    for (Line line : ((LineChartData) this.abstractChartData1).getLines()) {
                        pointValueList = line.getValues();
                        updatePoints(pointValueList, this.rotorCurrentRotorRotationSpeed_rpm);
//                    line.setValues(updatePoints(line.getValues(), this.rotorCurrentRotorRotationSpeed_rpm));
                        line.setValues(pointValueList);
                    }

                    this.abstractChartView1.startDataAnimation();
                }

                if (this.selectedDevice.equals(escsList[0])) {
                    selectedDeviceLogTag = getString(R.string.logTag_rotations_rotationsPerMinute_rotor1);
                    rotor = this.devicesDynamicsHandler.getRotors().get(RotorId.ONE);
                } else if (this.selectedDevice.equals(escsList[1])) {
                    selectedDeviceLogTag = getString(R.string.logTag_rotations_rotationsPerMinute_rotor2);
                    rotor = this.devicesDynamicsHandler.getRotors().get(RotorId.TWO);
                } else if (this.selectedDevice.equals(escsList[2])) {
                    selectedDeviceLogTag = getString(R.string.logTag_rotations_rotationsPerMinute_rotor3);
                    rotor = this.devicesDynamicsHandler.getRotors().get(RotorId.THREE);
                } else {
                    selectedDeviceLogTag = getString(R.string.logTag_rotations_rotationsPerMinute_rotor4);
                    rotor = this.devicesDynamicsHandler.getRotors().get(RotorId.FOUR);
                }

                if (this.logEnabledDeviceTests) {
                    this.logHandler.appendValueToLog(R.string.pref_key_enable_device_tests_logs,
                            selectedDeviceLogTag, this.rotorCurrentRotorRotationSpeed_rpm);
                }

                this.stringBuilderTextView.append(": ").
                        append(getString(R.string.measurementUnit_rotations_rotationsPerMinute_compact)).append(" ").
                        append(this.rotorCurrentRotorRotationSpeed_rpm);
                this.textView_sensorReading.setText(this.stringBuilderTextView);

                //Update rotor calibration look-up table
                rotor.updatePulseWidthMicrosecsToRpmHashtable(this.rotorCurrentPulseWidth_microsecs,
                        this.rotorCurrentRotorRotationSpeed_rpm);
            }
            break;
            case PeripheralKind.TACHOMETER: {
                //One LineGraph
                TachometerInterpreter interpreter = (TachometerInterpreter) this.sensorInterpreter;
                String[] tachometersList = getResources().getStringArray(
                        R.array.peripheral_tachometer_device_list);
                float rotationsPerMinute = interpreter.getRevolutionsFrequency_rpm();
                String selectedDeviceLogTag;
                List<PointValue> pointValueList;

                if (updateGraphics) {
                    for (Line line : ((LineChartData) this.abstractChartData1).getLines()) {
                        pointValueList = line.getValues();
                        updatePoints(pointValueList, rotationsPerMinute);
                        line.setValues(pointValueList);
//                    line.setValues(updatePoints(line.getValues(), rotationsPerMinute));
                    }

                    this.abstractChartView1.startDataAnimation();
                }

                if (this.selectedDevice.equals(tachometersList[0])) {
                    selectedDeviceLogTag = getString(R.string.logTag_rotations_rotationsPerMinute_rotor1);
                } else if (this.selectedDevice.equals(tachometersList[1])) {
                    selectedDeviceLogTag = getString(R.string.logTag_rotations_rotationsPerMinute_rotor2);
                } else if (this.selectedDevice.equals(tachometersList[2])) {
                    selectedDeviceLogTag = getString(R.string.logTag_rotations_rotationsPerMinute_rotor3);
                } else {
                    selectedDeviceLogTag = getString(R.string.logTag_rotations_rotationsPerMinute_rotor4);
                }

                if (this.logEnabledDeviceTests) {
                    this.logHandler.appendValueToLog(R.string.pref_key_enable_device_tests_logs, selectedDeviceLogTag,
                            rotationsPerMinute);
                }

                this.stringBuilderTextView.append(": ");
                this.stringBuilderTextView.append(getString(R.string.measurementUnit_rotations_rotationsPerMinute_compact)).append(" ").append(rotationsPerMinute);
                this.textView_sensorReading.setText(this.stringBuilderTextView);
            }
            break;
            case PeripheralKind.BAROMETER: {
                //One LineGraph
                BarometerInterpreter interpreter = (BarometerInterpreter) this.sensorInterpreter;
                float pressureHectoPascals, temperatureCelsius, altitudeMeters;

//                //Need to call that method here to update some variables used to calculate the pressure
//                interpreter.convertReadingsToDeciCelsius();
//                temperatureCelsius = interpreter.convertTemperatureToCelsius();
//                //Need to call that method here to calculate the pressure
//                interpreter.convertReadingsToPascals();
//                pressureHectoPascals = interpreter.convertPressureToHectoPascals();
//                altitudeMeters = interpreter.convertReadingsToAltitudeMeters();

                temperatureCelsius = interpreter.getTemperature_celsius();
                pressureHectoPascals = interpreter.getPressure_hectoPascals();
                altitudeMeters = interpreter.getAbsoluteAltitude_meters();
                List<PointValue> pointValueList;

                if (updateGraphics) {
                    for (Line line : ((LineChartData) this.abstractChartData1).getLines()) {
                        pointValueList = line.getValues();
                        updatePoints(pointValueList, pressureHectoPascals);
                        line.setValues(pointValueList);
//                    line.setValues(updatePoints(line.getValues(), pressureHectoPascals));
                    }

                    this.abstractChartView1.startDataAnimation();
                }

                if (this.logEnabledDeviceTests) {
                    this.stringBuilderLogTags.append(getString(R.string.measurementUnit_pressure_hectoPascals_compact)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.measurementUnit_temperature_celsius_compact)).append(",");
                    this.stringBuilderLogTags.append(getString(R.string.measurementUnit_altitude_meters_compact));

                    this.stringBuilderLogValues.append(pressureHectoPascals).append(",");
                    this.stringBuilderLogValues.append(temperatureCelsius).append(",");
                    this.stringBuilderLogValues.append(altitudeMeters);
                    this.logHandler.appendValueToLog(R.string.pref_key_enable_device_tests_logs,
                            this.stringBuilderLogTags, this.stringBuilderLogValues);
                }

                this.stringBuilderTextView.append(": ");
                this.stringBuilderTextView.append(getString(R.string.measurementUnit_pressure_hectoPascals_compact)).
                        append(" ").append(pressureHectoPascals);
                this.stringBuilderTextView.append("\n");
                this.stringBuilderTextView.append(getString(R.string.measurementUnit_temperature_celsius_compact)).
                        append(" ").append(temperatureCelsius);
                this.stringBuilderTextView.append("\n");
                this.stringBuilderTextView.append(getString(R.string.measurementUnit_altitude_meters_compact)).
                        append(" ").append(altitudeMeters);
                this.textView_sensorReading.setText(this.stringBuilderTextView);
            }
            break;
            case PeripheralKind.SONAR: {
                //One LineGraph
                SonarInterpreter interpreter = (SonarInterpreter) this.sensorInterpreter;
                String[] sonarsList = getResources().getStringArray(R.array.peripheral_sonar_device_list);
                float distanceCentimeters = interpreter.getDistance_centimeters();
                String selectedDeviceLogTag;
                List<PointValue> pointValueList;

                if (updateGraphics) {
                    for (Line line : ((LineChartData) this.abstractChartData1).getLines()) {
                        pointValueList = line.getValues();
                        updatePoints(pointValueList, distanceCentimeters);
                        line.setValues(pointValueList);
//                    line.setValues(updatePoints(line.getValues(), distanceCentimeters));
                    }

                    this.abstractChartView1.startDataAnimation();
                }

                if (this.selectedDevice.equals(sonarsList[0])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_east);
                } else if (this.selectedDevice.equals(sonarsList[1])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_northeast);
                } else if (this.selectedDevice.equals(sonarsList[2])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_north);
                } else if (this.selectedDevice.equals(sonarsList[3])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_northwest);
                } else if (this.selectedDevice.equals(sonarsList[4])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_west);
                } else if (this.selectedDevice.equals(sonarsList[5])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_southwest);
                } else if (this.selectedDevice.equals(sonarsList[6])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_south);
                } else if (this.selectedDevice.equals(sonarsList[7])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_southeast);
                } else if (this.selectedDevice.equals(sonarsList[8])) {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_up);
                } else {
                    selectedDeviceLogTag = getString(R.string.logTag_distance_centimeters_down);
                }

                if (this.logEnabledDeviceTests) {
                    this.logHandler.appendValueToLog(R.string.pref_key_enable_device_tests_logs,
                            selectedDeviceLogTag, distanceCentimeters);
                }

                this.stringBuilderTextView.append(": ");
                this.stringBuilderTextView.append(getString(R.string.measurementUnit_distance_centimeters_compact)).
                        append(" ").append(distanceCentimeters);
                this.textView_sensorReading.setText(this.stringBuilderTextView);
            }
//                for (Line line : ((LineChartData) this.abstractChartData1).getLines()) {
//                    for (PointValue value : line.getValues()) {
//                        value.setTarget(value.getX(), (float) Math.random() * 100);
//                    }
//                }
            break;
            case PeripheralKind.LONG_RANGE_COMMUNICATOR:
                //Two LineGraph
                //TODO: put calls related to logHandler.append
                for (Line line : ((LineChartData) this.abstractChartData1).getLines()) {
                    for (PointValue value : line.getValues()) {
                        value.setTarget(value.getX(), (float) Math.random() * 100);
                    }
                }
                break;
        }

        System.gc();
    }

    private void updatePoints(List<PointValue> pointValueList, float newReading) {
        int iterator;
        for (iterator = 0; iterator < pointValueList.size() - 1; iterator++) {
            pointValueList.get(iterator).set(iterator, pointValueList.get(iterator + 1).getY());
        }
        pointValueList.get(iterator).set(iterator, newReading);
    }

    private void createOneGraphColumnChartWithVerticalSeekBar() {
        List<Column> columnList;
        List<SubcolumnValue> values;
        Column column;
        Axis axisX;
        Axis axisY;

        this.abstractChartView1 = new ColumnChartView(this.context);
        //Need to instantiate abstractChartData here because you can't add null objects to hashtable
        this.abstractChartData1 = new ColumnChartData(new ArrayList<Column>());

        this.abstractChartView1.setInteractive(true);
        this.abstractChartView1.setZoomEnabled(false);

        values = new ArrayList<>();
        //Create some random numbers just to ilustrate
        values.add(new SubcolumnValue((float) Math.random() * (-1000f) + 5, ChartUtils.pickColor()));

        column = new Column(values);
        column.setHasLabels(true);
        columnList = new ArrayList<>();
        columnList.add(column);

        axisX = new Axis();
        axisX.setHasSeparationLine(true);
        axisX.setName(getString(R.string.graphAxisLabel_xAxisMonitored));

        axisY = new Axis();
        axisY.setHasLines(true);
        axisY.setName(getString(R.string.graphAxisLabel_signalAmplitude));

        this.abstractChartData1 = new ColumnChartData(columnList);
        this.abstractChartData1.setAxisXBottom(axisX);
        this.abstractChartData1.setAxisYLeft(axisY);

        ((ColumnChartView) this.abstractChartView1).setColumnChartData((ColumnChartData) this.abstractChartData1);

        this.linearLayout_graph1.addView(this.abstractChartView1);
    }

    private void createThreeGraphsColumnChart() {
        Hashtable<AbstractChartView, AbstractChartData> chartHashtable = new Hashtable<>();
        List<Column> columnList;
        List<SubcolumnValue> values;
        Column column;
        Axis axisX;
        Axis axisY;
        AbstractChartData currentColumnChartData;

        this.abstractChartView1 = new ColumnChartView(this);
        this.abstractChartView2 = new ColumnChartView(this);
        this.abstractChartView3 = new ColumnChartView(this);
        //Need to instantiate abstractChartData here because you can't add null objects to hashtable
        this.abstractChartData1 = new ColumnChartData(new ArrayList<Column>());
        this.abstractChartData2 = new ColumnChartData(new ArrayList<Column>());
        this.abstractChartData3 = new ColumnChartData(new ArrayList<Column>());

        chartHashtable.put(this.abstractChartView1, this.abstractChartData1);
        chartHashtable.put(this.abstractChartView2, this.abstractChartData2);
        chartHashtable.put(this.abstractChartView3, this.abstractChartData3);

        for (AbstractChartView currentChartView : chartHashtable.keySet()) {
            currentChartView.setInteractive(true);
            currentChartView.setZoomEnabled(false);

            values = new ArrayList<>();
            //Create some random numbers just to ilustrate
            values.add(new SubcolumnValue((float) Math.random() * (-1000f) + 5, ChartUtils.pickColor()));

            column = new Column(values);
            column.setHasLabels(true);
            columnList = new ArrayList<>();
            columnList.add(column);

            currentColumnChartData = new ColumnChartData(columnList);

            axisX = new Axis();
            axisX.setHasSeparationLine(true);

            if (currentChartView.equals(this.abstractChartView1)) {
                this.abstractChartData1 = currentColumnChartData;
                axisX.setName(getString(R.string.graphAxisLabel_xAxisMonitored));
            } else if (currentChartView.equals(this.abstractChartView2)) {
                this.abstractChartData2 = currentColumnChartData;
                axisX.setName(getString(R.string.graphAxisLabel_yAxisMonitored));
            } else if (currentChartView.equals(this.abstractChartView3)) {
                this.abstractChartData3 = currentColumnChartData;
                axisX.setName(getString(R.string.graphAxisLabel_zAxisMonitored));
            }

            axisY = new Axis();
            axisY.setHasLines(true);
            axisY.setName(getString(R.string.graphAxisLabel_signalAmplitude));

            currentColumnChartData.setAxisXBottom(axisX);
            currentColumnChartData.setAxisYLeft(axisY);

            ((ColumnChartView) currentChartView).setColumnChartData((ColumnChartData) currentColumnChartData);
        }

        this.linearLayout_graph1.addView(this.abstractChartView1);
        this.linearLayout_graph2.addView(this.abstractChartView2);
        this.linearLayout_graph3.addView(this.abstractChartView3);
    }

    private void createOneLineGraphLineChart() {
        List<Line> lineList = new ArrayList<>();
        List<PointValue> values;
        Line line;
        Axis axisX;
        Axis axisY;

        float randomNumbersArray[] = new float[20]; //[numberOfLines][numberOfPoints]
        for (int iterator = 0; iterator < randomNumbersArray.length; iterator++) {
            randomNumbersArray[iterator] = (float) Math.random() * (-1000f) + 5;
        }

        values = new ArrayList<>();
        for (int iterator = 0; iterator < randomNumbersArray.length; iterator++) {
            values.add(new PointValue(iterator, randomNumbersArray[iterator]));
        }

        line = new Line(values);
        line.setPointRadius(3);
        line.setSquare(true);
        line.setColor(ChartUtils.COLORS[0]);
        line.setCubic(false);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(true);
        line.setShape(ValueShape.CIRCLE);
        lineList.add(line);

        this.abstractChartData1 = new LineChartData(lineList);

        axisX = new Axis();
        axisY = new Axis();
        axisY.setHasLines(true);
        axisX.setName(getString(R.string.graphAxisLabel_measurementId));
        axisY.setName(getString(R.string.graphAxisLabel_signalAmplitude));
        this.abstractChartData1.setAxisXBottom(axisX);
        this.abstractChartData1.setAxisYLeft(axisY);

        ((LineChartData) this.abstractChartData1).setBaseValue(Float.NEGATIVE_INFINITY);
        this.abstractChartView1 = new LineChartView(this);
        this.abstractChartView1.setInteractive(true);
        this.abstractChartView1.setZoomEnabled(false);
        ((LineChartView) this.abstractChartView1).setLineChartData((LineChartData) this.abstractChartData1);

        this.linearLayout_graph1.addView(this.abstractChartView1);
    }

    private void createTwoLineGraphLineChart() {
        List<Line> lineList = new ArrayList<>();
        List<PointValue> values;
        Line line;
        Axis axisX;
        Axis axisY;

        float randomNumbersArray[][] = new float[2][20]; //[numberOfLines][numberOfPoints]
        for (int lineIterator = 0; lineIterator < randomNumbersArray.length; lineIterator++) {
            for (int iterator = 0; iterator < randomNumbersArray[lineIterator].length; iterator++) {
                randomNumbersArray[lineIterator][iterator] = (float) Math.random() * 2f;
            }

            values = new ArrayList<>();
            for (int iterator = 0; iterator < randomNumbersArray[lineIterator].length; iterator++) {
                values.add(new PointValue(iterator, randomNumbersArray[lineIterator][iterator]));
            }

            line = new Line(values);
            line.setPointRadius(3);
            line.setSquare(true);
            line.setColor(ChartUtils.COLORS[lineIterator]);
            line.setCubic(false);
            line.setHasLabels(true);
            line.setHasLabelsOnlyForSelected(true);
            line.setHasLines(true);
            line.setHasPoints(true);

            if (lineIterator % 2 == 0) {
                line.setShape(ValueShape.CIRCLE);
            } else {
                line.setShape(ValueShape.SQUARE);
            }

            lineList.add(line);
        }

        this.abstractChartData1 = new LineChartData(lineList);

        axisX = new Axis();
        axisY = new Axis();
        axisY.setHasLines(true);
        axisX.setName(getString(R.string.graphAxisLabel_measurementId));
        axisY.setName(getString(R.string.graphAxisLabel_signalAmplitude));
        this.abstractChartData1.setAxisXBottom(axisX);
        this.abstractChartData1.setAxisYLeft(axisY);

        ((LineChartData) this.abstractChartData1).setBaseValue(Float.NEGATIVE_INFINITY);
        this.abstractChartView1 = new LineChartView(this);
        this.abstractChartView1.setInteractive(true);
        this.abstractChartView1.setZoomEnabled(false);
        ((LineChartView) this.abstractChartView1).setLineChartData((LineChartData) this.abstractChartData1);

        this.linearLayout_graph1.addView(this.abstractChartView1);
    }

    private void populateSpinners() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, this.peripheral.getDeviceList(),
                android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        this.spinner_deviceSelector.setAdapter(adapter);
        this.selectedDevice = this.spinner_deviceSelector.getSelectedItem().toString();

        if (this.peripheral.getPeripheralKind() == PeripheralKind.ESC) {
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> activeTestTypeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.activeTestType_list, android.R.layout.simple_spinner_dropdown_item);
            // Specify the layout to use when the list of choices appears
            activeTestTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            this.spinner_activeTestTypeSelector.setAdapter(activeTestTypeAdapter);
            this.selectedActiveTestType = this.spinner_activeTestTypeSelector.getSelectedItem().toString();
        }
    }

    private void enableComponents() {
        this.verticalSeekBar_throttle.setEnabled(false);
        this.verticalSeekBar_throttle.setVisibility(View.GONE);
        this.textView_actuatorTarget.setVisibility(View.GONE);
        findViewById(R.id.spaceRight).setVisibility(View.GONE);
        if (this.peripheral.getPeripheralKind() != PeripheralKind.ESC) {
            this.spinner_activeTestTypeSelector.setVisibility(View.GONE);
        }

        if (this.peripheral.hasPassiveTest()) {
            this.linearLayout_passiveTest.setEnabled(true);
            this.linearLayout_passiveTest.setOnLongClickListener(this);
            this.aSwitch_passiveTest.setOnCheckedChangeListener(this);
        } else {
            this.linearLayout_passiveTest.setEnabled(false);
            this.aSwitch_passiveTest.setEnabled(false);
        }

        if (this.peripheral.hasActiveTest()) {
            this.linearLayout_activeTest.setEnabled(true);
            this.linearLayout_activeTest.setOnLongClickListener(this);
            this.aSwitch_activeTest.setOnCheckedChangeListener(this);
            this.spinner_activeTestTypeSelector.setEnabled(this.peripheral.getPeripheralKind() == PeripheralKind.ESC);
            this.textView_actuatorTarget.setVisibility(View.VISIBLE);
        } else {
            this.linearLayout_activeTest.setEnabled(false);
            this.aSwitch_activeTest.setEnabled(false);
            this.spinner_activeTestTypeSelector.setEnabled(false);
        }
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {
        boolean consumed = false;
        int id = v.getId();
        AlertDialog.Builder dialogBuilder;
        AlertDialog alertDialog;

        switch (id) {
            case R.id.passiveTest_envelope:
                dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage(R.string.dialog_message_passiveTest);
                dialogBuilder.setTitle(R.string.passiveTest);
                dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    /**
                     * This method will be invoked when a button in the dialog is clicked.
                     *
                     * @param dialog The dialog that received the click.
                     * @param which  The button that was clicked (e.g.
                     *               {@link DialogInterface#BUTTON1}) or the position
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alertDialog = dialogBuilder.create();
                alertDialog.show();
                consumed = true;
                break;
            case R.id.activeTest_envelope:
                dialogBuilder = new AlertDialog.Builder(this.context);
                dialogBuilder.setMessage(R.string.dialog_message_activeTest);
                dialogBuilder.setTitle(R.string.activeTest);
                dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    /**
                     * This method will be invoked when a button in the dialog is clicked.
                     *
                     * @param dialog The dialog that received the click.
                     * @param which  The button that was clicked (e.g.
                     *               {@link DialogInterface#BUTTON1}) or the position
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alertDialog = dialogBuilder.create();
                alertDialog.show();
                consumed = true;
                break;
        }

        return consumed;
    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(this.aSwitch_passiveTest)) {
            if (isChecked) {
                startPassiveTest();
            } else {
                endPassiveTest();
            }
        } else if (buttonView.equals(this.aSwitch_activeTest)) {
            if (isChecked) {
                startActiveTest();
            } else {
                endActiveTest();
                System.gc();
            }
        }
    }

    private void startPassiveTest() {
        if (this.rollingActiveTest) {
            this.aSwitch_activeTest.setChecked(false);
        }

        this.dataUpdateCounter = 0;
        registerBroadcastReceiverAccordingToPeripheralPassiveTest();
        this.rollingPassiveTest = true;
    }

    private void endPassiveTest() {
        saveLogAccordingToPreferences();
        this.localBroadcastManager.unregisterReceiver(this.broadcastReceiver);
        this.rollingPassiveTest = false;
    }

    private void startActiveTest() {
        String[] activeTestTypeList;

        if (this.rollingPassiveTest) {
            this.aSwitch_passiveTest.setChecked(false);
        }

        this.dataUpdateCounter = 0;
        registerBroadcastReceiverAccordingToPeripheralActiveTest();
        this.rollingActiveTest = true;

        if (this.peripheral.getPeripheralKind() == PeripheralKind.ESC ||
                this.peripheral.getPeripheralKind() == PeripheralKind.TACHOMETER) {
            this.linearLayout_graph2.setVisibility(View.VISIBLE);
            this.verticalSeekBar_throttle.setEnabled(true);
            this.verticalSeekBar_throttle.setVisibility(View.VISIBLE);
            this.verticalSeekBar_throttle.setOnSeekBarChangeListener(this);
            this.verticalSeekBar_throttle.setProgress(0);
            this.verticalSeekBar_throttle.setPadding(80, 50, 80, 50);
            findViewById(R.id.spaceRight).setVisibility(View.VISIBLE);
        }

        switch (this.peripheral.getPeripheralKind()){
            case PeripheralKind.ESC:
                activeTestTypeList = getResources().getStringArray(R.array.activeTestType_list);
                if (this.selectedActiveTestType.equals(activeTestTypeList[0])) {
                    enableRotorThrottleCurveCalibrationTimer();
                } else if (this.selectedActiveTestType.equals(activeTestTypeList[1])) {
                    enableRotorGateFunctionInputTestTimer();
                } else if (this.selectedActiveTestType.equals(activeTestTypeList[2])) {
                    enableRotorTriangleWaveInputTestTimer();
                } else if (this.selectedActiveTestType.equals(activeTestTypeList[3])) {
                    enableRotorVariableWaveInputTestTimer();
                }
                break;
            case PeripheralKind.MAGNETOMETER:
                enableMagnetometerCalibrationTimer();
                break;
            case PeripheralKind.GYROSCOPE:
                enableGyroscopeCalibrationTimer();
                break;
        }
    }

    private void endActiveTest() {
        int pulseWidth;
        String[] activeTestTypeList;

        this.localBroadcastManager.unregisterReceiver(this.broadcastReceiver);

        if (this.peripheral.getPeripheralKind() == PeripheralKind.ESC ||
                this.peripheral.getPeripheralKind() == PeripheralKind.TACHOMETER) {
            pulseWidth = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
            this.broadcastIntent.putExtra(ESC.ESC1_KEY, pulseWidth);
            this.broadcastIntent.putExtra(ESC.ESC2_KEY, pulseWidth);
            this.broadcastIntent.putExtra(ESC.ESC3_KEY, pulseWidth);
            this.broadcastIntent.putExtra(ESC.ESC4_KEY, pulseWidth);

            this.localBroadcastManager.sendBroadcast(this.broadcastIntent);

            this.linearLayout_graph2.setVisibility(View.GONE);
            this.verticalSeekBar_throttle.setEnabled(false);
            this.verticalSeekBar_throttle.setVisibility(View.GONE);
            this.verticalSeekBar_throttle.setOnSeekBarChangeListener(null);
            findViewById(R.id.spaceRight).setVisibility(View.GONE);
        }

        saveLogAccordingToPreferences();

        switch (this.peripheral.getPeripheralKind()){
            case PeripheralKind.ESC:
                activeTestTypeList = getResources().getStringArray(R.array.activeTestType_list);
                if (this.selectedActiveTestType.equals(activeTestTypeList[0])) {
                    disableRotorThrottleCurveCalibrationTimer();
                } else {
                    disableRotorInputTestTimer();
                }
                break;
            case PeripheralKind.GYROSCOPE:
            case PeripheralKind.MAGNETOMETER:
                disableSensorCalibrationTimer();
                break;
        }

        this.rollingActiveTest = false;
    }

    private void toggleComponentsEnablingAutomaticRoutine(boolean enabled) {
        this.spinner_deviceSelector.setEnabled(enabled);
        this.aSwitch_activeTest.setEnabled(enabled);

        switch (this.peripheral.getPeripheralKind()){
            case PeripheralKind.GYROSCOPE:
                this.aSwitch_passiveTest.setEnabled(enabled);
                break;
            case PeripheralKind.MAGNETOMETER:
                this.aSwitch_passiveTest.setEnabled(enabled);
                break;
            case PeripheralKind.ESC:
                this.spinner_activeTestTypeSelector.setEnabled(enabled);
                break;
        }
    }

    private void disableRotorInputTestTimer() {
        this.timer.cancel();
    }

    private void enableRotorVariableWaveInputTestTimer() {
        final int totalSteps = 100;
        this.stepCounter = 0;
        int interval_millisecs = 1000;
        this.viewModifierHandler = new Handler();
        this.timer = new Timer();

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (stepCounter < totalSteps) {
                    // Update the progress bar
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            int throttleSignal;

                            switch (stepCounter) {
                                case 0:
                                case 11:
                                case 14:
                                case 30:
                                case 45:
                                case 48:
                                case 55:
                                case 60:
                                case 70:
                                case 90:
                                    throttleSignal = Math.round(verticalSeekBar_throttle.getMax() * rotorMinThrottleActiveTests_percent);
                                    verticalSeekBar_throttle.setProgress(throttleSignal);
                                    break;
                                case 5:
                                case 12:
                                case 18:
                                case 32:
                                case 47:
                                case 52:
                                case 58:
                                case 69:
                                case 76:
                                    throttleSignal = Math.round(verticalSeekBar_throttle.getMax() * rotorMaxThrottleActiveTests_percent);
                                    verticalSeekBar_throttle.setProgress(throttleSignal);
                                    break;
                            }

                            stepCounter++;
                        }
                    });
                } else {
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            aSwitch_activeTest.setChecked(false);
                            toggleComponentsEnablingAutomaticRoutine(true);
                        }
                    });
                }
            }
        }, interval_millisecs, interval_millisecs);

        toggleComponentsEnablingAutomaticRoutine(false);
    }

    private void enableRotorTriangleWaveInputTestTimer() {
        final int totalSteps = 100;
        this.stepCounter = 0;
        int interval_millisecs = 1000;
        this.viewModifierHandler = new Handler();
        this.timer = new Timer();

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (stepCounter < totalSteps) {
                    // Update the progress bar
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            float triangleWave, factor, shiftedAndAmplifiedTriangleWave;
                            int throttleSignal, time;
                            final int stepShift = 10;

                            if ((stepCounter < stepShift) || (stepCounter > totalSteps - stepShift)) {
                                throttleSignal = Math.round(verticalSeekBar_throttle.getMax() * rotorMaxThrottleActiveTests_percent / 2);
                            } else {
                                //((stepCounter >= stepShift) && (stepCounter <= totalSteps - stepShift))
                                //Triangle wave function, reference: https://en.wikipedia.org/wiki/Triangle_wave
                                time = stepCounter - stepShift;
                                triangleWave = (float) Math.abs(2 * ((time / 20f) - Math.floor((time / 20f) + 0.5f)));
                                factor = rotorMaxThrottleActiveTests_percent / 2f;
                                shiftedAndAmplifiedTriangleWave = (triangleWave * factor) + factor;
                                throttleSignal = (int) (verticalSeekBar_throttle.getMax() * shiftedAndAmplifiedTriangleWave);
                            }

                            verticalSeekBar_throttle.setProgress(throttleSignal);
                            stepCounter++;
                        }
                    });
                } else {
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            aSwitch_activeTest.setChecked(false);
                            toggleComponentsEnablingAutomaticRoutine(true);
                        }
                    });
                }
            }
        }, interval_millisecs, interval_millisecs);

        toggleComponentsEnablingAutomaticRoutine(false);
    }

    private void enableRotorGateFunctionInputTestTimer() {
        final int totalSteps = 45;
        this.stepCounter = 0;
        int interval_millisecs = 1000;
        this.viewModifierHandler = new Handler();
        this.timer = new Timer();

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (stepCounter < totalSteps) {
                    // Update the progress bar
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            final int stepShift = 15;
                            int throttleSignal;

                            if ((stepCounter < stepShift) || (stepCounter > totalSteps - stepShift)) {
                                throttleSignal = Math.round(verticalSeekBar_throttle.getMax() * rotorMinThrottleActiveTests_percent);
                            } else {
                                throttleSignal = Math.round(verticalSeekBar_throttle.getMax() * rotorMaxThrottleActiveTests_percent);
                            }

                            verticalSeekBar_throttle.setProgress(throttleSignal);
                            stepCounter++;
                        }
                    });
                } else {
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            aSwitch_activeTest.setChecked(false);
                            toggleComponentsEnablingAutomaticRoutine(true);
                        }
                    });
                }
            }
        }, interval_millisecs, interval_millisecs);

        toggleComponentsEnablingAutomaticRoutine(false);
    }

    private void disableSensorCalibrationTimer() {
        this.timer.cancel();

        switch (this.peripheral.getPeripheralKind()){
            case PeripheralKind.GYROSCOPE:
                if (this.stepCounter == GyroscopeInterpreter.CALIBRATION_STEPS) {
                    this.devicesDynamicsHandler.saveCalibrationFile(GyroscopeInterpreter.GYROSCOPE_CALIBRATION_FILENAME);
                }
                break;
            case PeripheralKind.MAGNETOMETER:
                if (this.stepCounter == MagnetometerInterpreter.CALIBRATION_STEPS) {
                    this.devicesDynamicsHandler.saveCalibrationFile(MagnetometerInterpreter.MAGNETOMETER_CALIBRATION_FILENAME);
                }
                break;
        }
    }

    private void enableGyroscopeCalibrationTimer() {
        this.stepCounter = 0;
        int interval_millisecs = 1000;
        this.viewModifierHandler = new Handler();
        this.timer = new Timer();
        this.devicesDynamicsHandler.clearGyroscopeUncalibratedReadings();

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (stepCounter < GyroscopeInterpreter.CALIBRATION_STEPS) {
                    stepCounter++;
                } else {
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            aSwitch_activeTest.setChecked(false);
                            toggleComponentsEnablingAutomaticRoutine(true);
                        }
                    });
                }
            }
        }, interval_millisecs, interval_millisecs);

        toggleComponentsEnablingAutomaticRoutine(false);
    }

    private void enableMagnetometerCalibrationTimer() {
        this.stepCounter = 0;
        int interval_millisecs = 1000;
        this.viewModifierHandler = new Handler();
        this.timer = new Timer();
        this.devicesDynamicsHandler.clearMagnetometerUncalibratedReadings();

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (stepCounter < MagnetometerInterpreter.CALIBRATION_STEPS) {
                    stepCounter++;
                } else {
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            aSwitch_activeTest.setChecked(false);
                            toggleComponentsEnablingAutomaticRoutine(true);
                        }
                    });
                }
            }
        }, interval_millisecs, interval_millisecs);

        toggleComponentsEnablingAutomaticRoutine(false);
    }

    private void disableRotorThrottleCurveCalibrationTimer() {
        int totalSteps = (ESC.MAXIMUM_PULSE_WIDTH_MICROSECS - ESC.MINIMUM_PULSE_WIDTH_MICROSECS)
                / Rotor.ROTOR_CALIBRATION_INCREMENT;
        this.timer.cancel();

        if (this.stepCounter == totalSteps) {
            this.devicesDynamicsHandler.saveCalibrationFile(Rotor.ROTORS_CALIBRATION_FILENAME);
        }
    }

    private void enableRotorThrottleCurveCalibrationTimer() {
        final int totalSteps = (ESC.MAXIMUM_PULSE_WIDTH_MICROSECS - ESC.MINIMUM_PULSE_WIDTH_MICROSECS) /
                Rotor.ROTOR_CALIBRATION_INCREMENT;
        this.stepCounter = 0;
        this.rotorCurrentPulseWidth_microsecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
        int interval_millisecs = 750;
        this.viewModifierHandler = new Handler();
        this.timer = new Timer();

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (stepCounter < totalSteps) {
                    // Update the progress bar
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            int newProgressValue = 0;
                            float newPercent;

                            if (stepCounter >= 0) {
                                rotorCurrentPulseWidth_microsecs += Rotor.ROTOR_CALIBRATION_INCREMENT;
                                newPercent = (rotorCurrentPulseWidth_microsecs - ESC.MINIMUM_PULSE_WIDTH_MICROSECS) /
                                        ((float) (ESC.MAXIMUM_PULSE_WIDTH_MICROSECS - ESC.MINIMUM_PULSE_WIDTH_MICROSECS));
                                newProgressValue = Math.round(verticalSeekBar_throttle.getMax() * newPercent);
                            }

                            verticalSeekBar_throttle.setProgress(newProgressValue);
                            stepCounter++;
                        }
                    });
                } else {
                    viewModifierHandler.post(new Runnable() {
                        public void run() {
                            aSwitch_activeTest.setChecked(false);
                            toggleComponentsEnablingAutomaticRoutine(true);
                        }
                    });
                }
            }
        }, interval_millisecs, interval_millisecs);

        toggleComponentsEnablingAutomaticRoutine(false);
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int sourceId = parent.getId();

        switch (sourceId) {
            case R.id.spinner_deviceSelect:
                this.selectedDevice = this.spinner_deviceSelector.getSelectedItem().toString();
                this.aSwitch_passiveTest.setChecked(false);
                this.aSwitch_activeTest.setChecked(false);
                break;
            case R.id.spinner_activeTestType:
                this.selectedActiveTestType = this.spinner_activeTestTypeSelector.getSelectedItem().toString();
                this.aSwitch_passiveTest.setChecked(false);
                this.aSwitch_activeTest.setChecked(false);
                break;
        }
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        if (this.rollingPassiveTest) {
            endPassiveTest();
        } else if (this.rollingActiveTest) {
            endActiveTest();
        }

        System.gc();
        TestsExecutorActivity.this.finish();
//        super.onBackPressed();
    }

    private void saveLogAccordingToPreferences() {
        int permissionCheck;

        if (this.logEnabledDeviceTests) {
            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            } else {
                this.logHandler.saveLogFile(R.string.pref_key_enable_device_tests_logs);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    saveLogAccordingToPreferences();
                } else {
                    // Permission Denied
                    Toast toast = Toast.makeText(this, getString(R.string.externalStorageWritePermissionDeniedMessage),
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Notification that the progress level has changed. Clients can use the fromUser parameter
     * to distinguish user-initiated changes from those that occurred programmatically.
     *
     * @param seekBar  The SeekBar whose progress has changed
     * @param progress The current progress level. This will be in the range 0..max where max
     *                 was set by {@link ProgressBar#setMax(int)}. (The default value for max is 100.)
     * @param fromUser True if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int peripheralKind = this.peripheral.getPeripheralKind();
//        StringBuilder stringBuilderActuatorSetPoint = new StringBuilder(getString(R.string.actuatorTarget));
        if (this.stringBuilderActuatorSetPoint == null) {
            this.stringBuilderActuatorSetPoint = new StringBuilder(getString(R.string.actuatorTarget));
        } else {
            this.stringBuilderActuatorSetPoint.delete(0, this.stringBuilderActuatorSetPoint.length());
            this.stringBuilderActuatorSetPoint.append(getString(R.string.actuatorTarget));
        }

        switch (peripheralKind) {
            case PeripheralKind.TACHOMETER:
            case PeripheralKind.ESC:
                String[] escsList = getResources().getStringArray(R.array.peripheral_esc_device_list);
                float percent = NumberManipulation.unitBaseNormalize(progress, this.verticalSeekBar_throttle.getMax(), 0f);
                int currentPulseWidth_microsecs = ESC.percentToPulseWidth_microsecs(percent);

                this.stringBuilderActuatorSetPoint.append(": ").append(Float.toString(percent * 100)).append("%");
                this.textView_actuatorTarget.setText(this.stringBuilderActuatorSetPoint);

                if (this.selectedDevice.equals(escsList[0])) {
                    this.broadcastIntent.putExtra(ESC.ESC1_KEY, currentPulseWidth_microsecs);
                    this.broadcastIntent.putExtra(ESC.ESC2_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC3_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC4_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                } else if (this.selectedDevice.equals(escsList[1])) {
                    this.broadcastIntent.putExtra(ESC.ESC1_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC2_KEY, currentPulseWidth_microsecs);
                    this.broadcastIntent.putExtra(ESC.ESC3_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC4_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                } else if (this.selectedDevice.equals(escsList[2])) {
                    this.broadcastIntent.putExtra(ESC.ESC1_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC2_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC3_KEY, currentPulseWidth_microsecs);
                    this.broadcastIntent.putExtra(ESC.ESC4_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                } else {
                    this.broadcastIntent.putExtra(ESC.ESC1_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC2_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC3_KEY, ESC.MINIMUM_PULSE_WIDTH_MICROSECS);
                    this.broadcastIntent.putExtra(ESC.ESC4_KEY, currentPulseWidth_microsecs);
                }
                break;
        }

        this.localBroadcastManager.sendBroadcast(this.broadcastIntent);
    }

    /**
     * Notification that the user has started a touch gesture. Clients may want to use this
     * to disable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * Notification that the user has finished a touch gesture. Clients may want to use this
     * to re-enable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
