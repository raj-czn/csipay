package com.csipay.softpos.config;

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