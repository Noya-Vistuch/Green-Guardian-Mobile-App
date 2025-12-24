package com.example.greenguardian;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {

    // ID used to identify the notification channel
    private static final String CHANNEL_ID = "ReminderChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the reminder title passed through the intent
        String reminderTitle = intent.getStringExtra("REMINDER_TITLE");

        // Provide a default title if none was supplied
        if (reminderTitle == null || reminderTitle.trim().isEmpty()) {
            reminderTitle = "Reminder set off";
        }

        // For Android 8.0+ (API 26+), a NotificationChannel is required
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a new notification channel with high importance
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reminder", // Channel name displayed in system settings
                    NotificationManager.IMPORTANCE_HIGH
            );

            // Get the system's NotificationManager
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                // Register the notification channel with the system
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the notification to be shown to the user
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Icon to be displayed
                .setContentTitle("Reminder!")                     // Notification title
                .setContentText(reminderTitle)                    // Notification message (user’s reminder)
                .setPriority(NotificationCompat.PRIORITY_HIGH)    // High priority so it appears immediately
                .setAutoCancel(true)                              // Dismiss notification when clicked
                .build();

        // Get the notification manager to show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check for runtime permission to post notifications (required on Android 13+)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return; // Do not show the notification if permission is not granted
        }

        // Display the notification (ID = 1)
        notificationManager.notify(1, notification);
    }
}
