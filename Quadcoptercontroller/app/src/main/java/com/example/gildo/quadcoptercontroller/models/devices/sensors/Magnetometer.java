package com.example.gildo.quadcoptercontroller.models.devices.sensors;

import com.example.gildo.quadcoptercontroller.models.devices.I2CDevice;
import com.example.gildo.quadcoptercontroller.models.devices.TwoWireInterfaceController;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 11/04/16.
 */
public class Magnetometer extends I2CDevice {
    public static final String MAGNETIC_FIELD_X_RAW_KEY = "magneticFieldX_raw";
    public static final String MAGNETIC_FIELD_X_MICROTESLAS_KEY = "magneticFieldX_microTeslas";
    public static final String MAGNETIC_FIELD_Y_RAW_KEY = "magneticFieldY_raw";
    public static final String MAGNETIC_FIELD_Y_MICROTESLAS_KEY = "magneticFieldY_microTeslas";
    public static final String MAGNETIC_FIELD_Z_RAW_KEY = "magneticFieldZ_raw";
    public static final String MAGNETIC_FIELD_Z_MICROTESLAS_KEY = "magneticFieldZ_microTeslas";
    public static final String COMPASS_HEADING_KEY = "compassHeading_degrees";

    /* According to datasheet for the selected magnetic field range of ±1.3 Gauss
     * the resolution of the sensor is something about of 0.92 mG/LSB
     */
    public static final float RESOLUTION_GAUSS_PER_LSB = 0.00092f;

    private static final int DEVICE_ADDRESS_7_BIT_FORMAT = 0x1E;

    private static final byte MEMORY_ADDRESS_CONFIGURATION_REGISTER_A = 0x00;
    private static final byte CONFIGURATION_REGISTER_A_VALUE = 0x70;

    private static final byte MEMORY_ADDRESS_CONFIGURATION_REGISTER_B = 0x01;
    private static final byte CONFIGURATION_REGISTER_B_VALUE = 0x20;

    private static final byte MEMORY_ADDRESS_OPERATION_MODE = 0x02;
    // Put the device on constant reading
    private static final byte OPERATION_MODE_VALUE = 0x00;

    private static final byte MEMORY_ADDRESS_X_MSB = 0x03;
    private static final byte MEMORY_ADDRESS_X_LSB = 0x04;
    private static final byte MEMORY_ADDRESS_Z_MSB = 0x05;
    private static final byte MEMORY_ADDRESS_Z_LSB = 0x06;
    private static final byte MEMORY_ADDRESS_Y_MSB = 0x07;
    private static final byte MEMORY_ADDRESS_Y_LSB = 0x08;

    private byte[] responseMagneticField;
    private byte[] requestValues_x_msb;

    public Magnetometer(TwoWireInterfaceController twoWireInterface) throws ConnectionLostException {
        super(twoWireInterface);
        this.setDeviceAddress(DEVICE_ADDRESS_7_BIT_FORMAT);
        this.responseMagneticField = new byte[6];
        this.requestValues_x_msb = new byte[]{MEMORY_ADDRESS_X_MSB};
    }

    @Override
    public boolean initializeDevice() {
        boolean itsConfigured = true;

        itsConfigured = itsConfigured && configureConfigurationRegisterA();
        itsConfigured = itsConfigured && configureConfigurationRegisterB();
        itsConfigured = itsConfigured && configureOperationMode();

        return itsConfigured;
    }

    private boolean configureConfigurationRegisterA() {
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_CONFIGURATION_REGISTER_A, CONFIGURATION_REGISTER_A_VALUE};
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

    private boolean configureConfigurationRegisterB() {
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_CONFIGURATION_REGISTER_B, CONFIGURATION_REGISTER_B_VALUE};
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

    private boolean configureOperationMode() {
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_OPERATION_MODE, OPERATION_MODE_VALUE};
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

    public int[] readMagneticField() throws ConnectionLostException, InterruptedException{
        int magneticFieldX, magneticFieldY, magneticFieldZ;
//        byte[] requestValues = new byte[]{MEMORY_ADDRESS_X_MSB};
        byte xLsbReading, xMsbReading;
        byte zLsbReading, zMsbReading;
        byte yLsbReading, yMsbReading;
        magneticFieldX = magneticFieldY = magneticFieldZ = 0;

        try {
            getTwoWireInterface().sendReadRequest(getDeviceAddress(), this.requestValues_x_msb, this.responseMagneticField);
            xMsbReading = this.responseMagneticField[0];
            xLsbReading = this.responseMagneticField[1];
            zMsbReading = this.responseMagneticField[2];
            zLsbReading = this.responseMagneticField[3];
            yMsbReading = this.responseMagneticField[4];
            yLsbReading = this.responseMagneticField[5];

            magneticFieldX = concatenateTwoBytesToInt(xMsbReading, xLsbReading);
            magneticFieldZ = concatenateTwoBytesToInt(zMsbReading, zLsbReading);
            magneticFieldY = concatenateTwoBytesToInt(yMsbReading, yLsbReading);
        } catch (ConnectionLostException | InterruptedException e) {
            throw e;
        }

        return new int[]{magneticFieldX, magneticFieldY, magneticFieldZ};
    }
}
