package com.example.gildo.quadcoptercontroller.models.entities.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.devices.actuators.ESC;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.GyroscopeInterpreter;
import com.example.gildo.quadcoptercontroller.models.devices.interpreters.MagnetometerInterpreter;
import com.example.gildo.quadcoptercontroller.models.entities.EquilibriumAxis;
import com.example.gildo.quadcoptercontroller.models.entities.Rotor;
import com.example.gildo.quadcoptercontroller.models.constants.AxisName;
import com.example.gildo.quadcoptercontroller.models.constants.RotationDirection;
import com.example.gildo.quadcoptercontroller.models.constants.RotorId;
import com.example.gildo.quadcoptercontroller.models.util.NumberManipulation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by gildo on 20/10/16.
 */
public class DevicesDynamicsHandler {
    public static final String CURRENT_THROTTLE_PERCENT_KEY = "currentThrottle_percent";
    public static final String DESIRED_THROTTLE_PERCENT_KEY = "desiredThrottle_percent";

    private Hashtable<Integer, Rotor> rotors;

    private EquilibriumAxis equilibriumAxisX;
    private EquilibriumAxis equilibriumAxisY;
    private EquilibriumAxis equilibriumAxisZ;

    private float currentThrottle_percentage;
    private float desiredThrottle_percentage;

    private Hashtable<Integer, ArrayList<Float>> uncalibratedGyroscopeReadings; //key=axis, value=arraylist with leveled readings
    private Hashtable<Integer, ArrayList<Float>> uncalibratedMagnetometerReadings; //key=axis, value=arraylist with leveled readings

    private Context context;

    private static DevicesDynamicsHandler instance;

    public static DevicesDynamicsHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DevicesDynamicsHandler(context);
        }

        return instance;
    }

    private DevicesDynamicsHandler(Context context) {
        this.context = context;

        this.rotors = new Hashtable<>();
        this.rotors.put(RotorId.ONE, new Rotor(0f, 0, RotationDirection.CLOCKWISE));
        this.rotors.put(RotorId.TWO, new Rotor(0f, 0, RotationDirection.COUNTERCLOCKWISE));
        this.rotors.put(RotorId.THREE, new Rotor(0f, 0, RotationDirection.CLOCKWISE));
        this.rotors.put(RotorId.FOUR, new Rotor(0f, 0, RotationDirection.COUNTERCLOCKWISE));

        readCalibrationFile(Rotor.ROTORS_CALIBRATION_FILENAME);

        this.equilibriumAxisX = new EquilibriumAxis(AxisName.X);
        this.equilibriumAxisY = new EquilibriumAxis(AxisName.Y);
        this.equilibriumAxisZ = new EquilibriumAxis(AxisName.Z);

        this.currentThrottle_percentage = 0f;
        this.desiredThrottle_percentage = 0f;

        this.uncalibratedGyroscopeReadings = new Hashtable<>();
        this.uncalibratedGyroscopeReadings.put(AxisName.X, new ArrayList<Float>());
        this.uncalibratedGyroscopeReadings.put(AxisName.Y, new ArrayList<Float>());
        this.uncalibratedGyroscopeReadings.put(AxisName.Z, new ArrayList<Float>());

        readCalibrationFile(GyroscopeInterpreter.GYROSCOPE_CALIBRATION_FILENAME);

        this.uncalibratedMagnetometerReadings = new Hashtable<>();
        this.uncalibratedMagnetometerReadings.put(AxisName.X, new ArrayList<Float>());
        this.uncalibratedMagnetometerReadings.put(AxisName.Y, new ArrayList<Float>());

        readCalibrationFile(MagnetometerInterpreter.MAGNETOMETER_CALIBRATION_FILENAME);
    }

    public Hashtable<Integer, Rotor> getRotors() {
        return this.rotors;
    }

    public EquilibriumAxis getEquilibriumAxisX() {
        return this.equilibriumAxisX;
    }

    public EquilibriumAxis getEquilibriumAxisY() {
        return this.equilibriumAxisY;
    }

    public EquilibriumAxis getEquilibriumAxisZ() {
        return this.equilibriumAxisZ;
    }

    public float getDesiredThrottle_percentage() {
        return this.desiredThrottle_percentage;
    }

    public void setDesiredThrottle_percentage(float desiredThrottle_percentage) {
        this.desiredThrottle_percentage = desiredThrottle_percentage;
    }

    public float getCurrentThrottle_percentage() {
        return this.currentThrottle_percentage;
    }

    public void setCurrentThrottle_percentage(float currentThrottle_percentage) {
        this.currentThrottle_percentage = currentThrottle_percentage;
    }

    public Hashtable<Integer, ArrayList<Float>> getUncalibratedGyroscopeReadings() {
        return this.uncalibratedGyroscopeReadings;
    }

    public Hashtable<Integer, ArrayList<Float>> getUncalibratedMagnetometerReadings() {
        return this.uncalibratedMagnetometerReadings;
    }

    public void updateDesiredThrottle(int throttleValue, int maxValue, int minValue) {
        this.desiredThrottle_percentage = NumberManipulation.unitBaseNormalize(throttleValue, maxValue, minValue);
    }

    public void updateDesiredRotorRotationSpeed(int rotorId, int desiredRotationSpeed_pulseWidthMicrosecs) {
        this.rotors.get(rotorId).setDesiredSpeed_pulseWidthMicrosecs(desiredRotationSpeed_pulseWidthMicrosecs);
    }

    public void updateCurrentRotorRotationSpeed(int rotorId, float currentRotationSpeed_rpm) {
        this.rotors.get(rotorId).setCurrentSpeed_rpm(currentRotationSpeed_rpm);
    }

    public void updateDesiredYaw(int yawValue, int maxValue, int minValue) {
        int quadrotorRotationDirection;
        float maxRotation_degrees, rotation_degrees, percentage;

        rotation_degrees = 0;
        maxRotation_degrees = 90;
        percentage = NumberManipulation.unitBaseNormalize(yawValue, maxValue, minValue);

        if (percentage < 0.5) {
            quadrotorRotationDirection = RotationDirection.COUNTERCLOCKWISE;
            percentage = NumberManipulation.unitBaseNormalize(0.5f - percentage, 0.5f, 0);
        } else if (percentage > 0.5) {
            quadrotorRotationDirection = RotationDirection.CLOCKWISE;
            percentage = NumberManipulation.unitBaseNormalize(percentage, 1f, 0.5f);
        } else {
            quadrotorRotationDirection = RotationDirection.NONE;
            percentage = 0f;
        }

        switch (quadrotorRotationDirection) {
            case RotationDirection.NONE:
                rotation_degrees = 0f;
                break;
            case RotationDirection.COUNTERCLOCKWISE:
                rotation_degrees = percentage * maxRotation_degrees;
                break;
            case RotationDirection.CLOCKWISE:
                rotation_degrees = -(percentage * maxRotation_degrees);
                break;
        }

        this.equilibriumAxisZ.setDesiredAxisRotation_degrees(rotation_degrees);
    }

    public void updateCurrentYaw(float heading_degrees) {
        this.equilibriumAxisZ.setCurrentAxisRotation_degrees(heading_degrees);
    }

    public void updateDesiredAttitude(double xInclinationWeight, double yInclinationWeight) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        float maxInclination_degrees = sharedPreferences.getInt(this.context.getString(
                R.string.pref_key_maximum_inclination), 5);
        //Inclination of X axis by N degrees, is the same as Y axis rotated N degrees, and vice-versa
        float yRotation_degrees = maxInclination_degrees * (float) yInclinationWeight;
        float xRotation_degrees = maxInclination_degrees * (float) xInclinationWeight;

        this.equilibriumAxisX.setDesiredAxisRotation_degrees(xRotation_degrees);
        this.equilibriumAxisY.setDesiredAxisRotation_degrees(yRotation_degrees);
    }

    public void updateCurrentAttitude(float xRotation_degrees, float yRotation_degrees) {
        this.equilibriumAxisX.setCurrentAxisRotation_degrees(xRotation_degrees);
        this.equilibriumAxisY.setCurrentAxisRotation_degrees(yRotation_degrees);
    }

    public void addGyroscopeUncalibratedReadings(float uncalibratedReadingX_degreesPerSec, float uncalibratedReadingY_degreesPerSec,
                                                 float uncalibratedReadingZ_degreesPerSec) {
        this.uncalibratedGyroscopeReadings.get(AxisName.X).add(uncalibratedReadingX_degreesPerSec);
        this.uncalibratedGyroscopeReadings.get(AxisName.Y).add(uncalibratedReadingY_degreesPerSec);
        this.uncalibratedGyroscopeReadings.get(AxisName.Z).add(uncalibratedReadingZ_degreesPerSec);
    }

    public void clearGyroscopeUncalibratedReadings() {
        this.uncalibratedGyroscopeReadings.get(AxisName.X).clear();
        this.uncalibratedGyroscopeReadings.get(AxisName.Y).clear();
        this.uncalibratedGyroscopeReadings.get(AxisName.Z).clear();
    }

    public void addMagnetometerUncalibratedReadings(float uncalibratedReadingX_microTeslas, float uncalibratedReadingY_microTeslas) {
        this.uncalibratedMagnetometerReadings.get(AxisName.X).add(uncalibratedReadingX_microTeslas);
        this.uncalibratedMagnetometerReadings.get(AxisName.Y).add(uncalibratedReadingY_microTeslas);
    }

    public void clearMagnetometerUncalibratedReadings() {
        this.uncalibratedMagnetometerReadings.get(AxisName.X).clear();
        this.uncalibratedMagnetometerReadings.get(AxisName.Y).clear();
    }

    private File getCalibrationFile(final String filename) {
        File file = null;
        StringBuilder externalStoragePath;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //TODO: distinguish between non-removable and SD card
            externalStoragePath = new StringBuilder(Environment.getExternalStorageDirectory().getPath());
            file = new File(externalStoragePath.append("/").append(filename).toString());
        }

        return file;
    }

    public void saveCalibrationFile(final String filename) {
        Queue<String> calibrationFileLines;
        File file = getCalibrationFile(filename);
        TextFileHandler textFileHandler = TextFileHandler.getInstance();

        calibrationFileLines = insertCalibrationValuesOnFileLines(filename);
        textFileHandler.saveFile(file, calibrationFileLines, false);
    }

    private void readCalibrationFile(final String filename) {
        File calibrationFile = getCalibrationFile(filename);
        TextFileHandler textFileHandler = TextFileHandler.getInstance();
        Queue<String> calibrationFileLines;

        if (calibrationFile.exists()) {
            calibrationFileLines = textFileHandler.readFileLines(calibrationFile);
            readCalibrationValuesFromFileLines(filename, calibrationFileLines);
        }
    }

    private void readCalibrationValuesFromFileLines(final String filename, Queue<String> fileLines) {
        String[] splittedColumns;
        ArrayList<Float> xAxisReadings;
        ArrayList<Float> yAxisReadings;
        ArrayList<Float> zAxisReadings;
        boolean firstLine = true;
        Rotor currentRotor;
        int increments;
        int currentPulseWidth_microsecs;

        if (filename.equals(GyroscopeInterpreter.GYROSCOPE_CALIBRATION_FILENAME)) {
            xAxisReadings = this.uncalibratedGyroscopeReadings.get(AxisName.X);
            yAxisReadings = this.uncalibratedGyroscopeReadings.get(AxisName.Y);
            zAxisReadings = this.uncalibratedGyroscopeReadings.get(AxisName.Z);
            for (String currentLine : fileLines) {
                splittedColumns = currentLine.split(",");

                if (firstLine) {
                    firstLine = false;
                } else {
                    if (splittedColumns.length == 3) {
                        xAxisReadings.add(Float.parseFloat(splittedColumns[0]));
                        yAxisReadings.add(Float.parseFloat(splittedColumns[1]));
                        zAxisReadings.add(Float.parseFloat(splittedColumns[2]));
                    }
                }
            }
        } else if (filename.equals(Rotor.ROTORS_CALIBRATION_FILENAME)) {
            currentRotor = this.rotors.get(RotorId.ONE);
            if (fileLines.size() == 4) {
                for (String currentLine : fileLines) {
                    splittedColumns = currentLine.split(";");
                    if (splittedColumns.length == 3) {
                        //check rotorId
                        if (splittedColumns[0].equals(this.context.getString(R.string.rotor1_id))) {
                            currentRotor = this.rotors.get(RotorId.ONE);
                        } else if (splittedColumns[0].equals(this.context.getString(R.string.rotor2_id))) {
                            currentRotor = this.rotors.get(RotorId.TWO);
                        } else if (splittedColumns[0].equals(this.context.getString(R.string.rotor3_id))) {
                            currentRotor = this.rotors.get(RotorId.THREE);
                        } else if (splittedColumns[0].equals(this.context.getString(R.string.rotor4_id))) {
                            currentRotor = this.rotors.get(RotorId.FOUR);
                        }

                        increments = Integer.parseInt(splittedColumns[1]);

                        currentPulseWidth_microsecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
                        for (String currentRotationSpeedValue : Arrays.asList(splittedColumns[2].split(","))) {
                            currentRotor.getPulseWidthMicrosecsToRpmHashtable().put(currentPulseWidth_microsecs,
                                    Float.parseFloat(currentRotationSpeedValue));

                            currentPulseWidth_microsecs += increments;
                        }
                    }
                }
            }
        } else if (filename.equals(MagnetometerInterpreter.MAGNETOMETER_CALIBRATION_FILENAME)) {
            xAxisReadings = this.uncalibratedMagnetometerReadings.get(AxisName.X);
            yAxisReadings = this.uncalibratedMagnetometerReadings.get(AxisName.Y);
            for (String currentLine : fileLines) {
                splittedColumns = currentLine.split(",");

                if (firstLine) {
                    firstLine = false;
                } else {
                    if (splittedColumns.length == 2) {
                        xAxisReadings.add(Float.parseFloat(splittedColumns[0]));
                        yAxisReadings.add(Float.parseFloat(splittedColumns[1]));
                    }
                }
            }
        }
    }

    private Queue<String> insertCalibrationValuesOnFileLines(final String filename) {
        StringBuilder stringBuilder;
        Queue<String> fileLines = new ConcurrentLinkedQueue<>();
        ArrayList<Float> xAxisReadings;
        ArrayList<Float> yAxisReadings;
        ArrayList<Float> zAxisReadings = null;
        Rotor currentRotor;
        float currentValue;
        int indexTerminator = 0;

        if (filename.equals(Rotor.ROTORS_CALIBRATION_FILENAME)) {
            for (int rotorId = RotorId.ONE; rotorId <= RotorId.FOUR; rotorId++) {
                currentRotor = this.rotors.get(rotorId);
                stringBuilder = new StringBuilder();

                //Put rotor identification
                switch (rotorId) {
                    case RotorId.ONE:
                        stringBuilder.append(this.context.getString(R.string.rotor1_id)).append(";");
                        break;
                    case RotorId.TWO:
                        stringBuilder.append(this.context.getString(R.string.rotor2_id)).append(";");
                        break;
                    case RotorId.THREE:
                        stringBuilder.append(this.context.getString(R.string.rotor3_id)).append(";");
                        break;
                    case RotorId.FOUR:
                        stringBuilder.append(this.context.getString(R.string.rotor4_id)).append(";");
                        break;
                }
                //Put calibration increments
                stringBuilder.append(Rotor.ROTOR_CALIBRATION_INCREMENT).append(";");

                //Put rotor speed values
                for (int step = ESC.MINIMUM_PULSE_WIDTH_MICROSECS + Rotor.ROTOR_CALIBRATION_INCREMENT;
                     step <= ESC.MAXIMUM_PULSE_WIDTH_MICROSECS; step += Rotor.ROTOR_CALIBRATION_INCREMENT) {
                    currentValue = currentRotor.getPulseWidthMicrosecsToRpmHashtable().get(step);
                    stringBuilder.append(currentValue);
                    if (step != ESC.MAXIMUM_PULSE_WIDTH_MICROSECS) {
                        stringBuilder.append(",");
                    }
                }

                fileLines.add(stringBuilder.toString());
            }
        } else {
            stringBuilder = new StringBuilder();
            //Create header
            stringBuilder.append(this.context.getString(R.string.logTag_axis_x)).append(",");
            stringBuilder.append(this.context.getString(R.string.logTag_axis_y));
            if (filename.equals(GyroscopeInterpreter.GYROSCOPE_CALIBRATION_FILENAME)) {
                stringBuilder.append(",").append(this.context.getString(R.string.logTag_axis_z));

                xAxisReadings = this.uncalibratedGyroscopeReadings.get(AxisName.X);
                yAxisReadings = this.uncalibratedGyroscopeReadings.get(AxisName.Y);
                zAxisReadings = this.uncalibratedGyroscopeReadings.get(AxisName.Z);
            } else {
                xAxisReadings = this.uncalibratedMagnetometerReadings.get(AxisName.X);
                yAxisReadings = this.uncalibratedMagnetometerReadings.get(AxisName.Y);
            }
            fileLines.add(stringBuilder.toString());

            indexTerminator = xAxisReadings.size();
            for (int indexIterator = 0; indexIterator < indexTerminator; indexIterator++) {
                stringBuilder = new StringBuilder();

                stringBuilder.append(xAxisReadings.get(indexIterator)).append(",");
                stringBuilder.append(yAxisReadings.get(indexIterator));
                if (zAxisReadings != null) {
                    //Put gyroscope Z axis readings
                    stringBuilder.append(",").append(zAxisReadings.get(indexIterator));
                }

                fileLines.add(stringBuilder.toString());
            }
        }

        return fileLines;
    }

//    private File rotorsCalibrationGetFile() {
//        File file = null;
//        StringBuilder externalStoragePath;
//
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            //TODO: distinguish between non-removable and SD card
//            externalStoragePath = new StringBuilder(Environment.getExternalStorageDirectory().getPath());
//            file = new File(externalStoragePath.append("/").append(Rotor.ROTORS_CALIBRATION_FILENAME).toString());
//        }
//
//        return file;
//    }

//    public void rotorsCalibrationSaveFile(){
//        Queue<String> rotorsCalibrationFileLines;
//        File file = rotorsCalibrationGetFile();
//        TextFileHandler textFileHandler = TextFileHandler.getInstance();
//
//        rotorsCalibrationFileLines = rotorsCalibrationInsertValuesOnFileLines();
//        textFileHandler.saveFile(file, rotorsCalibrationFileLines, false);
//    }

//    private void rotorsCalibrationReadFile() {
//        File rotorsCalibrationFile = rotorsCalibrationGetFile();
//        TextFileHandler textFileHandler = TextFileHandler.getInstance();
//        Queue<String> rotorsCalibrationFileLines;
//
//        if (rotorsCalibrationFile.exists()) {
//            rotorsCalibrationFileLines = textFileHandler.readFileLines(rotorsCalibrationFile);
//            rotorsCalibrationReadValuesFromFileLines(rotorsCalibrationFileLines);
//        }
//    }

//    private void rotorsCalibrationReadValuesFromFileLines(Queue<String> fileLines) {
//        String[] splittedLines;
//        int increments;
//        int currentPulseWidth_microsecs;
//        Rotor currentRotor = this.rotors.get(RotorId.ONE);
//
//        if (fileLines.size() == 4) {
//            for (String currentLine : fileLines) {
//                splittedLines = currentLine.split(";");
//                //check rotorId
//                if (splittedLines[0].equals(this.context.getString(R.string.rotor1_id))) {
//                    currentRotor = this.rotors.get(RotorId.ONE);
//                } else if (splittedLines[0].equals(this.context.getString(R.string.rotor2_id))) {
//                    currentRotor = this.rotors.get(RotorId.TWO);
//                } else if (splittedLines[0].equals(this.context.getString(R.string.rotor3_id))) {
//                    currentRotor = this.rotors.get(RotorId.THREE);
//                } else if (splittedLines[0].equals(this.context.getString(R.string.rotor4_id))) {
//                    currentRotor = this.rotors.get(RotorId.FOUR);
//                }
//
//                increments = Integer.parseInt(splittedLines[1]);
//
//                currentPulseWidth_microsecs = ESC.MINIMUM_PULSE_WIDTH_MICROSECS;
//                for (String currentRotationSpeedValue : Arrays.asList(splittedLines[2].split(","))) {
//                    currentRotor.getPulseWidthMicrosecsToRpmHashtable().put(currentPulseWidth_microsecs,
//                            Float.parseFloat(currentRotationSpeedValue));
//
//                    currentPulseWidth_microsecs += increments;
//                }
//            }
//        }
//    }

//    private Queue<String> rotorsCalibrationInsertValuesOnFileLines() {
//        StringBuilder stringBuilder;
//        Queue<String> fileLines = new ConcurrentLinkedQueue<>();
//        Rotor currentRotor;
//        float currentValue;
//
//        for (int rotorId = RotorId.ONE; rotorId <= RotorId.FOUR; rotorId++) {
//            currentRotor = this.rotors.get(rotorId);
//
//            stringBuilder = new StringBuilder();
//
//            //Put rotor identification
//            switch (rotorId) {
//                case RotorId.ONE:
//                    stringBuilder.append(this.context.getString(R.string.rotor1_id)).append(";");
//                    break;
//                case RotorId.TWO:
//                    stringBuilder.append(this.context.getString(R.string.rotor2_id)).append(";");
//                    break;
//                case RotorId.THREE:
//                    stringBuilder.append(this.context.getString(R.string.rotor3_id)).append(";");
//                    break;
//                case RotorId.FOUR:
//                    stringBuilder.append(this.context.getString(R.string.rotor4_id)).append(";");
//                    break;
//            }
//            //Put calibration increments
//            stringBuilder.append(Rotor.ROTOR_CALIBRATION_INCREMENT).append(";");
//
//            //Put rotor speed values
//            for (int step = ESC.MINIMUM_PULSE_WIDTH_MICROSECS + Rotor.ROTOR_CALIBRATION_INCREMENT;
//                 step <= ESC.MAXIMUM_PULSE_WIDTH_MICROSECS; step += Rotor.ROTOR_CALIBRATION_INCREMENT) {
//                currentValue = currentRotor.getPulseWidthMicrosecsToRpmHashtable().get(step);
//                stringBuilder.append(currentValue);
//                if (step != ESC.MAXIMUM_PULSE_WIDTH_MICROSECS) {
//                    stringBuilder.append(",");
//                }
//            }
//
//            fileLines.add(stringBuilder.toString());
//        }
//
//        return fileLines;
//    }
}
