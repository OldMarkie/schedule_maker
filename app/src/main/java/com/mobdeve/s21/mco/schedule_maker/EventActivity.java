package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentContainer;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventActivity extends AppCompatActivity {


    private FloatingActionButton oneTimeEventButton;
    private FloatingActionButton weeklyEventButton;
    private TextView hintTextView;
    private FragmentContainer holder;

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
        holder = findViewById(R.id.holder);

        // One-time event button logic
        oneTimeEventButton.setOnClickListener(v -> {
            // Navigate to fragment for one-time event details

            hintTextView.setVisibility(View.GONE);
            oneTimeEventButton.setVisibility(View.GONE);
            weeklyEventButton.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OneTimeEventFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Weekly event button logic
        weeklyEventButton.setOnClickListener(v -> {
            hintTextView.setVisibility(View.GONE);
            weeklyEventButton.setVisibility(View.GONE);
            oneTimeEventButton.setVisibility(View.VISIBLE);
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

    public void showAddOneTimeEventButton(){
        oneTimeEventButton.setVisibility(View.VISIBLE);
    }

    public void showAddWeeklyActivityButton(){
        weeklyEventButton.setVisibility(View.VISIBLE);
    }
}
