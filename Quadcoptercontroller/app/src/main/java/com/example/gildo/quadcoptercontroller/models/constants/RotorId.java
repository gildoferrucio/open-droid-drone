package com.example.gildo.quadcoptercontroller.models.constants;

import com.example.gildo.quadcoptercontroller.R;

/**
 * Created by gildo on 23/04/16.
 */
public abstract class RotorId {
    /* Just following recomendation to use constants instead of enums.
     * Reference: https://www.youtube.com/watch?v=Hzs6OBcvNQE
     */

    public static final int ONE = 0;
    public static final int TWO = 1;
    public static final int THREE = 2;
    public static final int FOUR = 3;

    public static int getNameResourceId(final int rotorId) {
        int nameResourceId = ONE;

        switch (rotorId) {
            case ONE:
                nameResourceId = R.string.rotor1_id;
                break;
            case TWO:
                nameResourceId = R.string.rotor2_id;
                break;
            case THREE:
                nameResourceId = R.string.rotor3_id;
                break;
            case FOUR:
                nameResourceId = R.string.rotor4_id;
                break;
        }

        return nameResourceId;
    }
}
