package com.example.gildo.quadcoptercontroller.models.devices.sensors;

import com.example.gildo.quadcoptercontroller.models.devices.PulseSignalDevice;
import com.example.gildo.quadcoptercontroller.models.constants.RotorId;

import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 19/04/16.
 */
public class Tachometer extends PulseSignalDevice {
    public static final String REVOLUTIONS_ROTOR1_HZ_KEY = "revolutionsRotor1_hz";
    public static final String REVOLUTIONS_ROTOR1_RPM_KEY = "revolutionsRotor1_rpm";
    public static final String REVOLUTIONS_ROTOR2_HZ_KEY = "revolutionsRotor2_hz";
    public static final String REVOLUTIONS_ROTOR2_RPM_KEY = "revolutionsRotor2_rpm";
    public static final String REVOLUTIONS_ROTOR3_HZ_KEY = "revolutionsRotor3_hz";
    public static final String REVOLUTIONS_ROTOR3_RPM_KEY = "revolutionsRotor3_rpm";
    public static final String REVOLUTIONS_ROTOR4_HZ_KEY = "revolutionsRotor4_hz";
    public static final String REVOLUTIONS_ROTOR4_RPM_KEY = "revolutionsRotor4_rpm";

    private final int rotorId;

    public Tachometer(IOIO ioio, int pinNum, int rotorId, boolean isDoublePrecision) throws
            ConnectionLostException {
        super(ioio, pinNum, PulseInput.PulseMode.POSITIVE, isDoublePrecision);
        this.rotorId = rotorId;
    }

    public int getRotorId() {
        return rotorId;
    }

    public float readRevolutionsFrequency() throws ConnectionLostException, InterruptedException {
        return getFrequency();
    }

}
