package com.example.gildo.quadcoptercontroller.models.devices.sensors;

import com.example.gildo.quadcoptercontroller.models.devices.I2CDevice;
import com.example.gildo.quadcoptercontroller.models.devices.TwoWireInterfaceController;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 15/04/16.
 */
public class Barometer extends I2CDevice {
    public static final String AIR_PRESSURE_HECTOPASCALS_KEY = "airPressure_hectoPascals";
    public static final String AIR_PRESSURE_RAW_KEY = "airPressure_raw";
    public static final String TEMPERATURE_CELSIUS_KEY = "temperature_celsius";
    public static final String TEMPERATURE_RAW_KEY = "temperature_raw";
    public static final String ABSOLUTE_ALTITUDE_METERS_KEY = "barometerAbsoluteAltitude_meters";
    public static final String CALIBRATION_REGISTERS_KEY = "calibrationRegisters";

    public static final float AIR_PRESSURE_SEA_LEVEL_PASCALS = 101325f;

    public static final int OVERSAMPLING = 0;

    private static final int DEVICE_ADDRESS_7_BIT_FORMAT = 0x77;

    private static final byte MEMORY_ADDRESS_CALIBRATION_AC1_MSB = (byte) 0xAA;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC1_LSB = (byte) 0xAB;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC2_MSB = (byte) 0xAC;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC2_LSB = (byte) 0xAD;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC3_MSB = (byte) 0xAE;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC3_LSB = (byte) 0xAF;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC4_MSB = (byte) 0xB0;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC4_LSB = (byte) 0xB1;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC5_MSB = (byte) 0xB2;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC5_LSB = (byte) 0xB3;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC6_MSB = (byte) 0xB4;
    private static final byte MEMORY_ADDRESS_CALIBRATION_AC6_LSB = (byte) 0xB5;
    private static final byte MEMORY_ADDRESS_CALIBRATION_B1_MSB = (byte) 0xB6;
    private static final byte MEMORY_ADDRESS_CALIBRATION_B1_LSB = (byte) 0xB7;
    private static final byte MEMORY_ADDRESS_CALIBRATION_B2_MSB = (byte) 0xB8;
    private static final byte MEMORY_ADDRESS_CALIBRATION_B2_LSB = (byte) 0xB9;
    private static final byte MEMORY_ADDRESS_CALIBRATION_MB_MSB = (byte) 0xBA;
    private static final byte MEMORY_ADDRESS_CALIBRATION_MB_LSB = (byte) 0xBB;
    private static final byte MEMORY_ADDRESS_CALIBRATION_MC_MSB = (byte) 0xBC;
    private static final byte MEMORY_ADDRESS_CALIBRATION_MC_LSB = (byte) 0xBD;
    private static final byte MEMORY_ADDRESS_CALIBRATION_MD_MSB = (byte) 0xBE;
    private static final byte MEMORY_ADDRESS_CALIBRATION_MD_LSB = (byte) 0xBF;

    private int calibration_ac1_value;
    private int calibration_ac2_value;
    private int calibration_ac3_value;
    private int calibration_ac4_value;
    private int calibration_ac5_value;
    private int calibration_ac6_value;
    private int calibration_b1_value;
    private int calibration_b2_value;
    private int calibration_mb_value;
    private int calibration_mc_value;
    private int calibration_md_value;

    private static final byte MEMORY_ADDRESS_COMMAND_RECEPTION = (byte) 0xF4;
    private static final byte COMMAND_TEMPERATURE_VALUE = 0x2E;
    private static final byte COMMAND_AIR_PRESSURE_VALUE = 0x34;

    private static final byte MEMORY_ADDRESS_OUTPUT_MSB = (byte) 0xF6;
    private static final byte MEMORY_ADDRESS_OUTPUT_LSB = (byte) 0xF7;
    private static final byte MEMORY_ADDRESS_OUTPUT_XLSB = (byte) 0xF8;

    private byte[] responseTwoBytes;
    private byte[] responseThreeBytes;
    private byte[] requestValues_output_msb;

    private int[] calibrationRegisters;

    public Barometer(TwoWireInterfaceController twoWireInterface) throws ConnectionLostException {
        super(twoWireInterface);
        this.setDeviceAddress(DEVICE_ADDRESS_7_BIT_FORMAT);
        this.responseTwoBytes = new byte[2];
        this.responseThreeBytes = new byte[3];
        this.requestValues_output_msb = new byte[]{MEMORY_ADDRESS_OUTPUT_MSB};
        this.calibrationRegisters = new int[11];
    }

    public int getCalibration_ac1_value() {
        return this.calibration_ac1_value;
    }

    public int getCalibration_ac2_value() {
        return this.calibration_ac2_value;
    }

    public int getCalibration_ac3_value() {
        return this.calibration_ac3_value;
    }

    public int getCalibration_ac4_value() {
        return this.calibration_ac4_value;
    }

    public int getCalibration_ac5_value() {
        return this.calibration_ac5_value;
    }

    public int getCalibration_ac6_value() {
        return this.calibration_ac6_value;
    }

    public int getCalibration_b1_value() {
        return this.calibration_b1_value;
    }

    public int getCalibration_b2_value() {
        return this.calibration_b2_value;
    }

    public int getCalibration_mb_value() {
        return this.calibration_mb_value;
    }

    public int getCalibration_mc_value() {
        return this.calibration_mc_value;
    }

    public int getCalibration_md_value() {
        return this.calibration_md_value;
    }

    @Override
    public boolean initializeDevice() throws ConnectionLostException, InterruptedException {
        boolean itsConfigured = true;
        int[] calibrationRegistersA = new int[6];
        int[] calibrationRegistersB = new int[2];
        int[] calibrationRegistersM = new int[3];


        itsConfigured = itsConfigured && readCalibrationRegistersAc(calibrationRegistersA);
        itsConfigured = itsConfigured && readCalibrationRegistersB(calibrationRegistersB);
        itsConfigured = itsConfigured && readCalibrationRegistersM(calibrationRegistersM);

        this.calibrationRegisters[0] = this.calibration_ac1_value = calibrationRegistersA[0];
        this.calibrationRegisters[1] = this.calibration_ac2_value = calibrationRegistersA[1];
        this.calibrationRegisters[2] = this.calibration_ac3_value = calibrationRegistersA[2];
        this.calibrationRegisters[3] = this.calibration_ac4_value = calibrationRegistersA[3];
        this.calibrationRegisters[4] = this.calibration_ac5_value = calibrationRegistersA[4];
        this.calibrationRegisters[5] = this.calibration_ac6_value = calibrationRegistersA[5];

        this.calibrationRegisters[6] = this.calibration_b1_value = calibrationRegistersB[0];
        this.calibrationRegisters[7] = this.calibration_b2_value = calibrationRegistersB[1];

        this.calibrationRegisters[8] = this.calibration_mb_value = calibrationRegistersM[0];
        this.calibrationRegisters[9] = this.calibration_mc_value = calibrationRegistersM[1];
        this.calibrationRegisters[10] = this.calibration_md_value = calibrationRegistersM[2];

        return itsConfigured;
    }

    public int[] getCalibrationRegisters() {
        return this.calibrationRegisters;
//        return new int[]{this.calibration_ac1_value, this.calibration_ac2_value, this.calibration_ac3_value,
//                this.calibration_ac4_value, this.calibration_ac5_value, this.calibration_ac6_value,
//                this.calibration_b1_value, this.calibration_b2_value, this.calibration_mb_value,
//                this.calibration_mc_value, this.calibration_md_value};
    }

    public boolean readCalibrationRegistersAc(int[] calibrationRegisters) throws ConnectionLostException, InterruptedException {
        int calibration_ac1 = 0;
        int calibration_ac2 = 0;
        int calibration_ac3 = 0;
        int calibration_ac4 = 0;
        int calibration_ac5 = 0;
        int calibration_ac6 = 0;
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_CALIBRATION_AC1_MSB};
        byte[] response = new byte[12];
        byte ac1MsbReading;
        byte ac1LsbReading;
        byte ac2MsbReading;
        byte ac2LsbReading;
        byte ac3MsbReading;
        byte ac3LsbReading;
        byte ac4MsbReading;
        byte ac4LsbReading;
        byte ac5MsbReading;
        byte ac5LsbReading;
        byte ac6MsbReading;
        byte ac6LsbReading;
        boolean readOk = true;

        try {
            readOk = getTwoWireInterface().sendReadRequest(getDeviceAddress(), requestValues, response);
            ac1MsbReading = response[0];
            ac1LsbReading = response[1];
            ac2MsbReading = response[2];
            ac2LsbReading = response[3];
            ac3MsbReading = response[4];
            ac3LsbReading = response[5];
            ac4MsbReading = response[6];
            ac4LsbReading = response[7];
            ac5MsbReading = response[8];
            ac5LsbReading = response[9];
            ac6MsbReading = response[10];
            ac6LsbReading = response[11];

            calibration_ac1 = concatenateTwoBytesToInt(ac1MsbReading, ac1LsbReading);
            calibration_ac2 = concatenateTwoBytesToInt(ac2MsbReading, ac2LsbReading);
            calibration_ac3 = concatenateTwoBytesToInt(ac3MsbReading, ac3LsbReading);
            calibration_ac4 = concatenateTwoBytesToIntUnsigned(ac4MsbReading, ac4LsbReading);
            calibration_ac5 = concatenateTwoBytesToIntUnsigned(ac5MsbReading, ac5LsbReading);
            calibration_ac6 = concatenateTwoBytesToIntUnsigned(ac6MsbReading, ac6LsbReading);
//            /* Did that to force calibration_ac4, calibration_ac5 and calibration_ac6 to its MSBs be iterpreted as
//             * unsigned. What happens on concatenateThreeBytesToInt.
//             */
//            calibration_ac4 = concatenateThreeBytesToInt((byte) 0x00, ac4MsbReading, ac4LsbReading);
//            calibration_ac5 = concatenateThreeBytesToInt((byte) 0x00, ac5MsbReading, ac5LsbReading);
//            calibration_ac6 = concatenateThreeBytesToInt((byte) 0x00, ac6MsbReading, ac6LsbReading);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        calibrationRegisters[0] = calibration_ac1;
        calibrationRegisters[1] = calibration_ac2;
        calibrationRegisters[2] = calibration_ac3;
        calibrationRegisters[3] = calibration_ac4;
        calibrationRegisters[4] = calibration_ac5;
        calibrationRegisters[5] = calibration_ac6;

        return readOk;
    }

    public boolean readCalibrationRegistersB(int[] calibrationRegisters) throws ConnectionLostException, InterruptedException {
        int calibration_b1 = 0;
        int calibration_b2 = 0;
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_CALIBRATION_B1_MSB};
        byte[] response = new byte[4];
        byte b1MsbReading;
        byte b1LsbReading;
        byte b2MsbReading;
        byte b2LsbReading;
        boolean readOk = true;

        try {
            readOk = getTwoWireInterface().sendReadRequest(getDeviceAddress(), requestValues, response);
            b1MsbReading = response[0];
            b1LsbReading = response[1];
            b2MsbReading = response[2];
            b2LsbReading = response[3];

            calibration_b1 = concatenateTwoBytesToInt(b1MsbReading, b1LsbReading);
            calibration_b2 = concatenateTwoBytesToInt(b2MsbReading, b2LsbReading);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        calibrationRegisters[0] = calibration_b1;
        calibrationRegisters[1] = calibration_b2;

        return readOk;
    }

    public boolean readCalibrationRegistersM(int[] calibrationRegisters) throws ConnectionLostException, InterruptedException {
        int calibration_mb = 0;
        int calibration_mc = 0;
        int calibration_md = 0;
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_CALIBRATION_MB_MSB};
        byte[] response = new byte[6];
        byte mbMsbReading;
        byte mbLsbReading;
        byte mcMsbReading;
        byte mcLsbReading;
        byte mdMsbReading;
        byte mdLsbReading;
        boolean readOk = true;

        try {
            readOk = getTwoWireInterface().sendReadRequest(getDeviceAddress(), requestValues, response);
            mbMsbReading = response[0];
            mbLsbReading = response[1];
            mcMsbReading = response[2];
            mcLsbReading = response[3];
            mdMsbReading = response[4];
            mdLsbReading = response[5];

            calibration_mb = concatenateTwoBytesToInt(mbMsbReading, mbLsbReading);
            calibration_mc = concatenateTwoBytesToInt(mcMsbReading, mcLsbReading);
            calibration_md = concatenateTwoBytesToInt(mdMsbReading, mdLsbReading);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        calibrationRegisters[0] = calibration_mb;
        calibrationRegisters[1] = calibration_mc;
        calibrationRegisters[2] = calibration_md;

        return readOk;
    }

    public boolean sendReadTemperatureCommand() throws ConnectionLostException, InterruptedException {
        /* Actually to read temperature we need to write our desired information on commandReceptionRegister.
         * Then need to wait 4.5ms and read the values on the outputRegisters.
         */
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_COMMAND_RECEPTION, COMMAND_TEMPERATURE_VALUE};
        boolean writeOk = false;

        try {
            writeOk = getTwoWireInterface().sendWriteRequest(getDeviceAddress(), requestValues);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        return writeOk;
    }

    public boolean sendReadAirPressureCommand() throws ConnectionLostException, InterruptedException {
        /* Actually to read pressure we need to write our desired information on commandReceptionRegister.
         * Then need to wait 4.5ms and read the values on the outputRegisters.
         */
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_COMMAND_RECEPTION, COMMAND_AIR_PRESSURE_VALUE};
        boolean writeOk = false;

        try {
            writeOk = getTwoWireInterface().sendWriteRequest(getDeviceAddress(), requestValues);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        return writeOk;
    }

    public int readTemperature() throws ConnectionLostException, InterruptedException {
        int temperature = readOutputRegistersTwoBytes();

        return temperature;
    }

    private int readOutputRegistersTwoBytes() throws ConnectionLostException, InterruptedException {
        int output = 0;
//        byte[] requestValues = new byte[]{MEMORY_ADDRESS_OUTPUT_MSB};
        byte outputMsbReading;
        byte outputLsbReading;

        try {
            getTwoWireInterface().sendReadRequest(getDeviceAddress(), this.requestValues_output_msb, this.responseTwoBytes);
            outputMsbReading = this.responseTwoBytes[0];
            outputLsbReading = this.responseTwoBytes[1];

            output = concatenateTwoBytesToInt(outputMsbReading, outputLsbReading);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        return output;
    }

    public int readPressure() throws ConnectionLostException, InterruptedException {
        int pressure = readOutputRegistersThreeBytes();

        pressure = pressure >>> (8 - OVERSAMPLING);

        return pressure;
    }

    private int readOutputRegistersThreeBytes() throws ConnectionLostException, InterruptedException {
        int output = 0;
//        byte[] requestValues = new byte[]{MEMORY_ADDRESS_OUTPUT_MSB};
        byte outputMsbReading;
        byte outputLsbReading;
        byte outputXlsbReading;

        try {
            getTwoWireInterface().sendReadRequest(getDeviceAddress(), this.requestValues_output_msb, this.responseThreeBytes);
            outputMsbReading = this.responseThreeBytes[0];
            outputLsbReading = this.responseThreeBytes[1];
            outputXlsbReading = this.responseThreeBytes[2];

            output = concatenateThreeBytesToIntUnsigned(outputMsbReading, outputLsbReading, outputXlsbReading);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        return output;
    }
}
