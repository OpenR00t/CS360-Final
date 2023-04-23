package com.example.cs360proj.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cs360proj.R;
import com.example.cs360proj.data.DatabaseActivity;
import com.example.cs360proj.data.DatabaseManager;
import com.example.cs360proj.main.MainActivity;

public class LoginActivity extends AppCompatActivity  {
    // Initialize EditText fields for username and password input
    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity
        setContentView(R.layout.activity_login);

        // Initialize EditText fields for username and password input
        txtUsername = findViewById(R.id.usernameEditText);
        txtPassword = findViewById(R.id.passwordEditText);

        // Initialize and set onClickListeners for login and create account buttons
        Button btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(l -> handleLogin());

        Button btnCreate = findViewById(R.id.createAccountButton);
        btnCreate.setOnClickListener(l -> handleCreate());
    }

    // Method to handle login button click
    private void handleLogin() {
        // Get the entered username and password from EditText fields
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        // Check if the entered credentials match an existing user in the database
        if (DatabaseManager.getInstance(getApplicationContext()).authenticate(username, password)){
            // If authentication is successful, start a new activity to display user data
            Intent intent = new Intent(this, DatabaseActivity.class);
            startActivity(intent);
        } else {
            // If authentication fails, display an error message using Toast.makeText()
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_LONG).show();
        }
    }

    // Method to handle create account button click
    private void handleCreate() {
        // Get the entered username and password from EditText fields
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        // Check if both username and password fields are filled
        if (username.isEmpty() || password.isEmpty()) {
            // If either field is empty, display an error message using Toast.makeText()
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_LONG).show();
        } else if (DatabaseManager.getInstance(getApplicationContext()).userExists(username)) {
            // If the username already exists in the database, display an error message using Toast.makeText()
            Toast.makeText(this, "Username already exists", Toast.LENGTH_LONG).show();
        } else {
            // If the username is available, add the new user to the database and display a success message using Toast.makeText()
            DatabaseManager.getInstance(getApplicationContext()).addUser(username, password);
            Toast.makeText(this, "Account created successfully", Toast.LENGTH_LONG).show();
        }
    }

}
