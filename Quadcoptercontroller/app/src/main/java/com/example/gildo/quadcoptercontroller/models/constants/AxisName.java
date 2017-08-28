package com.example.gildo.quadcoptercontroller.models.constants;

import com.example.gildo.quadcoptercontroller.R;

/**
 * Created by gildo on 16/04/16.
 */
public abstract class AxisName {
    /* Just following recomendation to use constants instead of enums.
     * Reference: https://www.youtube.com/watch?v=Hzs6OBcvNQE
     */

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    public static int getNameResourceId(final int axis) {
        int nameResourceId = X;

        switch (axis) {
            case X:
                nameResourceId = R.string.pitch;
                break;
            case Y:
                nameResourceId = R.string.roll;
                break;
            case Z:
                nameResourceId = R.string.yaw;
                break;
        }

        return nameResourceId;
    }
}
