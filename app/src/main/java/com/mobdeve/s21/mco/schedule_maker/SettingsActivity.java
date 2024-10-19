package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private TextView pageTitle;
    private Switch themeSwitch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize the TextView
        pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("Settings");

        // Initialize theme switch
        themeSwitch = findViewById(R.id.switchDarkMode);

        // Initialize SharedPreferences to save theme preference
        sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Check current theme preference and set switch accordingly
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        themeSwitch.setChecked(isDarkMode);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Toggle theme when the switch is toggled
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                themeSwitch.setEnabled(false);
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkMode", true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkMode", false);
                }
                editor.apply();  // Save the preference

                //Recreate the activity to apply the new theme
                recreate();

                // Show a toast message indicating theme change
                Toast.makeText(SettingsActivity.this, "Theme changed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the BottomNavigationView
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
