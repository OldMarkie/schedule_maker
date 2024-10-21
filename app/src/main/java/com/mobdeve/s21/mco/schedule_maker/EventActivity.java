package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;  // Import Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Calendar;
import java.util.Locale;

public class EventActivity extends AppCompatActivity {


    private Button oneTimeEventButton;
    private Button weeklyEventButton;
    private TextView hintTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme preferences before setting content view
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);

        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        oneTimeEventButton = findViewById(R.id.oneTimeEventButton);
        weeklyEventButton = findViewById(R.id.weeklyEventButton);
        hintTextView = findViewById(R.id.hintTV);

        // One-time event button logic
        oneTimeEventButton.setOnClickListener(v -> {
            // Navigate to fragment for one-time event details
            hintTextView.setVisibility(View.INVISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OneTimeEventFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Weekly event button logic
        weeklyEventButton.setOnClickListener(v -> {
            hintTextView.setVisibility(View.INVISIBLE);
            // Navigate to fragment for weekly activity details
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WeeklyActivityFragment())
                    .addToBackStack(null)
                    .commit();
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
     public void showHintTextView(){
        hintTextView.setVisibility(View.VISIBLE);
     }
}
