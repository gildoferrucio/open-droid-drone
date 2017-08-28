package com.example.gildo.quadcoptercontroller.models.entities;

/**
 * Created by gildo on 23/08/16.
 */
public class PIDControl {
    private static final int RECTANGULAR_INTEGRATION_METHOD = 0;
    private static final int TRAPEZOIDAL_INTEGRATION_METHOD = 1;

    private float kProportional;
    private float kIntegrative;
    private float kDerivative;

    private float target;
    private float feedback;
    private float error;
    private float previousError;
    private float integrativeAccumulator;
    private float previousIntegrativeComponent;

    private float controlVariable;

    private float minimumOutput;
    private float maximumOutput;

    private float samplePeriod_millisecs;

    public PIDControl(float kProportional, float kIntegrative, float kDerivative, float minimumOutput,
                      float maximumOutput, float samplePeriod_millisecs) {
        this.samplePeriod_millisecs = samplePeriod_millisecs;
        updateCoefficients(kProportional, kIntegrative, kDerivative);
//        this.kProportional = kProportional;
//        this.kIntegrative = kIntegrative;
//        this.kDerivative = kDerivative;
        this.minimumOutput = minimumOutput;
        this.maximumOutput = maximumOutput;
        this.integrativeAccumulator = 0;
        this.previousIntegrativeComponent = 0;
        this.previousError = 0;
    }

    private void setSamplePeriod_millisecs(float samplePeriod_millisecs) {
        this.samplePeriod_millisecs = samplePeriod_millisecs;
        updateCoefficients(this.kProportional, this.kIntegrative, this.kDerivative);
    }

    private void updateCoefficients(float kProportional, float kIntegrative, float kDerivative) {
        float sampleTime_secs = this.samplePeriod_millisecs / 1000f;
        this.kProportional = kProportional;
        this.kIntegrative = kIntegrative * sampleTime_secs;
        this.kDerivative = kDerivative / sampleTime_secs;
    }

    private void updateError() {
        this.previousError = this.error;
        this.error = this.target - this.feedback;
    }

    private float calculateProportionalComponent() {
        float proportionalComponent = this.kProportional * this.error;

        return proportionalComponent;
    }

    private float calculateIntegrativeComponent(final int integrationMethod) {
        float integrativeComponent = 0f;

        switch (integrationMethod) {
            case TRAPEZOIDAL_INTEGRATION_METHOD:
                //Uses trapezoidal integration approximation
                //Reference: http://www.marcelomaciel.com/2012/05/pid-digital-para-pic.html
                if (this.kIntegrative != 0) {
                    integrativeComponent = this.previousIntegrativeComponent + (this.kIntegrative * (this.previousError + this.error) / 2f);

                    if (integrativeComponent < this.minimumOutput) {
                        integrativeComponent = this.minimumOutput;
                    } else if (integrativeComponent > this.maximumOutput) {
                        integrativeComponent = this.maximumOutput;
                    }

                    this.previousIntegrativeComponent = integrativeComponent;
                }
                break;
            case RECTANGULAR_INTEGRATION_METHOD:
                //Uses rectangular integration approximation
                //Reference: http://act.rasip.fer.hr/materijali/11/pid_Control_15_1_2007.pdf
                this.integrativeAccumulator += error;

                if (this.integrativeAccumulator < this.minimumOutput) {
                    this.integrativeAccumulator = this.minimumOutput;
                } else if (this.integrativeAccumulator > this.maximumOutput) {
                    this.integrativeAccumulator = this.maximumOutput;
                }

                integrativeComponent = this.kIntegrative * this.integrativeAccumulator;
                break;
        }

        return integrativeComponent;
    }

    private float calculateDerivativeComponent() {
        //Uses Euler Backward Method
        //Reference: http://www.mic-journal.no/PDF/ref/Haugen2010.pdf
        float derivativeComponent = this.kDerivative * (this.error - this.previousError);

        return derivativeComponent;
    }

    public float calculateControlVariable(float target, float feedback, long samplePeriod_millisecs) {
        this.target = target;
        this.feedback = feedback;

        this.updateError();

//        this.setSamplePeriod_millisecs(samplePeriod_millisecs);

        this.controlVariable = calculateProportionalComponent() +
                calculateIntegrativeComponent(TRAPEZOIDAL_INTEGRATION_METHOD) + calculateDerivativeComponent();

        if (this.controlVariable < this.minimumOutput) {
            this.controlVariable = this.minimumOutput;
        } else if (this.controlVariable > this.maximumOutput) {
            this.controlVariable = this.maximumOutput;
        }

        return this.controlVariable;
    }
}
