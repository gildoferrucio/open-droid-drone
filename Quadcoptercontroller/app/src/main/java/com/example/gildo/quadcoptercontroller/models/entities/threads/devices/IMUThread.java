package com.example.gildo.quadcoptercontroller.models.entities.threads.devices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.models.devices.I2CDevice;
import com.example.gildo.quadcoptercontroller.models.devices.TwoWireInterfaceController;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Accelerometer;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Gyroscope;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Magnetometer;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 11/03/17.
 */

public class IMUThread extends DeviceThread {
    public static final String INTENT_FILTER_IMU_THREAD = "imuThread";

    private TwoWireInterfaceController twoWireInterfaceController_imu;

    private I2CDevice i2cDevice_accelerometer;
    private I2CDevice i2cDevice_gyroscope;
    private I2CDevice i2cDevice_magnetometer;

    private boolean accelerometer_enabled;
    private boolean gyroscope_enabled;
    private boolean magnetometer_enabled;

    private int[] acceleration;
    private int[] angularSpeed;
    private int[] magneticField;

    public IMUThread(IOIO ioio, Context context, boolean accelerometer_enabled, boolean gyroscope_enabled,
                     boolean magnetometer_enabled) throws ConnectionLostException {
        this.twoWireInterfaceController_imu = new TwoWireInterfaceController(0, ioio);

        this.accelerometer_enabled = accelerometer_enabled;
        this.gyroscope_enabled = gyroscope_enabled;
        this.magnetometer_enabled = magnetometer_enabled;

        if (this.accelerometer_enabled) {
            this.i2cDevice_accelerometer = new Accelerometer(this.twoWireInterfaceController_imu);
            this.successfulInitialization = ((Accelerometer) this.i2cDevice_accelerometer).initializeDevice();
        }

        if (this.gyroscope_enabled) {
            this.i2cDevice_gyroscope = new Gyroscope(this.twoWireInterfaceController_imu);
            this.successfulInitialization = this.successfulInitialization && ((Gyroscope) this.i2cDevice_gyroscope).
                    initializeDevice();
        }

        if (this.magnetometer_enabled) {
            this.i2cDevice_magnetometer = new Magnetometer(this.twoWireInterfaceController_imu);
            this.successfulInitialization = this.successfulInitialization && ((Magnetometer) this.i2cDevice_magnetometer).
                    initializeDevice();
        }

        this.firstSample = true;
        this.broadcastIntent = new Intent(IMUThread.INTENT_FILTER_IMU_THREAD);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.setName(INTENT_FILTER_IMU_THREAD);
    }

    @Override
    public void run() {
        try {
            //Delay inserted to make sure that all devices had been initialized
            Thread.sleep(DeviceThread.FIRST_WAIT_TIME_MILLISECS);

            this.running = true;

            while (this.running) {
                readDevice();

                this.localBroadcastManager.sendBroadcast(this.broadcastIntent);

                //there's a need to put some sleep time here, some millisecs
                Thread.sleep(DeviceThread.STANDARD_SAMPLING_PERIOD_MILLISECS);
            }
        } catch (InterruptedException | ConnectionLostException e) {
            e.printStackTrace();
        } finally {
            this.running = false;
        }
    }

    @Override
    protected void readDevice() throws ConnectionLostException, InterruptedException {
        if (this.accelerometer_enabled) {
            this.acceleration = ((Accelerometer) this.i2cDevice_accelerometer).readAcceleration();
            this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_X_RAW_KEY, this.acceleration[0]);
            this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_Y_RAW_KEY, this.acceleration[1]);
            this.broadcastIntent.putExtra(Accelerometer.ACCELERATION_Z_RAW_KEY, this.acceleration[2]);
        }
        if (this.gyroscope_enabled) {
            this.angularSpeed = ((Gyroscope) this.i2cDevice_gyroscope).readAngularSpeed();
            this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_X_RAW_KEY, this.angularSpeed[0]);
            this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_Y_RAW_KEY, this.angularSpeed[1]);
            this.broadcastIntent.putExtra(Gyroscope.ANGULAR_SPEED_Z_RAW_KEY, this.angularSpeed[2]);
        }
        if (this.magnetometer_enabled) {
            this.magneticField = ((Magnetometer) this.i2cDevice_magnetometer).readMagneticField();
            this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_X_RAW_KEY, this.magneticField[0]);
            this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_Y_RAW_KEY, this.magneticField[1]);
            this.broadcastIntent.putExtra(Magnetometer.MAGNETIC_FIELD_Z_RAW_KEY, this.magneticField[2]);
        }
    }
}
