package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize the TextView
        pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("Settings");

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
                    return true;
                } else if (id == R.id.nav_add_event) {
                    startActivity(new Intent(SettingsActivity.this, EventActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_view_events) {
                    startActivity(new Intent(SettingsActivity.this, EventListActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_settings) {
                    return true;  // Stay on settings page
                }

                return false;
            }
        });
    }
}

