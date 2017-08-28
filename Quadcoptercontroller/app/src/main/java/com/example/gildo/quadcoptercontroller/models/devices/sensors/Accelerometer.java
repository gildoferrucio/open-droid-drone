package com.example.gildo.quadcoptercontroller.models.devices.sensors;

import com.example.gildo.quadcoptercontroller.models.devices.I2CDevice;
import com.example.gildo.quadcoptercontroller.models.devices.TwoWireInterfaceController;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 27/03/16.
 */
public class Accelerometer extends I2CDevice {
    public static final String ACCELERATION_X_RAW_KEY = "accelerationX_raw";
    public static final String ACCELERATION_X_METERS_PER_SEC_SQUARED_KEY = "accelerationX_metersPerSecSquared";
    public static final String ACCELERATION_Y_RAW_KEY = "accelerationY_raw";
    public static final String ACCELERATION_Y_METERS_PER_SEC_SQUARED_KEY = "accelerationY_metersPerSecSquared";
    public static final String ACCELERATION_Z_RAW_KEY = "accelerationZ_raw";
    public static final String ACCELERATION_Z_METERS_PER_SEC_SQUARED_KEY = "accelerationZ_metersPerSecSquared";

    public static final float STANDARD_GRAVITY_METERS_PER_SEC = 9.80665f;

    // (RANGE/NUMBER_OF_VALUES) = ((16G_neg + 16G_pos)/(2^13))
    public static final float RESOLUTION_G_PER_LSB = 32f / 8192f;

    private static final int DEVICE_ADDRESS_7_BIT_FORMAT = 0x1D;
    private static final int DEVICE_ALTERNATE_ADDRESS_7_BIT_FORMAT = 0x53;

    private static final byte MEMORY_ADDRESS_OPERATION_MODE = 0x2D;
    // Put the device on constant reading
    private static final byte OPERATION_MODE_VALUE = 0x08;

    private static final byte MEMORY_ADDRESS_ANSWER_FORMAT = 0x31;
    /* Configuration code 0X0B is composed of FULL_RES=0b01 (4 mg/LSB)
     * and Range=0b11 (±16g), made by the math (FULL_RES << 3) + 0x03
     */
    private static final byte ANSWER_FORMAT_VALUE = 0x08;

    private static final byte MEMORY_ADDRESS_X_LSB = 0x32;
    private static final byte MEMORY_ADDRESS_X_MSB = 0x33;
    private static final byte MEMORY_ADDRESS_Y_LSB = 0x34;
    private static final byte MEMORY_ADDRESS_Y_MSB = 0x35;
    private static final byte MEMORY_ADDRESS_Z_LSB = 0x36;
    private static final byte MEMORY_ADDRESS_Z_MSB = 0x37;

    private byte[] responseAcceleration;
    private byte[] requestValues_x_lsb;

    public Accelerometer(TwoWireInterfaceController twoWireInterface) throws ConnectionLostException {
        super(twoWireInterface);
        this.setDeviceAddress(DEVICE_ALTERNATE_ADDRESS_7_BIT_FORMAT);
        this.responseAcceleration = new byte[6];
        this.requestValues_x_lsb = new byte[]{MEMORY_ADDRESS_X_LSB};
    }

    @Override
    public boolean initializeDevice() {
        boolean itsConfigured = true;

        itsConfigured = itsConfigured && configureResolutionAndRange();
        itsConfigured = itsConfigured && configureOperationMode();

        return itsConfigured;
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

    private boolean configureResolutionAndRange() {
        byte[] requestValues = new byte[]{MEMORY_ADDRESS_ANSWER_FORMAT, ANSWER_FORMAT_VALUE};
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

    public int[] readAcceleration() throws InterruptedException, ConnectionLostException {
        int accelerationX, accelerationY, accelerationZ;
//        byte[] requestValues = new byte[]{MEMORY_ADDRESS_X_LSB};
        byte xLsbReading, xMsbReading;
        byte yLsbReading, yMsbReading;
        byte zLsbReading, zMsbReading;
        accelerationX = accelerationY = accelerationZ = 0;

        try {
            getTwoWireInterface().sendReadRequest(getDeviceAddress(), this.requestValues_x_lsb, this.responseAcceleration);
            xLsbReading = this.responseAcceleration[0];
            xMsbReading = this.responseAcceleration[1];
            yLsbReading = this.responseAcceleration[2];
            yMsbReading = this.responseAcceleration[3];
            zLsbReading = this.responseAcceleration[4];
            zMsbReading = this.responseAcceleration[5];

            accelerationX = concatenateTwoBytesToInt(xMsbReading, xLsbReading);
            accelerationY = concatenateTwoBytesToInt(yMsbReading, yLsbReading);
            accelerationZ = concatenateTwoBytesToInt(zMsbReading, zLsbReading);
        } catch (InterruptedException | ConnectionLostException e) {
            throw e;
        }

        return new int[]{accelerationX, accelerationY, accelerationZ};
    }
}
