package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;
import java.util.List;

public class EventListActivity extends AppCompatActivity implements EventDetailsDialogFragment.OnEventActionListener {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Initialize the TextView
        pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("Schedules");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_view_events);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(EventListActivity.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_add_event) {
                    startActivity(new Intent(EventListActivity.this, EventActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_view_events) {
                    return true;  // Stay on view events page
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(EventListActivity.this, SettingsActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();  // Reload the data and update the RecyclerView
    }

    // Load the events from the data source and update the adapter
    private void loadEvents() {
        eventList = DummyData.getEvents();  // Fetch the updated events from the data source
        eventAdapter = new EventAdapter(eventList, this, this); // Pass the listener to handle event actions
        recyclerView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    // Implement event delete action
    @Override
    public void onEventDelete(Event event) {
        // Remove the event from the list and refresh the RecyclerView
        eventList.remove(event);
        eventAdapter.notifyDataSetChanged();
    }

    // Implement event edit action
    @Override
    public void onEventEdit(Event event) {
        // Handle event editing (for example, navigate to an editing screen)
        //Intent intent = new Intent(EventListActivity.this, EventEditActivity.class);
        //intent.putExtra("eventName", event.getName());
        //intent.putExtra("eventDate", event.getDateTime().getTime()); // Pass the event date as a long
        //startActivity(intent);
    }
}
