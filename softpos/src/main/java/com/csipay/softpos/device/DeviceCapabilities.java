package com.csipay.softpos.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceCapabilities {

    private final boolean nfcAvailable;
    private final boolean bluetoothAvailable;
    private final boolean internetAvailable;
    private final List<String> warnings;

    DeviceCapabilities(boolean nfcAvailable, boolean bluetoothAvailable,
                       boolean internetAvailable, List<String> warnings) {
        this.nfcAvailable = nfcAvailable;
        this.bluetoothAvailable = bluetoothAvailable;
        this.internetAvailable = internetAvailable;
        this.warnings = Collections.unmodifiableList(warnings != null ? warnings : new ArrayList<>());
    }

    public boolean isNfcAvailable() { return nfcAvailable; }
    public boolean isBluetoothAvailable() { return bluetoothAvailable; }
    public boolean isInternetAvailable() { return internetAvailable; }
    public List<String> getWarnings() { return warnings; }

    public boolean hasRequiredCapabilities() {
        return internetAvailable && nfcAvailable;
    }
}