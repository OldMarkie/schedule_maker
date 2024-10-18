package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

public class EventActivity extends AppCompatActivity {

    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        // Initialize the TextView
        pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("Add Event");

        // Set up the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_add_event);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(EventActivity.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_add_event) {
                    return true;  // Stay on add event page
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
}
