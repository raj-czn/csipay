package com.csipay.softpos.api;

import com.csipay.softpos.device.DeviceCapabilities;

public interface InitializationListener {

    void onInitialized(DeviceCapabilities capabilities);

    void onDeviceConnected();

    void onDeviceDisconnected();

    void onInitializationError(SdkError error);
}