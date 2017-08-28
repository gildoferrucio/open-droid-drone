package com.example.gildo.quadcoptercontroller.models.devices.actuators;

import com.example.gildo.quadcoptercontroller.models.devices.PulseSignalDevice;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 11/05/16.
 */
public class ESC extends PulseSignalDevice {
    public static final String ESC1_KEY = "esc1";
    public static final String ESC2_KEY = "esc2";
    public static final String ESC3_KEY = "esc3";
    public static final String ESC4_KEY = "esc4";

    public static final int MINIMUM_PULSE_WIDTH_MICROSECS = 1000;
    public static final int MAXIMUM_PULSE_WIDTH_MICROSECS = 2000;
    private static final int FREQUENCY_HZ = 50;

    private int pulseWidth_Microsecs;

    public ESC(IOIO ioio, int pinNum) throws ConnectionLostException {
        super(ioio, pinNum, FREQUENCY_HZ);
        this.pulseWidth_Microsecs = MINIMUM_PULSE_WIDTH_MICROSECS;
    }

    public int getPulseWidth_Microsecs() {
        return this.pulseWidth_Microsecs;
    }

    public void setPulseWidth_Microsecs(int pulseWidth_Microsecs) {
        if ((pulseWidth_Microsecs >= MINIMUM_PULSE_WIDTH_MICROSECS) &&
                (pulseWidth_Microsecs <= MAXIMUM_PULSE_WIDTH_MICROSECS)) {
            this.pulseWidth_Microsecs = pulseWidth_Microsecs;
        }
    }

    public void setNewPulseWidthToESC() throws ConnectionLostException {
        this.getPwmOutput().setPulseWidth(this.pulseWidth_Microsecs);
    }

    public static int percentToPulseWidth_microsecs(float percent){
        float pulseWidth_microsecs;

        if(percent < 0){
            percent *= -1;
        }

        pulseWidth_microsecs = Math.round(ESC.MINIMUM_PULSE_WIDTH_MICROSECS * (percent + 1));

        return (int) pulseWidth_microsecs;
    }
}
