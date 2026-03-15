package com.csipay.softpos.api;

import com.csipay.softpos.device.DeviceCapabilities;

public interface InitializationListener {

    void onInitialized(DeviceCapabilities capabilities);

    void onDeviceConnected(String deviceDescription, String model, String serialNumber);

    void onDeviceDisconnected();

    void onDeviceReady();

    void onBatteryLow();

    void onInitializationError(SdkError error);
}