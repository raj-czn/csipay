package com.csipay.softpos.transaction;

public enum TransactionType {
    SALE,
    REFUND,
    VOID,
    AUTHORIZATION,
    AUTHORIZATION_COMPLETION,
    RETURN,
    REVERSAL,
    HEALTH_CHECK,
    PRE_READ_CARD
}
