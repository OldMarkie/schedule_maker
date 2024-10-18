package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Call this method to set up the navigation bar in any activity
    protected void setupBottomNavigation(int selectedItemId) {
        // Set the content view (can be overridden in the child activities)
        setContentView(R.layout.activity_base); // Add this layout in each activity's layout

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set the current selected item for navigation highlighting
        bottomNavigationView.setSelectedItemId(selectedItemId);

        // Bottom Navigation View Listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_add_event) {
                    startActivity(new Intent(BaseActivity.this, EventActivity.class));
                    return true;
                } else if (id == R.id.nav_view_events) {
                    startActivity(new Intent(BaseActivity.this, EventListActivity.class));
                    return true;
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(BaseActivity.this, SettingsActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}