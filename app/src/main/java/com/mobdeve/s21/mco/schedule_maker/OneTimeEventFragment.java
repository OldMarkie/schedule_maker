package com.mobdeve.s21.mco.schedule_maker;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/*
 * This fragment handles creating one-time events.
 * It includes features for setting event details such as name, description, location,
 * date, time, and color, along with saving the event locally and to Google Calendar.
 */

public class OneTimeEventFragment extends Fragment {

    private EditText eventNameInput;
    private EditText eventDescriptionInput;
    private EditText eventLocationInput;
    private EditText eventDateInput;
    private EditText eventTimeInput;
    private EditText eventEndTimeInput;
    private Button saveButton;

    private int selectedColor = 0xFFFFFFFF; // Default color is white
    private Button colorPickerInput;

    private ColorUtils colorUtils;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int MAP_REQUEST_CODE = 2;
    private PlacesClient placesClient;
    private final String googleEventId = "";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_time_event, container, false);

        eventNameInput = view.findViewById(R.id.eventNameInput);
        eventDescriptionInput = view.findViewById(R.id.eventDescriptionInput);
        eventLocationInput = view.findViewById(R.id.eventLocationInput);
        eventDateInput = view.findViewById(R.id.eventDateInput);
        eventTimeInput = view.findViewById(R.id.eventTimeInput);
        eventEndTimeInput = view.findViewById(R.id.eventEndTimeInput);
        saveButton = view.findViewById(R.id.saveButton);
        colorPickerInput = view.findViewById(R.id.colorPickerInput);

        colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(0xff6200EE));
        colorPickerInput.setHint("Electric Violet By Default");
        colorPickerInput.setHintTextColor(Color.WHITE);

        placesClient = Places.createClient(requireContext());
        eventLocationInput.setOnClickListener(v -> openMapForLocation());


        // Load color names
        try {
            InputStream inputStream = requireContext().getAssets().open("colornames.json");
            colorUtils = new ColorUtils(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

// Open the color picker dialog when the color input field is clicked
        colorPickerInput.setOnClickListener(v -> openColorPicker());

        // Retrieve dark mode preference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);

        // Set up Material Date Picker
        eventDateInput.setOnClickListener(v -> showDatePicker());

        // Set up Material Time Picker
        eventTimeInput.setOnClickListener(v -> showTimePicker(eventTimeInput));
        eventEndTimeInput.setOnClickListener(v -> showTimePicker(eventEndTimeInput));

        saveButton.setOnClickListener(v -> saveEvent());

        return view;
    }


    private void openMapForLocation() {
        Intent intent = new Intent(getContext(), MapLocationPickerActivity.class);
        startActivityForResult(intent, MAP_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            LatLng selectedLocation = data.getParcelableExtra("selected_location");
            String selectedAddress = data.getStringExtra("selected_address");
            if (selectedAddress != null) {
                eventLocationInput.setText(selectedAddress);
            } else if (selectedLocation != null) {
                String locationString = selectedLocation.latitude + ", " + selectedLocation.longitude;
                eventLocationInput.setText(locationString);
            }
        }
    }




    private void setTextColorBasedOnContrast(int selectedColor) {
        // Calculate the luminance
        double luminance = (0.299 * Color.red(selectedColor) + 0.587 * Color.green(selectedColor) + 0.114 * Color.blue(selectedColor)) / 255;
        if (luminance < 0.5) {
            colorPickerInput.setTextColor(Color.WHITE); // Light text on dark background
        } else {
            colorPickerInput.setTextColor(Color.BLACK); // Dark text on light background
        }
    }


    private void openColorPicker() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(selectedColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        // Get nearest color name
                        String nearestColorName = colorUtils.getNearestColorName(color);
                        Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color) + " (" + nearestColorName + ")", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int color, Integer[] allColors) {
                        selectedColor = color;
                        // Update colorPickerInput
                        String nearsteColorName = colorUtils.getNearestColorName(selectedColor);
                        colorPickerInput.setText(nearsteColorName);
                        colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(selectedColor));

                        // Set text color based on contrast
                        setTextColorBasedOnContrast(selectedColor);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing if user cancels
                })
                .build()
                .show();
    }


    private void showDatePicker() {
        // Get today's date in milliseconds
        long todayInMillis = MaterialDatePicker.todayInUtcMilliseconds();

        // Create a MaterialDatePicker with constraints
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Events Date")
                .setSelection(todayInMillis) // Start with today's date selected
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setStart(todayInMillis) // Set the minimum date to today
                        .setEnd(MaterialDatePicker.todayInUtcMilliseconds() + 1000L * 60 * 60 * 24 * 365) // Set the end date to one year from today
                        .build())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
            eventDateInput.setText(formattedDate);
        });
    }


    private void showTimePicker(EditText timeInput) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTitleText("Select Time")
                .setPositiveButtonText("OK")
                .setNegativeButtonText("CANCEL")
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                .build();

        timePicker.show(getParentFragmentManager(), "TIME_PICKER");
        timePicker.addOnPositiveButtonClickListener(v -> {
            String formattedTime = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());
            timeInput.setText(formattedTime);
        });
    }

    private void saveEvent() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        String eventName = eventNameInput.getText().toString().trim();
        String eventDescription = eventDescriptionInput.getText().toString().trim();
        String eventLocation = eventLocationInput.getText().toString().trim();
        String eventDate = eventDateInput.getText().toString().trim();
        String eventTime = eventTimeInput.getText().toString().trim();
        String eventEndTime = eventEndTimeInput.getText().toString().trim();
        String color = colorPickerInput.getText().toString().trim();

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventLocation.isEmpty() ||
                eventDate.isEmpty() || eventTime.isEmpty() || eventEndTime.isEmpty()) {
            showAlertDialog("Error", "Please fill in all fields.");
            return;
        }

        if (dbHelper.eventExists(eventName)) {
            showAlertDialog("Duplicate Event", "An event with the same name already exists!");
            return;
        }

        String startDateTimeString = eventDate + " " + eventTime;
        String endDateTimeString = eventDate + " " + eventEndTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date eventStartDateTime;
        Date eventEndDateTime;

        try {
            eventStartDateTime = dateFormat.parse(startDateTimeString);
            eventEndDateTime = dateFormat.parse(endDateTimeString);

            if (eventEndDateTime.equals(eventStartDateTime) || eventEndDateTime.before(eventStartDateTime)) {
                showAlertDialog("Error", "End time must be later than start time.");
                return;
            }
        } catch (ParseException e) {
            showAlertDialog("Error", "Invalid date or time format.");
            return;
        }

        getActivity().runOnUiThread(() -> {
            AlertDialog progressDialog = new AlertDialog.Builder(getContext())
                    .setView(R.layout.progress_dialog)
                    .setCancelable(false)
                    .create();
            progressDialog.show();

            new Thread(() -> {
                try {
                    String eventId = UUID.randomUUID().toString();
                    int eventColor = colorPickerInput.getBackgroundTintList().getDefaultColor();
                    Events newEvents = new Events(eventId, eventName, eventDescription, eventLocation,
                            eventStartDateTime, eventEndDateTime, false, eventColor, -1);

                    saveEventToGoogleCalendar(eventName, eventDescription, eventLocation, eventStartDateTime, eventEndDateTime, newEvents);

                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        showAlertDialog("Success", "Event successfully saved!");
                        ((EventActivity) getActivity()).resetEventActivity();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        showAlertDialog("Error", "Failed to save the event.");
                    });
                }
            }).start();
        });
    }



    private void saveEventToGoogleCalendar(String title, String description, String location, Date startDateTime, Date endDateTime, Events newEvents) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account == null) {
            if (getContext() != null) {
                getActivity().runOnUiThread(() ->
                        new AlertDialog.Builder(getContext())
                                .setTitle("Error")
                                .setMessage("You need to sign in with Google first!")
                                .setPositiveButton("OK", null)
                                .show()
                );
            }
            return;
        }

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                getContext(),
                Collections.singleton(CalendarScopes.CALENDAR)
        );
        credential.setSelectedAccount(account.getAccount());

        com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
                AndroidHttp.newCompatibleTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Schedule Maker").build();

        String sanitizedId = newEvents.getId().replace("-", "").toLowerCase();
        Event event = new Event()
                .setId(sanitizedId)
                .setSummary(title)
                .setDescription(description)
                .setLocation(location);

        newEvents.setGoogleEventId(event.getId());

        DateTime start = new DateTime(startDateTime);
        EventDateTime startEventDateTime = new EventDateTime().setDateTime(start).setTimeZone("Asia/Manila");
        event.setStart(startEventDateTime);

        DateTime end = new DateTime(endDateTime);
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(end).setTimeZone("Asia/Manila");
        event.setEnd(endEventDateTime);

        EventReminder[] reminders = new EventReminder[]{
                new EventReminder().setMethod("popup").setMinutes(10),
                new EventReminder().setMethod("email").setMinutes(30)
        };

        Event.Reminders eventReminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminders));
        event.setReminders(eventReminders);

        // Run the database and API operations in the background
        new Thread(() -> {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                dbHelper.addEvent(newEvents);
                Event insertedEvent = service.events().insert("primary", event).execute();
                String googleEventId = insertedEvent.getId();
                Log.d("GoogleCalendarEvent", "Inserted Event ID: " + googleEventId);
                newEvents.setGoogleEventId(googleEventId);
                dbHelper.updateEventWithGoogleEventId(newEvents);

                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() ->
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Success")
                                    .setMessage("Event added to Google Calendar!")
                                    .setPositiveButton("OK", null)
                                    .show()
                    );
                }
            } catch (Exception e) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() ->
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Error")
                                    .setMessage("Failed to save event to Google Calendar")
                                    .setPositiveButton("OK", null)
                                    .show()
                    );
                }
                e.printStackTrace();
            }
        }).start();
    }


    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }



    public void onDestroy() {
        super.onDestroy();
        if (placesClient != null) {
            placesClient = null;     // Avoid memory leaks
        }
    }


}
