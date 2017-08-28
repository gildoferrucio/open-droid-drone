package com.example.gildo.quadcoptercontroller.models.devices.fusedSensors;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.SonarInterpreter;

/**
 * Created by gildo on 26/09/16.
 */
public class AltimeterSensor extends SensorFusion {
    public static final String RELATIVE_ALTITUDE_METERS_KEY = "relativeAltitude_meters";
    public static final String ABSOLUTE_ALTITUDE_METERS_KEY = "absoluteAltitude_meters";

    private float firstBarometerAltitudeSample_meters;

    private float relativeAltitude_meters;
    private float absoluteAltitude_meters;

    private boolean isfirstAltitudeSample;

    public AltimeterSensor() {
        this.isfirstAltitudeSample = true;
    }

    public void updateAltitude(float currentAltitudeBarometer_meters, float currentAltitudeInferiorSonar_centimeters) {
        if (this.isfirstAltitudeSample) {
            //Normally the first altitude sample will be with the quadrotor on the land, or at least under 6,45
            // meters high
            this.firstBarometerAltitudeSample_meters = currentAltitudeBarometer_meters;
            this.isfirstAltitudeSample = false;

            this.absoluteAltitude_meters = currentAltitudeBarometer_meters;
            this.relativeAltitude_meters = (currentAltitudeInferiorSonar_centimeters / 100f);
        } else {
            if (currentAltitudeInferiorSonar_centimeters != SonarInterpreter.OUT_OF_RANGE) {
                this.relativeAltitude_meters = (currentAltitudeInferiorSonar_centimeters / 100f);
                this.absoluteAltitude_meters = this.firstBarometerAltitudeSample_meters + this.relativeAltitude_meters;
            } else {
                //Needs to be out of range because relative altitude is the altitude in respect to ground
                this.relativeAltitude_meters = SonarInterpreter.OUT_OF_RANGE;
                this.absoluteAltitude_meters = currentAltitudeBarometer_meters;
            }
        }
    }

    public float getRelativeAltitude_meters(){
        return this.relativeAltitude_meters;
    }

    public float getAbsoluteAltitude_meters(){
        return this.absoluteAltitude_meters;
    }
}
