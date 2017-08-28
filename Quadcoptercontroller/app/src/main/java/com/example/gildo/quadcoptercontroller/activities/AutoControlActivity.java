package com.example.gildo.quadcoptercontroller.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.services.control.AltitudeControlService;
import com.example.gildo.quadcoptercontroller.services.control.AttitudeControlService;
import com.example.gildo.quadcoptercontroller.services.control.HorizontalRotationControlService;
import com.example.gildo.quadcoptercontroller.services.control.RotorSpeedControlService;

public class AutoControlActivity extends AppCompatActivity implements View.OnClickListener {

    Button button_emergency_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startControlServices();

        adjustComponentsSize();

        this.button_emergency_stop = (Button) findViewById(R.id.button_emergency_stop_auto);
        this.button_emergency_stop.setOnClickListener(this);

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

    private void adjustComponentsSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int emergencyEnvelopeWidth = (int) (metrics.widthPixels * 0.7);
        //Adjusts the size of joystick_envelope
        findViewById(R.id.emergencyStop_envelope_auto).setLayoutParams(new LinearLayout.LayoutParams
                (emergencyEnvelopeWidth, emergencyEnvelopeWidth));
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
            case R.id.button_emergency_stop_auto:
                //TODO: add call to emergencyStop
                Toast toast = Toast.makeText(AutoControlActivity.this,
                        R.string.dialog_positive_message_emergency_stop, Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
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
                Toast toast = Toast.makeText(AutoControlActivity.this,
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
                //Code to "disconnect"
                stopControlServices();

                Toast toast = Toast.makeText(AutoControlActivity.this, R.string.dialog_positive_message_disconnect,
                        Toast.LENGTH_SHORT);
                toast.show();
                System.gc();
                AutoControlActivity.this.finish();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

//        super.onBackPressed();
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
