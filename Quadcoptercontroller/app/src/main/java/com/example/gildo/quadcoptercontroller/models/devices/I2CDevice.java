package com.example.gildo.quadcoptercontroller.models.devices;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 04/04/16.
 */
public abstract class I2CDevice {
    //It's on 7-bit format
    private int deviceAddress;

    private TwoWireInterfaceController twoWireInterface;

    public I2CDevice(TwoWireInterfaceController twoWireInterface) {
        this.twoWireInterface = twoWireInterface;
    }

    public int getDeviceAddress() {
        return this.deviceAddress;
    }

    public void setDeviceAddress(int deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public TwoWireInterfaceController getTwoWireInterface() {
        return this.twoWireInterface;
    }

    public void setTwoWireInterface(TwoWireInterfaceController twoWireInterface) {
        this.twoWireInterface = twoWireInterface;
    }

    public abstract boolean initializeDevice() throws ConnectionLostException, InterruptedException;

    public int concatenateTwoBytesToInt(byte msb, byte lsb) {
        /* References: https://stackoverflow.com/questions/563544/java-bit-manipulation
         *             http://sys.cs.rice.edu/course/comp314/10/p2/javabits.html
         *             https://stackoverflow.com/questions/3105437/converting-bytes-to-int-in-java
         *             https://stackoverflow.com/questions/4768933/read-two-bytes-into-an-integer
         * Java has a fucked up bit interpretation! It considers bytes as signed. That means
         * that the most significant bit of a single byte is considered to be a signal. Fucking up
         * with traditional two bytes concatenation to form a int.
         */
        return ((int) msb << 8) | ((int) lsb & 0xFF);
    }

    public int concatenateTwoBytesToIntUnsigned(byte msb, byte lsb) {
        /* References: https://stackoverflow.com/questions/563544/java-bit-manipulation
         *             http://sys.cs.rice.edu/course/comp314/10/p2/javabits.html
         *             https://stackoverflow.com/questions/3105437/converting-bytes-to-int-in-java
         *             https://stackoverflow.com/questions/4768933/read-two-bytes-into-an-integer
         * Java has a fucked up bit interpretation! It considers bytes as signed. That means
         * that the most significant bit of a single byte is considered to be a signal. Fucking up
         * with traditional two bytes concatenation to form a int.
         */
        return ((int) (msb & 0xFF) << 8) | ((int) lsb & 0xFF);
    }

    public int concatenateThreeBytesToInt(byte msb, byte lsb, byte xlsb) {
        /* References: https://stackoverflow.com/questions/563544/java-bit-manipulation
         *             http://sys.cs.rice.edu/course/comp314/10/p2/javabits.html
         *             https://stackoverflow.com/questions/3105437/converting-bytes-to-int-in-java
         *             https://stackoverflow.com/questions/4768933/read-two-bytes-into-an-integer
         * Java has a fucked up bit interpretation! It considers bytes as signed. That means
         * that the most significant bit of a single byte is considered to be a signal. Fucking up
         * with traditional two bytes concatenation to form a int.
         */
//        return ((int) msb << 16) | (((int) lsb & 0xFF) << 8) | ((int) xlsb & 0xFF);
        return ((int) msb << 16) | ((int) (lsb & 0xFF) << 8) | ((int) xlsb & 0xFF);
    }

    public int concatenateThreeBytesToIntUnsigned(byte msb, byte lsb, byte xlsb) {
        /* References: https://stackoverflow.com/questions/563544/java-bit-manipulation
         *             http://sys.cs.rice.edu/course/comp314/10/p2/javabits.html
         *             https://stackoverflow.com/questions/3105437/converting-bytes-to-int-in-java
         *             https://stackoverflow.com/questions/4768933/read-two-bytes-into-an-integer
         * Java has a fucked up bit interpretation! It considers bytes as signed. That means
         * that the most significant bit of a single byte is considered to be a signal. Fucking up
         * with traditional two bytes concatenation to form a int.
         */
        return ((int) (msb & 0xFF) << 16) | ((int) (lsb & 0xFF) << 8) | ((int) xlsb & 0xFF);
    }
}
