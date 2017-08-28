package com.example.gildo.quadcoptercontroller.models.devices.fusedSensors;

import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;

/**
 * Created by gildo on 16/08/16.
 */
public class HorizontalRotationSensor extends ComplementaryFilteredSensorFusion {
    public static final String HORIZONTAL_ROTATION_DEGREES_KEY = "horizontalRotation_degrees";

    private float previousCompassHeading_degrees;
    private boolean firstMagnetometerSample;
    private float rotationAccumulatorFromMagnetometer_degrees;
    private float instantRotationFromMagnetometer_degrees;

    private long previousTimestampGyroscopeSampleZ_millisec;
    private boolean firstGyroscopeSample;
    private float previousGyroscopeSampleZ_degreesPerSec;
    private float rotationAccumulatorFromGyroscopeZ_degrees;
    private float instantRotationFromGyroscopeZ_degrees;

    private float horizontalRotation_degrees;

    public HorizontalRotationSensor(float alpha) {
        super(alpha);
        this.firstMagnetometerSample = true;
        this.firstGyroscopeSample = true;
        this.horizontalRotation_degrees = 0;
    }

    public float getRotationAccumulatorFromMagnetometer_degrees() {
        return this.rotationAccumulatorFromMagnetometer_degrees;
    }

    public float getRotationAccumulatorFromGyroscopeZ_degrees() {
        return this.rotationAccumulatorFromGyroscopeZ_degrees;
    }

    private void extractRotationFromMagnetometer(float currentCompassHeading_degrees) {
        if (this.firstMagnetometerSample) {
            this.firstMagnetometerSample = false;
            this.rotationAccumulatorFromMagnetometer_degrees = 0;
        } else {
            if (this.previousCompassHeading_degrees >= 330f && currentCompassHeading_degrees <= 30f) {
                //Rollover occurred and treatment is done
                this.instantRotationFromMagnetometer_degrees = (360f - this.previousCompassHeading_degrees) + currentCompassHeading_degrees;
            } else if (this.previousCompassHeading_degrees <= 30f && currentCompassHeading_degrees >= 330f) {
                //Rollover occurred and treatment is done
                this.instantRotationFromMagnetometer_degrees = (360f - currentCompassHeading_degrees) + this.previousCompassHeading_degrees;
            } else {
                this.instantRotationFromMagnetometer_degrees = currentCompassHeading_degrees - this.previousCompassHeading_degrees;
            }
            /* In order to get the same magnitude from gyroscope rotation, it's needed to invert the signal of the
             * magnetometer rotation
             */
            this.instantRotationFromMagnetometer_degrees *= -1;
            this.rotationAccumulatorFromMagnetometer_degrees += this.instantRotationFromMagnetometer_degrees;

            //Correct for negative or positive value wrapping
            this.rotationAccumulatorFromMagnetometer_degrees = NumberManipulation.removeCompleteSpinsDegrees(
                    this.rotationAccumulatorFromMagnetometer_degrees);
        }
        //Update previous magnetometer sample
        this.previousCompassHeading_degrees = currentCompassHeading_degrees;
    }

    //    TODO: need to be called just before lift off
    public void clearRotationAmountFromMagnetometer() {
        this.rotationAccumulatorFromMagnetometer_degrees = 0;
    }

    private void extractRotationFromGyroscopeZ(float currentRotationZ_degreesPerSec) {
        long currentTimestamp_millisec;

        currentTimestamp_millisec = System.currentTimeMillis();

        if (this.firstGyroscopeSample) {
            this.firstGyroscopeSample = false;
            this.rotationAccumulatorFromGyroscopeZ_degrees = 0;
        } else {
            this.instantRotationFromGyroscopeZ_degrees = extractInstantRotationFromGyroscopeDegrees(
                    this.previousGyroscopeSampleZ_degreesPerSec, currentTimestamp_millisec,
                    this.previousTimestampGyroscopeSampleZ_millisec);
            this.rotationAccumulatorFromGyroscopeZ_degrees += this.instantRotationFromGyroscopeZ_degrees;

            //Correct for negative or positive value wrapping
            //remove complete spins, reducing the angle to unit circle angle
            this.rotationAccumulatorFromGyroscopeZ_degrees = NumberManipulation.removeCompleteSpinsDegrees(
                    this.rotationAccumulatorFromGyroscopeZ_degrees);
        }
        //Update previous gyroscope sample and timestamp
        this.previousGyroscopeSampleZ_degreesPerSec = currentRotationZ_degreesPerSec;
        this.previousTimestampGyroscopeSampleZ_millisec = currentTimestamp_millisec;
    }

    //    TODO: maybe it's needed to be called just before lift off
    public void clearRotationAmountFromGyroscopeZ() {
        this.rotationAccumulatorFromGyroscopeZ_degrees = 0;
    }

    public void updateHorizontalRotation(float currentCompassHeading_degrees, float currentRotationZ_degreesPerSec) {
        extractRotationFromMagnetometer(currentCompassHeading_degrees);
        extractRotationFromGyroscopeZ(currentRotationZ_degreesPerSec);

        this.horizontalRotation_degrees = fuseReadings(this.instantRotationFromGyroscopeZ_degrees,
                this.rotationAccumulatorFromMagnetometer_degrees, this.horizontalRotation_degrees);
//        //Uses gyroscope as most stable source with alpha = 0,9
//        this.horizontalRotation_degrees = fuseReadings(this.instantRotationFromMagnetometer_degrees,
//                this.rotationAccumulatorFromGyroscopeZ_degrees);
    }

    public float getHorizontalRotation_degrees() {
        return this.horizontalRotation_degrees;
    }
}
