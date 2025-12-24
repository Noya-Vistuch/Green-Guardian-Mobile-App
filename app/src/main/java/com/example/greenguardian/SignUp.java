package com.example.greenguardian;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    Button signup_btn;
    Button login_btn;
    CircleImageView profileImageView; // Use CircleImageView for round image
    HelperDB hlp;
    TextView errorMessageTextView;


    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;
    private Bitmap selectedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup_btn = findViewById(R.id.signup_btn);
        login_btn = findViewById(R.id.login_btn);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        profileImageView = findViewById(R.id.profileImageView);
        errorMessageTextView = findViewById(R.id.errorMessageTextView);


        hlp = new HelperDB(SignUp.this);

        // Set default profile image
        setDefaultProfileImage();

        // Handle profile image click (pick image)
        profileImageView.setOnClickListener(this::onSelectImage);

        // Handle signup button click
        signup_btn.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                errorMessageTextView.setText("Username and password cannot be empty or just spaces");
                errorMessageTextView.setVisibility(View.VISIBLE);
                return;
            }


            if (hlp.isUsernameTaken(username)) {
                errorMessageTextView.setText("Username taken");
                errorMessageTextView.setVisibility(View.VISIBLE);
            } else {
                // Create the new user object with username, password, and profile picture (Bitmap)
                Users newUser = new Users(username, password, selectedBitmap);
                hlp.addUser(newUser);

                Toast.makeText(SignUp.this, "Sign-up successful", Toast.LENGTH_SHORT).show();

                Intent go = new Intent(SignUp.this, HomePage.class);
                CurrentUser.userId   = hlp.getUserIdByName(username);
                CurrentUser.userName = username;
                go.putExtra("USERNAME", username);
                startActivity(go);
            }
        });

        // Handle login button click
        login_btn.setOnClickListener(view -> {
            Intent go = new Intent(SignUp.this, LogIn.class);
            startActivity(go);
        });
    }

    // Set the default profile image (from resources)
    private void setDefaultProfileImage() {
        // Assuming you have a default image in your drawable folder (e.g., "default_profile_pic")
        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic);
        profileImageView.setImageBitmap(defaultBitmap);
        selectedBitmap = defaultBitmap; // Set the selectedBitmap to the default image
    }

    // Image selection handler (pick image from gallery)
    public void onSelectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result when an image is picked
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                // Convert the selected image to a Bitmap
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                // Display the selected image in a circular shape
                profileImageView.setImageBitmap(selectedBitmap);

            } catch (IOException e) {
                e.printStackTrace();
                errorMessageTextView.setText("Failed to load image");
                errorMessageTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}


