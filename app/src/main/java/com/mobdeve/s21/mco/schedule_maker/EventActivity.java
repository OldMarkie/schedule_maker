package com.mobdeve.s21.mco.schedule_maker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {

    private EditText eventNameInput;
    private TextView eventDateInput;
    private Button saveButton;
    private Calendar eventCalendar;
    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Initialize the TextView
        pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("Add Schedule");

        eventNameInput = findViewById(R.id.eventNameInput);
        eventDateInput = findViewById(R.id.eventDateInput);
        saveButton = findViewById(R.id.saveButton);

        eventCalendar = Calendar.getInstance();

        // Set up the date picker
        eventDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        // Set up the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameInput.getText().toString();
                Date eventDate = eventCalendar.getTime();  // Get the selected date and time

                // Add the new event to the data source
                DummyData.getEvents().add(new Event(eventName, eventDate));

                // Navigate back to EventListActivity and refresh the list
                Intent intent = new Intent(EventActivity.this, EventListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Set up BottomNavigationView (same as in other activities)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_add_event);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                // Navigation based on the selected item
                if (id == R.id.nav_home) {
                    startActivity(new Intent(EventActivity.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_add_event) {
                    // Stay on Add Event page
                    return true;
                } else if (id == R.id.nav_view_events) {
                    startActivity(new Intent(EventActivity.this, EventListActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(EventActivity.this, SettingsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
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
