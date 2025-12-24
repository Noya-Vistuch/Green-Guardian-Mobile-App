package com.example.greenguardian;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * MyCropsFragment is responsible for displaying a list of crops added by the currently logged-in user.
 * It also handles real-time search filtering, personalized greeting with profile image,
 * and navigation to the AddCropActivity.
 */
public class MyCropsFragment extends Fragment {

    private RecyclerView recyclerView; // Displays crop items in a vertical list
    private FloatingActionButton fabAddCrop; // Button to add a new crop
    private CropAdapter adapter; // Adapter for binding crop data to RecyclerView
    private CropDatabaseHelper dbHelper; // Helper for database interactions (CRUD on crops)
    private SearchView searchView; // Used for filtering crop list based on text input
    CircleImageView profileImageView; // Displays the user's profile picture

    public MyCropsFragment() {
        // Default empty constructor required for fragment instantiation
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_my_crops, container, false);

        // Initialize UI elements
        searchView = view.findViewById(R.id.search);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Display items vertically

        fabAddCrop = view.findViewById(R.id.fab_add_crop);
        profileImageView = view.findViewById(R.id.profileImage);
        TextView greetingText = view.findViewById(R.id.greetingText);

        // Retrieve current user info and display greeting
        HelperDB helperDB = new HelperDB(getContext());
        Users currentUser = helperDB.getUserByName(CurrentUser.userName);

        if (currentUser != null) {
            greetingText.setText("Hello, " + currentUser.getName() + "!");

            // Set user's profile picture if available
            if (currentUser.getProfilePic() != null) {
                profileImageView.setImageBitmap(currentUser.getProfilePic());
            }
        }

        // When the FAB is clicked, open AddCropActivity to create a new crop entry
        fabAddCrop.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCropActivity.class);
            startActivity(intent);
        });

        // Initialize database helper
        dbHelper = new CropDatabaseHelper(getContext());

        // Handle search input and filter the crop list
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed because filtering happens live
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText); // Filter the crop list as the user types
                return true;
            }
        });

        // Load crops into the RecyclerView
        refreshCrops();

        return view;
    }

    /**
     * Filters the crop list based on the user's search query.
     * Matches against crop name and type.
     */
    private void searchList(String query) {
        List<Crop> filteredList = new ArrayList<>();
        List<Crop> allCrops = dbHelper.getAllCropsByUserId(CurrentUser.userId);

        for (Crop crop : allCrops) {
            if (crop.getName().toLowerCase().contains(query.toLowerCase()) ||
                    crop.getType().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(crop); // Add matching crops to filtered list
            }
        }

        adapter.updateCropList(filteredList); // Update RecyclerView with filtered results
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCrops(); // Always refresh crops when the fragment is visible again
    }

    /**
     * Loads all crops belonging to the current user from the database,
     * initializes the adapter, and sets it on the RecyclerView.
     */
    private void refreshCrops() {
        List<Crop> updatedCropList = dbHelper.getAllCropsByUserId(CurrentUser.userId);
        adapter = new CropAdapter(getActivity(), updatedCropList);
        recyclerView.setAdapter(adapter);
    }
}
