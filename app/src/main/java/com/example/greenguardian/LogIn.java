package com.example.greenguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {
    // Declare UI elements
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;

    Button signupButton;
    HelperDB hlp;

    TextView errorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // Initialize UI elements by finding them in the layout by their IDs
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);
        hlp = new HelperDB(LogIn.this);

        // Set up a click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input (username, password, etc.)
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Check if the user exists in the database
                if (hlp.isUserExists(username, password)) {
                    // Login successful, navigate to the main activity or perform other actions
                    Toast.makeText(LogIn.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent go = new Intent(LogIn.this, HomePage.class);

                    CurrentUser.userId   = hlp.getUserIdByName(username);
                    CurrentUser.userName = username;
                    go.putExtra("USERNAME", username);
                    startActivity(go);

                }
                else{
                    // Login failed, show an error message
                    errorMessageTextView.setText("Invalid username or password");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }

            }
        });

        // Set up a click listener for the signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to go from LogIn to SignUp activity
                Intent go = new Intent(LogIn.this, SignUp.class);
                startActivity(go);
            }
        });


    }
}