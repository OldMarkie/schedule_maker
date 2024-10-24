package com.mobdeve.s21.mco.schedule_maker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText eventLocationEditText;
    private EditText eventStartTimeEditText;
    private EditText eventEndTimeEditText;
    private CheckBox weeklyCheckBox; // Checkbox to indicate if the event is weekly
    private Button saveButton;

    private Event eventToEdit;

    private EditText eventDateEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Initialize UI components
        eventNameEditText = findViewById(R.id.activityNameInput);
        eventDescriptionEditText = findViewById(R.id.activityDescriptionInput);
        eventLocationEditText = findViewById(R.id.activityLocationInput);
        eventStartTimeEditText = findViewById(R.id.eventTimeInput);
        eventEndTimeEditText = findViewById(R.id.eventEndTimeInput);
        eventDateEditText = findViewById(R.id.eventDateInput); // Initialize date input
        saveButton = findViewById(R.id.saveButton);


        // Get the event to edit from the intent
        String eventId = getIntent().getStringExtra("EVENT_ID");
        eventToEdit = findEventById(eventId);

        // Populate fields with event data
        if (eventToEdit != null) {
            populateEventDetails();
        }

        // Set up time pickers
        eventStartTimeEditText.setOnClickListener(v -> showTimePicker(eventStartTimeEditText));
        eventEndTimeEditText.setOnClickListener(v -> showTimePicker(eventEndTimeEditText));

        // Set up save button listener
        saveButton.setOnClickListener(v -> saveEvent());
    }

    private Event findEventById(String eventId) {
        // Method to find the event by ID from the DummyData class
        for (Event event : DummyData.getEvents()) {
            if (event.getId().equals(eventId)) {
                return event;
            }
        }
        return null; // Event not found
    }

    private void populateEventDetails() {
        eventNameEditText.setText(eventToEdit.getName());
        eventDescriptionEditText.setText(eventToEdit.getDescription());
        eventLocationEditText.setText(eventToEdit.getLocation());
        eventStartTimeEditText.setText(formatDate(eventToEdit.getStartTime()));
        eventEndTimeEditText.setText(formatDate(eventToEdit.getEndTime()));
        weeklyCheckBox.setChecked(eventToEdit.isWeekly()); // Set checkbox based on event type
    }

    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String time = String.format("%02d:%02d", selectedHour, selectedMinute);
            editText.setText(time);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    private void saveEvent() {
        String name = eventNameEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();
        String location = eventLocationEditText.getText().toString().trim();
        String startTimeStr = eventStartTimeEditText.getText().toString();
        String endTimeStr = eventEndTimeEditText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(startTimeStr) || TextUtils.isEmpty(endTimeStr)) {
            Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert string time to Date
        Date startTime = parseDate(startTimeStr);
        Date endTime = parseDate(endTimeStr);

        // Check if end time is after start time
        if (endTime.before(startTime)) {
            Toast.makeText(this, "End time must be after start time.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the event in DummyData
        eventToEdit = new Event(eventToEdit.getId(), name, description, location, startTime, endTime, weeklyCheckBox.isChecked());
        DummyData.updateEvent(eventToEdit);

        Toast.makeText(this, "Event updated successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }
}
