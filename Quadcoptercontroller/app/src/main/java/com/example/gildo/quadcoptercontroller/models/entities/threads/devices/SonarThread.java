package com.example.gildo.quadcoptercontroller.models.entities.threads.devices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.models.constants.SonarId;
import com.example.gildo.quadcoptercontroller.models.devices.PulseSignalDevice;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Sonar;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 12/03/17.
 */

public class SonarThread extends DeviceThread {
    public static final String INTENT_FILTER_SONAR_EAST_THREAD = "sonarEastThread";
    public static final String INTENT_FILTER_SONAR_NORTHEAST_THREAD = "sonarNortheastThread";
    public static final String INTENT_FILTER_SONAR_NORTH_THREAD = "sonarNorthThread";
    public static final String INTENT_FILTER_SONAR_NORTHWEST_THREAD = "sonarNorthwestThread";
    public static final String INTENT_FILTER_SONAR_WEST_THREAD = "sonarWestThread";
    public static final String INTENT_FILTER_SONAR_SOUTHWEST_THREAD = "sonarSouthwestThread";
    public static final String INTENT_FILTER_SONAR_SOUTH_THREAD = "sonarSouthThread";
    public static final String INTENT_FILTER_SONAR_SOUTHEAST_THREAD = "sonarSoutheastThread";
    public static final String INTENT_FILTER_SONAR_UP_THREAD = "sonarUpThread";
    public static final String INTENT_FILTER_SONAR_DOWN_THREAD = "sonarDownThread";

    private PulseSignalDevice pulseSignalDevice_sonar;

    private float distance;

    public SonarThread(IOIO ioio, int pinNum, int sonarId, boolean isDoublePrecision, Context context)
            throws ConnectionLostException {
        this.pulseSignalDevice_sonar = new Sonar(ioio, pinNum, sonarId, isDoublePrecision);
        switch (sonarId) {
            case SonarId.EAST:
                this.setName(INTENT_FILTER_SONAR_EAST_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_EAST_THREAD);
                break;
            case SonarId.NORTHEAST:
                this.setName(INTENT_FILTER_SONAR_NORTHEAST_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_NORTHEAST_THREAD);
                break;
            case SonarId.NORTH:
                this.setName(INTENT_FILTER_SONAR_NORTH_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_NORTH_THREAD);
                break;
            case SonarId.NORTHWEST:
                this.setName(INTENT_FILTER_SONAR_NORTHWEST_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_NORTHWEST_THREAD);
                break;
            case SonarId.WEST:
                this.setName(INTENT_FILTER_SONAR_WEST_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_WEST_THREAD);
                break;
            case SonarId.SOUTHWEST:
                this.setName(INTENT_FILTER_SONAR_SOUTHWEST_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_SOUTHWEST_THREAD);
                break;
            case SonarId.SOUTH:
                this.setName(INTENT_FILTER_SONAR_SOUTH_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_SOUTH_THREAD);
                break;
            case SonarId.SOUTHEAST:
                this.setName(INTENT_FILTER_SONAR_SOUTHEAST_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_SOUTHEAST_THREAD);
                break;
            case SonarId.UP:
                this.setName(INTENT_FILTER_SONAR_UP_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_UP_THREAD);
                break;
            case SonarId.DOWN:
                this.setName(INTENT_FILTER_SONAR_DOWN_THREAD);
                this.broadcastIntent = new Intent(SonarThread.INTENT_FILTER_SONAR_DOWN_THREAD);
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
                Thread.sleep(DeviceThread.STANDARD_SAMPLING_PERIOD_MILLISECS);
            }
        } catch (ConnectionLostException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.running = false;
        }
    }

    @Override
    protected void readDevice() throws ConnectionLostException, InterruptedException {
        this.distance = ((Sonar) this.pulseSignalDevice_sonar).readDistance();

        switch (((Sonar) this.pulseSignalDevice_sonar).getSonarId()) {
            case SonarId.EAST:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_EAST_RAW_KEY, this.distance);
                break;
            case SonarId.NORTHEAST:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTHEAST_RAW_KEY, this.distance);
                break;
            case SonarId.NORTH:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTH_RAW_KEY, this.distance);
                break;
            case SonarId.NORTHWEST:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_NORTHWEST_RAW_KEY, this.distance);
                break;
            case SonarId.WEST:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_WEST_RAW_KEY, this.distance);
                break;
            case SonarId.SOUTHWEST:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTHWEST_RAW_KEY, this.distance);
                break;
            case SonarId.SOUTH:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTH_RAW_KEY, this.distance);
                break;
            case SonarId.SOUTHEAST:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_SOUTHEAST_RAW_KEY, this.distance);
                break;
            case SonarId.UP:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_UP_RAW_KEY, this.distance);
                break;
            case SonarId.DOWN:
                this.broadcastIntent.putExtra(Sonar.DISTANCE_DOWN_RAW_KEY, this.distance);
                break;
        }
    }
}
