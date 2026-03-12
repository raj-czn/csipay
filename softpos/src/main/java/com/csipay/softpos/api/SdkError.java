package com.csipay.softpos.api;

public class SdkError {

    private final int code;
    private final String message;

    public SdkError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return code + ": " + message;
    }

    // ── SDK Configuration (1000–1099) ──

    public static final SdkError INVALID_CONFIGURATION =
            new SdkError(1000, "Invalid SDK configuration");

    public static final SdkError MISSING_CREDENTIALS =
            new SdkError(1001, "Missing required credentials (acceptorId, accountId, or accountToken)");

    public static final SdkError MISSING_APPLICATION_INFO =
            new SdkError(1002, "Missing required application info (applicationId, applicationName, or applicationVersion)");

    public static final SdkError MISSING_TERMINAL_ID =
            new SdkError(1003, "Missing terminal ID");

    public static final SdkError ALREADY_INITIALIZED =
            new SdkError(1010, "SDK is already initialized");

    public static final SdkError NOT_INITIALIZED =
            new SdkError(1011, "SDK is not initialized. Call initialize() first");

    // ── Device / Hardware (2000–2099) ──

    public static final SdkError NFC_NOT_AVAILABLE =
            new SdkError(2000, "NFC hardware is not available on this device");

    public static final SdkError NFC_DISABLED =
            new SdkError(2001, "NFC is disabled. Please enable NFC in device settings");

    public static final SdkError BLUETOOTH_NOT_AVAILABLE =
            new SdkError(2010, "Bluetooth hardware is not available on this device");

    public static final SdkError BLUETOOTH_DISABLED =
            new SdkError(2011, "Bluetooth is disabled. Please enable Bluetooth in device settings");

    public static final SdkError DEVICE_CONNECTION_FAILED =
            new SdkError(2020, "Failed to connect to payment device");

    public static final SdkError DEVICE_DISCONNECTED =
            new SdkError(2021, "Payment device disconnected");

    // ── Network (3000–3099) ──

    public static final SdkError NO_INTERNET =
            new SdkError(3000, "No internet connection available");

    public static final SdkError NETWORK_TIMEOUT =
            new SdkError(3001, "Network request timed out");

    public static final SdkError NETWORK_ERROR =
            new SdkError(3002, "Network communication error");

    // ── Gateway / Authorization (4000–4099) ──

    public static final SdkError TRANSACTION_DECLINED =
            new SdkError(4000, "Transaction declined");

    public static final SdkError TRANSACTION_ERROR =
            new SdkError(4001, "Transaction processing error");

    public static final SdkError TRANSACTION_IN_PROGRESS =
            new SdkError(4002, "Another transaction is already in progress");

    public static final SdkError VOID_FAILED =
            new SdkError(4010, "Void transaction failed");

    public static final SdkError REFUND_FAILED =
            new SdkError(4011, "Refund transaction failed");

    public static final SdkError AUTHORIZATION_FAILED =
            new SdkError(4020, "Authorization failed");

    // ── SoftPOS / Tap-to-Pay (5000–5099) ──

    public static final SdkError CARD_READ_FAILED =
            new SdkError(5000, "Failed to read card data");

    public static final SdkError CARD_NOT_SUPPORTED =
            new SdkError(5001, "Card type not supported for contactless payment");

    public static final SdkError CONTACTLESS_LIMIT_EXCEEDED =
            new SdkError(5002, "Transaction amount exceeds contactless limit");

    public static final SdkError TAP_TIMEOUT =
            new SdkError(5010, "Card tap timed out. Please try again");

    public static final SdkError MULTIPLE_CARDS_DETECTED =
            new SdkError(5011, "Multiple cards detected. Please present only one card");

    public static SdkError fromException(int baseCode, Exception e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Unknown error";
        return new SdkError(baseCode, msg);
    }
}