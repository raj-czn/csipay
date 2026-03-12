package com.csipay.softpos.transaction;

import java.math.BigDecimal;

public class TransactionResult {

    private final TransactionType type;
    private final TransactionStatus status;
    private final BigDecimal approvedAmount;
    private final BigDecimal transactionAmount;
    private final String referenceNumber;
    private final String transactionId;
    private final String paymentType;
    private final boolean signatureRequired;
    private final String cardLastFour;
    private final String cardType;

    TransactionResult(Builder builder) {
        this.type = builder.type;
        this.status = builder.status;
        this.approvedAmount = builder.approvedAmount;
        this.transactionAmount = builder.transactionAmount;
        this.referenceNumber = builder.referenceNumber;
        this.transactionId = builder.transactionId;
        this.paymentType = builder.paymentType;
        this.signatureRequired = builder.signatureRequired;
        this.cardLastFour = builder.cardLastFour;
        this.cardType = builder.cardType;
    }

    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getTransactionId() { return transactionId; }
    public String getPaymentType() { return paymentType; }
    public boolean isSignatureRequired() { return signatureRequired; }
    public String getCardLastFour() { return cardLastFour; }
    public String getCardType() { return cardType; }

    public static class Builder {

        private TransactionType type;
        private TransactionStatus status = TransactionStatus.UNKNOWN;
        private BigDecimal approvedAmount;
        private BigDecimal transactionAmount;
        private String referenceNumber;
        private String transactionId;
        private String paymentType;
        private boolean signatureRequired;
        private String cardLastFour;
        private String cardType;

        public Builder setType(TransactionType type) { this.type = type; return this; }
        public Builder setStatus(TransactionStatus status) { this.status = status; return this; }
        public Builder setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; return this; }
        public Builder setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; return this; }
        public Builder setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public Builder setTransactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public Builder setPaymentType(String paymentType) { this.paymentType = paymentType; return this; }
        public Builder setSignatureRequired(boolean signatureRequired) { this.signatureRequired = signatureRequired; return this; }
        public Builder setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; return this; }
        public Builder setCardType(String cardType) { this.cardType = cardType; return this; }

        public TransactionResult build() { return new TransactionResult(this); }
    }
}