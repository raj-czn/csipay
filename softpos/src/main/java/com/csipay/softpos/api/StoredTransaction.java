package com.csipay.softpos.api;

import java.math.BigDecimal;

public class StoredTransaction {

    private final String transactionId;
    private final String referenceNumber;
    private final BigDecimal amount;
    private final String state;
    private final String type;
    private final String timestamp;

    public StoredTransaction(String transactionId, String referenceNumber,
                             BigDecimal amount, String state, String type,
                             String timestamp) {
        this.transactionId = transactionId;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.state = state;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getTransactionId() { return transactionId; }
    public String getReferenceNumber() { return referenceNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getState() { return state; }
    public String getType() { return type; }
    public String getTimestamp() { return timestamp; }
}