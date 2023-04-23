package com.example.cs360proj.permission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.cs360proj.R;
import com.example.cs360proj.data.DatabaseActivity;

public class PermissionActivity extends AppCompatActivity {

    private Switch permissionToggle;
    private static final int SMS_PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_perms);

        // Initialize Switch and Button views
        permissionToggle = findViewById(R.id.permission_toggle);
        Button btnSave = findViewById(R.id.saveButton);

        // Set onClickListener for save button to handle continue action
        btnSave.setOnClickListener(l -> handleContinue());

        // Load the saved phone number from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        String savedPhoneNumber = sharedPreferences.getString("phone_number", "");

        // Set the saved phone number to the EditText field
        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        phoneNumberEditText.setText(savedPhoneNumber);

        // Check if SMS permission is granted and set the toggle accordingly
        if (PermissionUtils.isSmsPermissionGranted(this)) {
            permissionToggle.setChecked(true);
        }

        // Set a listener for the permission toggle switch to request SMS permission if it is toggled on
        permissionToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestSmsPermission();
            }
        });
    }

    // Method to handle continue button click
    private void handleContinue() {
        // Get the entered phone number from EditText field
        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        String phoneNumber = phoneNumberEditText.getText().toString();

        if (phoneNumber.isEmpty()) {
            // If phone number field is empty, display an error message using Toast.makeText()
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the phone number in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone_number", phoneNumber);
        editor.apply();

        // Start the DatabaseActivity
        Intent intent = new Intent(this, DatabaseActivity.class);
        startActivity(intent);
    }

    // Method to request SMS permission
    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // If the SEND_SMS permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        }
    }

    // Method to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If the permission is granted, set the toggle to on
                permissionToggle.setChecked(true);
            } else {
                // If the permission is not granted, set the toggle to off
                permissionToggle.setChecked(false);
            }
        }
    }
}
