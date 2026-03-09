package com.csipay.softpos;

import android.content.Context;

import com.csipay.softpos.config.SoftposConfig;
import com.csipay.softpos.device.DeviceManager;
import com.csipay.softpos.api.InitializationListener;

public class SoftposClient {

    private static SoftposClient instance;
    private DeviceManager deviceManager;

    private SoftposClient() {
        deviceManager = new DeviceManager();
    }

    public static SoftposClient getInstance() {
        if(instance == null) {
            instance = new SoftposClient();
        }
        return instance;
    }

    public void initialize(
            Context context,
            SoftposConfig config,
            InitializationListener listener
    ) {
        deviceManager.initialize(context, config, listener);
    }
}