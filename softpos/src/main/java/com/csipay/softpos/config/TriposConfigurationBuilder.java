package com.csipay.softpos.config;

import com.vantiv.triposmobilesdk.*;
import com.vantiv.triposmobilesdk.enums.*;
import com.vantiv.triposmobilesdk.express.*;

import java.math.BigDecimal;

public class TriposConfigurationBuilder {

    public static Configuration build(SoftposConfig softposConfig) {

        Configuration configuration = new Configuration();

        configuration.setApplicationConfiguration(buildApplicationConfig(softposConfig));
        configuration.setHostConfiguration(buildHostConfig(softposConfig));
        configuration.setDeviceConfiguration(buildDeviceConfig(softposConfig));
        configuration.setTransactionConfiguration(buildTransactionConfig(softposConfig));
        configuration.setStoreAndForwardConfiguration(buildStoreForwardConfig(softposConfig));

        return configuration;
    }

    private static ApplicationConfiguration buildApplicationConfig(SoftposConfig softposConfig) {

        ApplicationConfiguration config = new ApplicationConfiguration();
        config.setIdlePrompt(softposConfig.getApplicationName());

        ApplicationMode mode = softposConfig.getEnvironment() == Environment.PRODUCTION
                ? ApplicationMode.Production
                : ApplicationMode.TestCertification;
        config.setApplicationMode(mode);

        return config;
    }

    private static HostConfiguration buildHostConfig(SoftposConfig softposConfig) {

        HostConfiguration host = new HostConfiguration();

        Credentials credentials =
            new Credentials(
                    softposConfig.getAcceptorId(),
                    softposConfig.getAccountToken(),
                    softposConfig.getAccountId()
            );

        host.setAcceptorId(credentials.getAcceptorID());
        host.setAccountId(credentials.getAccountID());
        host.setAccountToken(credentials.getAccountToken());

        Application app =
                new Application(
                        softposConfig.getApplicationId(),
                        softposConfig.getApplicationName(),
                        softposConfig.getApplicationVersion()
                );

        host.setApplicationId(app.getApplicationID());
        host.setApplicationName(app.getApplicationName());
        host.setApplicationVersion(app.getApplicationVersion());

        try {
            host.setPaymentProcessor(
                    PaymentProcessor.valueOf(softposConfig.getPaymentProcessor()));
        } catch (IllegalArgumentException e) {
            host.setPaymentProcessor(PaymentProcessor.Worldpay);
        }

        return host;
    }

    private static DeviceConfiguration buildDeviceConfig(SoftposConfig softposConfig) {

        DeviceConfiguration device = new DeviceConfiguration();

        device.setDeviceType(DeviceType.Null);
        device.setTerminalType(TerminalType.Mobile);
        device.setTerminalId(softposConfig.getTerminalId());

        device.setHeartbeatEnabled(true);
        device.setContactlessAllowed(true);
        device.setKeyedEntryAllowed(true);

        device.setSleepTimeoutSeconds(new BigDecimal(300));

        return device;
    }

    private static TransactionConfiguration buildTransactionConfig(SoftposConfig softposConfig) {

        TransactionConfiguration txn = new TransactionConfiguration();

        txn.setEmvAllowed(true);
        txn.setTipAllowed(true);
        txn.setDebitAllowed(true);

        try {
            txn.setCurrencyCode(
                    CurrencyCode.valueOf(softposConfig.getCurrencyCode()));
        } catch (IllegalArgumentException e) {
            txn.setCurrencyCode(CurrencyCode.USD);
        }

        return txn;
    }

    private static StoreAndForwardConfiguration buildStoreForwardConfig(SoftposConfig softposConfig) {

        StoreAndForwardConfiguration config =
            new StoreAndForwardConfiguration();

        config.setNumberOfDaysToRetainProcessedTransactions(1);
        config.setStoringTransactionsAllowed(true);

        return config;
    }
}