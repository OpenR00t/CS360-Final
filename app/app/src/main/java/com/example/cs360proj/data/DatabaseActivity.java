package com.example.cs360proj.data;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.telephony.SmsManager;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;

import com.example.cs360proj.R;
import com.example.cs360proj.permission.PermissionActivity;
import com.example.cs360proj.permission.PermissionUtils;

public class DatabaseActivity extends AppCompatActivity {

    //UI components
    private ListView inventoryList;

    // Database manager instance
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        // Initialize the database manager
        databaseManager = DatabaseManager.getInstance(this);

        // Find UI components
        inventoryList = findViewById(R.id.inventoryListView);
        Button btnAdd = findViewById(R.id.addDataButton);
        Button btnEdit = findViewById(R.id.editDataButton);
        Button btnDelete = findViewById(R.id.deleteDataButton);
        Button btnPerm = findViewById(R.id.permissionsButton);

        // Set click listeners for buttons
        btnAdd.setOnClickListener(l -> handleAdd());
        btnEdit.setOnClickListener(l -> handleEdit());
        btnDelete.setOnClickListener(l -> handleDelete());
        btnPerm.setOnClickListener(l -> permNavigate());
    }

    // Navigate to permissions activity
    private void permNavigate() {
        Intent intent = new Intent(this, PermissionActivity.class);
        startActivity(intent);
    }

    // Add item to inventory
    private void handleAdd() {
        // Find input fields
        EditText nameField = findViewById(R.id.editTextName);
        EditText quantityField = findViewById(R.id.editTextQuantity);
        EditText locationField = findViewById(R.id.editTextLocation);

        // Get input values
        String name = nameField.getText().toString();
        String quantity = quantityField.getText().toString();
        String location = locationField.getText().toString();

        // Validate input
        if (name.isEmpty() || quantity.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add item to database
        databaseManager.addInventoryItem(name, quantity, location);

        Toast.makeText(this, "Item added to inventory", Toast.LENGTH_SHORT).show();

        // Clear input fields and reload inventory
        nameField.setText("");
        quantityField.setText("");
        locationField.setText("");
        loadInventory();

        // Send SMS notification if permission is granted
        if (PermissionUtils.isSmsPermissionGranted(this)) {
            String message = "Item added to inventory: Name - " + name + ", Quantity - " + quantity + ", Location - " + location;
            sendSmsNotification(message);
        }
    }

    // Update item in inventory
    private void handleEdit() {
        // Find input fields
        EditText idField = findViewById(R.id.editTextId);
        EditText nameField = findViewById(R.id.editTextName);
        EditText quantityField = findViewById(R.id.editTextQuantity);
        EditText locationField = findViewById(R.id.editTextLocation);

        // Get input values
        String idString = idField.getText().toString();
        String name = nameField.getText().toString();
        String quantity = quantityField.getText().toString();
        String location = locationField.getText().toString();

        // Validate input
        if (idString.isEmpty() || name.isEmpty() || quantity.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = Long.parseLong(idString);

        databaseManager.updateInventoryItem(id, name, quantity, location);

        Toast.makeText(this, "Item updated in inventory", Toast.LENGTH_SHORT).show();

        // Clear input fields
        idField.setText("");
        nameField.setText("");
        quantityField.setText("");
        locationField.setText("");
        loadInventory();
    }

    private void handleDelete() {
        // Get the EditText for the ID field
        EditText idField = findViewById(R.id.editTextId);

        // Get the ID string from the EditText
        String idString = idField.getText().toString();

        // Check if the ID string is empty
        if (idString.isEmpty()) {
            Toast.makeText(this, "Please enter an ID to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse the ID string to a long
        long id = Long.parseLong(idString);

        // Call the database manager to delete the inventory item
        databaseManager.deleteInventoryItem(id);

        // Show a toast message to indicate success
        Toast.makeText(this, "Item deleted from inventory", Toast.LENGTH_SHORT).show();

        // Clear the input fields and refresh the inventory list
        idField.setText("");
        loadInventory();
    }

    private void loadInventory() {
        // Get an instance of the database manager
        DatabaseManager databaseManager = DatabaseManager.getInstance(this);
        // Get a cursor containing all inventory items
        Cursor cursor = databaseManager.getAllInventoryItems();

        // The desired columns to be bound
        String[] columns = new String[]{
                DatabaseManager.getInventoryColId(),
                DatabaseManager.getInventoryColName(),
                DatabaseManager.getInventoryColQty(),
                DatabaseManager.getInventoryColLoc()
        };

        // The XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.itemId,
                R.id.itemName,
                R.id.itemQuantity,
                R.id.itemLocation
        };

        // Create the adapter using the cursor pointing to the desired data as well as the layout information
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.inventory_item, cursor, columns, to, 0);

        // Set the adapter for the ListView
        inventoryList.setAdapter(mAdapter);
    }

    private void sendSmsNotification(String message) {
        // Get the saved phone number from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        String phoneNumber = sharedPreferences.getString("phone_number", "");

        // Check if phone number is empty
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Phone number not set", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Send SMS
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } else {
            Toast.makeText(this, "SMS permission is not granted.", Toast.LENGTH_SHORT).show();
        }
    }

}
