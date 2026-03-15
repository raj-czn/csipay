package com.csipay.softpos.device;

import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.provider.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeviceCapabilityChecker {

    private static final int MIN_API_LEVEL = 24;

    public static DeviceCapabilities check(Context context) {

        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        boolean nfcAvailable = checkNfcHardware(context, errors);
        boolean nfcEnabled = nfcAvailable && checkNfcEnabled(context, errors);
        boolean bluetoothAvailable = checkBluetooth(context, warnings);
        boolean internetAvailable = checkInternet(context, errors);
        boolean screenLockSet = checkScreenLock(context, errors);
        boolean locationAvailable = checkLocation(context, warnings);
        boolean deviceSecure = checkDeviceSecurity(context, errors);
        int androidApiLevel = Build.VERSION.SDK_INT;

        if (androidApiLevel < MIN_API_LEVEL) {
            errors.add("Android API level " + androidApiLevel + " is below minimum required " + MIN_API_LEVEL);
        }

        return new DeviceCapabilities(
                nfcAvailable, nfcEnabled, bluetoothAvailable, internetAvailable,
                screenLockSet, locationAvailable, deviceSecure, androidApiLevel,
                warnings, errors);
    }

    private static boolean checkNfcHardware(Context context, List<String> errors) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            errors.add("NFC hardware not available on this device");
            return false;
        }

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null) {
            errors.add("NFC adapter not found");
            return false;
        }

        return true;
    }

    private static boolean checkNfcEnabled(Context context, List<String> errors) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter != null && !nfcAdapter.isEnabled()) {
            errors.add("NFC is disabled. Please enable NFC in device settings");
            return false;
        }
        return true;
    }

    private static boolean checkBluetooth(Context context, List<String> warnings) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            warnings.add("Bluetooth hardware not available on this device");
            return false;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            warnings.add("Bluetooth adapter not found");
            return false;
        }

        if (!bluetoothAdapter.isEnabled()) {
            warnings.add("Bluetooth is disabled");
        }

        return true;
    }

    private static boolean checkInternet(Context context, List<String> errors) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            errors.add("Cannot check network connectivity");
            return false;
        }

        android.net.Network network = cm.getActiveNetwork();
        if (network == null) {
            errors.add("No active network connection");
            return false;
        }

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            errors.add("No internet connectivity");
            return false;
        }

        return true;
    }

    private static boolean checkScreenLock(Context context, List<String> errors) {
        KeyguardManager keyguardManager = (KeyguardManager)
                context.getSystemService(Context.KEYGUARD_SERVICE);

        if (keyguardManager == null) {
            errors.add("Cannot verify screen lock status");
            return false;
        }

        boolean isSecure;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isSecure = keyguardManager.isDeviceSecure();
        } else {
            isSecure = keyguardManager.isKeyguardSecure();
        }

        if (!isSecure) {
            errors.add("Screen lock is not set. A secure screen lock (PIN, pattern, or password) is required");
            return false;
        }

        return true;
    }

    private static boolean checkLocation(Context context, List<String> warnings) {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null) {
            warnings.add("Cannot access location services");
            return false;
        }

        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        if (!gpsEnabled && !networkEnabled) {
            warnings.add("Location services are disabled. Location is required for payment compliance");
            return false;
        }

        return true;
    }

    private static boolean checkDeviceSecurity(Context context, List<String> errors) {
        if (isDeviceRooted()) {
            errors.add("Device appears to be rooted. Payments cannot be processed on rooted devices");
            return false;
        }

        if (isDeveloperModeEnabled(context)) {
            // Warning only, not a blocking error
        }

        return true;
    }

    private static boolean isDeviceRooted() {
        String[] rootPaths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
        };

        for (String path : rootPaths) {
            if (new File(path).exists()) {
                return true;
            }
        }

        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean isDeveloperModeEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
    }
}