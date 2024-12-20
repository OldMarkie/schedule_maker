package com.mobdeve.s21.mco.schedule_maker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

/**
 * The SettingsActivity class handles the settings screen for the app.
 * It allows users to toggle dark mode, switch time formats, manage notifications,
 * and sign in/out of Google Calendar for event synchronization.
 *
 * Features:
 * - Dark mode toggle
 * - Time format (24-hour or 12-hour) switch
 * - Notification enable/disable functionality
 * - Google Calendar integration for syncing events
 * - Clear local database and unlink Google account
 * - Bottom navigation for switching between screens
 *
 * Dependencies:
 * - Google Sign-In API
 * - Google Calendar API
 * - Android SharedPreferences for storing user settings
 */


public class SettingsActivity extends AppCompatActivity {

    private View LinkAccount, AccountName;
    private TextView pageTitle, userNameTextView;
    private Switch themeSwitch, timeFormatSwitch, notificationSwitch;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences and editor
        sharedPreferences = getSharedPreferences("ThemePref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Retrieve preferences for dark mode and time format
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);
        boolean isNotificationsEnabled = sharedPreferences.getBoolean("isNotificationsEnabled", false);

        // Set dark mode based on the saved preference
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Initialize pageTitle
        pageTitle = findViewById(R.id.pageTitle);
        pageTitle.setText("Settings");

        userNameTextView = findViewById(R.id.userNameTextView);

        LinkAccount = findViewById(R.id.LinkAccount);
        AccountName = findViewById(R.id.AccountName);

        // Initialize switches
        themeSwitch = findViewById(R.id.themeSwitch);
        timeFormatSwitch = findViewById(R.id.timeFormatSwitch);
        notificationSwitch = findViewById(R.id.notifSwitch);

        // Set switch states based on saved preferences
        themeSwitch.setChecked(isDarkMode);
        timeFormatSwitch.setChecked(is24HourFormat);
        notificationSwitch.setChecked(isNotificationsEnabled);



        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean("isNotificationsEnabled", isChecked);
            editor.apply();

            if (!isChecked) {
                new EnableNotificationsTask().execute(true);
            } else {
                new EnableNotificationsTask().execute(false);
            }
        });


        // Set listener for theme switch
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isDarkMode", isChecked);
                editor.apply();

                // Refresh the BottomNavigationView to apply theme changes
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
                int iconColor = getResources().getColor(isChecked ? R.color.bottomNavIconColor : R.color.bottomNavIconColor, getTheme());
                int textColor = getResources().getColor(isChecked ? R.color.bottomNavTextColor : R.color.bottomNavTextColor, getTheme());

                bottomNavigationView.setItemIconTintList(ColorStateList.valueOf(iconColor));
                bottomNavigationView.setItemTextColor(ColorStateList.valueOf(textColor));
            }



        });

        // Google Sign-In Configuration
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(CalendarScopes.CALENDAR))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Initiate Google Sign-In
        findViewById(R.id.signInBtn).setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // Set up sign-out button
        findViewById(R.id.signOutBtn).setOnClickListener(v -> {
            deleteAllEventsFromGoogleCalendar();
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Delete all local database contents
                    deleteLocalDatabaseContents();
                    // Update UI
                    userNameTextView.setText("Not signed in");
                    LinkAccount.setVisibility(View.VISIBLE);
                    AccountName.setVisibility(View.GONE);

                    showToast("Account unlinked successfully");
                } else {
                    showToast("Failed to unlink account");
                }
            });
        });

        // Display the signed-in user's name if already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            LinkAccount.setVisibility(View.GONE); // Hide LinkAccount
            AccountName.setVisibility(View.VISIBLE); // Show AccountName
            userNameTextView.setText(account.getDisplayName());
        } else {
            LinkAccount.setVisibility(View.VISIBLE); // Show LinkAccount
            AccountName.setVisibility(View.GONE); // Hide AccountName
            userNameTextView.setText("Not signed in");
        }





        // Set listener for time format switch
        timeFormatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("is24HourFormat", isChecked);
                editor.apply();
            }
        });


        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_add_event) {
                    startActivity(new Intent(SettingsActivity.this, EventActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_view_events) {
                    startActivity(new Intent(SettingsActivity.this, EventListActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_settings) {
                    return true;  // Stay on settings page
                }

                return false;
            }
        });
    }

    private void enableGoogleCalendarEventNotifications(Events event, com.google.api.services.calendar.Calendar service) {
        try {
            // Fetch the event from Google Calendar
            Event googleEvent = service.events().get("primary", event.getGoogleEventId()).execute();

            // Enable reminders for this event
            Event.Reminders eventReminders = new Event.Reminders()
                    .setUseDefault(true);  // Enable default reminders
            googleEvent.setReminders(eventReminders);

            // Update the event with the reminders
            service.events().update("primary", googleEvent.getId(), googleEvent).execute();
            Log.d("GoogleCalendar", "Notifications enabled for event: " + event.getGoogleEventId());
        } catch (Exception e) {
            Log.e("GoogleCalendar", "Failed to enable notifications for event: " + event.getGoogleEventId(), e);
        }
    }


    private void disableGoogleCalendarEventNotifications(Events event, com.google.api.services.calendar.Calendar service) {
        try {
            // Fetch the event from Google Calendar
            Event googleEvent = service.events().get("primary", event.getGoogleEventId()).execute();

            // Disable all reminders for this event
            Event.Reminders eventReminders = new Event.Reminders()
                    .setUseDefault(false);  // Disabling all reminders
            googleEvent.setReminders(eventReminders);

            // Update the event with no notifications
            service.events().update("primary", googleEvent.getId(), googleEvent).execute();
            Log.d("GoogleCalendar", "Notifications disabled for event: " + event.getGoogleEventId());
        } catch (Exception e) {
            Log.e("GoogleCalendar", "Failed to disable notifications for event: " + event.getGoogleEventId(), e);
        }
    }


    private void deleteLocalDatabaseContents() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.clearAllEvents();
        showToast("Local database cleared");
    }


    private void deleteAllEventsFromGoogleCalendar() {
        Log.d("GoogleCalendarDelete", "Attempting to delete all events from Google Calendar");

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

        // Initialize Google Calendar API service
        com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Schedule Maker").build();

        // Retrieve all Google Calendar event IDs for all events
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<String> googleEventIds = dbHelper.getGoogleEventIdsForEvents(); // Get all event IDs
        if (googleEventIds == null || googleEventIds.isEmpty()) {
            Log.e("GoogleCalendarDelete", "No Google Event IDs found for any event.");
            Toast.makeText(this, "No Google Calendar events found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("GoogleCalendarDelete", "Google Event IDs found: " + googleEventIds);

        // Create and show the progress dialog
        View progressView = getLayoutInflater().inflate(R.layout.progress_dialog, null);
        ProgressBar progressBar = progressView.findViewById(R.id.progressBar);
        TextView progressText = progressView.findViewById(R.id.descriptionTV);
        AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setTitle("Deleting Events")
                .setView(progressView)
                .setCancelable(false) // Disable cancellation
                .create();
        progressDialog.show();

        // Start a background thread to delete all events
        new Thread(() -> {
            int totalEvents = googleEventIds.size();
            int deletedEvents = 0;

            for (String googleEventId : googleEventIds) {
                try {
                    // Delete each event from Google Calendar
                    service.events().delete("primary", googleEventId).execute();
                    deletedEvents++;
                    Log.d("GoogleCalendarDelete", "Successfully deleted Google Calendar event: " + googleEventId);

                    // Update the progress
                    int progress = (int) ((double) deletedEvents / totalEvents * 100);
                    int finalProgress = progress;
                    runOnUiThread(() -> {
                        progressBar.setProgress(finalProgress);
                        progressText.setText("Deleting events: " + finalProgress + "%");
                    });

                } catch (Exception e) {
                    Log.e("GoogleCalendarDelete", "Failed to delete event with ID: " + googleEventId, e);
                }
            }

            // Notify the user of success and dismiss the progress dialog
            runOnUiThread(() -> {
                progressDialog.dismiss();
                Toast.makeText(this, "All events deleted from Google Calendar!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }



    // AsyncTask to handle enabling/disabling notifications
    private class EnableNotificationsTask extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Create a custom AlertDialog using the layout
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setView(R.layout.progress_dialog);  // Use the custom layout
            builder.setCancelable(false);  // Prevent cancellation by user
            progressDialog = builder.create();



            // Show the dialog
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            boolean enableNotifications = params[0];

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SettingsActivity.this);
            if (account != null) {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                        SettingsActivity.this,
                        Collections.singleton(CalendarScopes.CALENDAR)
                );
                credential.setSelectedAccount(account.getAccount());

                com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential
                ).setApplicationName("Schedule Maker").build();

                // Get all events from your local database
                DatabaseHelper dbHelper = new DatabaseHelper(SettingsActivity.this);
                List<Events> allEvents = dbHelper.getAllEvents();

                // Process events for enabling/disabling notifications
                for (Events event : allEvents) {
                    if (enableNotifications) {
                        enableGoogleCalendarEventNotifications(event, service);
                    } else {
                        disableGoogleCalendarEventNotifications(event, service);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, "Notifications updated!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    LinkAccount.setVisibility(View.GONE); // Hide LinkAccount
                    AccountName.setVisibility(View.VISIBLE); // Show AccountName
                    userNameTextView.setText(account.getDisplayName());
                    showToast("Signed in as: " + account.getDisplayName());
                }
            } catch (ApiException e) {
                LinkAccount.setVisibility(View.VISIBLE); // Show LinkAccount
                AccountName.setVisibility(View.GONE); // Hide AccountName
                userNameTextView.setText("Not signed in");
                showToast("Sign-in failed: " + e.getMessage());
            }
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
