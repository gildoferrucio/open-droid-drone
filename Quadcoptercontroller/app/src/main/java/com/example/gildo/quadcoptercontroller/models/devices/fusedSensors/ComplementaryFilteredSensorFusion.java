package com.example.gildo.quadcoptercontroller.models.devices.fusedSensors;

/**
 * Created by gildo on 16/08/16.
 */
abstract class ComplementaryFilteredSensorFusion extends SensorFusion {
    private float alpha;

    ComplementaryFilteredSensorFusion(float alpha) {
        this.alpha = alpha;
    }

    //Fusion method itself!
    float fuseReadings(float highFrequencyBehavedReading, float lowFrequencyBehavedReading, float lastValue) {
        float result = (this.alpha * (lastValue + highFrequencyBehavedReading)) + ((1f - this.alpha) * lowFrequencyBehavedReading);

        return result;
    }

    /* In a fast and simple analysis this method would be better defined at GyroscopeInterpreter.java, but since every
     * ComplementaryFilteredSensorFusion device is done fusing gyroscope readings with another device reading. So this
     * method is correct to be defined here.
     */
    float extractInstantRotationFromGyroscopeDegrees(float currentRotationRate_degreesPerSec,
                                                     long currentTimestamp_millisecs, long previousTimestamp_millisecs) {
        float instantRotation_degrees;
        long timeDifference_millisecs;

        timeDifference_millisecs = currentTimestamp_millisecs - previousTimestamp_millisecs;
        instantRotation_degrees = currentRotationRate_degreesPerSec * ((float) timeDifference_millisecs / 1000f);

        return instantRotation_degrees;
    }
}