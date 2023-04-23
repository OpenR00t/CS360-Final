package com.example.cs360proj.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    // Method to check if SMS permission is granted
    public static boolean isSmsPermissionGranted(Context context) {
        // Check if the SEND_SMS permission is granted
        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }
}