package com.example.gildo.quadcoptercontroller.models.devices.fusedSensors;

import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;

/**
 * Created by gildo on 16/08/16.
 */
public class AttitudeSensor extends ComplementaryFilteredSensorFusion {
    public static final String ROTATION_ANGLE_X_DEGREES_KEY = "rotationAngleFusedX_degrees";
    public static final String ROTATION_ANGLE_Y_DEGREES_KEY = "rotationAngleFusedY_degrees";

    private boolean firstGyroscopeSampleX;
    private float rotationAngleAccumulatorFromGyroscopeX_degrees;
    private float instantRotationFromGyroscopeX_degrees;
    private long previousTimestampGyroscopeSampleX_millisec;

    private boolean firstGyroscopeSampleY;
    private float rotationAngleAccumulatorFromGyroscopeY_degrees;
    private float instantRotationFromGyroscopeY_degrees;
    private long previousTimestampGyroscopeSampleY_millisec;

    private float rotationAngleFromAccelerometerX_degrees;
    private float rotationAngleFromAccelerometerY_degrees;

    private float rotationAngleFusedX_degrees;
    private float rotationAngleFusedY_degrees;

    public AttitudeSensor(float alpha) {
        super(alpha);
        this.firstGyroscopeSampleX = true;
        this.firstGyroscopeSampleY = true;
        this.rotationAngleAccumulatorFromGyroscopeX_degrees = 0;
        this.instantRotationFromGyroscopeX_degrees = 0;
        this.rotationAngleAccumulatorFromGyroscopeY_degrees = 0;
        this.instantRotationFromGyroscopeY_degrees = 0;
        this.rotationAngleFusedX_degrees = 0;
        this.rotationAngleFusedY_degrees = 0;
    }

    public float getRotationAngleAccumulatorFromGyroscopeX_degrees() {
        return this.rotationAngleAccumulatorFromGyroscopeX_degrees;
    }

    public float getRotationAngleAccumulatorFromGyroscopeY_degrees() {
        return this.rotationAngleAccumulatorFromGyroscopeY_degrees;
    }

    public float getRotationAngleFromAccelerometerX_degrees() {
        return this.rotationAngleFromAccelerometerX_degrees;
    }

    public void setRotationAngleFromAccelerometerX_degrees(float rotationAngleFromAccelerometerX_degrees) {
        this.rotationAngleFromAccelerometerX_degrees = rotationAngleFromAccelerometerX_degrees;
    }

    public float getRotationAngleFromAccelerometerY_degrees() {
        return this.rotationAngleFromAccelerometerY_degrees;
    }

    public void setRotationAngleFromAccelerometerY_degrees(float rotationAngleFromAccelerometerY_degrees) {
        this.rotationAngleFromAccelerometerY_degrees = rotationAngleFromAccelerometerY_degrees;
    }

    private void extractRotationFromGyroscopeX(float currentRotationRateX_degreesPerSec) {
        long currentTimestamp_millisecs = System.currentTimeMillis();

        if (this.firstGyroscopeSampleX) {
            this.firstGyroscopeSampleX = false;
            this.rotationAngleAccumulatorFromGyroscopeX_degrees = 0;
        } else {
            this.instantRotationFromGyroscopeX_degrees = extractInstantRotationFromGyroscopeDegrees(
                    currentRotationRateX_degreesPerSec, currentTimestamp_millisecs,
                    this.previousTimestampGyroscopeSampleX_millisec);
            this.rotationAngleAccumulatorFromGyroscopeX_degrees += this.instantRotationFromGyroscopeX_degrees;

            //Correct for negative or positive value wrapping
            //remove complete spins, reducing the angle to unit circle angle
            this.rotationAngleAccumulatorFromGyroscopeX_degrees = NumberManipulation.removeCompleteSpinsDegrees(
                    this.rotationAngleAccumulatorFromGyroscopeX_degrees);
        }
        //Update previous gyroscope sample and timestamp
        this.previousTimestampGyroscopeSampleX_millisec = currentTimestamp_millisecs;
    }

    //    TODO: need to be called just before lift off
    public void clearRotationAmountFromGyroscopeX() {
        this.rotationAngleAccumulatorFromGyroscopeX_degrees = 0;
    }

    private void extractRotationFromGyroscopeY(float currentRotationRateY_degreesPerSec) {
        long currentTimestamp_millisec;

        currentTimestamp_millisec = System.currentTimeMillis();

        if (this.firstGyroscopeSampleY) {
            this.firstGyroscopeSampleY = false;
            this.rotationAngleAccumulatorFromGyroscopeY_degrees = 0;
        } else {
            this.instantRotationFromGyroscopeY_degrees = extractInstantRotationFromGyroscopeDegrees(
                    currentRotationRateY_degreesPerSec, currentTimestamp_millisec,
                    this.previousTimestampGyroscopeSampleY_millisec);
            this.rotationAngleAccumulatorFromGyroscopeY_degrees += this.instantRotationFromGyroscopeY_degrees;

            //Correct for negative or positive value wrapping
            //remove complete spins, reducing the angle to unit circle angle
            this.rotationAngleAccumulatorFromGyroscopeY_degrees = NumberManipulation.removeCompleteSpinsDegrees(
                    this.rotationAngleAccumulatorFromGyroscopeY_degrees);
        }
        //Update previous gyroscope sample and timestamp
        this.previousTimestampGyroscopeSampleY_millisec = currentTimestamp_millisec;
    }

    //    TODO: maybe its needed to be called just before lift off
    public void clearRotationAmountFromGyroscopeY() {
        this.rotationAngleAccumulatorFromGyroscopeY_degrees = 0;
    }

    private void extractRotationFromGyroscope(float currentRotationX_degreesPerSec,
                                              float currentRotationY_degreesPerSec) {
        extractRotationFromGyroscopeX(currentRotationX_degreesPerSec);
        extractRotationFromGyroscopeY(currentRotationY_degreesPerSec);
    }

    //Extracts roll
    private void extractRotationFromAccelerometerX(float accelerationX_metersPerSecSquared,
                                                   float accelerationY_metersPerSecSquared,
                                                   float accelerationZ_metersPerSecSquared) {
        double referenceAcceleration = Math.sqrt(Math.pow(accelerationZ_metersPerSecSquared, 2) +
                Math.pow(accelerationX_metersPerSecSquared, 2));
        this.rotationAngleFromAccelerometerX_degrees = (float) Math.toDegrees(
                Math.atan2(accelerationY_metersPerSecSquared, referenceAcceleration));
    }

    //Extracts pitch
    private void extractRotationFromAccelerometerY(float accelerationX_metersPerSecSquared,
                                                   float accelerationY_metersPerSecSquared,
                                                   float accelerationZ_metersPerSecSquared) {
        double referenceAcceleration = Math.sqrt(Math.pow(accelerationZ_metersPerSecSquared, 2) +
                Math.pow(accelerationY_metersPerSecSquared, 2));
        this.rotationAngleFromAccelerometerY_degrees = (float) Math.toDegrees(
                Math.atan2(accelerationX_metersPerSecSquared, referenceAcceleration));

        /* In order to get the same magnie rotation, it's needed to invert the signal of the
         * accelerometer Y axis rotation
         */
        this.rotationAngleFromAccelerometerY_degrees *= -1;
    }

    //Reference: http://www.st.com/content/ccc/resource/technical/document/application_note/8e/28/c0/ea/1f/ed/4e/48/CD00268887.pdf/files/CD00268887.pdf/jcr:content/translations/en.CD00268887.pdf
    private void extractRotationFromAccelerometer(float accelerationX_metersPerSecSquared,
                                                  float accelerationY_metersPerSecSquared,
                                                  float accelerationZ_metersPerSecSquared) {
        extractRotationFromAccelerometerX(accelerationX_metersPerSecSquared, accelerationY_metersPerSecSquared,
                accelerationZ_metersPerSecSquared);
        extractRotationFromAccelerometerY(accelerationX_metersPerSecSquared, accelerationY_metersPerSecSquared,
                accelerationZ_metersPerSecSquared);
    }

    public void updateAttitude(float accelerationX_metersPerSecSquared, float accelerationY_metersPerSecSquared,
                               float accelerationZ_metersPerSecSquared, float currentRotationX_degreesPerSec,
                               float currentRotationY_degreesPerSec) {
        extractRotationFromAccelerometer(accelerationX_metersPerSecSquared, accelerationY_metersPerSecSquared,
                accelerationZ_metersPerSecSquared);
        extractRotationFromGyroscope(currentRotationX_degreesPerSec, currentRotationY_degreesPerSec);

        this.rotationAngleFusedX_degrees = fuseReadings(this.instantRotationFromGyroscopeX_degrees,
                this.rotationAngleFromAccelerometerX_degrees, this.rotationAngleFusedX_degrees);
        this.rotationAngleFusedY_degrees = fuseReadings(this.instantRotationFromGyroscopeY_degrees,
                this.rotationAngleFromAccelerometerY_degrees, this.rotationAngleFusedY_degrees);
    }

    public float getRotationAngleFusedX_degrees() {
        return this.rotationAngleFusedX_degrees;
    }

    public float getRotationAngleFusedY_degrees() {
        return this.rotationAngleFusedY_degrees;
    }
}
