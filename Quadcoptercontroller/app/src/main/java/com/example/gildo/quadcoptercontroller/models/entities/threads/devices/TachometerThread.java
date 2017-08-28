package com.example.gildo.quadcoptercontroller.models.entities.threads.devices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.models.constants.RotorId;
import com.example.gildo.quadcoptercontroller.models.devices.PulseSignalDevice;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Tachometer;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 12/03/17.
 */

public class TachometerThread extends DeviceThread {
    public static final String INTENT_FILTER_TACHOMETER1_THREAD = "tachometer1Thread";
    public static final String INTENT_FILTER_TACHOMETER2_THREAD = "tachometer2Thread";
    public static final String INTENT_FILTER_TACHOMETER3_THREAD = "tachometer3Thread";
    public static final String INTENT_FILTER_TACHOMETER4_THREAD = "tachometer4Thread";

    private PulseSignalDevice pulseSignalDevice_tachometer;

    private float rotorRevolutions;

    public TachometerThread(IOIO ioio, int pinNum, int rotorId, boolean isDoublePrecision, Context context)
            throws ConnectionLostException {
        this.pulseSignalDevice_tachometer = new Tachometer(ioio, pinNum, rotorId, isDoublePrecision);
        switch (rotorId) {
            case RotorId.ONE:
                this.setName(INTENT_FILTER_TACHOMETER1_THREAD);
                this.broadcastIntent = new Intent(TachometerThread.INTENT_FILTER_TACHOMETER1_THREAD);
                break;
            case RotorId.TWO:
                this.setName(INTENT_FILTER_TACHOMETER2_THREAD);
                this.broadcastIntent = new Intent(TachometerThread.INTENT_FILTER_TACHOMETER2_THREAD);
                break;
            case RotorId.THREE:
                this.setName(INTENT_FILTER_TACHOMETER3_THREAD);
                this.broadcastIntent = new Intent(TachometerThread.INTENT_FILTER_TACHOMETER3_THREAD);
                break;
            case RotorId.FOUR:
                this.setName(INTENT_FILTER_TACHOMETER4_THREAD);
                this.broadcastIntent = new Intent(TachometerThread.INTENT_FILTER_TACHOMETER4_THREAD);
                break;
        }
        this.firstSample = true;
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void run() {
        try {
            //Delay inserted to make sure that all devices had been initialized
            Thread.sleep(DeviceThread.FIRST_WAIT_TIME_MILLISECS);

            this.running = true;

            while (this.running) {
                readDevice();

                this.localBroadcastManager.sendBroadcast(this.broadcastIntent);

                //there's a need to put some sleep time here, some millisecs
                Thread.sleep(2 * DeviceThread.STANDARD_SAMPLING_PERIOD_MILLISECS);
            }
        } catch (ConnectionLostException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.running = false;
        }
    }

    @Override
    protected void readDevice() throws ConnectionLostException, InterruptedException {
        this.rotorRevolutions = ((Tachometer) this.pulseSignalDevice_tachometer).readRevolutionsFrequency();

        switch (((Tachometer) this.pulseSignalDevice_tachometer).getRotorId()) {
            case RotorId.ONE:
                this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR1_HZ_KEY, this.rotorRevolutions);
                break;
            case RotorId.TWO:
                this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR2_HZ_KEY, this.rotorRevolutions);
                break;
            case RotorId.THREE:
                this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR3_HZ_KEY, this.rotorRevolutions);
                break;
            case RotorId.FOUR:
                this.broadcastIntent.putExtra(Tachometer.REVOLUTIONS_ROTOR4_HZ_KEY, this.rotorRevolutions);
                break;
        }
    }
}
