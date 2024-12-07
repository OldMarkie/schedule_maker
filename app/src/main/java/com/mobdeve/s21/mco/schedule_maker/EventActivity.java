package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * The `EventActivity` class represents the activity for adding new events,
 * either one-time or weekly events. It provides a user interface with buttons
 * to initiate the event creation process and a bottom navigation bar for
 * navigation within the app.
 */

public class EventActivity extends AppCompatActivity {


    private FloatingActionButton oneTimeEventButton;
    private FloatingActionButton weeklyEventButton;
    private TextView activityTitle;
    private TextView hintTextView;
    private View holder;
    private View weeklySec;
    private View oneTimeSec;
    private  View holderBtn;
    private View holderW;


    /**
     * Called when the activity is starting. This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById(int)} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     *
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) being called.
     *
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-created from
     *                           a previous saved state, this is the state.
     */

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
        holder = findViewById(R.id.holderOneTime);
        weeklySec = findViewById(R.id.AddWeekly);
        oneTimeSec = findViewById(R.id.AddOneTime);
        holderBtn = findViewById(R.id.ButtonHolder);
        holderW = findViewById(R.id.holderWeekly);
        activityTitle = findViewById(R.id.activityTitle);

        // One-time event button logic
        oneTimeEventButton.setOnClickListener(v -> {
            // Navigate to fragment for one-time event details

            hintTextView.setVisibility(View.GONE);
            weeklySec.setVisibility(View.VISIBLE);
            oneTimeSec.setVisibility(View.GONE);
            holder.setVisibility(View.VISIBLE);
            holderW.setVisibility(View.GONE);
            activityTitle.setText("Add One-Time Activity");

            ViewGroup.LayoutParams layoutParams = holderBtn.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holderBtn.setLayoutParams(layoutParams);


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.holderOneTime, new OneTimeEventFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Weekly event button logic
        weeklyEventButton.setOnClickListener(v -> {
            hintTextView.setVisibility(View.GONE);
            oneTimeSec.setVisibility(View.VISIBLE);
            weeklySec.setVisibility(View.GONE);
            holderW.setVisibility(View.VISIBLE);
            holder.setVisibility(View.GONE);
            activityTitle.setText("Add Weekly Activity");

            ViewGroup.LayoutParams layoutParams = holderBtn.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holderBtn.setLayoutParams(layoutParams);


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.holderWeekly, new WeeklyActivityFragment())
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

    public void hideHolder() {
        holder.setVisibility(View.GONE);

        // Convert 590dp to pixels
        int heightInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                590,
                getResources().getDisplayMetrics()
        );

        // Set the height of holderBtn
        ViewGroup.LayoutParams layoutParams = holderBtn.getLayoutParams();
        layoutParams.height = heightInPixels;
        holderBtn.setLayoutParams(layoutParams);
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

    public void resetEventActivity(){
        hintTextView.setVisibility(View.VISIBLE);
        oneTimeSec.setVisibility(View.VISIBLE);
        weeklySec.setVisibility(View.VISIBLE);
        holderW.setVisibility(View.GONE);
        holder.setVisibility(View.GONE);
        activityTitle.setText("Add Events");

// Convert 590dp to pixels
        int heightInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                590,
                getResources().getDisplayMetrics()
        );

// Revert the height of holderBtn to 590dp
        ViewGroup.LayoutParams layoutParams = holderBtn.getLayoutParams();
        layoutParams.height = heightInPixels;
        holderBtn.setLayoutParams(layoutParams);

    }
}
