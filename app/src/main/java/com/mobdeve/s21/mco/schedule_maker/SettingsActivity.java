package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private TextView pageTitle;
    private Switch themeSwitch, timeFormatSwitch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences and editor
        sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Retrieve preferences for dark mode and time format
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);

        // Set dark mode based on the saved preference
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize pageTitle
        pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("Settings");

        // Initialize switches
        themeSwitch = findViewById(R.id.themeSwitch);
        timeFormatSwitch = findViewById(R.id.timeFormatSwitch);

        // Set switch states based on saved preferences
        themeSwitch.setChecked(isDarkMode);
        timeFormatSwitch.setChecked(is24HourFormat);

        // Set listener for theme switch
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isDarkMode", isChecked);
                editor.apply();
            }
        });

        // Set listener for time format switch
        timeFormatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("is24HourFormat", isChecked);
                editor.apply();
            }
        });

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_add_event) {
                    startActivity(new Intent(SettingsActivity.this, EventActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_view_events) {
                    startActivity(new Intent(SettingsActivity.this, EventListActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_settings) {
                    return true;  // Stay on settings page
                }

                return false;
            }
        });
    }
}
