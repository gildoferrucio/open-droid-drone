package com.example.gildo.quadcoptercontroller.models.entities.threads.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.models.devices.actuators.ESC;
import com.example.gildo.quadcoptercontroller.models.entities.IOIOControllerLooper;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;

/**
 * Created by gildo on 07/12/16.
 */

public abstract class ControlThread extends Thread {
    protected boolean running;
    protected boolean pidControlEnabled;
    protected boolean hasInputToProcess;
    protected boolean logEnabled;
    protected boolean logHeaderCreated = false;

    protected long currentSamplePeriod_millisecs = IOIOControllerLooper.STANDARD_SAMPLE_PERIOD_MILLISECS;

    protected int minimumOutput_pulseWidthMicrosecs;
    protected int maximumOutput_pulseWidthMicrosecs;

    protected StringBuilder stringBuilderLogTags;
    protected StringBuilder stringBuilderLogValues;

    protected BroadcastReceiver broadcastReceiver;

    protected LocalBroadcastManager localBroadcastManager;

    protected Intent broadcastIntent;

    protected Context context;

    protected LogHandler logHandler;

    public void setRunning(boolean running) {
        this.running = running;
    }

    protected abstract void calculateControlVariable();

    protected abstract void processRotorsPulseWidthWithoutControl();

    protected abstract void distributeSystemOutputThroughRotors();

    protected abstract void logInputsAndOutputs();

    protected abstract void createLogHeader();

    protected abstract void sendLocalBroadcast();
}
