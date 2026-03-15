package com.csipay.softpos.api;

import java.util.List;

public interface StoreAndForwardListener {

    void onStoredTransactionsRetrieved(List<StoredTransaction> transactions);

    void onTransactionForwarded(String transactionId, boolean success);

    void onTransactionDeleted(String transactionId, boolean success);

    void onStoreForwardError(SdkError error);
}