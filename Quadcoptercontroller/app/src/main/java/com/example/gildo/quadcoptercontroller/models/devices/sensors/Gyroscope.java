package com.example.gildo.quadcoptercontroller.models.devices.sensors;

import com.example.gildo.quadcoptercontroller.models.devices.I2CDevice;
import com.example.gildo.quadcoptercontroller.models.devices.TwoWireInterfaceController;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 28/03/16.
 */
public class Gyroscope extends I2CDevice {
//    public static final String GYROSCOPE_TEMPERATURE = "gyroscopeTemperature";
    public static final String ANGULAR_SPEED_X_RAW_KEY = "angularSpeedX_raw";
    public static final String ANGULAR_SPEED_X_DEGREES_PER_SEC_KEY = "angularSpeedX_degreesPerSec";
    public static final String ANGULAR_SPEED_Y_RAW_KEY = "angularSpeedY_raw";
    public static final String ANGULAR_SPEED_Y_DEGREES_PER_SEC_KEY = "angularSpeedY_degreesPerSec";
    public static final String ANGULAR_SPEED_Z_RAW_KEY = "angularSpeedZ_raw";
    public static final String ANGULAR_SPEED_Z_DEGREES_PER_SEC_KEY = "angularSpeedZ_degreesPerSec";

    // (RANGE/NUMBER_OF_VALUES) = ((2000_neg + 2000_pos)/(2^16))
    public static final float RESOLUTION_ROTATION_DEGREES_PER_SEC = 4000f / 65536f;
    // (RANGE/NUMBER_OF_VALUES) = ((30°C_neg + 85°C_pos)/(2^16))
//    public static final float RESOLUTION_TEMPERATURE_CELSIUS = 115f / 65536f;
//    public static final float TEMPERATURE_OFFSET_CELSIUS = -30f;

    private static final int DEVICE_ADDRESS_7_BIT_FORMAT = 0x68;
    private static final int DEVICE_ALTERNATE_ADDRESS_7_BIT_FORMAT = 0x69;

    private static final byte MEMORY_ADDRESS_FREQUENCY_DIVIDER = 0x15;
    private static final byte FREQUENCY_DIVIDER_VALUE = 0x00;

    private static final byte MEMORY_ADDRESS_FILTER_SCALE_CONFIG = 0x16;
    /* Configuration code 0x18 is composed of FSEL=0x03, DPLF_CFG=0x00,
     * made by the math (FSEL << 3) + DPLF_CFG
     * Scale set to ±2000 degrees/sec
     * Bit resolution 16bits
     */
    private static final byte FILTER_SCALE_CONFIG_VALUE = 0x18;

    private static final byte MEMORY_ADDRESS_TEMP_MSB = 0x1B;
    private static final byte MEMORY_ADDRESS_TEMP_LSB = 0x1C;
    private static final byte MEMORY_ADDRESS_X_MSB = 0x1D;
    private static final byte MEMORY_ADDRESS_X_LSB = 0x1E;
    private static final byte MEMORY_ADDRESS_Y_MSB = 0x1F;
    private static final byte MEMORY_ADDRESS_Y_LSB = 0x20;
    private static final byte MEMORY_ADDRESS_Z_MSB = 0x21;
    private static final byte MEMORY_ADDRESS_Z_LSB = 0x22;

    private byte[] responseAngularSpeed;
    private byte[] requestValues_x_msb;

    public Gyroscope(TwoWireInterfaceController twoWireInterface) throws ConnectionLostException {
        super(twoWireInterface);
        this.setDeviceAddress(DEVICE_ADDRESS_7_BIT_FORMAT);
        this.responseAngularSpeed = new byte[6];
        this.requestValues_x_msb = new byte[]{MEMORY_ADDRESS_X_MSB};
    }

    @Override
    public boolean initializeDevice() {
        boolean itsConfigured = true;

        itsConfigured = itsConfigured && configureFrequencyDivider();
        itsConfigured = itsConfigured && configureFilterAndScale();

        return itsConfigured;
    }

    private boolean configureFrequencyDivider() {
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_FREQUENCY_DIVIDER, FREQUENCY_DIVIDER_VALUE};
        boolean writeSuccesfull = false;

        try {
            writeSuccesfull = getTwoWireInterface().sendWriteRequest(getDeviceAddress(),
                    requestValues);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return writeSuccesfull;
    }

    private boolean configureFilterAndScale() {
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_FILTER_SCALE_CONFIG, FILTER_SCALE_CONFIG_VALUE};
        boolean writeSuccesfull = false;

        try {
            writeSuccesfull = getTwoWireInterface().sendWriteRequest(getDeviceAddress(),
                    requestValues);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return writeSuccesfull;
    }

    public int readTemperature() {
        int temperature = 0;
        byte[] requestValues;
        byte[] response = new byte[2];
        byte msbReading;
        byte lsbReading;

        try {
            requestValues = new byte[]{MEMORY_ADDRESS_TEMP_MSB};
            getTwoWireInterface().sendReadRequest(getDeviceAddress(), requestValues, response);
            msbReading = response[0];
            lsbReading = response[1];

            temperature = concatenateTwoBytesToInt(msbReading, lsbReading);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return temperature;
    }

    public int[] readAngularSpeed() throws ConnectionLostException, InterruptedException {
        int angularSpeedX = 20;
        int angularSpeedY = 20;
        int angularSpeedZ = 20;
//        byte[] requestValues = new byte[]{MEMORY_ADDRESS_X_MSB, MEMORY_ADDRESS_X_LSB,
//                MEMORY_ADDRESS_Y_MSB, MEMORY_ADDRESS_Y_LSB, MEMORY_ADDRESS_Z_MSB,
//                MEMORY_ADDRESS_Z_LSB};
        byte xLsbReading = 0x10;
        byte xMsbReading = 0x11;
        byte yLsbReading = 0x12;
        byte yMsbReading = 0x13;
        byte zLsbReading = 0x14;
        byte zMsbReading = 0x15;

        try {
            getTwoWireInterface().sendReadRequest(getDeviceAddress(), this.requestValues_x_msb, this.responseAngularSpeed);
            xMsbReading = this.responseAngularSpeed[0];
            xLsbReading = this.responseAngularSpeed[1];
            yMsbReading = this.responseAngularSpeed[2];
            yLsbReading = this.responseAngularSpeed[3];
            zMsbReading = this.responseAngularSpeed[4];
            zLsbReading = this.responseAngularSpeed[5];

            angularSpeedX = concatenateTwoBytesToInt(xMsbReading, xLsbReading);
            angularSpeedY = concatenateTwoBytesToInt(yMsbReading, yLsbReading);
            angularSpeedZ = concatenateTwoBytesToInt(zMsbReading, zLsbReading);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        return new int[]{angularSpeedX, angularSpeedY, angularSpeedZ};
    }
}
