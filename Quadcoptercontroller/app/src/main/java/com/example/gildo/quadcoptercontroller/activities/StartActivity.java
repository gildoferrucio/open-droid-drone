package com.example.gildo.quadcoptercontroller.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;
import com.example.gildo.quadcoptercontroller.services.SensoryInterpretationService;
import com.example.gildo.quadcoptercontroller.services.communication.IOIOControllerService;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 15;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private LogHandler logHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Carrega os valores padrão das variáveis
        PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.pref_flight, false);
        PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.pref_log, false);

//        FloatingActionButton são os botões do canto inferior direito que servem de atalho para
// alguma ação
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.toggle = new ActionBarDrawerToggle(this, this.drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        this.drawer.setDrawerListener(this.toggle);
        this.toggle.syncState();

        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.navigationView.setNavigationItemSelectedListener(this);

        checkPermissions();

        startSensorReadingServices();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        this.logHandler = LogHandler.getInstance();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Toast toast;

        if (key.equals(this.getString(R.string.pref_key_esc1_enable)) ||
                key.equals(this.getString(R.string.pref_key_esc2_enable)) ||
                key.equals(this.getString(R.string.pref_key_esc3_enable)) ||
                key.equals(this.getString(R.string.pref_key_esc4_enable)) ||
                key.equals(this.getString(R.string.pref_key_tachometer1_enable)) ||
                key.equals(this.getString(R.string.pref_key_tachometer2_enable)) ||
                key.equals(this.getString(R.string.pref_key_tachometer3_enable)) ||
                key.equals(this.getString(R.string.pref_key_tachometer4_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarE_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarNE_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarN_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarNW_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarW_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarSW_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarS_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarSE_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarUp_enable)) ||
                key.equals(this.getString(R.string.pref_key_sonarDown_enable)) ||
                key.equals(this.getString(R.string.pref_key_barometer_enable)) ||
                key.equals(this.getString(R.string.pref_key_accelerometer_enable)) ||
                key.equals(this.getString(R.string.pref_key_gyroscope_enable)) ||
                key.equals(this.getString(R.string.pref_key_gyroscope_remove_offset_enable)) ||
                key.equals(this.getString(R.string.pref_key_magnetometer_enable)) ||
                key.equals(this.getString(R.string.pref_key_alpha_attitude_sensor)) ||
                key.equals(this.getString(R.string.pref_key_alpha_horizontal_rotation_sensor)) ||
                key.equals(this.getString(R.string.pref_key_lpf_accelerometer_enable)) ||
                key.equals(this.getString(R.string.pref_key_alpha_lpf_accelerometer)) ||
                key.equals(this.getString(R.string.pref_key_hpf_gyroscope_enable)) ||
                key.equals(this.getString(R.string.pref_key_alpha_hpf_gyroscope)) ||
                key.equals(this.getString(R.string.pref_key_lpf_magnetometer_enable)) ||
                key.equals(this.getString(R.string.pref_key_alpha_lpf_magnetometer)) ||
                key.equals(this.getString(R.string.pref_key_maf_tachometer_enable))) {
            toast = Toast.makeText(this.getApplicationContext(), R.string.devicesConfigurationChangedMessage,
                    Toast.LENGTH_SHORT);
            toast.show();

            closeApplication();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                Hashtable<String, Integer> permissionsHashtable = new Hashtable<>();
                // Initialize with "default" values
                permissionsHashtable.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                permissionsHashtable.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    permissionsHashtable.put(permissions[i], grantResults[i]);
                }

                if (permissionsHashtable.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                        && permissionsHashtable.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    // Permission Denied
                    Toast toast = Toast.makeText(this, getString(R.string.locationPermissionsDeniedMessage),
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    // All Permissions Granted
                    checkPermissions();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.auto_control:
                Intent autoControlIntent = new Intent(this.getApplicationContext(), AutoControlActivity.class);
                startActivity(autoControlIntent);
                break;
            case R.id.devices_tests:
                Intent devicesTestsIntent = new Intent(this.getApplicationContext(), DevicesTestsActivity.class);
                startActivity(devicesTestsIntent);
                break;
            case R.id.manual_control:
                Intent manualControlIntent = new Intent(this.getApplicationContext(), ManualControlActivity.class);
                startActivity(manualControlIntent);
                break;
            case R.id.settings:
                Intent settingsIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.exit:
                closeApplication();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkPermissions() {
        List<String> permissionsNeededButDenied;
        int permissionFineLocation;
        int permissionCoarseLocation;

        permissionFineLocation = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        permissionCoarseLocation = ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsNeededButDenied = new ArrayList<>();
        if (permissionFineLocation == PackageManager.PERMISSION_DENIED) {
            permissionsNeededButDenied.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionCoarseLocation == PackageManager.PERMISSION_DENIED) {
            permissionsNeededButDenied.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!permissionsNeededButDenied.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeededButDenied.toArray(new
                    String[permissionsNeededButDenied.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
    }

    private void startSensorReadingServices() {
        startService(new Intent(this.getApplicationContext(), IOIOControllerService.class));
        startService(new Intent(this.getApplicationContext(), SensoryInterpretationService.class));
    }

    private void stopSensorReadingServices() {
        Intent stopIOIOControllerIntent, stopSensoryInterpretationIntent;
        NotificationManager notificationManager;

        stopSensoryInterpretationIntent = new Intent(this.getApplicationContext(), SensoryInterpretationService.class);
        stopService(stopSensoryInterpretationIntent);

        stopIOIOControllerIntent = new Intent(this.getApplicationContext(), IOIOControllerService.class);
        stopService(stopIOIOControllerIntent);

//        Remove notification
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void closeApplication() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sensoryLogEnabled = sharedPreferences.getBoolean(getString(R.string.pref_key_enable_sensory_logs), false);
        boolean rscLogEnabled = sharedPreferences.getBoolean(getString(R.string.pref_key_enable_sensory_logs), false);
        boolean atcLogEnabled = sharedPreferences.getBoolean(getString(R.string.pref_key_enable_sensory_logs), false);
        boolean hrcLogEnabled = sharedPreferences.getBoolean(getString(R.string.pref_key_enable_sensory_logs), false);
        boolean alcLogEnabled = sharedPreferences.getBoolean(getString(R.string.pref_key_enable_sensory_logs), false);


        stopSensorReadingServices();

        if (sensoryLogEnabled) {
            this.logHandler.saveLogFile(R.string.pref_key_enable_sensory_logs);
        }
        if (rscLogEnabled) {
            this.logHandler.saveLogFile(R.string.pref_key_enable_rsc_logs);
        }
        if (atcLogEnabled) {
            this.logHandler.saveLogFile(R.string.pref_key_enable_atc_logs);
        }
        if (hrcLogEnabled) {
            this.logHandler.saveLogFile(R.string.pref_key_enable_hrc_logs);
        }
        if (alcLogEnabled) {
            this.logHandler.saveLogFile(R.string.pref_key_enable_alc_logs);
        }

        finish();
    }
}
