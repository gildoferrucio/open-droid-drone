package com.example.gildo.quadcoptercontroller.models.devices.interpreters;

import com.example.gildo.quadcoptercontroller.models.devices.sensors.Barometer;

/**
 * Created by gildo on 15/04/16.
 */
public class BarometerInterpreter extends SensorInterpreter {
    private int calibration_ac1;
    private int calibration_ac2;
    private int calibration_ac3;
    private int calibration_ac4;
    private int calibration_ac5;
    private int calibration_ac6;
    private int calibration_b1;
    private int calibration_b2;
    private int calibration_mb;
    private int calibration_mc;
    private int calibration_md;

    private long x1;
    private long x2;
    private long x3;
    private long b3;
    private long b4;
    private long b5;
    private long b6;
    private long b7;

    private long realPressure_pascals;
    private long realTemperature_deciCelsius;

    private int temperature_raw;
    private int pressure_raw;

    private float temperature_celsius;
    private float pressure_hectoPascals;
    private float absoluteAltitude_meters;

    private boolean calibrationValuesAlreadySet = false;

    public void setCalibrationValues(int[] calibrationValues) {
        if (!this.calibrationValuesAlreadySet) {
            this.calibration_ac1 = calibrationValues[0];
            this.calibration_ac2 = calibrationValues[1];
            this.calibration_ac3 = calibrationValues[2];
            this.calibration_ac4 = calibrationValues[3];
            this.calibration_ac5 = calibrationValues[4];
            this.calibration_ac6 = calibrationValues[5];
            this.calibration_b1 = calibrationValues[6];
            this.calibration_b2 = calibrationValues[7];
            this.calibration_mb = calibrationValues[8];
            this.calibration_mc = calibrationValues[9];
            this.calibration_md = calibrationValues[10];
            this.calibrationValuesAlreadySet = true;
        }
    }

    public int getCalibration_ac1() {
        return this.calibration_ac1;
    }

    public void setCalibration_ac1(int calibration_ac1) {
        this.calibration_ac1 = calibration_ac1;
    }

    public int getCalibration_ac2() {
        return this.calibration_ac2;
    }

    public void setCalibration_ac2(int calibration_ac2) {
        this.calibration_ac2 = calibration_ac2;
    }

    public int getCalibration_ac3() {
        return this.calibration_ac3;
    }

    public void setCalibration_ac3(int calibration_ac3) {
        this.calibration_ac3 = calibration_ac3;
    }

    public int getCalibration_ac4() {
        return this.calibration_ac4;
    }

    public void setCalibration_ac4(int calibration_ac4) {
        this.calibration_ac4 = calibration_ac4;
    }

    public int getCalibration_ac5() {
        return this.calibration_ac5;
    }

    public void setCalibration_ac5(int calibration_ac5) {
        this.calibration_ac5 = calibration_ac5;
    }

    public int getCalibration_ac6() {
        return this.calibration_ac6;
    }

    public void setCalibration_ac6(int calibration_ac6) {
        this.calibration_ac6 = calibration_ac6;
    }

    public int getCalibration_b1() {
        return this.calibration_b1;
    }

    public void setCalibration_b1(int calibration_b1) {
        this.calibration_b1 = calibration_b1;
    }

    public int getCalibration_b2() {
        return this.calibration_b2;
    }

    public void setCalibration_b2(int calibration_b2) {
        this.calibration_b2 = calibration_b2;
    }

    public int getCalibration_mb() {
        return this.calibration_mb;
    }

    public void setCalibration_mb(int calibration_mb) {
        this.calibration_mb = calibration_mb;
    }

    public int getCalibration_mc() {
        return this.calibration_mc;
    }

    public void setCalibration_mc(int calibration_mc) {
        this.calibration_mc = calibration_mc;
    }

    public int getCalibration_md() {
        return this.calibration_md;
    }

    public void setCalibration_md(int calibration_md) {
        this.calibration_md = calibration_md;
    }

    public int getTemperature_raw() {
        return this.temperature_raw;
    }

    public void setTemperature_raw(int temperature_raw) {
        this.temperature_raw = temperature_raw;
    }

    public int getPressure_raw() {
        return this.pressure_raw;
    }

    public void setPressure_raw(int pressure_raw) {
        this.pressure_raw = pressure_raw;
    }

    public float getPressure_hectoPascals() {
        return this.pressure_hectoPascals;
    }

    public void setPressure_hectoPascals(float pressure_hectoPascals) {
        this.pressure_hectoPascals = pressure_hectoPascals;
    }

    public float getTemperature_celsius() {
        return this.temperature_celsius;
    }

    public void setTemperature_celsius(float temperature_celsius) {
        this.temperature_celsius = temperature_celsius;
    }

    public float getAbsoluteAltitude_meters() {
        return this.absoluteAltitude_meters;
    }

    public void setAbsoluteAltitude_meters(float absoluteAltitude_meters) {
        this.absoluteAltitude_meters = absoluteAltitude_meters;
    }

    public long convertReadingsToDeciCelsius() {
        if (this.calibrationValuesAlreadySet) {
            this.x1 = (this.temperature_raw - this.calibration_ac6) * this.calibration_ac5 / ((int) Math.pow(2, 15));
            this.x2 = this.calibration_mc * ((int) Math.pow(2, 11)) / (this.x1 + this.calibration_md);
            this.b5 = this.x1 + this.x2;
            this.realTemperature_deciCelsius = (this.b5 + 8) / ((int) Math.pow(2, 4));   //°C * 10^-1
        }

        return this.realTemperature_deciCelsius;
    }

    public long convertReadingsToPascals() {
        if (this.calibrationValuesAlreadySet) {
            this.b6 = this.b5 - 4000;
            this.x1 = (this.calibration_b2 * (this.b6 * this.b6 / ((int) Math.pow(2, 12)))) / ((int) Math.pow(2, 11));
            this.x2 = this.calibration_ac2 * this.b6 / ((int) Math.pow(2, 11));
            this.x3 = this.x1 + this.x2;
            this.b3 = (((this.calibration_ac1 * 4 + this.x3) << Barometer.OVERSAMPLING) + 2) / 4;
            this.x1 = this.calibration_ac3 * this.b6 / ((int) Math.pow(2, 13));
            this.x2 = (this.calibration_b1 * (this.b6 * this.b6 / ((int) Math.pow(2, 12)))) / ((int) Math.pow(2, 16));
            this.x3 = ((this.x1 + this.x2) + 2) / ((int) Math.pow(2, 2));
            this.b4 = this.calibration_ac4 * (this.x3 + 32768) / ((int) Math.pow(2, 15));
            this.b7 = (this.pressure_raw - this.b3) * (50000 >> Barometer.OVERSAMPLING);
            //TODO: verify the condition of if clause, because java considers the msb of byte as signed
            if ((this.b7) < ((long) 0x80000000)) {
                this.realPressure_pascals = (this.b7 * 2) / this.b4;
            } else {
                this.realPressure_pascals = (this.b7 / this.b4) * 2;
            }
            this.x1 = (int) Math.pow((this.realPressure_pascals / ((int) Math.pow(2, 8))), 2);
            this.x1 = (this.x1 * 3038) / ((int) Math.pow(2, 16));
            this.x2 = (-7357 * this.realPressure_pascals) / ((int) Math.pow(2, 16));
            this.realPressure_pascals = this.realPressure_pascals + (this.x1 + this.x2 + 3791) / ((int) Math.pow(2, 4));
            //Pa
        }

        return this.realPressure_pascals;
    }

    public float convertTemperatureToCelsius() {
        float temperature_celsius;

        temperature_celsius = this.realTemperature_deciCelsius / 10f;

        this.temperature_celsius = temperature_celsius;

        return temperature_celsius;
    }

    public float convertPressureToHectoPascals() {
        float pressure_hpa;

        pressure_hpa = this.realPressure_pascals * 0.01f;

        this.pressure_hectoPascals = pressure_hpa;

        return pressure_hpa;
    }

    public float convertReadingsToAltitudeMeters() {
        float altitude;

        altitude = 44330f * (1 - ((float) Math.pow(this.realPressure_pascals / Barometer
                .AIR_PRESSURE_SEA_LEVEL_PASCALS, 1f / 5.255)));

        this.absoluteAltitude_meters = altitude;

        return altitude;
    }
}
