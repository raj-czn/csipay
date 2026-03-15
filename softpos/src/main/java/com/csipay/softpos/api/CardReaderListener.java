package com.csipay.softpos.api;

import java.math.BigDecimal;

public interface CardReaderListener {

    void onPromptForCard(String message);

    void onDisplayMessage(String message);

    void onRemoveCard();

    void onCardRemoved();

    void onCardReadError(String message);

    void onMultipleCardsDetected();

    void onAmountConfirmation(BigDecimal amount, String type, AmountConfirmCallback callback);

    void onPinEntryRequired();

    void onSignatureRequired(SignatureCallback callback);

    void onApplicationSelection(String[] applications, ApplicationSelectionCallback callback);

    void onTransactionStatus(String status);

    interface AmountConfirmCallback {
        void confirm(boolean accepted);
    }

    interface SignatureCallback {
        void approveSignature();
        void declineSignature();
    }

    interface ApplicationSelectionCallback {
        void selectApplication(int index);
    }
}