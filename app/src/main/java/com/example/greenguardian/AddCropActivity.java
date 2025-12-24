package com.example.greenguardian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AddCropActivity extends AppCompatActivity {
    private EditText nameEditText, sizeEditText;
    private DatePicker plantingDatePicker;  // Correcting the reference
    private Spinner typeSpinner;
    private Button addButton;
    private CropDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        nameEditText = findViewById(R.id.nameEditText);
        sizeEditText = findViewById(R.id.sizeEditText);
        plantingDatePicker = findViewById(R.id.plantingDatePicker); // Ensure correct ID
        typeSpinner = findViewById(R.id.typeSpinner);
        addButton = findViewById(R.id.addButton);

        dbHelper = new CropDatabaseHelper(this);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Set up spinner with crop types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.crop_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String size = sizeEditText.getText().toString();
            String type = typeSpinner.getSelectedItem().toString();

            // Retrieve selected date from DatePicker
            int day = plantingDatePicker.getDayOfMonth();
            int month = plantingDatePicker.getMonth() + 1; // Months start from 0
            int year = plantingDatePicker.getYear();
            String plantingDate = year + "-" + month + "-" + day; // Format YYYY-MM-DD

            if (!name.isEmpty() && !size.isEmpty()) {
                dbHelper.addCrop(new Crop(0, CurrentUser.userId, name, size, type, plantingDate));

                Toast.makeText(this, "Crop added successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);  // Indicate success
                finish(); // Close the activity
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
