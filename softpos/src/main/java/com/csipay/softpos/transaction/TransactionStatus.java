package com.csipay.softpos.transaction;

public enum TransactionStatus {
    APPROVED,
    PARTIALLY_APPROVED,
    DECLINED,
    VOIDED,
    REVERSED,
    RETURNED,
    CANCELLED,
    ERROR,
    UNKNOWN
}