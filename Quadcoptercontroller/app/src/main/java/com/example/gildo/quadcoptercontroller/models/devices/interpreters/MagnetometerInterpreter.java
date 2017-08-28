package com.example.gildo.quadcoptercontroller.models.devices.interpreters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.example.gildo.quadcoptercontroller.models.devices.sensors.Magnetometer;
import com.example.gildo.quadcoptercontroller.models.constants.AxisName;
import com.example.gildo.quadcoptercontroller.models.constants.Cardinal;
import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by gildo on 12/04/16.
 */
public class MagnetometerInterpreter extends SensorInterpreter {
    //    final private int REQUEST_CODE_ASK_PERMISSIONS = 11;
    public static final String MAGNETOMETER_CALIBRATION_FILENAME = "quadcopterControllerMagnetometerCalibration.txt";
    public static final int CALIBRATION_STEPS = 25;

    private int magneticFieldX_raw;
    private int magneticFieldY_raw;
    private int magneticFieldZ_raw;

    private float magneticFieldX_microTeslas;
    private float magneticFieldY_microTeslas;
    private float magneticFieldZ_microTeslas;

    private float compassHeading_degrees;

    private float magneticDeclination_degrees;

    private Hashtable<Integer, Float> previousFilteredValues_scaled;
    private Hashtable<Integer, Float> currentUnfilteredValues_scaled;
    private Hashtable<Integer, Float> currentFilteredValues_scaled;

    private float offsetX_microTeslas = 0;
    private float offsetY_microTeslas = 0;

    public int getMagneticFieldX_raw() {
        return this.magneticFieldX_raw;
    }

    public void setMagneticFieldX_raw(int magneticFieldX_raw) {
        this.magneticFieldX_raw = magneticFieldX_raw;
    }

    public int getMagneticFieldY_raw() {
        return this.magneticFieldY_raw;
    }

    public void setMagneticFieldY_raw(int magneticFieldY_raw) {
        this.magneticFieldY_raw = magneticFieldY_raw;
    }

    public int getMagneticFieldZ_raw() {
        return this.magneticFieldZ_raw;
    }

    public void setMagneticFieldZ_raw(int magneticFieldZ_raw) {
        this.magneticFieldZ_raw = magneticFieldZ_raw;
    }

    public float getMagneticFieldX_microTeslas() {
        return this.magneticFieldX_microTeslas;
    }

    public void setMagneticFieldX_microTeslas(float magneticFieldX_microTeslas) {
        this.magneticFieldX_microTeslas = magneticFieldX_microTeslas;
    }

    public float getMagneticFieldY_microTeslas() {
        return this.magneticFieldY_microTeslas;
    }

    public void setMagneticFieldY_microTeslas(float magneticFieldY_microTeslas) {
        this.magneticFieldY_microTeslas = magneticFieldY_microTeslas;
    }

    public float getMagneticFieldZ_microTeslas() {
        return this.magneticFieldZ_microTeslas;
    }

    public void setMagneticFieldZ_microTeslas(float magneticFieldZ_microTeslas) {
        this.magneticFieldZ_microTeslas = magneticFieldZ_microTeslas;
    }

    private void setMagneticDeclination_degrees(Location location) {
        float latitude = (float) location.getLatitude();
        float longitude = (float) location.getLongitude();
        float altitude = (float) location.getAltitude();

        GeomagneticField geomagneticField = new GeomagneticField(latitude, longitude, altitude,
                System.currentTimeMillis());
        this.magneticDeclination_degrees = geomagneticField.getDeclination();
    }

    private float getMagneticDeclination_degrees() {
        return this.magneticDeclination_degrees;
    }

    public float getCompassHeading_degrees() {
        return this.compassHeading_degrees;
    }

    public void setCompassHeading_degrees(float compassHeading_degrees) {
        this.compassHeading_degrees = compassHeading_degrees;
    }

    public Hashtable<Integer, Float> getCurrentFilteredValues_scaled() {
        return this.currentFilteredValues_scaled;
    }

    public float convertReadingToGauss(int reading) {
        float result = Magnetometer.RESOLUTION_GAUSS_PER_LSB * reading;

        return result;
    }

    public float convertReadingToMicroTeslas(int reading) {
        float result = Magnetometer.RESOLUTION_GAUSS_PER_LSB * reading * 100f;

        return result;
    }

    public float convertReadingToTeslas(int reading) {
        float result = Magnetometer.RESOLUTION_GAUSS_PER_LSB * reading * 0.0001f;

        return result;
    }

    public float convertGaussToTeslas(float readingGauss) {
        float result;

        result = readingGauss * 0.0001f;

        return result;
    }

    public float calculateCompassHeadingDegrees(int gravityOnAxis, Context context, boolean filtered) {
        float compassHeading_degrees = 0;
        float magneticFieldA, magneticFieldB;

        setMagneticDeclination_degrees(getCurrentLocation(context));

        // Filtered values are already scaled, thus there's no need to convert them to some measurement unit
        switch (gravityOnAxis) {
            case AxisName.X:
                if (filtered) {
                    magneticFieldA = this.currentFilteredValues_scaled.get(AxisName.Y);
                    magneticFieldB = this.currentFilteredValues_scaled.get(AxisName.Z);
                } else {
                    magneticFieldA = convertReadingToMicroTeslas(this.magneticFieldY_raw);
                    magneticFieldB = convertReadingToMicroTeslas(this.magneticFieldZ_raw);
                }
                break;
            case AxisName.Y:
                if (filtered) {
                    magneticFieldA = this.currentFilteredValues_scaled.get(AxisName.Z);
                    magneticFieldB = this.currentFilteredValues_scaled.get(AxisName.X);
                } else {
                    magneticFieldA = convertReadingToMicroTeslas(this.magneticFieldZ_raw);
                    magneticFieldB = convertReadingToMicroTeslas(this.magneticFieldX_raw);
                }
                break;
            case AxisName.Z:
            default:
                if (filtered) {
                    magneticFieldA = this.currentFilteredValues_scaled.get(AxisName.Y);
                    magneticFieldB = this.currentFilteredValues_scaled.get(AxisName.X);
                } else {
                    magneticFieldA = convertReadingToMicroTeslas(this.magneticFieldY_raw);
                    magneticFieldB = convertReadingToMicroTeslas(this.magneticFieldX_raw);
                }
                break;
        }
        compassHeading_degrees = (float) Math.toDegrees(Math.atan2(magneticFieldA, magneticFieldB));

        compassHeading_degrees = compassHeading_degrees + this.magneticDeclination_degrees;

        //Correct for negative or positive value wrapping
        //remove complete spins, reducing the angle to unit circle angle
        compassHeading_degrees = NumberManipulation.removeCompleteSpinsDegrees(compassHeading_degrees);
        compassHeading_degrees = NumberManipulation.transformNegativeAngleToPositiveDegrees(compassHeading_degrees);

        this.compassHeading_degrees = compassHeading_degrees;

        return this.compassHeading_degrees;
    }

    public int getCompassHeadingCardinalNameResourceId(float compassHeading_deegres) {
        int cardinal = Cardinal.getCardinalFromCompassHeading(compassHeading_deegres);

        return Cardinal.getNameResourceId(cardinal);
    }

    //The app needs to request the locations permission on it's start
    private Location getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        int permissionFineLocation, permissionCoarseLocation;

        permissionFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
//        permissionCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionFineLocation == PackageManager.PERMISSION_GRANTED) {
            // Find most accurate last known location
            for (int iterator = providers.size() - 1; iterator >= 0; iterator--) {
                location = locationManager.getLastKnownLocation(providers.get(iterator));
                if (location != null)
                    break;
            }
        }

        return location;
    }

    //TODO: implement cutoffFrequency_hertz!!
    public Hashtable<Integer, Float> applyLowPassFilterRC(float cutoffFrequency_hertz, float dt_seconds,
                                                          float currentUnfilteredX_scaled,
                                                          float currentUnfilteredY_scaled,
                                                          float currentUnfilteredZ_scaled, boolean firstSample) {
        //dt_seconds ---> Time interval between samples
        float currentFilteredValue;
        float rc = 1f / (cutoffFrequency_hertz * 2f * (float) Math.PI);
        float alpha = dt_seconds / (rc + dt_seconds);


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
        } else {
            this.currentUnfilteredValues_scaled.put(AxisName.X, currentUnfilteredX_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Y, currentUnfilteredY_scaled);
            this.currentUnfilteredValues_scaled.put(AxisName.Z, currentUnfilteredZ_scaled);

            for (int axis = AxisName.X; axis <= AxisName.Z; axis++) {
                currentFilteredValue = this.previousFilteredValues_scaled.get(axis) + (alpha *
                        (this.currentUnfilteredValues_scaled.get(axis) - this.previousFilteredValues_scaled.get(axis)));
                this.currentFilteredValues_scaled.put(axis, currentFilteredValue);

                //update previous values with current ones
                this.previousFilteredValues_scaled.put(axis, currentFilteredValue);
            }
        }

        return this.currentFilteredValues_scaled;
    }

    public void calculateOffset(ArrayList<Float> uncalibratedReadingsX_microTeslas,
                                ArrayList<Float> uncalibratedReadingsY_microTeslas) {
        float uncalibratedReadingsAverageX, uncalibratedReadingsAverageY, lowestX, biggestX, lowestY, biggestY;

        lowestX = lowestY = 999999999;
        biggestX = biggestY = -lowestX;

        if (uncalibratedReadingsX_microTeslas.size() != 0) {
            for (float currentReading : uncalibratedReadingsX_microTeslas) {
                if( currentReading < lowestX){
                    lowestX = currentReading;
                }
                if( currentReading > biggestX){
                    biggestX = currentReading;
                }
            }
            uncalibratedReadingsAverageX = (biggestX + lowestX) / 2 ;
        } else {
            uncalibratedReadingsAverageX = 0;
        }

        if (uncalibratedReadingsY_microTeslas.size() != 0) {
            for (float currentReading : uncalibratedReadingsY_microTeslas) {
                if( currentReading < lowestY){
                    lowestY = currentReading;
                }
                if( currentReading > biggestY){
                    biggestY = currentReading;
                }
            }
            uncalibratedReadingsAverageY = (biggestY + lowestY) / 2 ;
        } else {
            uncalibratedReadingsAverageY = 0;
        }

        this.offsetX_microTeslas = uncalibratedReadingsAverageX;
        this.offsetY_microTeslas = uncalibratedReadingsAverageY;
    }

    public float removeOffset(final int axis, float reading_microTeslas) {
        float cleanReading = 0;

        switch (axis) {
            case AxisName.X:
                cleanReading = reading_microTeslas - this.offsetX_microTeslas;
                break;
            case AxisName.Y:
                cleanReading = reading_microTeslas - this.offsetY_microTeslas;
                break;
        }

        return cleanReading;
    }
}
