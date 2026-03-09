package com.csipay.softpos.device;

import android.content.Context;

import com.csipay.softpos.api.InitializationListener;
import com.csipay.softpos.config.SoftposConfig;
import com.csipay.softpos.config.TriposConfigurationBuilder;
import com.vantiv.triposmobilesdk.*;

public class DeviceManager {

    private VTP vtp;

    public void initialize(Context context, SoftposConfig softposConfig, InitializationListener listener) {

        Configuration configuration = TriposConfigurationBuilder.build(softposConfig);

        try {

            vtp = triPOSMobileSDK.getSharedVtp();

            vtp.initialize(
                context.getApplicationContext(),
                configuration,
                new DeviceConnectionListener() {

                    @Override
                    public void onConnected(Device device, String s, String s1, String s2) {
                        if(listener != null) listener.onDeviceConnected();
                    }

                    @Override
                    public void onDisconnected(Device device) {
                        if(listener != null) listener.onDeviceDisconnected();
                    }

                    @Override
                    public void onError(Exception e) {
                        if(listener != null)
                            listener.onInitializationError(e.getMessage());
                    }

                    @Override
                    public void onBatteryLow() {

                    }

                    @Override
                    public void onWarning(Exception e) {

                    }
                });

        } catch(Exception e) {

            if(listener != null)
                listener.onInitializationError(e.getMessage());
        }
    }
}