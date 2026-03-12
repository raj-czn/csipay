package com.csipay.softpos.device;

import android.content.Context;

import com.csipay.softpos.api.CardReaderListener;
import com.csipay.softpos.api.InitializationListener;
import com.csipay.softpos.api.SdkError;
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
import com.vantiv.triposmobilesdk.requests.RefundRequest;
import com.vantiv.triposmobilesdk.requests.SaleRequest;
import com.vantiv.triposmobilesdk.requests.VoidRequest;
import com.vantiv.triposmobilesdk.responses.RefundResponse;
import com.vantiv.triposmobilesdk.responses.SaleResponse;
import com.vantiv.triposmobilesdk.responses.TransactionResponse;
import com.vantiv.triposmobilesdk.responses.VoidResponse;

import java.math.BigDecimal;

public class DeviceManager {

    private VTP vtp;

    public void initialize(Context context, SoftposConfig softposConfig, InitializationListener listener) {

        if (isInitialized()) {
            if (listener != null) listener.onInitializationError(SdkError.ALREADY_INITIALIZED);
            return;
        }

        // Validate device capabilities
        Context appContext = context.getApplicationContext();
        DeviceCapabilities capabilities = DeviceCapabilityChecker.check(appContext);

        if (!capabilities.isNfcAvailable()) {
            if (listener != null) {
                boolean isDisabled = false;
                for (String w : capabilities.getWarnings()) {
                    if (w.contains("disabled")) { isDisabled = true; break; }
                }
                listener.onInitializationError(
                        isDisabled ? SdkError.NFC_DISABLED : SdkError.NFC_NOT_AVAILABLE
                );
            }
            return;
        }

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

            vtp.initialize(
                appContext,
                configuration,
                new DeviceConnectionListener() {

                    @Override
                    public void onConnected(Device device, String s, String s1, String s2) {
                        if (listener != null) listener.onDeviceConnected();
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
            } catch (Exception e) {
                // Ignore cleanup errors
            }
            vtp = null;
        }
    }

    public boolean isInitialized() {
        return vtp != null && vtp.getIsInitialized();
    }

    public void processSale(BigDecimal amount, String referenceNumber,
                            CardReaderListener cardReaderListener, TransactionListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onTransactionError(SdkError.NOT_INITIALIZED);
            return;
        }

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
                        listener.onTransactionComplete(buildSaleResult(response, amount, referenceNumber));
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

    public void processRefund(BigDecimal amount, String referenceNumber,
                              CardReaderListener cardReaderListener, TransactionListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onTransactionError(SdkError.NOT_INITIALIZED);
            return;
        }

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
                        listener.onTransactionComplete(buildRefundResult(response, amount, referenceNumber));
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

    public void processVoid(String transactionId, TransactionListener listener) {
        if (!isInitialized()) {
            if (listener != null) listener.onTransactionError(SdkError.NOT_INITIALIZED);
            return;
        }

        try {
            VoidRequest request = new VoidRequest();
            request.setTransactionID(transactionId);

            vtp.processVoidRequest(request, new VoidRequestListener() {
                @Override
                public void onVoidRequestCompleted(VoidResponse response) {
                    if (listener != null) {
                        listener.onTransactionComplete(buildVoidResult(response, transactionId));
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
                if (cardReaderListener != null) cardReaderListener.onDisplayMessage(message);
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
            }

            @Override
            public void onAmountConfirmation(com.vantiv.triposmobilesdk.enums.AmountConfirmationType type,
                                             BigDecimal amount,
                                             DeviceInteractionListener.ConfirmAmountListener confirmListener) {
                confirmListener.confirmAmount(true);
            }

            @Override
            public void onChoiceSelections(String[] choices, SelectionType selectionType,
                                           DeviceInteractionListener.SelectChoiceListener choiceListener) {
                if (choices != null && choices.length > 0) {
                    choiceListener.selectChoice(0);
                }
            }

            @Override
            public void onNumericInput(NumericInputType type,
                                       DeviceInteractionListener.NumericInputListener inputListener) {
            }

            @Override
            public void onSelectApplication(String[] apps,
                                            DeviceInteractionListener.SelectChoiceListener choiceListener) {
                if (apps != null && apps.length > 0) {
                    choiceListener.selectChoice(0);
                }
            }

            @Override
            public void onVerifySignature(TransactionResponse response,
                                          DeviceInteractionListener.VerifySignatureListener signatureListener) {
                signatureListener.approveSignature();
            }
        };
    }

    private TransactionResult buildSaleResult(SaleResponse response, BigDecimal amount, String referenceNumber) {
        TransactionResult.Builder builder = new TransactionResult.Builder()
                .setType(TransactionType.SALE)
                .setStatus(mapStatus(response.getTransactionStatus()))
                .setApprovedAmount(response.getApprovedAmount())
                .setTransactionAmount(amount)
                .setReferenceNumber(referenceNumber)
                .setSignatureRequired(response.isSignatureRequired());

        if (response.getHost() != null) {
            builder.setTransactionId(response.getHost().getTransactionID());
        }

        return builder.build();
    }

    private TransactionResult buildRefundResult(RefundResponse response, BigDecimal amount, String referenceNumber) {
        TransactionResult.Builder builder = new TransactionResult.Builder()
                .setType(TransactionType.REFUND)
                .setStatus(mapStatus(response.getTransactionStatus()))
                .setApprovedAmount(response.getApprovedAmount())
                .setTransactionAmount(amount)
                .setReferenceNumber(referenceNumber);

        if (response.getHost() != null) {
            builder.setTransactionId(response.getHost().getTransactionID());
        }

        return builder.build();
    }

    private TransactionResult buildVoidResult(VoidResponse response, String transactionId) {
        TransactionResult.Builder builder = new TransactionResult.Builder()
                .setType(TransactionType.VOID)
                .setStatus(TransactionStatus.VOIDED)
                .setTransactionId(transactionId);

        if (response.getHost() != null) {
            builder.setTransactionId(response.getHost().getTransactionID());
        }

        return builder.build();
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
}