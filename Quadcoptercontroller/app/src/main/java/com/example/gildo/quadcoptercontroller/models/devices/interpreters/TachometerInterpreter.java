package com.example.gildo.quadcoptercontroller.models.devices.interpreters;

/**
 * Created by gildo on 23/04/16.
 */
public class TachometerInterpreter extends SensorInterpreter {
    public static final int READINGS_WINDOW_SIZE = 3;

    private float revolutionsFrequency_hz;
    private float revolutionsFrequency_rpm;

//    private float[] previousValues_rpm;

    public float getRevolutionsFrequency_hz() {
        return this.revolutionsFrequency_hz;
    }

    public void setRevolutionsFrequency_hz(float revolutionsFrequency_hz) {
        this.revolutionsFrequency_hz = revolutionsFrequency_hz;
    }

    public float getRevolutionsFrequency_rpm() {
        return this.revolutionsFrequency_rpm;
    }

    public void setRevolutionsFrequency_rpm(float revolutionsFrequency_rpm) {
        this.revolutionsFrequency_rpm = revolutionsFrequency_rpm;
    }

//    public float[] getPreviousValues_rpm() {
//        return this.previousValues_rpm;
//    }

    public float convertReadingToRevolutionsPerMinute(float reading_hz) {
        float result = reading_hz * 60;

        this.revolutionsFrequency_rpm = result;

//        addToPreviousValues_rpm(this.revolutionsFrequency_rpm);

        return result;
    }

//    private void addToPreviousValues_rpm(float reading) {
//        // Here previousValues_rpm is operated as a queue
//        if (this.previousValues_rpm == null) {
//            this.previousValues_rpm = new float[READINGS_WINDOW_SIZE];
//        } else {
//            for(int iterator = 0; iterator < READINGS_WINDOW_SIZE - 1; iterator++){
//                this.previousValues_rpm[iterator] = this.previousValues_rpm[iterator + 1];
//            }
//        }
//        this.previousValues_rpm[READINGS_WINDOW_SIZE - 1] = reading;
//    }

    // Applies a Moving Average Filter if the last added value is zero and the others aren't null.
//    public float applyMovingAverageFilterIfLastValueIsZero() {
//        boolean allPreviousNonZero = true;
//        float accumulator = 0;
//        float previousValue;
//        float currentFilteredValue;
//        int iterator;
//
//        if (this.previousValues_rpm[READINGS_WINDOW_SIZE - 1] == 0) {
//            for (iterator = 0; iterator < READINGS_WINDOW_SIZE - 1; iterator++) {
//                previousValue = this.previousValues_rpm[iterator];
//                if (previousValue == 0) {
//                    allPreviousNonZero = false;
//                    break;
//                }
//                accumulator += previousValue;
//            }
//
//            if (allPreviousNonZero) {
//                currentFilteredValue = accumulator / ((float) READINGS_WINDOW_SIZE - 1);
//                // Here currentFilteredValue substitutes the old unfiltered value;
//                this.previousValues_rpm[READINGS_WINDOW_SIZE - 1] = currentFilteredValue;
//            } else {
//                /* There's no need to insert the currentUnfiltered_scaled on previousValues because
//                 * it was already inserted on the queue on the conversion method
//                 */
//                currentFilteredValue = this.previousValues_rpm[READINGS_WINDOW_SIZE - 1];
//            }
//        } else {
//            /* There's no need to insert the currentUnfiltered_scaled on previousValues because
//             * it was already inserted on the queue on the conversion method
//             */
//            currentFilteredValue = this.previousValues_rpm[READINGS_WINDOW_SIZE - 1];
//        }
//
//        return currentFilteredValue;
//    }
}
