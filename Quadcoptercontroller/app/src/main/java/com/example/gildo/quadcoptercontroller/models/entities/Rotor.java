package com.example.gildo.quadcoptercontroller.models.entities;

import com.example.gildo.quadcoptercontroller.models.devices.actuators.ESC;
import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by gildo on 20/10/16.
 */
public class Rotor {
    public static final String ROTORS_CALIBRATION_FILENAME = "quadcopterControllerRotorsCalibration.txt";

    public static final int ROTOR_CALIBRATION_INCREMENT = 5;

    private float currentSpeed_rpm;
    private int desiredSpeed_pulseWidthMicrosecs;
    private final int rotationDirection;

    private Hashtable<Integer, Float> pulseWidthMicrosecsToRpmHashtable; //key=microsecs, value=RPM

    public Rotor(float currentRotationSpeed_rpm, int desiredRotationSpeed_pulseWidthMicrosecs,
                 int rotationDirection) {
        this.currentSpeed_rpm = currentRotationSpeed_rpm;
        this.desiredSpeed_pulseWidthMicrosecs = desiredRotationSpeed_pulseWidthMicrosecs;
        this.rotationDirection = rotationDirection;
        this.pulseWidthMicrosecsToRpmHashtable = new Hashtable<>();
        //hashtable initialization
        for (int step = ESC.MINIMUM_PULSE_WIDTH_MICROSECS + Rotor.ROTOR_CALIBRATION_INCREMENT;
             step <= ESC.MAXIMUM_PULSE_WIDTH_MICROSECS; step += Rotor.ROTOR_CALIBRATION_INCREMENT) {
            this.pulseWidthMicrosecsToRpmHashtable.put(step, 0f);
        }
    }

    public float getCurrentSpeed_rpm() {
        return this.currentSpeed_rpm;
    }

    public int getDesiredSpeed_pulseWidthMicrosecs() {
        return desiredSpeed_pulseWidthMicrosecs;
    }

    public float getDesiredSpeed_rpm() {
        int roundedSpeed_pulseWidthMicrosecs;
        float desiredSpeed_rpm;

        roundedSpeed_pulseWidthMicrosecs = NumberManipulation.roundToNearestMultiplier(this.desiredSpeed_pulseWidthMicrosecs,
                Rotor.ROTOR_CALIBRATION_INCREMENT);
        if (roundedSpeed_pulseWidthMicrosecs < ESC.MINIMUM_PULSE_WIDTH_MICROSECS) {
            roundedSpeed_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
        }
        desiredSpeed_rpm = this.pulseWidthMicrosecsToRpmHashtable.get(roundedSpeed_pulseWidthMicrosecs);

        return desiredSpeed_rpm;
    }

    public int getRotationDirection() {
        return this.rotationDirection;
    }

    public Hashtable<Integer, Float> getPulseWidthMicrosecsToRpmHashtable() {
        return this.pulseWidthMicrosecsToRpmHashtable;
    }

    public void setCurrentSpeed_rpm(float currentSpeed_rpm) {
        this.currentSpeed_rpm = currentSpeed_rpm;
    }

    public void setDesiredSpeed_pulseWidthMicrosecs(int desiredSpeed_pulseWidthMicrosecs) {
        this.desiredSpeed_pulseWidthMicrosecs = desiredSpeed_pulseWidthMicrosecs;
    }

    public void setPulseWidthMicrosecsToRpmHashtable(Hashtable<Integer, Float> pulseWidthMicrosecsToRpmHashtable) {
        this.pulseWidthMicrosecsToRpmHashtable = pulseWidthMicrosecsToRpmHashtable;
    }

    public int convertRpmToPulseWidth_microsecs(float rotationSpeed_rpm) {
        Iterator<Integer> iteratorPrevious = this.pulseWidthMicrosecsToRpmHashtable.keySet().iterator();
        Iterator<Integer> iteratorSubsequent = this.pulseWidthMicrosecsToRpmHashtable.keySet().iterator();
        int previousPulseWidth_microsecs, subsequentPulseWidth_microsecs;
        float previousSpeed_rpm, subsequentSpeed_rpm;

        previousPulseWidth_microsecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;

        subsequentPulseWidth_microsecs = iteratorSubsequent.next();
        while (iteratorSubsequent.hasNext()) {
            previousPulseWidth_microsecs = iteratorPrevious.next();
            previousSpeed_rpm = this.pulseWidthMicrosecsToRpmHashtable.get(previousPulseWidth_microsecs);
            subsequentPulseWidth_microsecs = iteratorSubsequent.next();
            subsequentSpeed_rpm = this.pulseWidthMicrosecsToRpmHashtable.get(subsequentPulseWidth_microsecs);

            if ((rotationSpeed_rpm >= previousSpeed_rpm) && (rotationSpeed_rpm <= subsequentSpeed_rpm)) {
                return previousPulseWidth_microsecs;
            }
        }

        return previousPulseWidth_microsecs;
    }

    public void updatePulseWidthMicrosecsToRpmHashtable(int pulseWidth_microsecs, float rotationSpeed_rpm) {
        //update rotor calibration hashtable
        this.pulseWidthMicrosecsToRpmHashtable.put(pulseWidth_microsecs, rotationSpeed_rpm);
    }

    public float convertPulseWidthToRpm(float pulseWidth_microsecs) {
        int roundedSpeed_pulseWidthMicrosecs;
        float desiredSpeed_rpm;

        roundedSpeed_pulseWidthMicrosecs = NumberManipulation.roundToNearestMultiplier(pulseWidth_microsecs,
                Rotor.ROTOR_CALIBRATION_INCREMENT);
        if (roundedSpeed_pulseWidthMicrosecs < ESC.MINIMUM_PULSE_WIDTH_MICROSECS) {
            roundedSpeed_pulseWidthMicrosecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
        }
        desiredSpeed_rpm = this.pulseWidthMicrosecsToRpmHashtable.get(roundedSpeed_pulseWidthMicrosecs);

        return desiredSpeed_rpm;
    }
}
