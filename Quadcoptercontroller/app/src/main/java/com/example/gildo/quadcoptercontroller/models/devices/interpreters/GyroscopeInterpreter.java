package com.example.gildo.quadcoptercontroller.models.devices.interpreters;

import com.example.gildo.quadcoptercontroller.models.devices.sensors.Gyroscope;
import com.example.gildo.quadcoptercontroller.models.constants.AxisName;
import com.example.gildo.quadcoptercontroller.models.entities.handlers.LogHandler;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gildo on 06/04/16.
 */
public class GyroscopeInterpreter extends SensorInterpreter {
    public static final String GYROSCOPE_CALIBRATION_FILENAME = "quadcopterControllerGyroscopeCalibration.txt";
    public static final int CALIBRATION_STEPS = 10;

    //    private int temperature;
    private int angularSpeedX_raw;
    private int angularSpeedY_raw;
    private int angularSpeedZ_raw;

    private float angularSpeedX_degreesPerSec;
    private float angularSpeedY_degreesPerSec;
    private float angularSpeedZ_degreesPerSec;

    private Hashtable<Integer, Float> previousUnfilteredValues_scaled;
    private Hashtable<Integer, Float> previousFilteredValues_scaled;
    private Hashtable<Integer, Float> currentUnfilteredValues_scaled;
    private Hashtable<Integer, Float> currentFilteredValues_scaled;

    private float offsetX_degreesPerSec = 0;
    private float offsetY_degreesPerSec = 0;
    private float offsetZ_degreesPerSec = 0;

//    public int getTemperature_raw() {
//        return this.temperature;
//    }
//
//    public void setTemperature_raw(int temperature) {
//        this.temperature = temperature;
//    }

    public int getAngularSpeedX_raw() {
        return this.angularSpeedX_raw;
    }

    public void setAngularSpeedX_raw(int angularSpeedX_raw) {
        this.angularSpeedX_raw = angularSpeedX_raw;
    }

    public int getAngularSpeedY_raw() {
        return this.angularSpeedY_raw;
    }

    public void setAngularSpeedY_raw(int angularSpeedY_raw) {
        this.angularSpeedY_raw = angularSpeedY_raw;
    }

    public int getAngularSpeedZ_raw() {
        return this.angularSpeedZ_raw;
    }

    public void setAngularSpeedZ_raw(int angularSpeedZ_raw) {
        this.angularSpeedZ_raw = angularSpeedZ_raw;
    }

    public float getAngularSpeedX_degreesPerSec() {
        return this.angularSpeedX_degreesPerSec;
    }

    public void setAngularSpeedX_degreesPerSec(float angularSpeedX_degreesPerSec) {
        this.angularSpeedX_degreesPerSec = angularSpeedX_degreesPerSec;
    }

    public float getAngularSpeedY_degreesPerSec() {
        return this.angularSpeedY_degreesPerSec;
    }

    public void setAngularSpeedY_degreesPerSec(float angularSpeedY_degreesPerSec) {
        this.angularSpeedY_degreesPerSec = angularSpeedY_degreesPerSec;
    }

    public float getAngularSpeedZ_degreesPerSec() {
        return this.angularSpeedZ_degreesPerSec;
    }

    public void setAngularSpeedZ_degreesPerSec(float angularSpeedZ_degreesPerSec) {
        this.angularSpeedZ_degreesPerSec = angularSpeedZ_degreesPerSec;
    }

    public Hashtable<Integer, Float> getCurrentFilteredValues_scaled() {
        return this.currentFilteredValues_scaled;
    }

    public float convertReadingToDegreesPerSec(int reading) {
        float result;

        result = Gyroscope.RESOLUTION_ROTATION_DEGREES_PER_SEC * reading;

        return result;
    }

//    public float convertReadingsToCelsius(int reading) {
//        float result;
//
//        result = (Gyroscope.RESOLUTION_TEMPERATURE_CELSIUS * reading) +
//                Gyroscope.TEMPERATURE_OFFSET_CELSIUS;
//
//        return result;
//    }

    public void calculateOffset(ArrayList<Float> leveledReadingsX_degreesPerSec,
                                ArrayList<Float> leveledReadingsY_degreesPerSec,
                                ArrayList<Float> leveledReadingsZ_degreesPerSec) {
        float leveledReadingsAverageX, leveledReadingsAverageY, leveledReadingsAverageZ;

        leveledReadingsAverageX = leveledReadingsAverageY = leveledReadingsAverageZ = 0;

        if (leveledReadingsX_degreesPerSec.size() != 0) {
            for (float currentReading : leveledReadingsX_degreesPerSec) {
                leveledReadingsAverageX += currentReading;
            }
            leveledReadingsAverageX /= leveledReadingsX_degreesPerSec.size();
        } else {
            leveledReadingsAverageX = 0;
        }

        if (leveledReadingsY_degreesPerSec.size() != 0) {
            for (float currentReading : leveledReadingsY_degreesPerSec) {
                leveledReadingsAverageY += currentReading;
            }
            leveledReadingsAverageY /= leveledReadingsY_degreesPerSec.size();
        } else {
            leveledReadingsAverageY = 0;
        }

        if (leveledReadingsZ_degreesPerSec.size() != 0) {
            for (float currentReading : leveledReadingsZ_degreesPerSec) {
                leveledReadingsAverageZ += currentReading;
            }
            leveledReadingsAverageZ /= leveledReadingsZ_degreesPerSec.size();
        } else {
            leveledReadingsAverageZ = 0;
        }
        
        this.offsetX_degreesPerSec = leveledReadingsAverageX;
        this.offsetY_degreesPerSec = leveledReadingsAverageY;
        this.offsetZ_degreesPerSec = leveledReadingsAverageZ;
    }

    public float removeOffset(final int axis, float reading_degreesPerSec) {
        float cleanReading = 0;

        switch (axis) {
            case AxisName.X:
                cleanReading = reading_degreesPerSec - this.offsetX_degreesPerSec;
                break;
            case AxisName.Y:
                cleanReading = reading_degreesPerSec - this.offsetY_degreesPerSec;
                break;
            case AxisName.Z:
                cleanReading = reading_degreesPerSec - this.offsetZ_degreesPerSec;
                break;
        }

        return cleanReading;
    }

    //TODO: implement cutoffFrequency_hertz!!
    public Hashtable<Integer, Float> applyHighPassFilterRC(float cutoffFrequency_hertz, float dt_seconds,
                                                           float currentUnfilteredX_scaled,
                                                           float currentUnfilteredY_scaled,
                                                           float currentUnfilteredZ_scaled, boolean firstSample) {
        //dt_seconds ---> Time interval between samples
        float currentFilteredValue;
        float rc = 1f / (cutoffFrequency_hertz * 2f * (float) Math.PI);
        float alpha = rc / (rc + dt_seconds);

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

            this.previousUnfilteredValues_scaled = new Hashtable<>();
            this.previousUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.previousUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.previousUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);
        } else {
            this.currentUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            for (int axis : this.currentUnfilteredValues_scaled.keySet()) {
                currentFilteredValue = alpha * (this.previousFilteredValues_scaled.get(axis) +
                        this.currentUnfilteredValues_scaled.get(axis) - this.previousUnfilteredValues_scaled.get(axis));
                this.currentFilteredValues_scaled.put(axis, currentFilteredValue);

                //update previous values with current ones
                this.previousFilteredValues_scaled.put(axis, currentFilteredValue);
                this.previousUnfilteredValues_scaled.put(axis, this.currentUnfilteredValues_scaled.get(axis));
            }
        }

        return this.currentFilteredValues_scaled;
    }

    public Hashtable<Integer, Float> applyHighPassFilterRC(float alpha, float currentUnfilteredX_scaled,
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

            this.previousUnfilteredValues_scaled = new Hashtable<>();
            this.previousUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.previousUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.previousUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);
        } else {
            this.currentUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            for (int axis : this.currentUnfilteredValues_scaled.keySet()) {
                currentFilteredValue = alpha * (this.previousFilteredValues_scaled.get(axis) +
                        this.currentUnfilteredValues_scaled.get(axis) - this.previousUnfilteredValues_scaled.get(axis));
                this.currentFilteredValues_scaled.put(axis, currentFilteredValue);

                //update previous values with current ones
                this.previousFilteredValues_scaled.put(axis, currentFilteredValue);
                this.previousUnfilteredValues_scaled.put(axis, this.currentUnfilteredValues_scaled.get(axis));
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

            this.previousUnfilteredValues_scaled = new Hashtable<>();
            this.previousUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.previousUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.previousUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);
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
