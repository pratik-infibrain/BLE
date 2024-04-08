package com.bleflutter.bleflutter;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class BluetoothPermissionRequester {

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    // Request Bluetooth permissions at runtime
    public static void requestPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                    REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }

    // Handle the result of permission request
    public static boolean handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            // Permissions granted
            // Permissions denied
            return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
}
