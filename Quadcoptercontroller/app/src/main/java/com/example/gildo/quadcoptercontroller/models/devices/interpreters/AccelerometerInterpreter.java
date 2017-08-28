package com.example.gildo.quadcoptercontroller.models.devices.interpreters;

import com.example.gildo.quadcoptercontroller.models.devices.sensors.Accelerometer;
import com.example.gildo.quadcoptercontroller.models.constants.AxisName;
import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;

import java.util.Hashtable;

/**
 * Created by gildo on 05/04/16.
 */
public class AccelerometerInterpreter extends SensorInterpreter {
    private int accelerationX_raw;
    private int accelerationY_raw;
    private int accelerationZ_raw;

    private float accelerationX_metersPerSecSquared;
    private float accelerationY_metersPerSecSquared;
    private float accelerationZ_metersPerSecSquared;

    private Hashtable<Integer, Float> previousFilteredValues_scaled; //Axis, Value
    private Hashtable<Integer, Float> currentUnfilteredValues_scaled; //Axis, Value
    private Hashtable<Integer, Float> currentFilteredValues_scaled; //Axis, Value

    public int getAccelerationX_raw() {
        return this.accelerationX_raw;
    }

    public void setAccelerationX_raw(int accelerationX_raw) {
        this.accelerationX_raw = accelerationX_raw;
    }

    public int getAccelerationY_raw() {
        return this.accelerationY_raw;
    }

    public void setAccelerationY_raw(int accelerationY_raw) {
        this.accelerationY_raw = accelerationY_raw;
    }

    public int getAccelerationZ_raw() {
        return this.accelerationZ_raw;
    }

    public void setAccelerationZ_raw(int accelerationZ_raw) {
        this.accelerationZ_raw = accelerationZ_raw;
    }

    public float getAccelerationX_metersPerSecSquared() {
        return this.accelerationX_metersPerSecSquared;
    }

    public void setAccelerationX_metersPerSecSquared(float accelerationX_metersPerSecSquared) {
        this.accelerationX_metersPerSecSquared = accelerationX_metersPerSecSquared;
    }

    public float getAccelerationY_metersPerSecSquared() {
        return this.accelerationY_metersPerSecSquared;
    }

    public void setAccelerationY_metersPerSecSquared(float accelerationY_metersPerSecSquared) {
        this.accelerationY_metersPerSecSquared = accelerationY_metersPerSecSquared;
    }

    public float getAccelerationZ_metersPerSecSquared() {
        return this.accelerationZ_metersPerSecSquared;
    }

    public void setAccelerationZ_metersPerSecSquared(float accelerationZ_metersPerSecSquared) {
        this.accelerationZ_metersPerSecSquared = accelerationZ_metersPerSecSquared;
    }

    public Hashtable<Integer, Float> getCurrentFilteredValues_scaled() {
        return this.currentFilteredValues_scaled;
    }

    public float convertReadingToGs(int reading) {
        float result = Accelerometer.RESOLUTION_G_PER_LSB * reading;

        return result;
    }

    public float convertReadingToMetersPerSecSquared(int reading) {
        float result = (Accelerometer.RESOLUTION_G_PER_LSB * reading) *
                Accelerometer.STANDARD_GRAVITY_METERS_PER_SEC;

        return result;
    }

    public float convertGtoMetersPerSecSquared(int readingInGs) {
        float result = Accelerometer.STANDARD_GRAVITY_METERS_PER_SEC * readingInGs;

        return result;
    }

    public int getGravityAxis() {
        float accelerationX_metersPerSec = convertReadingToMetersPerSecSquared(this.accelerationX_raw);
        float accelerationY_metersPerSec = convertReadingToMetersPerSecSquared(this.accelerationY_raw);
        float accelerationZ_metersPerSec = convertReadingToMetersPerSecSquared(this.accelerationZ_raw);
        int gravityOnAxis = AxisName.X;

        if (NumberManipulation.isAlmostEqual(accelerationX_metersPerSec,
                Accelerometer.STANDARD_GRAVITY_METERS_PER_SEC, 0.165f)) {
            gravityOnAxis = AxisName.X;
        } else if (NumberManipulation.isAlmostEqual(accelerationY_metersPerSec,
                Accelerometer.STANDARD_GRAVITY_METERS_PER_SEC, 0.165f)) {
            gravityOnAxis = AxisName.Y;
        } else if (NumberManipulation.isAlmostEqual(accelerationZ_metersPerSec,
                Accelerometer.STANDARD_GRAVITY_METERS_PER_SEC, 0.165f)) {
            gravityOnAxis = AxisName.Z;
        }

        return gravityOnAxis;
    }

    //TODO: implement cutoffFrequency_hertz!!
    public Hashtable<Integer, Float> applyLowPassFilterRC(float cutoffFrequency_hertz, float dt_seconds,
                                                           float currentUnfilteredX_scaled,
                                                           float currentUnfilteredY_scaled, float currentUnfilteredZ_scaled,
                                                           boolean firstSample) {
        //dt_seconds ---> Time interval between samples
        float currentFilteredValue;
        float rc = 1f / (cutoffFrequency_hertz * 2f * (float) Math.PI);
        float alpha = dt_seconds / (rc + dt_seconds);

        if (firstSample) {
            this.currentUnfilteredValues_scaled = new Hashtable<>();
            this.currentUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            this.currentFilteredValues_scaled = new Hashtable<>();
            this.currentFilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentFilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentFilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            this.previousFilteredValues_scaled = new Hashtable<>();
            this.previousFilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.previousFilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.previousFilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);
        } else {
            this.currentUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            for (int axis : this.currentUnfilteredValues_scaled.keySet()) {
                currentFilteredValue = this.previousFilteredValues_scaled.get(axis) + (alpha *
                        (this.currentUnfilteredValues_scaled.get(axis) - this.previousFilteredValues_scaled.get(axis)));
                this.currentFilteredValues_scaled.put(axis, currentFilteredValue);

                //update previous values with current ones
                this.previousFilteredValues_scaled.put(axis, currentFilteredValue);
            }
        }

        return this.currentFilteredValues_scaled;
    }

    public Hashtable<Integer, Float> applyLowPassFilterRC(float alpha, float currentUnfilteredX_scaled,
                                                          float currentUnfilteredY_scaled,
                                                          float currentUnfilteredZ_scaled, boolean firstSample) {
        float currentFilteredValue;

        if (firstSample) {
            this.currentUnfilteredValues_scaled = new Hashtable<>();
            this.currentUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            this.currentFilteredValues_scaled = new Hashtable<>();
            this.currentFilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentFilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentFilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            this.previousFilteredValues_scaled = new Hashtable<>();
            this.previousFilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.previousFilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.previousFilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);
        } else {
            this.currentUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            for (int axis : this.currentUnfilteredValues_scaled.keySet()) {
                currentFilteredValue = this.previousFilteredValues_scaled.get(axis) + (alpha *
                        (this.currentUnfilteredValues_scaled.get(axis) - this.previousFilteredValues_scaled.get(axis)));
                this.currentFilteredValues_scaled.put(axis, currentFilteredValue);

                //update previous values with current ones
                this.previousFilteredValues_scaled.put(axis, currentFilteredValue);
            }
        }

        return this.currentFilteredValues_scaled;
    }
}

