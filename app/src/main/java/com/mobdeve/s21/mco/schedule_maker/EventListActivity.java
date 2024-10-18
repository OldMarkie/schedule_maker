package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private TextView pageTitle;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Use a linear layout manager

        // Fetch events and set the adapter
        List<Event> eventList = DummyData.getEvents(); // Fetch the events from the data source
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);


        // Initialize the TextView
        pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("View Events");

        // Set up the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_view_events);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(EventListActivity.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_add_event) {
                    startActivity(new Intent(EventListActivity.this, EventActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_view_events) {
                    return true;  // Stay on view events page
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(EventListActivity.this, SettingsActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
    }
}
