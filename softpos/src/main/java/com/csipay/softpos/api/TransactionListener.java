package com.csipay.softpos.api;

import com.csipay.softpos.transaction.TransactionResult;

public interface TransactionListener {

    void onTransactionComplete(TransactionResult result);

    void onTransactionError(String error);
}
