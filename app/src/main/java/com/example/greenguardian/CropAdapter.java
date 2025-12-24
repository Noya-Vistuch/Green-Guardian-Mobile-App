package com.example.greenguardian;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    // List of crops to be displayed in the RecyclerView
    private List<Crop> cropList;

    // Helper for database operations
    private CropDatabaseHelper dbHelper;

    // Context for accessing resources and launching dialogs
    private Context context;

    // Constructor initializes context, crop list, and database helper
    public CropAdapter(Context context, List<Crop> cropList) {
        this.context = context;
        this.cropList = cropList;
        this.dbHelper = new CropDatabaseHelper(context);
    }

    // Updates the list of crops and refreshes the view
    public void updateCropList(List<Crop> newCropList) {
        this.cropList = newCropList;
        notifyDataSetChanged();
    }

    // ViewHolder class that holds references to the views in each RecyclerView item
    public static class CropViewHolder extends RecyclerView.ViewHolder {

        // UI elements in the item view
        TextView nameTextView, cropSizeTextView, stageTextView;
        ImageView typeImageView;
        LinearLayout timelineLayout;

        public CropViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.cropNameTextView);
            cropSizeTextView = itemView.findViewById(R.id.cropSizeTextView);
            stageTextView = itemView.findViewById(R.id.cropStageTextView);
            typeImageView = itemView.findViewById(R.id.cropTypeImageView);
            timelineLayout = itemView.findViewById(R.id.timelineLayout);
        }

        // Binds data from a Crop object to the item view
        public void bind(Crop crop) {
            nameTextView.setText(crop.getName());
            cropSizeTextView.setText("Size: " + crop.getSize());

            // Get the current growth stage using CropUtils
            String growthStage = CropUtils.getGrowthStage(crop.getPlantingDate(), crop.getType());
            stageTextView.setText("Stage: " + growthStage);

            // Set image for the crop type
            typeImageView.setImageResource(getImageForType(crop.getType()));

            // Build and display the timeline of growth stages
            populateTimeline(crop.getPlantingDate(), crop.getType());

            // Tag the item view with this crop (optional, useful for future reference)
            itemView.setTag(crop);
        }

        // Builds the visual timeline based on growth stages
        private void populateTimeline(String plantingDate, String cropType) {
            timelineLayout.removeAllViews();

            // Stage names and corresponding icons
            String[] stages = {"Germination", "Seedling", "Vegetative", "Flowering", "Harvest"};
            int[] stageImages = {
                    R.drawable.seed_stage,
                    R.drawable.sprout_stage,
                    R.drawable.vegetable_stage,
                    R.drawable.flower_stage,
                    R.drawable.harvest_stage
            };

            // Calculate the current stage index
            int currentStageIndex = CropUtils.getGrowthStageIndex(plantingDate, cropType);

            // Loop through and build the timeline
            for (int i = 0; i < stages.length; i++) {
                ImageView stageImage = new ImageView(itemView.getContext());
                stageImage.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                stageImage.setImageResource(stageImages[i]);

                // Dim future stages
                if (i > currentStageIndex) {
                    stageImage.setAlpha(0.5f);
                }

                timelineLayout.addView(stageImage);

                // Add a dot between stages except after the last one
                if (i < stages.length - 1) {
                    View dot = new View(itemView.getContext());
                    LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(20, 20);
                    dotParams.setMargins(10, 40, 10, 0);
                    dot.setLayoutParams(dotParams);
                    dot.setBackgroundResource(R.drawable.timeline_dot);
                    timelineLayout.addView(dot);
                }
            }
        }

        // Returns the correct image resource based on crop type
        private int getImageForType(String cropType) {
            switch (cropType.toLowerCase(Locale.ROOT)) {
                case "cucumber": return R.drawable.cucumber;
                case "tomato": return R.drawable.tomato;
                case "lettuce": return R.drawable.lettuce;
                case "peppers": return R.drawable.peppers;
                case "carrots": return R.drawable.carrots;
                case "onions": return R.drawable.onions;
                case "cabbage": return R.drawable.cabbage;
                case "cauliflower": return R.drawable.cauliflower;
                case "broccoli": return R.drawable.broccoli;
                case "eggplant": return R.drawable.eggplant;
                case "zucchini": return R.drawable.zucchini;
                case "potatoes": return R.drawable.potaoes;
                case "sweet corn": return R.drawable.sweet_corn;
                case "green beans": return R.drawable.green_beans;
                case "beets": return R.drawable.beets;
                default: return R.drawable.defult_image;
            }
        }
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each crop item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crop, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        // Get the crop for the current position and bind it to the view
        Crop crop = cropList.get(position);
        holder.bind(crop);

        // Set long-click listener for deleting a crop
        holder.itemView.setOnLongClickListener(v -> {
            showDeleteConfirmationDialog(crop.getId(), position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return cropList.size(); // Return total number of crop items
    }

    // Shows a confirmation dialog before deleting a crop
    private void showDeleteConfirmationDialog(int cropId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Crop");
        builder.setMessage("Do you want to delete this crop?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteCrop(cropId, position);
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Removes the crop from the database and the list, and updates the UI
    private void deleteCrop(int cropId, int position) {
        dbHelper.deleteCropById(cropId);            // Remove from database
        cropList.remove(position);                  // Remove from list
        notifyItemRemoved(position);                // Notify adapter of item removal
        notifyItemRangeChanged(position, cropList.size()); // Refresh list range
        Toast.makeText(context, "Crop deleted", Toast.LENGTH_SHORT).show(); // Feedback to user
    }
}

