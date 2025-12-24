package com.example.greenguardian;

// Import statements for required AndroidX and Google Material classes
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomePage extends AppCompatActivity {

    // Declare BottomNavigationView which will handle navigation between fragments
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity's layout from XML resource (activity_home_page.xml)
        // This defines the UI structure including the bottom navigation and fragment container
        setContentView(R.layout.activity_home_page);

        // Initialize the BottomNavigationView by finding it from the layout using its ID
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Display the default fragment (MyCropsFragment) when the app starts
        // Fragment transactions are used to dynamically switch UI components
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, new MyCropsFragment()) // main_container is the FrameLayout where fragments will be loaded
                .commit(); // commit() applies the transaction

        // Set a listener to respond to user interactions with the bottom navigation items
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null; // Will hold the fragment to be loaded

                // Use the item ID to determine which menu item was clicked
                // Each case initializes a different fragment accordingly
                if (item.getItemId() == R.id.nav_chatBot) {
                    fragment = new ChatBotAIFragment(); // AI-powered chatbot
                } else if (item.getItemId() == R.id.nav_notifications) {
                    fragment = new SetAlarmFragment(); // Allows users to set reminders or alarms
                } else if (item.getItemId() == R.id.nav_myCrops) {
                    fragment = new MyCropsFragment(); // Displays user's crop information
                } else if (item.getItemId() == R.id.nav_market) {
                    fragment = new MarketFragment(); // Opens a market interface (likely for buying/selling)

                }

                // If a valid fragment has been selected, perform a fragment transaction to display it
                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, fragment) // Replace current fragment with new one
                            .commit(); // Finalize the transaction

                    return true; // Indicate successful handling of the navigation item selection
                }

                // Return false if no matching fragment was found
                // This helps the system know that the event was not consumed
                return false;
            }
        });
    }
}
