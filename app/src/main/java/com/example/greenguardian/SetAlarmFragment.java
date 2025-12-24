package com.example.greenguardian;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class SetAlarmFragment extends Fragment {

    // TextView to display the selected time
    private TextView selectedTimeTextView;

    // Calendar object to store and manipulate time data
    private Calendar calendar;

    // AlarmManager to schedule the alarm
    private AlarmManager alarmManager;

    // PendingIntent to be triggered when the alarm goes off
    private PendingIntent pendingIntent;

    // EditText for the user to enter the reminder's title
    private EditText reminderTitleEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.fragment_set_alarm, container, false);

        // Initialize UI components
        selectedTimeTextView = view.findViewById(R.id.selectedTime);
        Button selectTimeBtn = view.findViewById(R.id.selectedTimeBtn);
        Button cancelAlarmBtn = view.findViewById(R.id.cancelAlarmBtn);
        Button setAlarmBtn = view.findViewById(R.id.selectedAlarmBtn);
        reminderTitleEditText = view.findViewById(R.id.reminderTitleEditText);

        // Set the listener for the "Set Alarm" button
        setAlarmBtn.setOnClickListener(v -> setAlarm());

        // Check for null context (safety check)
        if (getContext() == null) {
            Toast.makeText(getContext(), "Error: Context is null", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Get the system alarm service
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        // Initialize the calendar instance to the current time
        calendar = Calendar.getInstance();

        // Set listeners for "Select Time" and "Cancel Alarm" buttons
        selectTimeBtn.setOnClickListener(v -> openTimePicker());
        cancelAlarmBtn.setOnClickListener(v -> cancelAlarm());

        return view;
    }

    // Opens a TimePicker dialog to allow the user to select a time
    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(), // Context
                (view, hourOfDay, minute) -> {
                    // Set the selected hour and minute in the calendar
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    // Format and display the selected time
                    String formattedTime = String.format(Locale.getDefault(), "%02d : %02d", hourOfDay, minute);
                    selectedTimeTextView.setText(formattedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY), // Default hour
                calendar.get(Calendar.MINUTE),      // Default minute
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    // Sets an alarm at the selected time with the entered reminder title
    private void setAlarm() {
        if (getContext() == null) {
            Toast.makeText(getContext(), "Error: Context is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the user input for the reminder title
        String title = reminderTitleEditText.getText().toString();

        // Use a default title if the field is empty
        if (title.trim().isEmpty()) {
            title = "Reminder set off";
        }

        // Create an intent to trigger the ReminderReceiver
        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        intent.putExtra("REMINDER_TITLE", title); // Pass the title as extra

        // Create a PendingIntent that will be triggered when the alarm goes off
        pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Set an exact alarm that will go off even in Doze mode
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, // Type of alarm
                calendar.getTimeInMillis(), // Time in milliseconds
                pendingIntent
        );

        // Notify the user
        Toast.makeText(getContext(), "Alarm set for " + calendar.getTime(), Toast.LENGTH_SHORT).show();
    }

    // Cancels an existing alarm if it has been set
    private void cancelAlarm() {
        if (getContext() == null) {
            Toast.makeText(getContext(), "Error: Context is null", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pendingIntent != null) {
            // Cancel the scheduled alarm
            alarmManager.cancel(pendingIntent);
            Toast.makeText(getContext(), "Alarm canceled!", Toast.LENGTH_SHORT).show();
        } else {
            // No alarm was previously set
            Toast.makeText(getContext(), "No alarm to cancel!", Toast.LENGTH_SHORT).show();
        }
    }
}
