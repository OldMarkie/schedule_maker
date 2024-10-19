package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    // Use onResume to reload the list whenever this activity is returned to
    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();  // Reload the data and update the RecyclerView
    }

    // Load the events from the data source and update the adapter
    private void loadEvents() {
        eventList = DummyData.getEvents();  // Fetch the updated events from the data source
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();  // Notify the adapter that the data has changed
    }
}
