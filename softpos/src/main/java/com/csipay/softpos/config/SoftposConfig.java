package com.csipay.softpos.config;

public class SoftposConfig {

    private String acceptorId;
    private String accountId;
    private String accountToken;

    private String applicationId;
    private String applicationName;
    private String applicationVersion;

    private String terminalId;

    public SoftposConfig(
            String acceptorId,
            String accountId,
            String accountToken,
            String applicationId,
            String applicationName,
            String applicationVersion,
            String terminalId
    ) {
        this.acceptorId = acceptorId;
        this.accountId = accountId;
        this.accountToken = accountToken;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
        this.terminalId = terminalId;
    }

    public String getAcceptorId() { return acceptorId; }

    public String getAccountId() { return accountId; }

    public String getAccountToken() { return accountToken; }

    public String getApplicationId() { return applicationId; }

    public String getApplicationName() { return applicationName; }

    public String getApplicationVersion() { return applicationVersion; }

    public String getTerminalId() { return terminalId; }
}