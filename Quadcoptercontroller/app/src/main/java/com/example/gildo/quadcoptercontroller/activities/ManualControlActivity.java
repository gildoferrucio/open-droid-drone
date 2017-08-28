package com.example.gildo.quadcoptercontroller.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.entities.EquilibriumAxis;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.DevicesDynamicsHandler;
import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;
import com.example.gildo.quadcoptercontroller.services.communication.UDPConnectionService;
import com.example.gildo.quadcoptercontroller.services.control.AltitudeControlService;
import com.example.gildo.quadcoptercontroller.services.control.AttitudeControlService;
import com.example.gildo.quadcoptercontroller.services.control.HorizontalRotationControlService;
import com.example.gildo.quadcoptercontroller.services.control.RotorSpeedControlService;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

public class ManualControlActivity extends AppCompatActivity implements JoystickListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final String INTENT_FILTER_MANUAL_CONTROL_ACTIVITY = "manualControlActivity";

    public static final String START_STOP_ROTORS_KEY = "startStopRotors";
    public static final String EXECUTE_TAKE_OFF_LANDING_MANOUVER_KEY = "takeOffLandingManouver";
    public static final String EXECUTE_GET_CLOSER_MANOUVER_KEY = "comeCloserManouver";
    public static final String EXECUTE_EMERGENCY_STOP_MANOUVER_KEY = "emergencyStopManouver";

    private boolean connected;
    private boolean landed;
    private boolean rotorsStarted;
    private boolean actAsClient;
    private boolean executeTakeOffLandingManouver;
    private boolean executeGetCloserManouver;
    private boolean executeEmergencyStopManouver;

    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver broadcastReceiver;

    private DevicesDynamicsHandler devicesDynamicsHandler;

    private Joystick joystick_inclination;

    private Button button_connection;
    private Button button_get_closer;
    private Button button_lock_joystick_inclination;
    private Button button_start_stop_rotors;
    private Button button_emergency_stop;

    private TextView textView_inputsFromController;

    private VerticalSeekBar verticalSeekBar_throttle;

    private SeekBar seekBar_steering;

    private StringBuilder stringBuilderTextView;

    private float throttle_percent;
    private float desiredRotationX;
    private float desiredRotationY;
    private float desiredRotationZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context context = this.getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.actAsClient = sharedPreferences.getBoolean(context.getString(R.string.pref_key_act_as_client), false);

        this.devicesDynamicsHandler = DevicesDynamicsHandler.getInstance(this.getApplicationContext());

        if (!actAsClient) {
            startControlServices();
        }

        adjustComponentsSize();

        this.connected = false;
        this.landed = true;
        this.rotorsStarted = false;
        this.executeGetCloserManouver = false;
        this.executeEmergencyStopManouver = false;

        this.localBroadcastManager = LocalBroadcastManager.getInstance(this.getApplicationContext());

        this.textView_inputsFromController = (TextView) findViewById(R.id.textView_inputs_from_controller);

        this.stringBuilderTextView = new StringBuilder();

        //Testing purposes
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (action.equals(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE)) {
                    // get inputs from remote controller
                    throttle_percent = intent.getFloatExtra(DevicesDynamicsHandler.DESIRED_THROTTLE_PERCENT_KEY, 0);
                    desiredRotationX = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_X_DEGREES_KEY, 0);
                    desiredRotationY = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_Y_DEGREES_KEY, 0);
                    desiredRotationZ = intent.getFloatExtra(EquilibriumAxis.DESIRED_ROTATION_Z_DEGREES_KEY, 0);

                    stringBuilderTextView.delete(0, stringBuilderTextView.length());
                    stringBuilderTextView.append("T: ").append(throttle_percent).append(" ");
                    stringBuilderTextView.append("X: ").append(desiredRotationX).append("\n");
                    stringBuilderTextView.append("Y: ").append(desiredRotationY).append(" ");
                    stringBuilderTextView.append("Z: ").append(desiredRotationZ);

                    textView_inputsFromController.setText(stringBuilderTextView);

//                    hasInputToProcess = true;
                }
            }
        };

        if (this.broadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            //register from UDP Server
            intentFilter.addAction(UDPConnectionService.INTENT_FILTER_UDP_CONNECTION_SERVICE);
            this.localBroadcastManager.registerReceiver(this.broadcastReceiver, intentFilter);
//                this.localBroadcastManager.registerReceiver(this.broadcastReceiver,
//                        new IntentFilter(TestsExecutorActivity.INTENT_FILTER_TEST_EXECUTOR_ACTIVITY));
        }

        this.joystick_inclination = (Joystick) findViewById(R.id.joystick_inclination);
        this.joystick_inclination.setJoystickListener(this);
        this.joystick_inclination.setMotionConstraint(Joystick.MotionConstraint.NONE);

        this.button_connection = (Button) findViewById(R.id.button_connection);
        this.button_connection.setOnClickListener(this);

        this.button_get_closer = (Button) findViewById(R.id.button_get_closer);
        this.button_get_closer.setOnClickListener(this);

        this.button_lock_joystick_inclination = (Button) findViewById(R.id.button_lock_joystick_inclination);
        this.button_lock_joystick_inclination.setOnClickListener(this);

        this.button_start_stop_rotors = (Button) findViewById(R.id.button_start_stop_rotors);
        this.button_start_stop_rotors.setOnClickListener(this);

        this.button_emergency_stop = (Button) findViewById(R.id.button_emergency_stop_manual);
        this.button_emergency_stop.setOnClickListener(this);

        this.verticalSeekBar_throttle = (VerticalSeekBar) findViewById(R.id.verticalSeekBar_throttle_manualControl);
        this.verticalSeekBar_throttle.setOnSeekBarChangeListener(this);

        this.seekBar_steering = (SeekBar) findViewById(R.id.seekBar_steering);
        this.seekBar_steering.setOnSeekBarChangeListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateJoystickInclinationLock(Joystick.MotionConstraint motionConstraint) {
        switch (motionConstraint) {
            case NONE:
                this.button_lock_joystick_inclination.setText(getResources().getString(
                        R.string.button_lock_joystick_inclination_none));
                break;
            case HORIZONTAL:
                this.button_lock_joystick_inclination.setText(getResources().getString(
                        R.string.button_lock_joystick_inclination_horizontal));
                break;
            case VERTICAL:
                this.button_lock_joystick_inclination.setText(getResources().getString(
                        R.string.button_lock_joystick_inclination_vertical));
                break;
        }
        this.joystick_inclination.setMotionConstraint(motionConstraint);
    }

    private void adjustComponentsSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

//        int joystickEnvelopeHeight = (int) (metrics.heightPixels * 0.7f);
        int emergencyEnvelopeHeight = (int) (metrics.heightPixels * 0.3f);
        //Adjusts the size of joystick_envelope
//        findViewById(R.id.joystick_inclination_envelope).setLayoutParams(new LinearLayout.LayoutParams(joystickEnvelopeHeight,
//                joystickEnvelopeHeight));
        findViewById(R.id.emergencyStop_envelope_manual).setLayoutParams(new LinearLayout.LayoutParams(
                emergencyEnvelopeHeight, emergencyEnvelopeHeight));
    }

    @Override
    public void onDown() {
        //TODO: implement if necessary the actions when the joystick_inclination is started to be touched
    }

    /**
     * @param degrees -180 -> 180.
     * @param offset  normalized, 0 -> 1.
     */
    @Override
    public void onDrag(float degrees, float offset) {
//        offset *= 10;

        sendInclinationRectangularCoordinates(degrees, offset);
    }

    @Override
    public void onUp() {
        sendInclinationRectangularCoordinates(0, 0);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.button_lock_joystick_inclination:
                Joystick.MotionConstraint currentMotionConstraint = this.joystick_inclination.getMotionConstraint();

                switch (currentMotionConstraint) {
                    case NONE:
                        currentMotionConstraint = Joystick.MotionConstraint.HORIZONTAL;
                        break;
                    case HORIZONTAL:
                        currentMotionConstraint = Joystick.MotionConstraint.VERTICAL;
                        break;
                    case VERTICAL:
                        currentMotionConstraint = Joystick.MotionConstraint.NONE;
                        break;
                }
                updateJoystickInclinationLock(currentMotionConstraint);
                break;
            case R.id.button_connection:
                boolean hasConnectivity = checkConnectivity();

                if (this.connected) {
                    // disconnect, stopping UDPConnectionService
                    Intent stopServiceIntent = new Intent(this.getApplicationContext(), UDPConnectionService.class);
                    stopService(stopServiceIntent);
                    this.button_connection.setText(R.string.button_connection_connect_vant);
                } else {
                    if (hasConnectivity) {
                        // connect, starting UDPConnectionService
                        startService(new Intent(this.getApplicationContext(), UDPConnectionService.class));

                        this.button_connection.setText(R.string.button_connection_disconnect_vant);
                    }
                }
                this.connected = !this.connected;
                break;
            case R.id.button_get_closer:
                Toast toast = Toast.makeText(ManualControlActivity.this, R.string.dialog_message_get_closer,
                        Toast.LENGTH_SHORT);
                toast.show();

                getCloser();
                break;
            case R.id.button_start_stop_rotors:
                if (this.connected && actAsClient) {
                    this.rotorsStarted = !this.rotorsStarted;

                    if (this.rotorsStarted) {
                        this.button_start_stop_rotors.setText(R.string.button_stop_rotors);
                    } else {
                        this.button_start_stop_rotors.setText(R.string.button_start_rotors);
                    }

                    sendStartStopRotors();
                }
                break;
            case R.id.button_emergency_stop_manual:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage(R.string.dialog_message_emergency_stop);
                dialogBuilder.setTitle(R.string.dialog_title_emergency_stop);
                dialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface
                        .OnClickListener() {
                    /**
                     * This method will be invoked when a button in the dialog is clicked.
                     *
                     * @param dialog The dialog that received the click.
                     * @param which  The button that was clicked (e.g.
                     *               {@link DialogInterface#BUTTON1}) or the position
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast toast = Toast.makeText(ManualControlActivity.this,
                                R.string.dialog_negative_message_emergency_stop, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
                dialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface
                        .OnClickListener() {

                    /**
                     * This method will be invoked when a button in the dialog is clicked.
                     *
                     * @param dialog The dialog that received the click.
                     * @param which  The button that was clicked (e.g.
                     *               {@link DialogInterface#BUTTON1}) or the position
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        button_start_stop_rotors.setText(R.string.button_start_rotors);
                        rotorsStarted = false;
                        landed = true;

                        emergencyStop();

                        Toast toast = Toast.makeText(ManualControlActivity.this,
                                R.string.dialog_positive_message_emergency_stop, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                break;
        }
    }

    private void getCloser() {
        this.executeGetCloserManouver = true;

        //TODO: get remoteController location

        sendLocalBroadcastToConnectionService();
    }

    private void emergencyStop() {
        SharedPreferences sharedPreferences;
        boolean actAsClient;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        actAsClient = sharedPreferences.getBoolean(getApplicationContext().getString(R.string.pref_key_act_as_client),
                false);

        if (actAsClient) {
            for (int repetition = 0; repetition < 5; repetition++) {
                this.executeEmergencyStopManouver = true;
                sendLocalBroadcastToConnectionService();
            }
        }

        this.button_connection.performClick();
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
        if (seekBar.equals(this.verticalSeekBar_throttle)) {
//            if (this.throttleChangeCounter < 25) {
//                this.throttleChangeCounter++;
//            } else {
//                this.throttleChangeCounter = 0;
//                sendThrottle(progress);
//            }
            sendThrottle(progress);
        } else if (seekBar.equals(this.seekBar_steering)) {
            sendSteering(progress);
        }
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
        if (seekBar.equals(this.verticalSeekBar_throttle)) {
            sendThrottle(seekBar.getProgress());
        } else if (seekBar.equals(this.seekBar_steering)) {
            this.seekBar_steering.setProgress(this.seekBar_steering.getMax() / 2);

            sendSteering(seekBar.getProgress());
        }
    }

    @Override
    public void onBackPressed() {
        if (this.connected) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(R.string.dialog_message_disconnect);
            dialogBuilder.setTitle(R.string.dialog_title_disconnect);
            dialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                /**
                 * This method will be invoked when a button in the dialog is clicked.
                 *
                 * @param dialog The dialog that received the click.
                 * @param which  The button that was clicked (e.g.
                 *               {@link DialogInterface#BUTTON1}) or the position
                 */
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast toast = Toast.makeText(ManualControlActivity.this,
                            R.string.dialog_negative_message_disconnect, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
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
                    if (!landed) {
                        //TODO: add call to "getCloser", then to "land"
                    }
                    button_connection.performClick();

                    Toast toast = Toast.makeText(ManualControlActivity.this,
                            R.string.dialog_positive_message_disconnect, Toast.LENGTH_SHORT);
                    toast.show();
                    if (!actAsClient) {
                        stopControlServices();
                    }
                    System.gc();
                    ManualControlActivity.this.finish();
                }
            });

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

//        super.onBackPressed();
        } else {
            if (!actAsClient) {
                stopControlServices();
            }
            System.gc();
            ManualControlActivity.this.finish();
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

    private boolean checkConnectivity() {
        boolean hasConnection = true;

        //TODO: implement

        return hasConnection;
    }

    private void sendThrottle(int throttleValue) {
//        Toast toast = Toast.makeText(ManualControlActivity.this, "O acelerador está em " + throttleValue,
//                Toast.LENGTH_SHORT);
//        toast.show();

        this.devicesDynamicsHandler.updateDesiredThrottle(throttleValue, this.verticalSeekBar_throttle.getMax(), 0);

        if (this.connected) {
            sendLocalBroadcastToConnectionService();
        }
    }

    private void sendSteering(int steeringValue) {
//        Toast toast = Toast.makeText(ManualControlActivity.this, "A direção está em " + steeringValue,
//                Toast.LENGTH_SHORT);
//        toast.show();

        this.devicesDynamicsHandler.updateDesiredYaw(steeringValue, this.seekBar_steering.getMax(), 0);

        if (this.connected) {
            sendLocalBroadcastToConnectionService();
        }
    }

    private void sendInclinationRectangularCoordinates(float degrees, float offset) {
        float xInclinationWeight;
        float yInclinationWeight;

        xInclinationWeight = NumberManipulation.roundFloatToNDecimalsPoints(offset *
                (float) Math.cos(Math.toRadians(degrees)), 5);
        yInclinationWeight = NumberManipulation.roundFloatToNDecimalsPoints(offset *
                (float) Math.sin(Math.toRadians(degrees)), 5);

        this.devicesDynamicsHandler.updateDesiredAttitude(xInclinationWeight, yInclinationWeight);

        if (this.connected) {
            sendLocalBroadcastToConnectionService();
        }
    }

    private void sendStartStopRotors() {
        if (this.connected) {
            sendLocalBroadcastToConnectionService();
        }
    }

    private void sendLocalBroadcastToConnectionService() {
        Intent broadcastIntent = new Intent(ManualControlActivity.INTENT_FILTER_MANUAL_CONTROL_ACTIVITY);

        broadcastIntent.putExtra(EquilibriumAxis.DESIRED_ROTATION_X_DEGREES_KEY,
                this.devicesDynamicsHandler.getEquilibriumAxisX().getDesiredAxisRotation_degrees());
        broadcastIntent.putExtra(EquilibriumAxis.DESIRED_ROTATION_Y_DEGREES_KEY,
                this.devicesDynamicsHandler.getEquilibriumAxisY().getDesiredAxisRotation_degrees());
        broadcastIntent.putExtra(EquilibriumAxis.DESIRED_ROTATION_Z_DEGREES_KEY,
                this.devicesDynamicsHandler.getEquilibriumAxisZ().getDesiredAxisRotation_degrees());

        broadcastIntent.putExtra(DevicesDynamicsHandler.DESIRED_THROTTLE_PERCENT_KEY,
                this.devicesDynamicsHandler.getDesiredThrottle_percentage());

        broadcastIntent.putExtra(ManualControlActivity.START_STOP_ROTORS_KEY,
                this.rotorsStarted);

        //TODO: it isn't implemented where to receive this info and execute manouver
        broadcastIntent.putExtra(ManualControlActivity.EXECUTE_TAKE_OFF_LANDING_MANOUVER_KEY,
                executeTakeOffLandingManouver);

        //TODO: it isn't implemented where to receive this info and execute manouver
        broadcastIntent.putExtra(ManualControlActivity.EXECUTE_GET_CLOSER_MANOUVER_KEY,
                this.executeGetCloserManouver);

        //TODO: send location of getCloserManouver!!!

        broadcastIntent.putExtra(ManualControlActivity.EXECUTE_EMERGENCY_STOP_MANOUVER_KEY,
                this.executeEmergencyStopManouver);

        this.localBroadcastManager.sendBroadcast(broadcastIntent);

        if (this.executeTakeOffLandingManouver) {
            this.executeTakeOffLandingManouver = false;
        }

        if (this.executeGetCloserManouver) {
            this.executeGetCloserManouver = false;
        }

        if (this.executeEmergencyStopManouver) {
            this.executeEmergencyStopManouver = false;
        }
    }

    private void startControlServices() {
        //ControlServices
        startService(new Intent(this.getApplicationContext(), AltitudeControlService.class));
        startService(new Intent(this.getApplicationContext(), AttitudeControlService.class));
        startService(new Intent(this.getApplicationContext(), HorizontalRotationControlService.class));
        startService(new Intent(this.getApplicationContext(), RotorSpeedControlService.class));
    }

    private void stopControlServices() {
        Intent stopAlcIntent, stopAtcIntent, stopHrcIntent, stopRscIntent;

        stopAlcIntent = new Intent(this.getApplicationContext(), AltitudeControlService.class);
        stopAtcIntent = new Intent(this.getApplicationContext(), AttitudeControlService.class);
        stopHrcIntent = new Intent(this.getApplicationContext(), HorizontalRotationControlService.class);
        stopRscIntent = new Intent(this.getApplicationContext(), RotorSpeedControlService.class);

        //ControlServices
        stopService(stopAlcIntent);
        stopService(stopAtcIntent);
        stopService(stopHrcIntent);
        stopService(stopRscIntent);
    }
}
