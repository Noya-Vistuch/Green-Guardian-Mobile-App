package com.example.greenguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomePage extends AppCompatActivity {
    // Declare buttons for login and sign-up
    Button log_in_btn;
    Button sign_up_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        // Initialize buttons by finding them in the layout by their IDs
        log_in_btn = findViewById(R.id.log_in_btn);
        sign_up_btn = findViewById(R.id.sign_up_btn);

        // Set up a click listener for the sign-up button
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to go from WelcomePage to SignUp activity
                Intent go = new Intent(WelcomePage.this, SignUp.class);
                startActivity(go);
            }
        });

        // Set up a click listener for the login button
        log_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to go from WelcomePage to LogIn activity
                Intent go = new Intent(WelcomePage.this, LogIn.class);// יצרנו שליח שיודע לעבור מדף לדף
                startActivity(go);

            }
        });


    }
}