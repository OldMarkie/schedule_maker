package com.mobdeve.s21.mco.schedule_maker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView digitalClock;
    private TextView latestSchedule;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        digitalClock = findViewById(R.id.digitalClock);
        latestSchedule = findViewById(R.id.latestSchedule);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Start the clock
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updateClock();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(runnable);

        // Fetch the latest schedule
        loadLatestSchedule();

        // Bottom navigation functionality
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_add_event) {
                    Intent intent = new Intent(MainActivity.this, EventActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_view_events) {
                    Intent intent = new Intent(MainActivity.this, EventListActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void updateClock() {
        // Get current time and format it as HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        digitalClock.setText(currentTime);
    }

    private void loadLatestSchedule() {
        // Fetch the latest schedule from DummyData (for now)
        List<Event> events = DummyData.getEvents();
        if (!events.isEmpty()) {
            Event latestEvent = events.get(events.size() - 1);
            latestSchedule.setText("Next: " + latestEvent.getName() + " at " + latestEvent.getTime());
        } else {
            latestSchedule.setText("No upcoming schedule");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable); // Stop clock when activity is destroyed
    }
}