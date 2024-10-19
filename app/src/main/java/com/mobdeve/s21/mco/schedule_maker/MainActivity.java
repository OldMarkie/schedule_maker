package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    return true;
                } else if (id == R.id.nav_view_events) {
                    startActivity(new Intent(MainActivity.this, EventListActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    overridePendingTransition(0, 0);
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
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(runnable);

        // Display the current day and date
        displayCurrentDayAndDate();

        // Load the upcoming schedule
        loadLatestSchedule();
    }

    // Update the digital clock
    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
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

    // Load the latest schedule (dummy data for now)
    private void loadLatestSchedule() {
        List<Event> events = DummyData.getEvents();  // Fetch the events from a data source
        if (!events.isEmpty()) {
            Event nextEvent = events.get(0);  // Assuming this is sorted by date
            latestSchedule.setText("Next: " + nextEvent.getName() + " at " + nextEvent.getFormattedDate());  // Use formatted date
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
