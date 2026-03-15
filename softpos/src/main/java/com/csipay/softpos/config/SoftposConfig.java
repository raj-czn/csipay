package com.csipay.softpos.config;

import java.math.BigDecimal;

public class SoftposConfig {

    private final String acceptorId;
    private final String accountId;
    private final String accountToken;
    private final String applicationId;
    private final String applicationName;
    private final String applicationVersion;
    private final String terminalId;
    private final Environment environment;
    private final String currencyCode;
    private final String paymentProcessor;
    private final boolean tipAllowed;
    private final boolean cashbackAllowed;
    private final boolean debitAllowed;
    private final boolean emvAllowed;
    private final boolean contactlessAllowed;
    private final boolean quickChipAllowed;
    private final boolean storeAndForwardEnabled;
    private final int storeAndForwardRetentionDays;
    private final BigDecimal storeAndForwardAmountLimit;
    private final int sleepTimeoutSeconds;
    private final String vaultId;

    private SoftposConfig(Builder builder) {
        this.acceptorId = builder.acceptorId;
        this.accountId = builder.accountId;
        this.accountToken = builder.accountToken;
        this.applicationId = builder.applicationId;
        this.applicationName = builder.applicationName;
        this.applicationVersion = builder.applicationVersion;
        this.terminalId = builder.terminalId;
        this.environment = builder.environment;
        this.currencyCode = builder.currencyCode;
        this.paymentProcessor = builder.paymentProcessor;
        this.tipAllowed = builder.tipAllowed;
        this.cashbackAllowed = builder.cashbackAllowed;
        this.debitAllowed = builder.debitAllowed;
        this.emvAllowed = builder.emvAllowed;
        this.contactlessAllowed = builder.contactlessAllowed;
        this.quickChipAllowed = builder.quickChipAllowed;
        this.storeAndForwardEnabled = builder.storeAndForwardEnabled;
        this.storeAndForwardRetentionDays = builder.storeAndForwardRetentionDays;
        this.storeAndForwardAmountLimit = builder.storeAndForwardAmountLimit;
        this.sleepTimeoutSeconds = builder.sleepTimeoutSeconds;
        this.vaultId = builder.vaultId;
    }

    public String getAcceptorId() { return acceptorId; }
    public String getAccountId() { return accountId; }
    public String getAccountToken() { return accountToken; }
    public String getApplicationId() { return applicationId; }
    public String getApplicationName() { return applicationName; }
    public String getApplicationVersion() { return applicationVersion; }
    public String getTerminalId() { return terminalId; }
    public Environment getEnvironment() { return environment; }
    public String getCurrencyCode() { return currencyCode; }
    public String getPaymentProcessor() { return paymentProcessor; }
    public boolean isTipAllowed() { return tipAllowed; }
    public boolean isCashbackAllowed() { return cashbackAllowed; }
    public boolean isDebitAllowed() { return debitAllowed; }
    public boolean isEmvAllowed() { return emvAllowed; }
    public boolean isContactlessAllowed() { return contactlessAllowed; }
    public boolean isQuickChipAllowed() { return quickChipAllowed; }
    public boolean isStoreAndForwardEnabled() { return storeAndForwardEnabled; }
    public int getStoreAndForwardRetentionDays() { return storeAndForwardRetentionDays; }
    public BigDecimal getStoreAndForwardAmountLimit() { return storeAndForwardAmountLimit; }
    public int getSleepTimeoutSeconds() { return sleepTimeoutSeconds; }
    public String getVaultId() { return vaultId; }

    public static class Builder {

        private String acceptorId;
        private String accountId;
        private String accountToken;
        private String applicationId;
        private String applicationName;
        private String applicationVersion;
        private String terminalId;
        private Environment environment = Environment.TEST;
        private String currencyCode = "USD";
        private String paymentProcessor = "Worldpay";
        private boolean tipAllowed = false;
        private boolean cashbackAllowed = false;
        private boolean debitAllowed = true;
        private boolean emvAllowed = true;
        private boolean contactlessAllowed = true;
        private boolean quickChipAllowed = true;
        private boolean storeAndForwardEnabled = true;
        private int storeAndForwardRetentionDays = 1;
        private BigDecimal storeAndForwardAmountLimit = new BigDecimal("50.00");
        private int sleepTimeoutSeconds = 300;
        private String vaultId;

        public Builder setAcceptorId(String acceptorId) { this.acceptorId = acceptorId; return this; }
        public Builder setAccountId(String accountId) { this.accountId = accountId; return this; }
        public Builder setAccountToken(String accountToken) { this.accountToken = accountToken; return this; }
        public Builder setApplicationId(String applicationId) { this.applicationId = applicationId; return this; }
        public Builder setApplicationName(String applicationName) { this.applicationName = applicationName; return this; }
        public Builder setApplicationVersion(String applicationVersion) { this.applicationVersion = applicationVersion; return this; }
        public Builder setTerminalId(String terminalId) { this.terminalId = terminalId; return this; }
        public Builder setEnvironment(Environment environment) { this.environment = environment; return this; }
        public Builder setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; return this; }
        public Builder setPaymentProcessor(String paymentProcessor) { this.paymentProcessor = paymentProcessor; return this; }
        public Builder setTipAllowed(boolean tipAllowed) { this.tipAllowed = tipAllowed; return this; }
        public Builder setCashbackAllowed(boolean cashbackAllowed) { this.cashbackAllowed = cashbackAllowed; return this; }
        public Builder setDebitAllowed(boolean debitAllowed) { this.debitAllowed = debitAllowed; return this; }
        public Builder setEmvAllowed(boolean emvAllowed) { this.emvAllowed = emvAllowed; return this; }
        public Builder setContactlessAllowed(boolean contactlessAllowed) { this.contactlessAllowed = contactlessAllowed; return this; }
        public Builder setQuickChipAllowed(boolean quickChipAllowed) { this.quickChipAllowed = quickChipAllowed; return this; }
        public Builder setStoreAndForwardEnabled(boolean enabled) { this.storeAndForwardEnabled = enabled; return this; }
        public Builder setStoreAndForwardRetentionDays(int days) { this.storeAndForwardRetentionDays = days; return this; }
        public Builder setStoreAndForwardAmountLimit(BigDecimal limit) { this.storeAndForwardAmountLimit = limit; return this; }
        public Builder setSleepTimeoutSeconds(int seconds) { this.sleepTimeoutSeconds = seconds; return this; }
        public Builder setVaultId(String vaultId) { this.vaultId = vaultId; return this; }

        public SoftposConfig build() {
            requireNonEmpty(acceptorId, "acceptorId");
            requireNonEmpty(accountId, "accountId");
            requireNonEmpty(accountToken, "accountToken");
            requireNonEmpty(applicationId, "applicationId");
            requireNonEmpty(applicationName, "applicationName");
            requireNonEmpty(applicationVersion, "applicationVersion");
            requireNonEmpty(terminalId, "terminalId");

            if (environment == null) {
                throw new IllegalArgumentException("environment must not be null");
            }

            requireNonEmpty(currencyCode, "currencyCode");
            requireNonEmpty(paymentProcessor, "paymentProcessor");

            return new SoftposConfig(this);
        }

        private void requireNonEmpty(String value, String fieldName) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " must not be null or empty");
            }
        }
    }
}