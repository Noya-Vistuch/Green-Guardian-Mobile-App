package com.example.greenguardian;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CropUtils {

    // A map that holds predefined growth stage durations (in days) for different crop types.
    // Each int[] represents thresholds (in days) for stages like Germination, Seedling, etc.
    private static final Map<String, int[]> cropGrowthStages = new HashMap<>();

    // Static block to populate the map with stage durations for various crops
    static {
        cropGrowthStages.put("cucumber", new int[]{10, 21, 49, 70});
        cropGrowthStages.put("tomato", new int[]{10, 24, 45, 56, 100});
        cropGrowthStages.put("peppers", new int[]{14, 28, 60, 80});
        cropGrowthStages.put("carrots", new int[]{10, 20, 40, 60});
        cropGrowthStages.put("onions", new int[]{14, 30, 60, 90});
        cropGrowthStages.put("lettuce", new int[]{7, 14, 30, 45});
        cropGrowthStages.put("cabbage", new int[]{10, 25, 50, 80});
        cropGrowthStages.put("cauliflower", new int[]{10, 25, 50, 80});
        cropGrowthStages.put("broccoli", new int[]{10, 25, 50, 80});
        cropGrowthStages.put("eggplant", new int[]{14, 28, 60, 90});
        cropGrowthStages.put("zucchini", new int[]{10, 20, 40, 60});
        cropGrowthStages.put("potatoes", new int[]{14, 28, 56, 90});
        cropGrowthStages.put("sweet corn", new int[]{10, 25, 50, 80});
        cropGrowthStages.put("green beans", new int[]{7, 14, 28, 45});
        cropGrowthStages.put("beets", new int[]{10, 21, 40, 60});
        cropGrowthStages.put("default", new int[]{14, 28, 50, 75}); // fallback values
    }

    // Public method that determines and returns the current growth stage name of a crop
    public static String getGrowthStage(String plantingDate, String cropType) {

        // Validate the input: if planting date or crop type is missing, return an "unknown" result
        if (plantingDate == null || plantingDate.isEmpty()) {
            return "Unknown Stage";
        }

        if (cropType == null || cropType.isEmpty()) {
            return "Unknown Stage";  // Handle empty crop type
        }

        // Define a date formatter to parse the planting date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Parse the planting date string into a Date object
            Date datePlanted = sdf.parse(plantingDate);

            // Get the current system time and calculate how many days have passed since planting
            long currentTime = System.currentTimeMillis();
            long daysElapsed = TimeUnit.MILLISECONDS.toDays(currentTime - datePlanted.getTime());

            // Retrieve the correct stage thresholds based on the crop type
            int[] stageDurations = cropGrowthStages.getOrDefault(
                    cropType.toLowerCase(Locale.ROOT),
                    cropGrowthStages.get("default") // fallback to default if crop type not found
            );

            // Stage names corresponding to their position in the duration array
            String[] stageNames = {"Germination", "Seedling", "Vegetative", "Flowering", "Harvest Ready"};

            // Loop through the stage durations and determine the current stage
            for (int i = 0; i < stageDurations.length; i++) {
                if (daysElapsed < stageDurations[i]) {
                    return stageNames[i]; // Return stage that matches current elapsed time
                }
            }

            // If the crop has passed all defined stages, it is considered ready for harvest
            return "Harvest Ready";

        } catch (ParseException e) {
            // Handle parsing errors (invalid date format)
            e.printStackTrace();
            return "Unknown Stage";
        }
    }

    public static int getGrowthStageIndex(String plantingDate, String cropType) {
        if (plantingDate == null || plantingDate.isEmpty() || cropType == null || cropType.isEmpty()) {
            return 0;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date datePlanted = sdf.parse(plantingDate);
            long currentTime = System.currentTimeMillis();
            long daysElapsed = TimeUnit.MILLISECONDS.toDays(currentTime - datePlanted.getTime());

            int[] stageDurations = cropGrowthStages.getOrDefault(
                    cropType.toLowerCase(Locale.ROOT),
                    cropGrowthStages.get("default")
            );

            for (int i = 0; i < stageDurations.length; i++) {
                if (daysElapsed < stageDurations[i]) {
                    return i;
                }
            }

            return stageDurations.length; // Harvest stage

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

}



