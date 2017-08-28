package com.example.gildo.quadcoptercontroller.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.constants.PeripheralKind;
import com.example.gildo.quadcoptercontroller.models.gui.adapters.Peripheral;
import com.example.gildo.quadcoptercontroller.models.gui.adapters.PeripheralAdapter;

import java.util.ArrayList;
import java.util.List;

public class DevicesTestsActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView listView;

    private List<Peripheral> peripheralList;

    private PeripheralAdapter peripheralAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_tests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.listView = (ListView) findViewById(R.id.listView_peripherics);
        this.listView.setOnItemClickListener(this);

        populatePeripheralList();
        this.peripheralAdapter = new PeripheralAdapter(this.getApplicationContext(), this.peripheralList);
        this.listView.setAdapter(this.peripheralAdapter);

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

    private void populatePeripheralList() {
        this.peripheralList = new ArrayList<>();
        this.peripheralList.add(new Peripheral(PeripheralKind.ACCELEROMETER));
        this.peripheralList.add(new Peripheral(PeripheralKind.BAROMETER));
        this.peripheralList.add(new Peripheral(PeripheralKind.ESC));
        this.peripheralList.add(new Peripheral(PeripheralKind.GYROSCOPE));
        this.peripheralList.add(new Peripheral(PeripheralKind.LONG_RANGE_COMMUNICATOR));
        this.peripheralList.add(new Peripheral(PeripheralKind.MAGNETOMETER));
        this.peripheralList.add(new Peripheral(PeripheralKind.TACHOMETER));
        this.peripheralList.add(new Peripheral(PeripheralKind.SONAR));
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Peripheral selectedPeripheral = this.peripheralList.get(position);
        Intent intent = new Intent(this.getApplicationContext(), TestsExecutorActivity.class);
        intent.putExtra(Peripheral.PARCELABLE_EXTRA_KEY, selectedPeripheral);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        System.gc();
        DevicesTestsActivity.this.finish();
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
}
