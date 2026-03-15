package com.csipay.softpos.device;

import android.content.Context;

import com.csipay.softpos.api.CardReaderListener;
import com.csipay.softpos.api.HealthCheckListener;
import com.csipay.softpos.api.InitializationListener;
import com.csipay.softpos.api.SdkError;
import com.csipay.softpos.api.StoreAndForwardListener;
import com.csipay.softpos.api.StoredTransaction;
import com.csipay.softpos.api.TransactionListener;
import com.csipay.softpos.config.SoftposConfig;
import com.csipay.softpos.config.TriposConfigurationBuilder;
import com.csipay.softpos.transaction.TransactionResult;
import com.csipay.softpos.transaction.TransactionStatus;
import com.csipay.softpos.transaction.TransactionType;
import com.vantiv.triposmobilesdk.*;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.DisplayTextIdentifiers;
import com.vantiv.triposmobilesdk.enums.NumericInputType;
import com.vantiv.triposmobilesdk.enums.SelectionType;
import com.vantiv.triposmobilesdk.VtpProcessStatusListener;
import com.vantiv.triposmobilesdk.VtpStatus;
import com.vantiv.triposmobilesdk.express.Credentials;
import com.vantiv.triposmobilesdk.express.Application;
import com.vantiv.triposmobilesdk.requests.*;
import com.vantiv.triposmobilesdk.responses.*;
import com.vantiv.triposmobilesdk.storeandforward.StoredTransactionRecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DeviceManager {

    private VTP vtp;
    private SoftposConfig currentConfig;

    // ── Initialization ──

    public void initialize(Context context, SoftposConfig softposConfig, InitializationListener listener) {

        if (isInitialized()) {
            if (listener != null) listener.onInitializationError(SdkError.ALREADY_INITIALIZED);
            return;
        }

        Context appContext = context.getApplicationContext();
        DeviceCapabilities capabilities = DeviceCapabilityChecker.check(appContext);

        // Check NFC hardware
        if (!capabilities.isNfcAvailable()) {
            if (listener != null) listener.onInitializationError(SdkError.NFC_NOT_AVAILABLE);
            return;
        }

        // Check NFC enabled
        if (!capabilities.isNfcEnabled()) {
            if (listener != null) listener.onInitializationError(SdkError.NFC_DISABLED);
            return;
        }

        // Check screen lock (SoftPOS security requirement)
        if (!capabilities.isScreenLockSet()) {
            if (listener != null) listener.onInitializationError(SdkError.SCREEN_LOCK_NOT_SET);
            return;
        }

        // Check device security (root detection)
        if (!capabilities.isDeviceSecure()) {
            if (listener != null) listener.onInitializationError(SdkError.DEVICE_ROOTED);
            return;
        }

        // Check Android API level
        if (capabilities.getAndroidApiLevel() < 24) {
            if (listener != null) listener.onInitializationError(SdkError.ANDROID_VERSION_NOT_SUPPORTED);
            return;
        }

        // Check internet
        if (!capabilities.isInternetAvailable()) {
            if (listener != null) listener.onInitializationError(SdkError.NO_INTERNET);
            return;
        }

        // Notify capabilities validated
        if (listener != null) listener.onInitialized(capabilities);

        // Build triPOS configuration
        Configuration configuration;
        try {
            configuration = TriposConfigurationBuilder.build(softposConfig);
        } catch (Exception e) {
            if (listener != null) listener.onInitializationError(SdkError.INVALID_CONFIGURATION);
            return;
        }

        try {
            vtp = triPOSMobileSDK.getSharedVtp();
            currentConfig = softposConfig;

            vtp.initialize(
                appContext,
                configuration,
                new DeviceConnectionListener() {

                    @Override
                    public void onConnected(Device device, String description, String model, String serialNumber) {
                        if (listener != null) listener.onDeviceConnected(description, model, serialNumber);
                    }

                    @Override
                    public void onDisconnected(Device device) {
                        if (listener != null) listener.onDeviceDisconnected();
                    }

                    @Override
                    public void onError(Exception e) {
                        if (listener != null)
                            listener.onInitializationError(SdkError.DEVICE_CONNECTION_FAILED);
                    }

                    @Override
                    public void onBatteryLow() {
                        if (listener != null) listener.onBatteryLow();
                    }

                    @Override
                    public void onWarning(Exception e) {
                    }
                });

        } catch (Exception e) {
            if (listener != null)
                listener.onInitializationError(SdkError.fromException(2020, e));
        }
    }

    public void deinitialize() {
        if (vtp != null) {
            try {
                vtp.deinitialize();
            } catch (Exception ignored) {
            }
            vtp = null;
            currentConfig = null;
        }
    }

    public boolean isInitialized() {
        return vtp != null && vtp.getIsInitialized();
    }

    public void cancelCurrentTransaction() {
        if (vtp != null) {
            try {
                vtp.cancelCurrentFlow();
            } catch (Exception ignored) {
            }
        }
    }

    // ── Sale ──

    public void processSale(BigDecimal amount, String referenceNumber,
                            CardReaderListener cardReaderListener, TransactionListener listener) {
        if (!validateTransactionPreConditions(amount, referenceNumber, listener)) return;

        setupStatusListener(cardReaderListener);

        try {
            SaleRequest request = new SaleRequest();
            request.setTransactionAmount(amount);
            request.setReferenceNumber(referenceNumber);
            request.setCardholderPresentCode(CardHolderPresentCode.Present);
            request.setContactlessAllowed(true);

            vtp.processSaleRequest(request, new SaleRequestListener() {
                @Override
                public void onSaleRequestCompleted(SaleResponse response) {
                    if (listener != null) {
                        listener.onTransactionComplete(buildTransactionResult(
                                response, TransactionType.SALE, amount, referenceNumber));
                    }
                }

                @Override
                public void onSaleRequestError(Exception e) {
                    if (listener != null) listener.onTransactionError(SdkError.fromException(4001, e));
                }
            }, createDeviceInteractionListener(cardReaderListener));
        } catch (Exception e) {
            if (listener != null) listener.onTransactionError(SdkError.fromException(4001, e));
        }
    }

    // ── Authorization ──

    public void processAuthorization(BigDecimal amount, String referenceNumber,
                                     CardReaderListener cardReaderListener, TransactionListener listener) {
        if (!validateTransactionPreConditions(amount, referenceNumber, listener)) return;

        setupStatusListener(cardReaderListener);

        try {
            AuthorizationRequest request = new AuthorizationRequest();
            request.setTransactionAmount(amount);
            request.setReferenceNumber(referenceNumber);
            request.setCardholderPresentCode(CardHolderPresentCode.Present);
            request.setContactlessAllowed(true);

            vtp.processAuthorizationRequest(request, new AuthorizationRequestListener() {
                @Override
                public void onAuthorizationRequestCompleted(AuthorizationResponse response) {
                    if (listener != null) {
                        listener.onTransactionComplete(buildTransactionResult(
                                response, TransactionType.AUTHORIZATION, amount, referenceNumber));
                    }
                }

                @Override
                public void onAuthorizationRequestError(Exception e) {
                    if (listener != null) listener.onTransactionError(SdkError.fromException(4020, e));
                }
            }, createDeviceInteractionListener(cardReaderListener));
        } catch (Exception e) {
            if (listener != null) listener.onTransactionError(SdkError.fromException(4020, e));
        }
    }

    // ── Authorization Completion ──

    public void processAuthorizationCompletion(String transactionId, BigDecimal amount,
                                               TransactionListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onTransactionError(SdkError.NOT_INITIALIZED);
            return;
        }
        if (transactionId == null || transactionId.isEmpty()) {
            if (listener != null) listener.onTransactionError(SdkError.MISSING_TRANSACTION_ID);
            return;
        }

        try {
            AuthorizationCompletionRequest request = new AuthorizationCompletionRequest();
            request.setTransactionId(transactionId);
            if (amount != null) {
                request.setTransactionAmount(amount);
            }

            vtp.processAuthorizationCompletionRequest(request, new AuthorizationCompletionRequestListener() {
                @Override
                public void onAuthorizationCompletionRequestCompleted(AuthorizationCompletionResponse response) {
                    if (listener != null) {
                        TransactionResult.Builder builder = new TransactionResult.Builder()
                                .setType(TransactionType.AUTHORIZATION_COMPLETION)
                                .setStatus(mapStatus(response.getTransactionStatus()))
                                .setTransactionId(transactionId)
                                .setTransactionAmount(amount);

                        populateHostFields(builder, response);
                        listener.onTransactionComplete(builder.build());
                    }
                }

                @Override
                public void onAuthorizationCompletionRequestError(Exception e) {
                    if (listener != null) listener.onTransactionError(SdkError.fromException(4021, e));
                }
            });
        } catch (Exception e) {
            if (listener != null) listener.onTransactionError(SdkError.fromException(4021, e));
        }
    }

    // ── Refund ──

    public void processRefund(BigDecimal amount, String referenceNumber,
                              CardReaderListener cardReaderListener, TransactionListener listener) {
        if (!validateTransactionPreConditions(amount, referenceNumber, listener)) return;

        setupStatusListener(cardReaderListener);

        try {
            RefundRequest request = new RefundRequest();
            request.setTransactionAmount(amount);
            request.setReferenceNumber(referenceNumber);
            request.setCardholderPresentCode(CardHolderPresentCode.Present);
            request.setContactlessAllowed(true);

            vtp.processRefundRequest(request, new RefundRequestListener() {
                @Override
                public void onRefundRequestCompleted(RefundResponse response) {
                    if (listener != null) {
                        listener.onTransactionComplete(buildTransactionResult(
                                response, TransactionType.REFUND, amount, referenceNumber));
                    }
                }

                @Override
                public void onRefundRequestError(Exception e) {
                    if (listener != null) listener.onTransactionError(SdkError.fromException(4011, e));
                }
            }, createDeviceInteractionListener(cardReaderListener));
        } catch (Exception e) {
            if (listener != null) listener.onTransactionError(SdkError.fromException(4011, e));
        }
    }

    // ── Void ──

    public void processVoid(String transactionId, TransactionListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onTransactionError(SdkError.NOT_INITIALIZED);
            return;
        }
        if (transactionId == null || transactionId.isEmpty()) {
            if (listener != null) listener.onTransactionError(SdkError.MISSING_TRANSACTION_ID);
            return;
        }

        try {
            VoidRequest request = new VoidRequest();
            request.setTransactionID(transactionId);

            vtp.processVoidRequest(request, new VoidRequestListener() {
                @Override
                public void onVoidRequestCompleted(VoidResponse response) {
                    if (listener != null) {
                        TransactionResult.Builder builder = new TransactionResult.Builder()
                                .setType(TransactionType.VOID)
                                .setStatus(TransactionStatus.VOIDED)
                                .setTransactionId(transactionId);

                        populateHostFields(builder, response);
                        listener.onTransactionComplete(builder.build());
                    }
                }

                @Override
                public void onVoidRequestError(Exception e) {
                    if (listener != null) listener.onTransactionError(SdkError.fromException(4010, e));
                }
            });
        } catch (Exception e) {
            if (listener != null) listener.onTransactionError(SdkError.fromException(4010, e));
        }
    }

    // ── Return ──

    public void processReturn(BigDecimal amount, String referenceNumber,
                              CardReaderListener cardReaderListener, TransactionListener listener) {
        if (!validateTransactionPreConditions(amount, referenceNumber, listener)) return;

        setupStatusListener(cardReaderListener);

        try {
            ReturnRequest request = new ReturnRequest();
            request.setTransactionAmount(amount);
            request.setReferenceNumber(referenceNumber);
            request.setCardholderPresentCode(CardHolderPresentCode.Present);

            vtp.processReturnRequest(request, new ReturnListener() {
                @Override
                public void onReturnRequestCompleted(ReturnResponse response) {
                    if (listener != null) {
                        TransactionResult.Builder builder = new TransactionResult.Builder()
                                .setType(TransactionType.RETURN)
                                .setStatus(TransactionStatus.RETURNED)
                                .setTransactionAmount(amount)
                                .setReferenceNumber(referenceNumber);

                        populateHostFields(builder, response);
                        listener.onTransactionComplete(builder.build());
                    }
                }

                @Override
                public void onReturnRequestError(Exception e) {
                    if (listener != null) listener.onTransactionError(SdkError.fromException(4030, e));
                }
            });
        } catch (Exception e) {
            if (listener != null) listener.onTransactionError(SdkError.fromException(4030, e));
        }
    }

    // ── Reversal ──

    public void processReversal(String transactionId, TransactionListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onTransactionError(SdkError.NOT_INITIALIZED);
            return;
        }
        if (transactionId == null || transactionId.isEmpty()) {
            if (listener != null) listener.onTransactionError(SdkError.MISSING_TRANSACTION_ID);
            return;
        }

        try {
            ReversalRequest request = new ReversalRequest();
            request.setTransactionId(transactionId);

            vtp.processReversalRequest(request, new ReversalRequestListener() {
                @Override
                public void onReversalRequestCompleted(ReversalResponse response) {
                    if (listener != null) {
                        TransactionResult.Builder builder = new TransactionResult.Builder()
                                .setType(TransactionType.REVERSAL)
                                .setStatus(TransactionStatus.REVERSED)
                                .setTransactionId(transactionId);

                        listener.onTransactionComplete(builder.build());
                    }
                }

                @Override
                public void onReversalRequestError(Exception e) {
                    if (listener != null) listener.onTransactionError(SdkError.fromException(4031, e));
                }
            });
        } catch (Exception e) {
            if (listener != null) listener.onTransactionError(SdkError.fromException(4031, e));
        }
    }

    // ── Health Check ──

    public void performHealthCheck(HealthCheckListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onHealthCheckError(SdkError.NOT_INITIALIZED);
            return;
        }

        try {
            HealthCheckRequest request = new HealthCheckRequest();

            if (currentConfig != null) {
                request.setCredentials(new Credentials(
                        currentConfig.getAcceptorId(),
                        currentConfig.getAccountToken(),
                        currentConfig.getAccountId()));
                request.setApplication(new Application(
                        currentConfig.getApplicationId(),
                        currentConfig.getApplicationName(),
                        currentConfig.getApplicationVersion()));
            }

            vtp.processHealthCheckRequest(request, new HealthCheckRequestListener() {
                @Override
                public void onHealthCheckRequestCompleted(HealthCheckResponse response) {
                    if (listener != null) {
                        listener.onHealthCheckComplete(true, "System is healthy");
                    }
                }

                @Override
                public void onHealthCheckRequestError(Exception e) {
                    if (listener != null) listener.onHealthCheckError(SdkError.fromException(4040, e));
                }
            });
        } catch (Exception e) {
            if (listener != null) listener.onHealthCheckError(SdkError.fromException(4040, e));
        }
    }

    // ── Pre-Read Card Data ──

    public void preReadCardData(CardReaderListener cardReaderListener, TransactionListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onTransactionError(SdkError.NOT_INITIALIZED);
            return;
        }

        setupStatusListener(cardReaderListener);

        try {
            PreReadRequest request = new PreReadRequest();

            vtp.processPreReadRequestWithTransactionType(request, new PreReadCardDataRequestListener() {
                @Override
                public void onPreReadRequestCompleted(PreReadCardDataResponse response) {
                    if (listener != null) {
                        TransactionResult.Builder builder = new TransactionResult.Builder()
                                .setType(TransactionType.PRE_READ_CARD)
                                .setStatus(TransactionStatus.APPROVED);

                        listener.onTransactionComplete(builder.build());
                    }
                }

                @Override
                public void onPreReadRequestError(Exception e) {
                    if (listener != null) listener.onTransactionError(SdkError.fromException(4050, e));
                }
            }, createDeviceInteractionListener(cardReaderListener));
        } catch (Exception e) {
            if (listener != null) listener.onTransactionError(SdkError.fromException(4050, e));
        }
    }

    // ── Store & Forward ──

    public void getStoredTransactions(StoreAndForwardListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onStoreForwardError(SdkError.NOT_INITIALIZED);
            return;
        }

        try {
            List<StoredTransactionRecord> records = vtp.getAllStoredTransactions();
            List<StoredTransaction> transactions = mapStoredTransactions(records);
            if (listener != null) listener.onStoredTransactionsRetrieved(transactions);
        } catch (Exception e) {
            if (listener != null) listener.onStoreForwardError(SdkError.fromException(4500, e));
        }
    }

    public void forwardStoredTransaction(String transactionId, StoreAndForwardListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onStoreForwardError(SdkError.NOT_INITIALIZED);
            return;
        }
        if (transactionId == null || transactionId.isEmpty()) {
            if (listener != null) listener.onStoreForwardError(SdkError.MISSING_TRANSACTION_ID);
            return;
        }

        try {
            ManuallyForwardRequest request = new ManuallyForwardRequest();
            request.setTpId(transactionId);

            vtp.processManuallyForwardRequest(request, new ManuallyForwardRequestListener() {
                @Override
                public void onManuallyForwardRequestCompleted(ManuallyForwardResponse response) {
                    if (listener != null) {
                        listener.onTransactionForwarded(transactionId, true);
                    }
                }

                @Override
                public void onManuallyForwardRequestError(Exception e) {
                    if (listener != null) listener.onStoreForwardError(SdkError.fromException(4502, e));
                }
            });
        } catch (Exception e) {
            if (listener != null) listener.onStoreForwardError(SdkError.fromException(4502, e));
        }
    }

    public void deleteStoredTransactions(StoreAndForwardListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onStoreForwardError(SdkError.NOT_INITIALIZED);
            return;
        }

        try {
            // Delete all stored transactions by forwarding first, then clearing
            List<StoredTransactionRecord> records = vtp.getAllStoredTransactions();
            if (records != null) {
                for (StoredTransactionRecord record : records) {
                    try {
                        vtp.deleteStoredTransactionWithStateStored(record.getTpId());
                    } catch (Throwable ignored) {
                    }
                }
            }
            if (listener != null) listener.onTransactionDeleted("all", true);
        } catch (Exception e) {
            if (listener != null) listener.onStoreForwardError(SdkError.fromException(4500, e));
        }
    }

    // ── VTP Status Listener ──

    private void setupStatusListener(CardReaderListener cardReaderListener) {
        if (vtp == null) return;

        vtp.setStatusListener(status -> {
            if (cardReaderListener == null) return;

            String statusName = status != null ? status.name() : "Unknown";
            cardReaderListener.onTransactionStatus(statusName);

            if (status == VtpStatus.CardRemoved) {
                cardReaderListener.onCardRemoved();
            } else if (status == VtpStatus.TransactionCancelled) {
                cardReaderListener.onTransactionStatus("Transaction Cancelled");
            }
        });
    }

    // ── Device Interaction Listener ──

    private DeviceInteractionListener createDeviceInteractionListener(CardReaderListener cardReaderListener) {
        return new DeviceInteractionListener() {

            @Override
            public void onPromptUserForCard(String message) {
                if (cardReaderListener != null) cardReaderListener.onPromptForCard(message);
            }

            @Override
            public void onPromptUserForCard(String message, DisplayTextIdentifiers displayTextIdentifiers) {
                if (cardReaderListener != null) cardReaderListener.onPromptForCard(message);
            }

            @Override
            public void onDisplayText(String message) {
                if (cardReaderListener != null) cardReaderListener.onDisplayMessage(message);
            }

            @Override
            public void onDisplayText(String message, DisplayTextIdentifiers displayTextIdentifiers) {
                if (cardReaderListener != null) {
                    cardReaderListener.onDisplayMessage(message);
                    cardReaderListener.onTransactionStatus(message);
                }
            }

            @Override
            public void onWait(String message) {
                if (cardReaderListener != null) cardReaderListener.onDisplayMessage(message);
            }

            @Override
            public void onRemoveCard() {
                if (cardReaderListener != null) cardReaderListener.onRemoveCard();
            }

            @Override
            public void onCardRemoved() {
                if (cardReaderListener != null) cardReaderListener.onCardRemoved();
            }

            @Override
            public void onAmountConfirmation(com.vantiv.triposmobilesdk.enums.AmountConfirmationType type,
                                             BigDecimal amount,
                                             DeviceInteractionListener.ConfirmAmountListener confirmListener) {
                if (cardReaderListener != null) {
                    String typeStr = type != null ? type.name() : "Amount";
                    cardReaderListener.onAmountConfirmation(amount, typeStr,
                            accepted -> confirmListener.confirmAmount(accepted));
                } else {
                    confirmListener.confirmAmount(true);
                }
            }

            @Override
            public void onChoiceSelections(String[] choices, SelectionType selectionType,
                                           DeviceInteractionListener.SelectChoiceListener choiceListener) {
                if (cardReaderListener != null && choices != null && choices.length > 0) {
                    cardReaderListener.onApplicationSelection(choices,
                            index -> choiceListener.selectChoice(index));
                } else if (choices != null && choices.length > 0) {
                    choiceListener.selectChoice(0);
                }
            }

            @Override
            public void onNumericInput(NumericInputType type,
                                       DeviceInteractionListener.NumericInputListener inputListener) {
                if (cardReaderListener != null) {
                    cardReaderListener.onDisplayMessage("Numeric input required: " + type.name());
                }
            }

            @Override
            public void onSelectApplication(String[] apps,
                                            DeviceInteractionListener.SelectChoiceListener choiceListener) {
                if (cardReaderListener != null && apps != null && apps.length > 0) {
                    cardReaderListener.onApplicationSelection(apps,
                            index -> choiceListener.selectChoice(index));
                } else if (apps != null && apps.length > 0) {
                    choiceListener.selectChoice(0);
                }
            }

            @Override
            public void onVerifySignature(TransactionResponse response,
                                          DeviceInteractionListener.VerifySignatureListener signatureListener) {
                if (cardReaderListener != null) {
                    cardReaderListener.onSignatureRequired(new CardReaderListener.SignatureCallback() {
                        @Override
                        public void approveSignature() {
                            signatureListener.approveSignature();
                        }

                        @Override
                        public void declineSignature() {
                            signatureListener.declineSignature();
                        }
                    });
                } else {
                    signatureListener.approveSignature();
                }
            }
        };
    }

    // ── Result Builders ──

    private TransactionResult buildTransactionResult(TransactionResponse response,
                                                     TransactionType type,
                                                     BigDecimal amount,
                                                     String referenceNumber) {
        TransactionResult.Builder builder = new TransactionResult.Builder()
                .setType(type)
                .setStatus(mapStatus(response.getTransactionStatus()))
                .setApprovedAmount(response.getApprovedAmount())
                .setTransactionAmount(amount)
                .setReferenceNumber(referenceNumber)
                .setSignatureRequired(response.isSignatureRequired());

        populateHostFields(builder, response);
        return builder.build();
    }

    private void populateHostFields(TransactionResult.Builder builder, TransactionResponse response) {
        try {
            if (response.getHost() != null) {
                builder.setTransactionId(response.getHost().getTransactionID());
                try {
                    builder.setAuthorizationCode(response.getHost().getApprovalNumber());
                } catch (Exception ignored) {
                }
                try {
                    builder.setResponseCode(response.getHost().getHostResponseCode());
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

        try {
            if (response.getCard() != null) {
                builder.setCardLastFour(response.getCard().getMaskedAccountNumber());
                try {
                    Object logo = response.getCard().getCardLogo();
                    builder.setCardType(logo != null ? logo.toString() : null);
                } catch (Exception ignored) {
                }
                try {
                    Object entryMode = response.getCard().getEntryMode();
                    builder.setEntryMode(entryMode != null ? entryMode.toString() : null);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    private TransactionStatus mapStatus(com.vantiv.triposmobilesdk.enums.TransactionStatus vendorStatus) {
        if (vendorStatus == null) return TransactionStatus.UNKNOWN;

        switch (vendorStatus) {
            case Approved:
                return TransactionStatus.APPROVED;
            case PartiallyApproved:
                return TransactionStatus.PARTIALLY_APPROVED;
            case Declined:
                return TransactionStatus.DECLINED;
            default:
                return TransactionStatus.UNKNOWN;
        }
    }

    // ── Validation ──

    private boolean validateTransactionPreConditions(BigDecimal amount, String referenceNumber,
                                                     TransactionListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onTransactionError(SdkError.NOT_INITIALIZED);
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            if (listener != null) listener.onTransactionError(SdkError.INVALID_AMOUNT);
            return false;
        }
        if (referenceNumber == null || referenceNumber.isEmpty()) {
            if (listener != null) listener.onTransactionError(SdkError.MISSING_REFERENCE_NUMBER);
            return false;
        }
        return true;
    }

    // ── Store & Forward Helpers ──

    private List<StoredTransaction> mapStoredTransactions(List<StoredTransactionRecord> records) {
        List<StoredTransaction> result = new ArrayList<>();
        if (records == null) return result;

        for (StoredTransactionRecord record : records) {
            try {
                result.add(new StoredTransaction(
                        record.getTpId(),
                        null,
                        null,
                        record.getState() != null ? record.getState().name() : "Unknown",
                        record.getTransactionType() != null ? record.getTransactionType().name() : "Unknown",
                        null
                ));
            } catch (Exception ignored) {
            }
        }
        return result;
    }
}