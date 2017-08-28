package com.example.gildo.quadcoptercontroller.models.devices.sensors;

import com.example.gildo.quadcoptercontroller.models.devices.PulseSignalDevice;
import com.example.gildo.quadcoptercontroller.models.constants.SonarId;

import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 19/04/16.
 */
public class Sonar extends PulseSignalDevice {
    public static final String DISTANCE_EAST_RAW_KEY = "distanceEast_raw";
    public static final String DISTANCE_EAST_CENTIMETERS_KEY = "distanceEast_centimeters";
    public static final String DISTANCE_NORTHEAST_RAW_KEY = "distanceNortheast_raw";
    public static final String DISTANCE_NORTHEAST_CENTIMETERS_KEY = "distanceNortheast_centimeters";
    public static final String DISTANCE_NORTH_RAW_KEY = "distanceNorth_raw";
    public static final String DISTANCE_NORTH_CENTIMETERS_KEY = "distanceNorth_centimeters";
    public static final String DISTANCE_NORTHWEST_RAW_KEY = "distanceNorthwest_raw";
    public static final String DISTANCE_NORTHWEST_CENTIMETERS_KEY = "distanceNorthwest_centimeters";
    public static final String DISTANCE_WEST_RAW_KEY = "distanceWest_raw";
    public static final String DISTANCE_WEST_CENTIMETERS_KEY = "distanceWest_centimeters";
    public static final String DISTANCE_SOUTHWEST_RAW_KEY = "distanceSouthwest_raw";
    public static final String DISTANCE_SOUTHWEST_CENTIMETERS_KEY = "distanceSouthwest_centimeters";
    public static final String DISTANCE_SOUTH_RAW_KEY = "distanceSouth_raw";
    public static final String DISTANCE_SOUTH_CENTIMETERS_KEY = "distanceSouth_centimeters";
    public static final String DISTANCE_SOUTHEAST_RAW_KEY = "distanceSoutheast_raw";
    public static final String DISTANCE_SOUTHEAST_CENTIMETERS_KEY = "distanceSoutheast_centimeters";
    public static final String DISTANCE_UP_RAW_KEY = "distanceUp_raw";
    public static final String DISTANCE_UP_CENTIMETERS_KEY = "distanceUp_centimeters";
    public static final String DISTANCE_DOWN_RAW_KEY = "distanceDown_raw";
    public static final String DISTANCE_DOWN_CENTIMETERS_KEY = "distanceDown_centimeters";


    public static final float RESOLUTION_SECS_PER_INCHES = 0.000147f;
//    public static final float READING_NOTHING_DETECTED_SECS = 0.0000375f;
    public static final float READING_NOTHING_DETECTED_SECS = 0.0375f;

    private final int sonarId;

    public Sonar(IOIO ioio, int pinNum, int sonarId, boolean isDoublePrecision)
            throws ConnectionLostException {
        super(ioio, pinNum, PulseInput.PulseMode.POSITIVE, isDoublePrecision);
        this.sonarId = sonarId;
    }

    public int getSonarId() {
        return this.sonarId;
    }

    public float readDistance() throws ConnectionLostException, InterruptedException {
        return this.getPulseDuration();
//        return this.getPulseDurationInterruptible();
    }
}
