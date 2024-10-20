package com.mobdeve.s21.mco.schedule_maker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    private EditText eventNameInput;
    private TextView eventDateInput;
    private CheckBox weeklyCheckBox;
    private Button saveButton;
    private Calendar eventCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme preferences before setting content view
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);

        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventNameInput = findViewById(R.id.eventNameInput);
        eventDateInput = findViewById(R.id.eventDateInput);
        weeklyCheckBox = findViewById(R.id.weeklyCheckBox);
        saveButton = findViewById(R.id.saveButton);

        eventCalendar = Calendar.getInstance();

        // Date picker logic for eventDateInput
        eventDateInput.setOnClickListener(v -> openDatePicker());

        // Save button logic
        saveButton.setOnClickListener(v -> {
            String eventName = eventNameInput.getText().toString();
            Date eventDate = eventCalendar.getTime();
            boolean isWeekly = weeklyCheckBox.isChecked();

            if (eventName.isEmpty()) {
                Toast.makeText(EventActivity.this, "Please enter an event name", Toast.LENGTH_SHORT).show();
            } else {
                Event newEvent = new Event(eventName, eventDate, isWeekly);
                DummyData.addEvent(newEvent);  // Add the event
                Toast.makeText(EventActivity.this, "Event Saved!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(EventActivity.this, EventListActivity.class));
                finish();
            }
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_add_event);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(EventActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_add_event) {
                return true;
            } else if (id == R.id.nav_view_events) {
                startActivity(new Intent(EventActivity.this, EventListActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(EventActivity.this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }

    // Opens the DatePickerDialog
    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EventActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventCalendar.set(Calendar.YEAR, year);
                        eventCalendar.set(Calendar.MONTH, month);
                        eventCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        openTimePicker();  // After selecting a date, open the time picker
                    }
                },
                eventCalendar.get(Calendar.YEAR),
                eventCalendar.get(Calendar.MONTH),
                eventCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void openTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                EventActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        eventCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        eventCalendar.set(Calendar.MINUTE, minute);

                        // Get user preference for 24-hour or 12-hour format
                        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
                        boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);

                        // Set the correct time format based on preference
                        SimpleDateFormat dateFormat;
                        if (is24HourFormat) {
                            dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault());  // 24-hour format
                        } else {
                            dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());  // 12-hour AM/PM format
                        }

                        eventDateInput.setText(dateFormat.format(eventCalendar.getTime()));
                    }
                },
                eventCalendar.get(Calendar.HOUR_OF_DAY),
                eventCalendar.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }
}
