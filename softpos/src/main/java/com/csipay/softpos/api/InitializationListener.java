package com.csipay.softpos.api;

public interface InitializationListener {

    void onDeviceConnected();

    void onDeviceDisconnected();

    void onInitializationError(String error);
}