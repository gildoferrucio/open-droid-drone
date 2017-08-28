package com.example.gildo.quadcoptercontroller.models.entities;

import com.example.gildo.quadcoptercontroller.models.constants.AxisName;

/**
 * Created by gildo on 24/10/16.
 */
public class EquilibriumAxis {
    public static final String CURRENT_ROTATION_X_DEGREES_KEY = "currentRotationX_degrees";
    public static final String CURRENT_ROTATION_Y_DEGREES_KEY = "currentRotationY_degrees";
    public static final String CURRENT_ROTATION_Z_DEGREES_KEY = "currentRotationZ_degrees";

    public static final String DESIRED_ROTATION_X_DEGREES_KEY = "desiredRotationX_degrees";
    public static final String DESIRED_ROTATION_Y_DEGREES_KEY = "desiredRotationY_degrees";
    public static final String DESIRED_ROTATION_Z_DEGREES_KEY = "desiredRotationZ_degrees";

    private final int axis;
    private float currentAxisRotation_degrees;
    private float desiredAxisRotation_degrees;

    public EquilibriumAxis(int axisName) {
        this.axis = axisName;
    }

    public int getAxis() {
        return this.axis;
    }

    public float getCurrentAxisRotation_degrees() {
        return this.currentAxisRotation_degrees;
    }

    public void setCurrentAxisRotation_degrees(float currentAxisRotation_degrees) {
        this.currentAxisRotation_degrees = currentAxisRotation_degrees;
    }

    public float getDesiredAxisRotation_degrees() {
        return this.desiredAxisRotation_degrees;
    }

    public void setDesiredAxisRotation_degrees(float desiredAxisRotation_degrees) {
        this.desiredAxisRotation_degrees = desiredAxisRotation_degrees;
    }
}
