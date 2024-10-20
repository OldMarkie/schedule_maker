package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView digitalClock;
    private TextView currentDay;
    private TextView currentDate;
    private TextView latestSchedule;
    private Handler handler;
    private Runnable runnable;
    private boolean is24HourFormat;  // Store the time format preference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme preferences before setting content view
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);  // Load time format preference

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews for clock, date, and schedule
        digitalClock = findViewById(R.id.digitalClock);
        currentDay = findViewById(R.id.currentDay);
        currentDate = findViewById(R.id.currentDate);
        latestSchedule = findViewById(R.id.latestSchedule);

        // Set up the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);  // Highlight Home as the selected tab

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                // Convert switch to if-else statement
                if (id == R.id.nav_home) {
                    return true;  // Stay on the home page
                } else if (id == R.id.nav_add_event) {
                    startActivity(new Intent(MainActivity.this, EventActivity.class));
                    overridePendingTransition(0, 0);  // No animation
                    finish();
                    return true;
                } else if (id == R.id.nav_view_events) {
                    startActivity(new Intent(MainActivity.this, EventListActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });

        // Start the real-time clock
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                handler.postDelayed(this, 60000); // Update every minute (60000 milliseconds)
            }
        };
        handler.post(runnable);

        // Display the current day and date
        displayCurrentDayAndDate();

        // Load the upcoming schedule
        loadLatestSchedule();
    }

    // Update the digital clock based on user preference (24-hour or 12-hour format)
    private void updateClock() {
        SimpleDateFormat sdf;
        if (is24HourFormat) {
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());  // 24-hour format, no seconds
        } else {
            sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());  // 12-hour format with AM/PM
        }
        String currentTime = sdf.format(new Date());
        digitalClock.setText(currentTime);
    }

    // Display the current day and date
    private void displayCurrentDayAndDate() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());  // Friday
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());  // Jan 1, 2025

        String day = dayFormat.format(new Date());
        String date = dateFormat.format(new Date());

        currentDay.setText(day);
        currentDate.setText(date);
    }

    // Load the latest schedule and apply the correct time format based on user preference
    private void loadLatestSchedule() {
        List<Event> events = DummyData.getEvents();  // Fetch the events from a data source
        if (!events.isEmpty()) {
            Event nextEvent = events.get(0);  // Assuming this is sorted by date

            // Get user preference for time format
            SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
            boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);

            // Format time based on user preference
            SimpleDateFormat timeFormat;
            if (is24HourFormat) {
                timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());  // 24-hour format
            } else {
                timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());  // 12-hour format with AM/PM
            }

            // Display the next event with formatted time
            latestSchedule.setText("Next: " + nextEvent.getName() + " at " + timeFormat.format(nextEvent.getDateTime()));
        } else {
            latestSchedule.setText("No upcoming schedule");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);  // Stop the clock updates when the activity is destroyed
    }
}
