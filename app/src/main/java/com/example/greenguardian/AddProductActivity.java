package com.example.greenguardian;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView productImage;
    private EditText priceInput, commentInput;
    private Button btnSave;
    private HelperDBProducts dbHelper;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productImage = findViewById(R.id.product_image);
        priceInput = findViewById(R.id.price_input);
        commentInput = findViewById(R.id.comment_input);
        btnSave = findViewById(R.id.btn_save);
        dbHelper = new HelperDBProducts(this);

        // Click to select an image
        productImage.setOnClickListener(v -> openImagePicker());

        // Save product to DB
        btnSave.setOnClickListener(v -> saveProduct());

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Or use onBackPressed() for consistent behavior

    }

    // Open gallery to pick an image
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                productImage.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Convert Bitmap to ByteArray
    private byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Save product to database
    private void saveProduct() {
        String price = priceInput.getText().toString();
        String comment = commentInput.getText().toString();

        if (selectedImageBitmap != null && !price.isEmpty()) {
            byte[] imageBytes = getImageBytes(selectedImageBitmap);
            boolean inserted = dbHelper.insertProduct(imageBytes, price, comment, CurrentUser.userId);

            if (inserted) {
                Toast.makeText(this, "Product Added!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity
            } else {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
