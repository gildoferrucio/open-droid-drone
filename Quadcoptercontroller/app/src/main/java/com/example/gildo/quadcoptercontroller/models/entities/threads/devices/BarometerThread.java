package com.example.gildo.quadcoptercontroller.models.entities.threads.devices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.gildo.quadcoptercontroller.models.devices.I2CDevice;
import com.example.gildo.quadcoptercontroller.models.devices.TwoWireInterfaceController;
import com.example.gildo.quadcoptercontroller.models.devices.sensors.Barometer;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 11/03/17.
 */

public class BarometerThread extends DeviceThread {
    public static final String INTENT_FILTER_BAROMETER_THREAD = "barometerThread";

    private TwoWireInterfaceController twoWireInterfaceController_barometer;

    private I2CDevice i2cDevice_barometer;

    private int temperatureBarometer;
    private int airPressure;

    public BarometerThread(IOIO ioio, Context context) throws ConnectionLostException,
            InterruptedException {
        this.twoWireInterfaceController_barometer = new TwoWireInterfaceController(1, ioio);
        this.i2cDevice_barometer = new Barometer(this.twoWireInterfaceController_barometer);
        Thread.sleep(10);
        this.successfulInitialization = ((Barometer) this.i2cDevice_barometer).initializeDevice();
        this.firstSample = true;
        this.broadcastIntent = new Intent(BarometerThread.INTENT_FILTER_BAROMETER_THREAD);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.setName(INTENT_FILTER_BAROMETER_THREAD);
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
        } catch (ConnectionLostException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.running = false;
        }
    }

    @Override
    protected void readDevice() throws ConnectionLostException, InterruptedException {
        //Barometer calibration registers send
        this.broadcastIntent.putExtra(Barometer.CALIBRATION_REGISTERS_KEY,
                ((Barometer) this.i2cDevice_barometer).getCalibrationRegisters());
        //Barometer temperature reading request
        ((Barometer) this.i2cDevice_barometer).sendReadTemperatureCommand();
        Thread.sleep(5);
        //Barometer temperature readings
        this.temperatureBarometer = ((Barometer) this.i2cDevice_barometer).readTemperature();
        this.broadcastIntent.putExtra(Barometer.TEMPERATURE_RAW_KEY, this.temperatureBarometer);
        //Barometer pressure reading request
        ((Barometer) this.i2cDevice_barometer).sendReadAirPressureCommand();
        Thread.sleep(5);
        //Barometer pressure readings
        this.airPressure = ((Barometer) this.i2cDevice_barometer).readPressure();
        this.broadcastIntent.putExtra(Barometer.AIR_PRESSURE_RAW_KEY, this.airPressure);
    }
}
