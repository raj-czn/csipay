package com.csipay.softpos.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceCapabilities {

    private final boolean nfcAvailable;
    private final boolean nfcEnabled;
    private final boolean bluetoothAvailable;
    private final boolean internetAvailable;
    private final boolean screenLockSet;
    private final boolean locationAvailable;
    private final boolean deviceSecure;
    private final int androidApiLevel;
    private final List<String> warnings;
    private final List<String> errors;

    DeviceCapabilities(boolean nfcAvailable, boolean nfcEnabled, boolean bluetoothAvailable,
                       boolean internetAvailable, boolean screenLockSet, boolean locationAvailable,
                       boolean deviceSecure, int androidApiLevel,
                       List<String> warnings, List<String> errors) {
        this.nfcAvailable = nfcAvailable;
        this.nfcEnabled = nfcEnabled;
        this.bluetoothAvailable = bluetoothAvailable;
        this.internetAvailable = internetAvailable;
        this.screenLockSet = screenLockSet;
        this.locationAvailable = locationAvailable;
        this.deviceSecure = deviceSecure;
        this.androidApiLevel = androidApiLevel;
        this.warnings = Collections.unmodifiableList(warnings != null ? warnings : new ArrayList<>());
        this.errors = Collections.unmodifiableList(errors != null ? errors : new ArrayList<>());
    }

    public boolean isNfcAvailable() { return nfcAvailable; }
    public boolean isNfcEnabled() { return nfcEnabled; }
    public boolean isBluetoothAvailable() { return bluetoothAvailable; }
    public boolean isInternetAvailable() { return internetAvailable; }
    public boolean isScreenLockSet() { return screenLockSet; }
    public boolean isLocationAvailable() { return locationAvailable; }
    public boolean isDeviceSecure() { return deviceSecure; }
    public int getAndroidApiLevel() { return androidApiLevel; }
    public List<String> getWarnings() { return warnings; }
    public List<String> getErrors() { return errors; }

    public boolean hasRequiredCapabilities() {
        return nfcAvailable && nfcEnabled && internetAvailable
                && screenLockSet && deviceSecure;
    }
}