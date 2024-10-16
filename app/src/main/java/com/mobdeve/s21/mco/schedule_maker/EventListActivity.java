package com.mobdeve.s21.mco.schedule_maker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private TextView tvNoEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        recyclerView = findViewById(R.id.recyclerView);
        tvNoEvents = findViewById(R.id.tvNoEvents);

        // Fetch event data (dummy data for now)
        List<Event> eventList = DummyData.getEvents();

        if (eventList.isEmpty()) {
            tvNoEvents.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoEvents.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Set up the RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            eventAdapter = new EventAdapter(eventList);
            recyclerView.setAdapter(eventAdapter);
        }
    }
}
