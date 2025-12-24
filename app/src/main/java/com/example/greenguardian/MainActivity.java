package com.example.greenguardian;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    // Start the countdown timer when the activity is created
        countDownTimer.start();

    }

    // CountDownTimer that waits for 4 seconds (4000 milliseconds) with a tick interval of 1 second (1000 milliseconds)
    CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            // This method is called when the countdown timer reaches zero (finishes)
            // Go to another screen (Welcomepage activity)
            Intent gop = new Intent(MainActivity.this, WelcomePage.class);
            startActivity(gop);

        }
    };
}