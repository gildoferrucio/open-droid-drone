package com.example.gildo.quadcoptercontroller.models.constants;

import com.example.gildo.quadcoptercontroller.R;

/**
 * Created by gildo on 27/03/16.
 */
public abstract class PeripheralKind {
    /* Just following recomendation to use constants instead of enums.
     * Reference: https://www.youtube.com/watch?v=Hzs6OBcvNQE
     */

    public static final int ACCELEROMETER = 0;
    public static final int BAROMETER = 1;
    public static final int ESC = 2;
    public static final int GYROSCOPE = 3;
    public static final int LONG_RANGE_COMMUNICATOR = 4;
    public static final int MAGNETOMETER = 5;
    public static final int TACHOMETER = 6;
    public static final int SONAR = 7;

    public static int getNameResourceId(final int peripheralKind) {
        int nameResourceId = ACCELEROMETER;

        switch (peripheralKind) {
            case ACCELEROMETER:
                nameResourceId = R.string.peripheral_accelerometer;
                break;
            case BAROMETER:
                nameResourceId = R.string.peripheral_barometer;
                break;
            case ESC:
                nameResourceId = R.string.peripheral_esc;
                break;
            case GYROSCOPE:
                nameResourceId = R.string.peripheral_gyroscope;
                break;
            case LONG_RANGE_COMMUNICATOR:
                nameResourceId = R.string.peripheral_long_range_communicator;
                break;
            case MAGNETOMETER:
                nameResourceId = R.string.peripheral_magnetometer;
                break;
            case TACHOMETER:
                nameResourceId = R.string.peripheral_tachometer;
                break;
            case SONAR:
                nameResourceId = R.string.peripheral_sonar;
                break;
        }

        return nameResourceId;
    }
}