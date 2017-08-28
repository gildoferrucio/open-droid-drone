package com.example.gildo.quadcoptercontroller.models.devices.interpreters;

import com.example.gildo.quadcoptercontroller.models.devices.sensors.Sonar;
import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;

/**
 * Created by gildo on 19/04/16.
 */
public class SonarInterpreter extends SensorInterpreter {
    public static final float OUT_OF_RANGE = 9999999f;

    private float distance_raw;

    private float distance_centimeters;

    public float getDistance_raw() {
        return this.distance_raw;
    }

    public void setDistance_raw(float distance_raw) {
        this.distance_raw = distance_raw;
    }

    public float getDistance_centimeters() {
        return this.distance_centimeters;
    }

    public void setDistance_centimeters(float distance_centimeters) {
        this.distance_centimeters = distance_centimeters;
    }

    public float convertReadingToCentimeters(float reading) {
        float result;

//        if (reading != Sonar.READING_NOTHING_DETECTED_SECS) {
        if (NumberManipulation.isAlmostEqual(reading, Sonar.READING_NOTHING_DETECTED_SECS, 0.025f)) {
            result = SonarInterpreter.OUT_OF_RANGE;
        } else {
            result = 2.54f * reading / Sonar.RESOLUTION_SECS_PER_INCHES;

            if (result >= 625) {
                result = SonarInterpreter.OUT_OF_RANGE;
            }
        }

        this.distance_centimeters = result;

        return result;
    }

    public float convertReadingToInches(float reading) {
        float result;

        if (reading != Sonar.READING_NOTHING_DETECTED_SECS) {
            result = reading / Sonar.RESOLUTION_SECS_PER_INCHES;
        } else {
            result = 0;
        }

        return result;
    }
}
