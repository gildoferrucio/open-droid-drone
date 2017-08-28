package com.example.gildo.quadcoptercontroller.models.constants;

import com.example.gildo.quadcoptercontroller.R;

/**
 * Created by gildo on 19/07/16.
 */
public abstract class Cardinal {
    /* Just following recomendation to use constants instead of enums.
     * Reference: https://www.youtube.com/watch?v=Hzs6OBcvNQE
     */

    public static final int NORTH = 0;
    public static final int NORTHEAST = 1;
    public static final int EAST = 2;
    public static final int SOUTHEAST = 3;
    public static final int SOUTH = 4;
    public static final int SOUTHwEST = 5;
    public static final int WEST = 6;
    public static final int NORTHWEST = 7;

    public static int getNameResourceId(final int rotorId) {
        int nameResourceId = NORTH;

        switch (rotorId) {
            case NORTH:
                nameResourceId = R.string.cardinal_north_abbreviation;
                break;
            case NORTHEAST:
                nameResourceId = R.string.cardinal_northeast_abbreviation;
                break;
            case EAST:
                nameResourceId = R.string.cardinal_east_abbreviation;
                break;
            case SOUTHEAST:
                nameResourceId = R.string.cardinal_southeast_abbreviation;
                break;
            case SOUTH:
                nameResourceId = R.string.cardinal_south_abbreviation;
                break;
            case SOUTHwEST:
                nameResourceId = R.string.cardinal_southwest_abbreviation;
                break;
            case WEST:
                nameResourceId = R.string.cardinal_west_abbreviation;
                break;
            case NORTHWEST:
                nameResourceId = R.string.cardinal_northwest_abbreviation;
                break;
        }

        return nameResourceId;
    }

    public static int getCardinalFromCompassHeading(float compassHeading) {
        int cardinal;

        if (compassHeading >= 22.5 && compassHeading < 67.5) {
            cardinal = NORTHEAST;
        } else if (compassHeading >= 67.5 && compassHeading < 112.5) {
            cardinal = EAST;
        } else if (compassHeading >= 112.5 && compassHeading < 157.5) {
            cardinal = SOUTHEAST;
        } else if (compassHeading >= 157.5 && compassHeading < 202.5) {
            cardinal = SOUTH;
        } else if (compassHeading >= 202.5 && compassHeading < 247.5) {
            cardinal = SOUTHwEST;
        } else if (compassHeading >= 247.5 && compassHeading < 292.5) {
            cardinal = WEST;
        } else if (compassHeading >= 292.5 && compassHeading < 337.5) {
            cardinal = NORTHWEST;
        } else {
            cardinal = NORTH;
        }

        return cardinal;
    }
}
