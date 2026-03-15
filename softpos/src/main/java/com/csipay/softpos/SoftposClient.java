package com.csipay.softpos;

import android.content.Context;

import com.csipay.softpos.api.CardReaderListener;
import com.csipay.softpos.api.HealthCheckListener;
import com.csipay.softpos.api.InitializationListener;
import com.csipay.softpos.api.StoreAndForwardListener;
import com.csipay.softpos.api.TransactionListener;
import com.csipay.softpos.config.SoftposConfig;
import com.csipay.softpos.device.DeviceCapabilities;
import com.csipay.softpos.device.DeviceCapabilityChecker;
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

    // ── Device Validation ──

    public DeviceCapabilities checkDeviceCapabilities(Context context) {
        return DeviceCapabilityChecker.check(context.getApplicationContext());
    }

    // ── Lifecycle ──

    public void initialize(Context context, SoftposConfig config, InitializationListener listener) {
        deviceManager.initialize(context, config, listener);
    }

    public void deinitialize() {
        deviceManager.deinitialize();
    }

    public boolean isInitialized() {
        return deviceManager.isInitialized();
    }

    public void cancelCurrentTransaction() {
        deviceManager.cancelCurrentTransaction();
    }

    // ── Sale ──

    public void processSale(BigDecimal amount, String referenceNumber,
                            CardReaderListener cardReaderListener, TransactionListener listener) {
        deviceManager.processSale(amount, referenceNumber, cardReaderListener, listener);
    }

    // ── Authorization ──

    public void processAuthorization(BigDecimal amount, String referenceNumber,
                                     CardReaderListener cardReaderListener, TransactionListener listener) {
        deviceManager.processAuthorization(amount, referenceNumber, cardReaderListener, listener);
    }

    public void processAuthorizationCompletion(String transactionId, BigDecimal amount,
                                               TransactionListener listener) {
        deviceManager.processAuthorizationCompletion(transactionId, amount, listener);
    }

    // ── Refund ──

    public void processRefund(BigDecimal amount, String referenceNumber,
                              CardReaderListener cardReaderListener, TransactionListener listener) {
        deviceManager.processRefund(amount, referenceNumber, cardReaderListener, listener);
    }

    // ── Void ──

    public void processVoid(String transactionId, TransactionListener listener) {
        deviceManager.processVoid(transactionId, listener);
    }

    // ── Return ──

    public void processReturn(BigDecimal amount, String referenceNumber,
                              CardReaderListener cardReaderListener, TransactionListener listener) {
        deviceManager.processReturn(amount, referenceNumber, cardReaderListener, listener);
    }

    // ── Reversal ──

    public void processReversal(String transactionId, TransactionListener listener) {
        deviceManager.processReversal(transactionId, listener);
    }

    // ── Health Check ──

    public void performHealthCheck(HealthCheckListener listener) {
        deviceManager.performHealthCheck(listener);
    }

    // ── Pre-Read Card ──

    public void preReadCardData(CardReaderListener cardReaderListener, TransactionListener listener) {
        deviceManager.preReadCardData(cardReaderListener, listener);
    }

    // ── Store & Forward ──

    public void getStoredTransactions(StoreAndForwardListener listener) {
        deviceManager.getStoredTransactions(listener);
    }

    public void forwardStoredTransaction(String transactionId, StoreAndForwardListener listener) {
        deviceManager.forwardStoredTransaction(transactionId, listener);
    }

    public void deleteStoredTransactions(StoreAndForwardListener listener) {
        deviceManager.deleteStoredTransactions(listener);
    }
}