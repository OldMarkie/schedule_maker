package com.mobdeve.s21.mco.schedule_maker;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class WeeklyActivityFragment extends Fragment {

    // DatabaseHelper instance
    private DatabaseHelper dbHelper;

    // Other fields...
    private EditText activityNameInput, activityDescriptionInput, activityLocationInput;
    private CheckBox checkMonday, checkTuesday, checkWednesday, checkThursday, checkFriday, checkSaturday, checkSunday;
    private EditText mondayStartTimeInput, mondayEndTimeInput;
    private EditText tuesdayStartTimeInput, tuesdayEndTimeInput;
    private EditText wednesdayStartTimeInput, wednesdayEndTimeInput;
    private EditText thursdayStartTimeInput, thursdayEndTimeInput;
    private EditText fridayStartTimeInput, fridayEndTimeInput;
    private EditText saturdayStartTimeInput, saturdayEndTimeInput;
    private EditText sundayStartTimeInput, sundayEndTimeInput;
    private Button saveButton, colorPickerInput;
    private int selectedColor = 0xFFFFFFFF; // Default color is white
    private ColorUtils colorUtils;
    private static final int MAP_REQUEST_CODE = 2;

    private PlacesClient placesClient;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context); // Initialize DatabaseHelper
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_activity, container, false);

        // Initialize views...
        activityNameInput = view.findViewById(R.id.activityNameInput);
        activityDescriptionInput = view.findViewById(R.id.activityDescriptionInput);
        activityLocationInput = view.findViewById(R.id.activityLocationInput);
        checkMonday = view.findViewById(R.id.checkMonday);
        mondayStartTimeInput = view.findViewById(R.id.mondayStartTimeInput);
        mondayEndTimeInput = view.findViewById(R.id.mondayEndTimeInput);
        checkTuesday = view.findViewById(R.id.checkTuesday);
        tuesdayStartTimeInput = view.findViewById(R.id.tuesdayStartTimeInput);
        tuesdayEndTimeInput = view.findViewById(R.id.tuesdayEndTimeInput);
        checkWednesday = view.findViewById(R.id.checkWednesday);
        wednesdayStartTimeInput = view.findViewById(R.id.wednesdayStartTimeInput);
        wednesdayEndTimeInput = view.findViewById(R.id.wednesdayEndTimeInput);
        checkThursday = view.findViewById(R.id.checkThursday);
        thursdayStartTimeInput = view.findViewById(R.id.thursdayStartTimeInput);
        thursdayEndTimeInput = view.findViewById(R.id.thursdayEndTimeInput);
        checkFriday = view.findViewById(R.id.checkFriday);
        fridayStartTimeInput = view.findViewById(R.id.fridayStartTimeInput);
        fridayEndTimeInput = view.findViewById(R.id.fridayEndTimeInput);
        checkSaturday = view.findViewById(R.id.checkSaturday);
        saturdayStartTimeInput = view.findViewById(R.id.saturdayStartTimeInput);
        saturdayEndTimeInput = view.findViewById(R.id.saturdayEndTimeInput);
        checkSunday = view.findViewById(R.id.checkSunday);
        sundayStartTimeInput = view.findViewById(R.id.sundayStartTimeInput);
        sundayEndTimeInput = view.findViewById(R.id.sundayEndTimeInput);
        saveButton = view.findViewById(R.id.saveButton);
        colorPickerInput = view.findViewById(R.id.colorPickerInput);

        saveButton.setOnClickListener(v -> saveActivity());

        // Open the color picker dialog when the color input field is clicked
        colorPickerInput.setOnClickListener(v -> openColorPicker());


        colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(0xff6200EE));
        colorPickerInput.setHint("Electric Violet By Default");
        colorPickerInput.setHintTextColor(Color.WHITE);

        // Load color names
        try {
            InputStream inputStream = requireContext().getAssets().open("colornames.json");
            colorUtils = new ColorUtils(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initially set all time inputs to be gone
        setTimeInputVisibility();

        // Set up listeners to show/hide time input fields based on checkbox state
        setupCheckBoxListeners();

        List<Events> eventsList = new ArrayList<>();


        saveButton.setOnClickListener(v -> {
            saveActivity();
        });

        activityLocationInput.setOnClickListener(v -> openMapForLocation());
        placesClient = Places.createClient(requireContext());
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
                activityLocationInput.setText(selectedAddress);
            } else if (selectedLocation != null) {
                String locationString = selectedLocation.latitude + ", " + selectedLocation.longitude;
                activityLocationInput.setText(locationString);
            }
        }
    }

    private void setupCheckBoxListeners() {
        checkMonday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTimeInputs(mondayStartTimeInput, mondayEndTimeInput, isChecked);
        });
        checkTuesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTimeInputs(tuesdayStartTimeInput, tuesdayEndTimeInput, isChecked);
        });
        checkWednesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTimeInputs(wednesdayStartTimeInput, wednesdayEndTimeInput, isChecked);
        });
        checkThursday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTimeInputs(thursdayStartTimeInput, thursdayEndTimeInput, isChecked);
        });
        checkFriday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTimeInputs(fridayStartTimeInput, fridayEndTimeInput, isChecked);
        });
        checkSaturday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTimeInputs(saturdayStartTimeInput, saturdayEndTimeInput, isChecked);
        });
        checkSunday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleTimeInputs(sundayStartTimeInput, sundayEndTimeInput, isChecked);
        });
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


    private void toggleTimeInputs(EditText startInput, EditText endInput, boolean isChecked) {
        int visibility = isChecked ? View.VISIBLE : View.GONE;
        startInput.setVisibility(visibility);
        endInput.setVisibility(visibility);
        if (isChecked) {
            startInput.setOnClickListener(v -> showTimePicker(startInput));
            endInput.setOnClickListener(v -> showTimePicker(endInput));
        }
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

    private void setTimeInputVisibility() {
        mondayStartTimeInput.setVisibility(View.GONE);
        mondayEndTimeInput.setVisibility(View.GONE);
        tuesdayStartTimeInput.setVisibility(View.GONE);
        tuesdayEndTimeInput.setVisibility(View.GONE);
        wednesdayStartTimeInput.setVisibility(View.GONE);
        wednesdayEndTimeInput.setVisibility(View.GONE);
        thursdayStartTimeInput.setVisibility(View.GONE);
        thursdayEndTimeInput.setVisibility(View.GONE);
        fridayStartTimeInput.setVisibility(View.GONE);
        fridayEndTimeInput.setVisibility(View.GONE);
        saturdayStartTimeInput.setVisibility(View.GONE);
        saturdayEndTimeInput.setVisibility(View.GONE);
        sundayStartTimeInput.setVisibility(View.GONE);
        sundayEndTimeInput.setVisibility(View.GONE);
    }


    private void saveActivity() {
        String activityName = activityNameInput.getText().toString().trim();
        String activityDescription = activityDescriptionInput.getText().toString().trim();
        String activityLocation = activityLocationInput.getText().toString().trim();

        boolean isWeekly = checkMonday.isChecked() || checkTuesday.isChecked() || checkWednesday.isChecked() ||
                checkThursday.isChecked() || checkFriday.isChecked() || checkSaturday.isChecked() || checkSunday.isChecked();

        if (isWeekly) {
            Calendar baseDate = Calendar.getInstance();

            if (checkMonday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                saveWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        mondayStartTimeInput, mondayEndTimeInput, 1);
            }
            if (checkTuesday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                saveWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        tuesdayStartTimeInput, tuesdayEndTimeInput, 2);
            }
            if (checkWednesday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                saveWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        wednesdayStartTimeInput, wednesdayEndTimeInput, 3);
            }
            if (checkThursday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                saveWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        thursdayStartTimeInput, thursdayEndTimeInput, 4);
            }
            if (checkFriday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                saveWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        fridayStartTimeInput, fridayEndTimeInput, 5);
            }
            if (checkSaturday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                saveWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        saturdayStartTimeInput, saturdayEndTimeInput, 6);
            }
            if (checkSunday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                saveWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        sundayStartTimeInput, sundayEndTimeInput, 7);
            }

            Toast.makeText(getActivity(), "Events saved successfully!", Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // After saving, show the hint text view again
            ((EventActivity) getActivity()).resetEventActivity();

        } else {
            Toast.makeText(getActivity(), "Please select at least one day for the weekly event.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveWeeklyEventToDatabase(String name, String description, String location, Calendar baseDate,
                                           EditText startTimeInput, EditText endTimeInput, int dayWeek) {
        Date startTime = parseTime(startTimeInput.getText().toString());
        Date endTime = parseTime(endTimeInput.getText().toString());
        ColorStateList colorStateList = colorPickerInput.getBackgroundTintList();
        int eventColor = colorStateList.getDefaultColor();

        // Debugging the dayWeek value
        Log.d("Events", "dayWeek value: " + dayWeek);

        // Ensure dayWeek is not null or invalid (set a default value if necessary)
        if (dayWeek < 1 || dayWeek > 7) {
            Log.w("Events", "Invalid dayWeek value. Setting to default (0 - Monday).");
            dayWeek = 1;  // Default to Sunday (or handle this appropriately)
        }

        if (startTime != null && endTime != null) {

            // Iterate through each week for the next year (52 weeks)
            for (int i = 0; i < 13; i++) {
                // Create the start and end Date for the events instance
                Calendar eventDate = (Calendar) baseDate.clone();
                eventDate.add(Calendar.WEEK_OF_YEAR, i); // Move to the correct week

                Date eventStartDate = combineDateAndTime(eventDate, startTime);
                Date eventEndDate = combineDateAndTime(eventDate, endTime);

                Log.d("Events", "Events " + (i + 1) + " - Start: " + eventStartDate + ", End: " + eventEndDate);

                // Check for conflicts
                if (dbHelper.isTimeConflict(eventStartDate, eventEndDate)) {
                    Toast.makeText(getActivity(), "Events time conflict for week " + (i + 1) + "!", Toast.LENGTH_SHORT).show();
                    continue; // Optionally continue to check the next week
                }

                String counter = UUID.randomUUID().toString();
                // Create the Events object
                Events events = new Events(counter, name, description, location, eventStartDate, eventEndDate, true, eventColor, dayWeek);
                saveEventToGoogleCalendar(name,description,location,eventStartDate,eventEndDate, events);
                // Save the Events object to the database
                boolean success = dbHelper.addEvent(events);
                if (!success) {
                    Toast.makeText(getContext(), "Failed to save events for week " + (i + 1), Toast.LENGTH_SHORT).show();
                    Log.e("Events", "Failed to save events for week " + (i + 1));
                } else {
                    Log.d("Events", "Events saved for week " + (i + 1));
                }
            }
            Toast.makeText(getContext(), "Weekly Events Saved for Three Months!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Invalid start or end time!", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveEventToGoogleCalendar(String title, String description, String location, Date startDateTime, Date endDateTime, Events newEvents) {
        // Initialize the Calendar API service
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account == null) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "You need to sign in with Google first!", Toast.LENGTH_SHORT).show();
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
        // Create the Google Calendar Event
        Event event = new Event()
                .setId(sanitizedId)
                .setSummary(title)
                .setDescription(description)
                .setLocation(location);

        newEvents.setGoogleEventId(event.getId());

        // Set start and end times
        DateTime start = new DateTime(startDateTime);
        EventDateTime startEventDateTime = new EventDateTime().setDateTime(start).setTimeZone("Asia/Manila");
        event.setStart(startEventDateTime);

        DateTime end = new DateTime(endDateTime);
        EventDateTime endEventDateTime = new EventDateTime().setDateTime(end).setTimeZone("Asia/Manila");
        event.setEnd(endEventDateTime);

        // Add notifications (reminders)
        EventReminder[] reminders = new EventReminder[]{
                new EventReminder().setMethod("popup").setMinutes(10),  // Pop-up reminder 10 minutes before
                new EventReminder().setMethod("email").setMinutes(30)   // Email reminder 30 minutes before
        };

        Event.Reminders eventReminders = new Event.Reminders()
                .setUseDefault(false)  // Disable default reminders
                .setOverrides(Arrays.asList(reminders));
        event.setReminders(eventReminders);

        Context context = getContext();
        // Insert event into the user's primary calendar
        new Thread(() -> {
            try {
                Event insertedEvent = service.events().insert("primary", event).execute();
                String googleEventId = insertedEvent.getId();
                Log.d("GoogleCalendarEvent", "Inserted Event ID: " + googleEventId);
                newEvents.setGoogleEventId(googleEventId);
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                dbHelper.updateEventWithGoogleEventId(newEvents);
                Log.d("GoogleCalendarEvent", "Event saved to database");


                // Safely access the activity for UI updates
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Event added to Google Calendar!", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to save event to Google Calendar", Toast.LENGTH_SHORT).show()
                    );
                }
                e.printStackTrace();
            }
        }).start();
    }


    public void onDestroy() {
        super.onDestroy();
        if (placesClient != null) {
            placesClient = null;     // Avoid memory leaks
        }
    }


    // Helper method to combine a date with a specific time
    private Date combineDateAndTime(Calendar date, Date time) {
        Calendar dateTime = (Calendar) date.clone();
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        // Set the hour, minute, second, and millisecond from the time to the dateTime
        dateTime.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        dateTime.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        dateTime.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        dateTime.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));

        return dateTime.getTime();
    }



    private Date parseTime(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
