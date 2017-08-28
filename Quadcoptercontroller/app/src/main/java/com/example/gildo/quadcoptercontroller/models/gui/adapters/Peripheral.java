package com.example.gildo.quadcoptercontroller.models.gui.adapters;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.gildo.quadcoptercontroller.R;
import com.example.gildo.quadcoptercontroller.models.constants.PeripheralKind;

/**
 * Created by gildo on 14/03/16.
 */
public class Peripheral implements Parcelable {
    private int iconResource;

    private int description;

    private int peripheralKind;

    private int deviceList;

    private boolean passiveTest;

    private boolean activeTest;

    public static final String PARCELABLE_EXTRA_KEY = "peripheral";

    //Creator field
    public static final Parcelable.Creator<Peripheral> CREATOR = new Parcelable.Creator<Peripheral>() {
        @Override
        public Peripheral createFromParcel(Parcel in) {
            return new Peripheral(in);
        }

        @Override
        public Peripheral[] newArray(int size) {
            return new Peripheral[size];
        }
    };

    public Peripheral(int peripheralKind) {
        this.peripheralKind = peripheralKind;
        setDescription(peripheralKind);
        setIconResource(peripheralKind);
        fillDeviceListBasedOnPheripheralKind(peripheralKind);
        fillDeviceTestProfileBasedOnPheripheralKind(peripheralKind);
    }

    //Called to reconstruct object
    private Peripheral(Parcel in) {
        int nameResourceId = in.readInt();

        switch (nameResourceId) {
            case R.string.peripheral_accelerometer:
                this.peripheralKind = PeripheralKind.ACCELEROMETER;
                break;
            case R.string.peripheral_barometer:
                this.peripheralKind = PeripheralKind.BAROMETER;
                break;
            case R.string.peripheral_esc:
                this.peripheralKind = PeripheralKind.ESC;
                break;
            case R.string.peripheral_gyroscope:
                this.peripheralKind = PeripheralKind.GYROSCOPE;
                break;
            case R.string.peripheral_long_range_communicator:
                this.peripheralKind = PeripheralKind.LONG_RANGE_COMMUNICATOR;
                break;
            case R.string.peripheral_magnetometer:
                this.peripheralKind = PeripheralKind.MAGNETOMETER;
                break;
            case R.string.peripheral_tachometer:
                this.peripheralKind = PeripheralKind.TACHOMETER;
                break;
            case R.string.peripheral_sonar:
                this.peripheralKind = PeripheralKind.SONAR;
                break;
        }

        setDescription(this.peripheralKind);
        setIconResource(this.peripheralKind);
        fillDeviceListBasedOnPheripheralKind(this.peripheralKind);
        fillDeviceTestProfileBasedOnPheripheralKind(this.peripheralKind);
    }

    private void fillDeviceTestProfileBasedOnPheripheralKind(int peripheralKind) {
        switch (peripheralKind) {
            case PeripheralKind.ACCELEROMETER:
            case PeripheralKind.BAROMETER:
            case PeripheralKind.SONAR:
                this.passiveTest = true;
                this.activeTest = false;
                break;
            case PeripheralKind.ESC:
                this.passiveTest = false;
                this.activeTest = true;
                break;
            case PeripheralKind.LONG_RANGE_COMMUNICATOR:
            case PeripheralKind.GYROSCOPE:
            case PeripheralKind.MAGNETOMETER:
            case PeripheralKind.TACHOMETER:
                this.passiveTest = true;
                this.activeTest = true;
                break;
        }
    }

    private void fillDeviceListBasedOnPheripheralKind(int peripheralKind) {
        switch (peripheralKind) {
            case PeripheralKind.ACCELEROMETER:
                this.deviceList = R.array.peripheral_accelerometer_device_list;
                break;
            case PeripheralKind.BAROMETER:
                this.deviceList = R.array.peripheral_barometer_device_list;
                break;
            case PeripheralKind.ESC:
                this.deviceList = R.array.peripheral_esc_device_list;
                break;
            case PeripheralKind.GYROSCOPE:
                this.deviceList = R.array.peripheral_gyroscope_device_list;
                break;
            case PeripheralKind.LONG_RANGE_COMMUNICATOR:
                this.deviceList = R.array.peripheral_long_range_communicator_device_list;
                break;
            case PeripheralKind.MAGNETOMETER:
                this.deviceList = R.array.peripheral_magnetometer_device_list;
                break;
            case PeripheralKind.TACHOMETER:
                this.deviceList = R.array.peripheral_tachometer_device_list;
                break;
            case PeripheralKind.SONAR:
                this.deviceList = R.array.peripheral_sonar_device_list;
                break;
        }
    }

    public int getDeviceList() {
        return this.deviceList;
    }

    private void setIconResource(int peripheralKind) {
        switch (peripheralKind) {
            case PeripheralKind.ACCELEROMETER:
                this.iconResource = R.drawable.ic_accelerometer;
                break;
            case PeripheralKind.BAROMETER:
                this.iconResource = R.drawable.ic_barometer;
                break;
            case PeripheralKind.ESC:
                this.iconResource = R.drawable.ic_menu_auto_control;
                break;
            case PeripheralKind.GYROSCOPE:
                this.iconResource = R.drawable.ic_gyroscope;
                break;
            case PeripheralKind.LONG_RANGE_COMMUNICATOR:
                this.iconResource = R.drawable.ic_long_range_communicator;
                break;
            case PeripheralKind.MAGNETOMETER:
                this.iconResource = R.drawable.ic_magnetometer;
                break;
            case PeripheralKind.TACHOMETER:
                this.iconResource = R.drawable.ic_tachometer;
                break;
            case PeripheralKind.SONAR:
                this.iconResource = R.drawable.ic_sonar;
                break;
        }
    }

    public int getIconResource() {
        return this.iconResource;
    }

    public int getName() {
        return PeripheralKind.getNameResourceId(this.peripheralKind);
    }

    private void setDescription(int peripheralKind) {
        switch (peripheralKind) {
            case PeripheralKind.ACCELEROMETER:
                this.description = R.string.peripheral_accelerometer_description;
                break;
            case PeripheralKind.BAROMETER:
                this.description = R.string.peripheral_barometer_description;
                break;
            case PeripheralKind.ESC:
                this.description = R.string.peripheral_esc_description;
                break;
            case PeripheralKind.GYROSCOPE:
                this.description = R.string.peripheral_gyroscope_description;
                break;
            case PeripheralKind.LONG_RANGE_COMMUNICATOR:
                this.description = R.string.peripheral_long_range_communicator_description;
                break;
            case PeripheralKind.MAGNETOMETER:
                this.description = R.string.peripheral_magnetometer_description;
                break;
            case PeripheralKind.TACHOMETER:
                this.description = R.string.peripheral_tachometer_description;
                break;
            case PeripheralKind.SONAR:
                this.description = R.string.peripheral_sonar_description;
                break;
        }
    }

    public int getDescription() {
        return this.description;
    }

    public int getPeripheralKind() {
        return this.peripheralKind;
    }

    public boolean hasPassiveTest() {
        return this.passiveTest;
    }

    public boolean hasActiveTest() {
        return this.activeTest;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(peripheralKind.getNameResourceId());
        dest.writeInt(PeripheralKind.getNameResourceId(this.peripheralKind));
    }
}