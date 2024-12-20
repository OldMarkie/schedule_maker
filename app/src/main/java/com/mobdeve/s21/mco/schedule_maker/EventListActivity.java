package com.mobdeve.s21.mco.schedule_maker;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * EventListActivity is a subclass of AppCompatActivity that serves as the main screen
 * for displaying a list of events in the application. This activity is responsible for
 * retrieving and presenting a list of events from the database or other data sources
 * in a user-friendly manner.
 * */

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Events> eventsList;
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
        eventsList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventsList, this, new EventDetailsDialogFragment.OnEventActionListener(){
            @Override
            public void onEventDelete(Events events) {
                confirmDeleteEvent(events);
            }

            @Override
            public void onEventEdit(Events events) {
                Log.d("EventListActivity", "Editing Events: " + events.getName());

                if (events.isWeekly()) {
                    // Handle weekly events edit
                    Bundle args = new Bundle();
                    args.putString("eventName", events.getName()); // Pass the events ID
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
                    // Handle one-time events edit
                    OneTimeEventEditFragment oneTimeEditDialog = new OneTimeEventEditFragment();
                    Bundle args = new Bundle();

                    // Add events data to the bundle
                    args.putString("eventId", events.getId());
                    args.putString("eventName", events.getName());
                    args.putString("eventDescription", events.getDescription());
                    args.putString("eventLocation", events.getLocation());
                    args.putLong("startTime", events.getStartTime().getTime());
                    args.putLong("endTime", events.getEndTime().getTime());
                    args.putInt("eventColor", events.getColor());

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

        eventsList.clear();  // Clear current event list
        List<Events> eventsForDate = dbHelper.getEventsForDate(date);  // Fetch events from DatabaseHelper
        Log.d("EventAdapter", "Loaded " + eventsForDate.size() + " events for " + selectedDateString);


        if (eventsForDate.isEmpty()) {
            eventsForDateTitle.setText("No Scheduled Events For " + selectedDateString);
        } else {
            eventsList.addAll(eventsForDate);  // Add events if available
        }

        Log.d("EventAdapter", "Item count: " + eventsList.size());
        eventAdapter.notifyDataSetChanged();
    }

    // Method to confirm events deletion
    private void confirmDeleteEvent(Events events) {
        Log.d("ConfirmDeleteEvent", "User initiated delete for event: " + events.getName());
        //Events updatedEvent = dbHelper.getEventById(events.getId());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Events")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if(events.isWeekly()){
                        Log.d("ConfirmDeleteEvent", "User confirmed deletion for event: " + events.getName());
                        deleteAllInstancesFromGoogleCalendar(events);
                        dbHelper.deleteEvent(events.getName());
                        Log.d("ConfirmDeleteEvent", "Event deleted from local database: " + events.getName());
                    }else{
                        Log.d("ConfirmDeleteEvent", "User confirmed deletion for event: " + events.getName());
                        // Use DatabaseHelper to delete the event
                        deleteEventFromGoogleCalendar(events);
                        dbHelper.deleteEvent(events.getName()); // Call the delete method with events ID
                        Log.d("ConfirmDeleteEvent", "Event deleted from local database: " + events.getName());
                    }
                    loadEventsForDate(currentSelectedDate);  // Refresh events for the current date
                    Log.d("ConfirmDeleteEvent", "Events reloaded for date: " + currentSelectedDate);
                    refreshEventsForCurrentDate();
                })
                .setNegativeButton("Cancel", (dialog, which) ->
                        Log.d("ConfirmDeleteEvent", "User canceled event deletion: " + events.getName())
                )
                .show();
    }

    // Method to delete event from Google Calendar
    private void deleteEventFromGoogleCalendar(Events event) {
        Log.d("GoogleCalendarDelete", "Attempting to delete event from Google Calendar: " + event.getName());
        Log.d("ConfirmDeleteEvent", "User initiated delete for event: " + formatEventDetails(event));

        // Ensure the user is signed in with Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Log.e("GoogleCalendarDelete", "Google account not signed in");
            Toast.makeText(this, "You need to sign in with Google first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Google Calendar API credentials
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                this,
                Collections.singleton(CalendarScopes.CALENDAR)
        );
        credential.setSelectedAccount(account.getAccount());
        Log.d("GoogleCalendarDelete", "Google API credentials initialized");

        com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Schedule Maker").build();

        // Get the Google Event ID
        String googleEventId = event.getGoogleEventId();
        if (googleEventId == null || googleEventId.isEmpty()) {
            Log.e("GoogleCalendarDelete", "Google Event ID is missing for event: " + event.getName());
            Toast.makeText(this, "Google Calendar Event ID not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("GoogleCalendarDelete", "Google Event ID found: " + googleEventId);

        // Inflate custom layout for progress dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View progressDialogView = inflater.inflate(R.layout.progress_dialog, null);

        // Set the text in the description TextView
        TextView descriptionTV = progressDialogView.findViewById(R.id.descriptionTV);
        if (descriptionTV != null) {
            descriptionTV.setText("Deleting...");
        }

        // Build the AlertDialog
        androidx.appcompat.app.AlertDialog progressDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(progressDialogView) // Use the inflated view
                .setCancelable(false)
                .create();

        // Show the progress dialog
        progressDialog.show();

        // Start a background thread to perform the deletion
        new Thread(() -> {
            try {
                // Delete the event from Google Calendar
                service.events().delete("primary", googleEventId).execute();
                Log.d("GoogleCalendarDelete", "Successfully deleted Google Calendar event: " + googleEventId);

                runOnUiThread(() -> {
                    // Dismiss progress dialog
                    progressDialog.dismiss();
                    Toast.makeText(this, "Event deleted from Google Calendar!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e("GoogleCalendarDelete", "Failed to delete event from Google Calendar", e);
                runOnUiThread(() -> {
                    // Dismiss progress dialog
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to delete event from Google Calendar", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


    // Method to delete all Google Calendar instances of a recurring event
    private void deleteAllInstancesFromGoogleCalendar(Events recurringEvent) {
        Log.d("GoogleCalendarDelete", "Attempting to delete all instances of recurring event: " + recurringEvent.getName());

        // Ensure the user is signed in with Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this); // Use 'this' for Activity context
        if (account == null) {
            Log.e("GoogleCalendarDelete", "Google account not signed in");
            Toast.makeText(this, "You need to sign in with Google first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Google Calendar API credentials
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                this, // Use 'this' for Activity context
                Collections.singleton(CalendarScopes.CALENDAR)
        );
        credential.setSelectedAccount(account.getAccount());
        Log.d("GoogleCalendarDelete", "Google API credentials initialized");

        // Initialize Google Calendar API service
        com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Schedule Maker").build();

        // Retrieve all Google Calendar event IDs for this recurring event
        List<String> googleEventIds = dbHelper.getGoogleEventIdsForRecurringEvent(recurringEvent.getName());
        if (googleEventIds == null || googleEventIds.isEmpty()) {
            Log.e("GoogleCalendarDelete", "No Google Event IDs found for recurring event: " + recurringEvent.getName());
            Toast.makeText(this, "No Google Calendar events found for this recurring event!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("GoogleCalendarDelete", "Google Event IDs found: " + googleEventIds);


        // Inflate custom layout for progress dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View progressDialogView = inflater.inflate(R.layout.progress_dialog, null);

        // Set the text in the description TextView
        TextView descriptionTV = progressDialogView.findViewById(R.id.descriptionTV);
        if (descriptionTV != null) {
            descriptionTV.setText("Deleting...");
        }

        // Build the AlertDialog
        androidx.appcompat.app.AlertDialog progressDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(progressDialogView) // Use the inflated view
                .setCancelable(false)
                .create();


        // Show the progress dialog
        progressDialog.show();

        // Start a background thread to delete all events
        new Thread(() -> {
            for (String googleEventId : googleEventIds) {
                try {
                    // Delete each event from Google Calendar
                    service.events().delete("primary", googleEventId).execute();
                    Log.d("GoogleCalendarDelete", "Successfully deleted Google Calendar event: " + googleEventId);
                } catch (Exception e) {
                    Log.e("GoogleCalendarDelete", "Failed to delete event with ID: " + googleEventId, e);
                }
            }

            // Dismiss progress dialog and notify user
            runOnUiThread(() -> {
                progressDialog.dismiss();
                Toast.makeText(this, "All instances of the recurring event deleted from Google Calendar!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }






    public void refreshEventsForCurrentDate() {
        loadEventsForDate(currentSelectedDate);
        eventAdapter.notifyDataSetChanged();
    }

    private String formatEventDetails(Events event) {
        return "Event Details: " +
                "\nID: " + event.getId() +
                "\nName: " + event.getName() +
                "\nDescription: " + event.getDescription() +
                "\nLocation: " + event.getLocation() +
                "\nStart: " + event.getStartTime() +
                "\nEnd: " + event.getEndTime() +
                "\nGoogle Event ID: " + (event.getGoogleEventId() != null ? event.getGoogleEventId() : "N/A");
    }



}
