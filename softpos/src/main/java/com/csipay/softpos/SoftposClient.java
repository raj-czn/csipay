package com.csipay.softpos;

import android.content.Context;

import com.csipay.softpos.api.CardReaderListener;
import com.csipay.softpos.api.InitializationListener;
import com.csipay.softpos.api.TransactionListener;
import com.csipay.softpos.config.SoftposConfig;
import com.csipay.softpos.device.DeviceManager;

import java.math.BigDecimal;

public class SoftposClient {

    private static volatile SoftposClient instance;
    private final DeviceManager deviceManager;

    private SoftposClient() {
        deviceManager = new DeviceManager();
    }

    public static SoftposClient getInstance() {
        if (instance == null) {
            synchronized (SoftposClient.class) {
                if (instance == null) {
                    instance = new SoftposClient();
                }
            }
        }
        return instance;
    }

    public void initialize(Context context, SoftposConfig config, InitializationListener listener) {
        deviceManager.initialize(context, config, listener);
    }

    public void deinitialize() {
        deviceManager.deinitialize();
    }

    public boolean isInitialized() {
        return deviceManager.isInitialized();
    }

    public void processSale(BigDecimal amount, String referenceNumber,
                            CardReaderListener cardReaderListener, TransactionListener listener) {
        deviceManager.processSale(amount, referenceNumber, cardReaderListener, listener);
    }

    public void processRefund(BigDecimal amount, String referenceNumber,
                              CardReaderListener cardReaderListener, TransactionListener listener) {
        deviceManager.processRefund(amount, referenceNumber, cardReaderListener, listener);
    }

    public void processVoid(String transactionId, TransactionListener listener) {
        deviceManager.processVoid(transactionId, listener);
    }
}