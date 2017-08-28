package com.example.gildo.quadcoptercontroller.models.constants;

/**
 * Created by gildo on 22/04/16.
 */
public abstract class SonarId {
    /* Just following recomendation to use constants instead of enums.
     * Reference: https://www.youtube.com/watch?v=Hzs6OBcvNQE
     */

    public static final int EAST = 0;
    public static final int NORTHEAST = 1;
    public static final int NORTH = 2;
    public static final int NORTHWEST = 3;
    public static final int WEST = 4;
    public static final int SOUTHWEST = 5;
    public static final int SOUTH = 6;
    public static final int SOUTHEAST = 7;
    public static final int DOWN = 8;
    public static final int UP = 9;
}
