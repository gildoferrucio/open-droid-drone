package com.example.gildo.quadcoptercontroller.models.devices;

import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 27/03/16.
 */
public class TwoWireInterfaceController {
    private int twiPort;

    private TwiMaster twiMaster;

    private IOIO ioio;

    public TwoWireInterfaceController(int twiPortNum, IOIO ioio) throws ConnectionLostException {
        this.twiPort = twiPortNum;
        this.ioio = ioio;
        this.twiMaster = this.ioio.openTwiMaster(this.twiPort, TwiMaster.Rate.RATE_400KHz, false);
    }

    public int getTwiPort() {
        return this.twiPort;
    }

    public void setTwiPort(int twiPort) {
        this.twiPort = twiPort;
    }

    public TwiMaster getTwiMaster() {
        return this.twiMaster;
    }

    public void setTwiMaster(TwiMaster twiMaster) {
        this.twiMaster = twiMaster;
    }

    public IOIO getIoio() {
        return this.ioio;
    }

    public void setIoio(IOIO ioio) {
        this.ioio = ioio;
    }

    public boolean sendReadRequest(int deviceAddress, byte[] requestValues, byte[] response) throws
            ConnectionLostException, InterruptedException {
        boolean readDone = false;

        if (this.twiMaster != null) {
            readDone = this.twiMaster.writeRead(deviceAddress, false, requestValues, requestValues.length, response,
                    response.length);
        }

        return readDone;
    }

    public boolean sendWriteRequest(int deviceAddress, byte[] requestValues) throws ConnectionLostException,
            InterruptedException {
        boolean writeDone = false;

        if (this.twiMaster != null) {
            writeDone = this.twiMaster.writeRead(deviceAddress, false, requestValues, requestValues.length, null, 0);
        }

        return writeDone;
    }

    public void closeTwiMaster() {
        if (this.twiMaster != null) {
            this.twiMaster.close();
        }
    }
}
