package com.mobdeve.s21.mco.schedule_maker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView, recyclerViewWeekly;
    private EventAdapter eventAdapter, weeklyAdapter;
    private List<Event> eventList, weeklyEventList;
    private TextView pageTitle, weeklyScheduleTitle, eventsForDateTitle;
    private CalendarView calendarView;
    private Date currentSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        // Initialize views
        pageTitle = findViewById(R.id.pageTitle);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewWeekly = findViewById(R.id.recyclerViewWeekly);
        weeklyScheduleTitle = findViewById(R.id.weeklyScheduleTitle);
        eventsForDateTitle = findViewById(R.id.eventsForDateTitle);

        // Disable past dates in the CalendarView
        calendarView.setMinDate(System.currentTimeMillis() - 1000);  // Disable past dates
        pageTitle.setText("Schedules");

        // Set up RecyclerView for weekly events
        recyclerViewWeekly.setLayoutManager(new LinearLayoutManager(this));
        weeklyEventList = new ArrayList<>();
        weeklyAdapter = new EventAdapter(weeklyEventList, this, new EventDetailsDialogFragment.OnEventActionListener() {
            @Override
            public void onEventDelete(Event event) {
                confirmDeleteEvent(event);
            }

            @Override
            public void onEventEdit(Event event) {
                // Edit event logic
            }
        });
        recyclerViewWeekly.setAdapter(weeklyAdapter);

        // Set up RecyclerView for events of a selected date
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this, new EventDetailsDialogFragment.OnEventActionListener() {
            @Override
            public void onEventDelete(Event event) {
                confirmDeleteEvent(event);
            }

            @Override
            public void onEventEdit(Event event) {
                // Edit event logic
            }
        });
        recyclerView.setAdapter(eventAdapter);

        // Load the weekly schedule and events for today
        loadWeeklySchedule();
        currentSelectedDate = Calendar.getInstance().getTime();  // Set current date as default
        loadEventsForDate(currentSelectedDate);

        // Handle date selection on CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            currentSelectedDate = selectedDate.getTime();  // Update the current selected date
            loadEventsForDate(currentSelectedDate);
        });

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_view_events);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Convert switch to if-else statement
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

            return false;  // Return false if no cases matched
        });
    }

    // Load the weekly schedule for the current week
    private void loadWeeklySchedule() {
        Calendar calendar = Calendar.getInstance();
        int currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK);

        // Move to the first day of the week (Sunday)
        calendar.add(Calendar.DATE, -(currentWeekDay - Calendar.SUNDAY));

        weeklyEventList.clear();  // Clear the list for reloading

        for (int i = 0; i < 7; i++) {  // Loop through the 7 days of the week
            Date weekDay = calendar.getTime();
            List<Event> events = DummyData.getEventsForWeekDay(weekDay);  // Fetch events for the specific day
            weeklyEventList.addAll(events);
            calendar.add(Calendar.DATE, 1);  // Move to the next day
        }

        // If no weekly events are available, show "No schedule"
        if (weeklyEventList.isEmpty()) {
            weeklyScheduleTitle.setText("Weekly Schedule: No schedule");
        } else {
            weeklyScheduleTitle.setText("Weekly Schedule");
        }

        weeklyAdapter.notifyDataSetChanged();
    }

    // Load events for a specific date
    private void loadEventsForDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String selectedDateString = dateFormat.format(date);
        eventsForDateTitle.setText("Events for " + selectedDateString);

        eventList.clear();  // Clear current event list
        eventList.addAll(DummyData.getEventsForDate(date));  // Load events for the selected date
        eventAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the RecyclerView
    }

    // Method to confirm event deletion
    private void confirmDeleteEvent(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    DummyData.deleteEvent(event);
                    loadWeeklySchedule();  // Refresh weekly schedule
                    loadEventsForDate(currentSelectedDate);  // Refresh events for the current date
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
