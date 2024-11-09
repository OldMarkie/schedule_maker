package com.mobdeve.s21.mco.schedule_maker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
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

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private TextView pageTitle, eventsForDateTitle;
    private CalendarView calendarView;
    private Date currentSelectedDate;
    private Button weeklyEventButton;
    private DatabaseHelper dbHelper;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsForDateTitle = findViewById(R.id.eventsForDateTitle);
        weeklyEventButton = findViewById(R.id.weeklyEventButton); // Initialize the button
        dbHelper= new DatabaseHelper(this);

        boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);


        // Disable past dates in the CalendarView
        calendarView.setMinDate(System.currentTimeMillis() - 1000);  // Disable past dates
        pageTitle.setText("Schedules");

        // Set up RecyclerView for events of a selected date
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this, new EventDetailsDialogFragment.OnEventActionListener(){
            @Override
            public void onEventDelete(Event event) {
                confirmDeleteEvent(event);
            }

            @Override
            public void onEventEdit(Event event) {
                Log.d("EventListActivity", "Editing Event: " + event.getName());

                if (event.isWeekly()) {
                    // Handle weekly event edit
                    Bundle args = new Bundle();
                    args.putString("eventId", event.getId()); // Pass the event ID
                    WeeklyActivityEditFragment editFragment = new WeeklyActivityEditFragment();
                    editFragment.setArguments(args);

                    // Replace the fragment or add to back stack using getSupportFragmentManager
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.editOneTImeEvent, editFragment)
                            .addToBackStack(null)
                            .commit();

                    findViewById(R.id.mainAEL).setVisibility(View.GONE);
                    findViewById(R.id.editOneTImeEvent).setVisibility(View.VISIBLE); // Ensure container is correct
                } else {
                    // Handle one-time event edit
                    OneTimeEventEditFragment oneTimeEditDialog = new OneTimeEventEditFragment();
                    Bundle args = new Bundle();

                    // Add event data to the bundle
                    args.putString("eventId", event.getId());
                    args.putString("eventName", event.getName());
                    args.putString("eventDescription", event.getDescription());
                    args.putString("eventLocation", event.getLocation());
                    args.putLong("startTime", event.getStartTime().getTime());
                    args.putLong("endTime", event.getEndTime().getTime());
                    args.putInt("eventColor", event.getColor());

                    // Set the arguments to the fragment
                    oneTimeEditDialog.setArguments(args);

                    // Begin transaction to replace the fragment using getSupportFragmentManager
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.editOneTImeEvent, oneTimeEditDialog)
                            .addToBackStack(null) // Add to back stack for navigation
                            .commit();

                    // Manage the visibility of views
                    findViewById(R.id.mainAEL).setVisibility(View.GONE);
                    findViewById(R.id.editOneTImeEvent).setVisibility(View.VISIBLE); // Ensure container is correct
                }
            }
        }, is24HourFormat);

        recyclerView.setAdapter(eventAdapter);

        // Load events for today
        currentSelectedDate = Calendar.getInstance().getTime();  // Set current date as default
        loadEventsForDate(currentSelectedDate);

        // Handle date selection on CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            currentSelectedDate = selectedDate.getTime();  // Update the current selected date
            loadEventsForDate(currentSelectedDate);
        });

        // Handle click for weekly event button
        weeklyEventButton.setOnClickListener(v -> {
            // Start the WeeklyEventActivity or Fragment
            Intent intent = new Intent(EventListActivity.this, WeeklySchedActivity.class);
            startActivity(intent);
        });

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_view_events);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
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

            return false;  // Return false if no cases matched
        });
    }

    // Load events for a specific date
    private void loadEventsForDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String selectedDateString = dateFormat.format(date);
        eventsForDateTitle.setText("Events for " + selectedDateString);

        eventList.clear();  // Clear current event list
        List<Event> eventsForDate = dbHelper.getEventsForDate(date);  // Fetch events from DatabaseHelper
        Log.d("EventAdapter", "Loaded " + eventsForDate.size() + " events for " + selectedDateString);


        if (eventsForDate.isEmpty()) {
            eventsForDateTitle.setText("No Scheduled Events For " + selectedDateString);
        } else {
            eventList.addAll(eventsForDate);  // Add events if available
        }

        Log.d("EventAdapter", "Item count: " + eventList.size());
        runOnUiThread(() -> eventAdapter.notifyDataSetChanged());
    }

    // Method to confirm event deletion
    private void confirmDeleteEvent(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Use DatabaseHelper to delete the event
                    dbHelper.deleteEvent(event.getName()); // Call the delete method with event ID
                    loadEventsForDate(currentSelectedDate);  // Refresh events for the current date
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void refreshEventsForCurrentDate() {
        loadEventsForDate(currentSelectedDate);
    }


}
