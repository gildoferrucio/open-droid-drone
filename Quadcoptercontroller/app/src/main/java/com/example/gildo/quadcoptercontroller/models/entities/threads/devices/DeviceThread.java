package com.example.gildo.quadcoptercontroller.models.entities.threads.devices;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 09/03/17.
 */

public abstract class DeviceThread extends Thread {
    public static final int STANDARD_SAMPLING_PERIOD_MILLISECS = 20;
    public static final int FIRST_WAIT_TIME_MILLISECS = 500;

    protected boolean successfulInitialization;
    protected boolean firstSample;
    protected boolean running;

    protected Intent broadcastIntent;

    protected LocalBroadcastManager localBroadcastManager;

    public boolean isSuccessfulInitialization(){
        return this.successfulInitialization;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    protected abstract void readDevice() throws ConnectionLostException, InterruptedException;
}
