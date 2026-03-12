package com.csipay.softpos.device;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.nfc.NfcAdapter;

import java.util.ArrayList;
import java.util.List;

class DeviceCapabilityChecker {

    static DeviceCapabilities check(Context context) {

        List<String> warnings = new ArrayList<>();

        boolean nfcAvailable = checkNfc(context, warnings);
        boolean bluetoothAvailable = checkBluetooth(context, warnings);
        boolean internetAvailable = checkInternet(context, warnings);

        return new DeviceCapabilities(nfcAvailable, bluetoothAvailable, internetAvailable, warnings);
    }

    private static boolean checkNfc(Context context, List<String> warnings) {

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            warnings.add("NFC hardware not available on this device");
            return false;
        }

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null) {
            warnings.add("NFC adapter not found");
            return false;
        }

        if (!nfcAdapter.isEnabled()) {
            warnings.add("NFC is disabled. Please enable NFC in device settings");
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

    private static boolean checkInternet(Context context, List<String> warnings) {

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            warnings.add("Cannot check network connectivity");
            return false;
        }

        android.net.Network network = cm.getActiveNetwork();
        if (network == null) {
            warnings.add("No active network connection");
            return false;
        }

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            warnings.add("No internet connectivity");
            return false;
        }

        return true;
    }
}