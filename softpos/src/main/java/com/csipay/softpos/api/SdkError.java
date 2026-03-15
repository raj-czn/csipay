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

    public static final SdkError INVALID_AMOUNT =
            new SdkError(1020, "Transaction amount must be greater than zero");

    public static final SdkError MISSING_TRANSACTION_ID =
            new SdkError(1021, "Transaction ID is required");

    public static final SdkError MISSING_REFERENCE_NUMBER =
            new SdkError(1022, "Reference number is required");

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

    public static final SdkError SCREEN_LOCK_NOT_SET =
            new SdkError(2030, "Screen lock is required. Please set a screen lock in device settings");

    public static final SdkError LOCATION_NOT_AVAILABLE =
            new SdkError(2031, "Location services are not available or disabled");

    public static final SdkError LOCATION_PERMISSION_DENIED =
            new SdkError(2032, "Location permission is required for payment processing");

    public static final SdkError ANDROID_VERSION_NOT_SUPPORTED =
            new SdkError(2040, "Android version not supported. Minimum Android 7.0 (API 24) required");

    public static final SdkError DEVICE_ROOTED =
            new SdkError(2050, "Device appears to be rooted. Payments cannot be processed on rooted devices");

    public static final SdkError DEVICE_BATTERY_LOW =
            new SdkError(2060, "Device battery is too low to process payments");

    // ── Network (3000–3099) ──

    public static final SdkError NO_INTERNET =
            new SdkError(3000, "No internet connection available");

    public static final SdkError NETWORK_TIMEOUT =
            new SdkError(3001, "Network request timed out");

    public static final SdkError NETWORK_ERROR =
            new SdkError(3002, "Network communication error");

    public static final SdkError SERVER_NOT_CONNECTED =
            new SdkError(3010, "Not connected to payment server");

    public static final SdkError SERVER_CONNECTION_LOST =
            new SdkError(3011, "Connection to payment server was lost during transaction");

    // ── Gateway / Authorization (4000–4099) ──

    public static final SdkError TRANSACTION_DECLINED =
            new SdkError(4000, "Transaction declined");

    public static final SdkError TRANSACTION_ERROR =
            new SdkError(4001, "Transaction processing error");

    public static final SdkError TRANSACTION_IN_PROGRESS =
            new SdkError(4002, "Another transaction is already in progress");

    public static final SdkError TRANSACTION_CANCELLED =
            new SdkError(4003, "Transaction was cancelled");

    public static final SdkError TRANSACTION_ABORTED =
            new SdkError(4004, "Transaction was aborted");

    public static final SdkError VOID_FAILED =
            new SdkError(4010, "Void transaction failed");

    public static final SdkError REFUND_FAILED =
            new SdkError(4011, "Refund transaction failed");

    public static final SdkError AUTHORIZATION_FAILED =
            new SdkError(4020, "Authorization failed");

    public static final SdkError AUTH_COMPLETION_FAILED =
            new SdkError(4021, "Authorization completion failed");

    public static final SdkError RETURN_FAILED =
            new SdkError(4030, "Return transaction failed");

    public static final SdkError REVERSAL_FAILED =
            new SdkError(4031, "Reversal transaction failed");

    public static final SdkError HEALTH_CHECK_FAILED =
            new SdkError(4040, "Health check failed");

    public static final SdkError PRE_READ_FAILED =
            new SdkError(4050, "Pre-read card data failed");

    // ── Store & Forward (4500–4599) ──

    public static final SdkError STORE_FORWARD_ERROR =
            new SdkError(4500, "Store and forward error");

    public static final SdkError STORED_TRANSACTION_NOT_FOUND =
            new SdkError(4501, "Stored transaction not found");

    public static final SdkError FORWARD_FAILED =
            new SdkError(4502, "Failed to forward stored transaction");

    // ── SoftPOS / Tap-to-Pay (5000–5099) ──

    public static final SdkError CARD_READ_FAILED =
            new SdkError(5000, "Failed to read card data. Please tap again");

    public static final SdkError CARD_NOT_SUPPORTED =
            new SdkError(5001, "Card type not supported for contactless payment");

    public static final SdkError CONTACTLESS_LIMIT_EXCEEDED =
            new SdkError(5002, "Transaction amount exceeds contactless limit");

    public static final SdkError TAP_TIMEOUT =
            new SdkError(5010, "Card tap timed out. Please try again");

    public static final SdkError MULTIPLE_CARDS_DETECTED =
            new SdkError(5011, "Multiple cards detected. Please present only one card");

    public static final SdkError PIN_ENTRY_FAILED =
            new SdkError(5020, "PIN entry failed");

    public static final SdkError PIN_ENTRY_CANCELLED =
            new SdkError(5021, "PIN entry was cancelled");

    public static final SdkError SIGNATURE_FAILED =
            new SdkError(5030, "Signature capture failed");

    public static SdkError fromException(int baseCode, Exception e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Unknown error";
        return new SdkError(baseCode, msg);
    }
}