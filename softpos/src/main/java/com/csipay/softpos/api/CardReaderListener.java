package com.csipay.softpos.api;

public interface CardReaderListener {

    void onPromptForCard(String message);

    void onDisplayMessage(String message);

    void onRemoveCard();
}