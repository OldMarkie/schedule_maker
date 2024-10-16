package com.mobdeve.s21.mco.schedule_maker;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchNotifications;
    private Switch switchDarkMode;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences
        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        editor = preferences.edit();

        // Find switches in the layout
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        // Load and apply saved preferences
        loadSettings();

        // Handle notification switch toggling
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("notifications", isChecked);
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle dark mode switch toggling
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("darkMode", isChecked);
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Dark Mode " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();

                // Apply dark mode immediately
                if (isChecked) {
                    setTheme(R.style.DarkTheme);
                } else {
                    setTheme(R.style.LightTheme);
                }
                recreate();  // Recreate the activity to apply theme changes
            }
        });
    }

    // Load saved settings
    private void loadSettings() {
        boolean notificationsEnabled = preferences.getBoolean("notifications", false);
        boolean darkModeEnabled = preferences.getBoolean("darkMode", false);

        switchNotifications.setChecked(notificationsEnabled);
        switchDarkMode.setChecked(darkModeEnabled);

        // Apply the theme based on saved preference
        if (darkModeEnabled) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }
    }
}