package com.csipay.softpos.transaction;

import java.math.BigDecimal;

public class TransactionResult {

    private final TransactionType type;
    private final TransactionStatus status;
    private final BigDecimal approvedAmount;
    private final BigDecimal transactionAmount;
    private final BigDecimal tipAmount;
    private final String referenceNumber;
    private final String transactionId;
    private final String paymentType;
    private final boolean signatureRequired;
    private final String cardLastFour;
    private final String cardType;
    private final String authorizationCode;
    private final String entryMode;
    private final String responseCode;
    private final String responseMessage;
    private final String emvData;
    private final String merchantId;
    private final String terminalId;

    TransactionResult(Builder builder) {
        this.type = builder.type;
        this.status = builder.status;
        this.approvedAmount = builder.approvedAmount;
        this.transactionAmount = builder.transactionAmount;
        this.tipAmount = builder.tipAmount;
        this.referenceNumber = builder.referenceNumber;
        this.transactionId = builder.transactionId;
        this.paymentType = builder.paymentType;
        this.signatureRequired = builder.signatureRequired;
        this.cardLastFour = builder.cardLastFour;
        this.cardType = builder.cardType;
        this.authorizationCode = builder.authorizationCode;
        this.entryMode = builder.entryMode;
        this.responseCode = builder.responseCode;
        this.responseMessage = builder.responseMessage;
        this.emvData = builder.emvData;
        this.merchantId = builder.merchantId;
        this.terminalId = builder.terminalId;
    }

    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public BigDecimal getApprovedAmount() { return approvedAmount; }
    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public BigDecimal getTipAmount() { return tipAmount; }
    public String getReferenceNumber() { return referenceNumber; }
    public String getTransactionId() { return transactionId; }
    public String getPaymentType() { return paymentType; }
    public boolean isSignatureRequired() { return signatureRequired; }
    public String getCardLastFour() { return cardLastFour; }
    public String getCardType() { return cardType; }
    public String getAuthorizationCode() { return authorizationCode; }
    public String getEntryMode() { return entryMode; }
    public String getResponseCode() { return responseCode; }
    public String getResponseMessage() { return responseMessage; }
    public String getEmvData() { return emvData; }
    public String getMerchantId() { return merchantId; }
    public String getTerminalId() { return terminalId; }

    public static class Builder {

        private TransactionType type;
        private TransactionStatus status = TransactionStatus.UNKNOWN;
        private BigDecimal approvedAmount;
        private BigDecimal transactionAmount;
        private BigDecimal tipAmount;
        private String referenceNumber;
        private String transactionId;
        private String paymentType;
        private boolean signatureRequired;
        private String cardLastFour;
        private String cardType;
        private String authorizationCode;
        private String entryMode;
        private String responseCode;
        private String responseMessage;
        private String emvData;
        private String merchantId;
        private String terminalId;

        public Builder setType(TransactionType type) { this.type = type; return this; }
        public Builder setStatus(TransactionStatus status) { this.status = status; return this; }
        public Builder setApprovedAmount(BigDecimal approvedAmount) { this.approvedAmount = approvedAmount; return this; }
        public Builder setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; return this; }
        public Builder setTipAmount(BigDecimal tipAmount) { this.tipAmount = tipAmount; return this; }
        public Builder setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; return this; }
        public Builder setTransactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public Builder setPaymentType(String paymentType) { this.paymentType = paymentType; return this; }
        public Builder setSignatureRequired(boolean signatureRequired) { this.signatureRequired = signatureRequired; return this; }
        public Builder setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; return this; }
        public Builder setCardType(String cardType) { this.cardType = cardType; return this; }
        public Builder setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; return this; }
        public Builder setEntryMode(String entryMode) { this.entryMode = entryMode; return this; }
        public Builder setResponseCode(String responseCode) { this.responseCode = responseCode; return this; }
        public Builder setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; return this; }
        public Builder setEmvData(String emvData) { this.emvData = emvData; return this; }
        public Builder setMerchantId(String merchantId) { this.merchantId = merchantId; return this; }
        public Builder setTerminalId(String terminalId) { this.terminalId = terminalId; return this; }

        public TransactionResult build() { return new TransactionResult(this); }
    }
}