package com.mobdeve.s21.mco.schedule_maker;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeeklyScheduleActivity extends AppCompatActivity {

    private RecyclerView weeklyRecyclerView;
    private TextView weekTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_schedule);

        weeklyRecyclerView = findViewById(R.id.weeklyRecyclerView);
        weekTitle = findViewById(R.id.weekTitle);

        // Set the week range (Monday to Sunday)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date monday = calendar.getTime();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date sunday = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
        weekTitle.setText("Week: " + format.format(monday) + " - " + format.format(sunday));

        // Get events for this week
        List<Event> weeklyEvents = getEventsForWeek(monday, sunday);

        // Set up RecyclerView and Adapter
        weeklyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        EventAdapter adapter = new EventAdapter(weeklyEvents, this, null); // Pass null if you don't need listener
        weeklyRecyclerView.setAdapter(adapter);
    }

    private List<Event> getEventsForWeek(Date monday, Date sunday) {
        List<Event> allEvents = DummyData.getEvents();
        List<Event> weeklyEvents = new ArrayList<>();

        for (Event event : allEvents) {
            if (!event.getDateTime().before(monday) && !event.getDateTime().after(sunday)) {
                weeklyEvents.add(event);
            }
        }

        return weeklyEvents;
    }
}

