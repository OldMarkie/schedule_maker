package com.mobdeve.s21.mco.schedule_maker;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class OneTimeEventEditFragment extends Fragment {

    private EditText eventNameInput, eventDescriptionInput, eventLocationInput;
    private EditText eventDateInput, eventTimeInput, eventEndTimeInput;
    private Button saveButton, colorPickerInput, cancelButton;
    private int selectedColor = Color.WHITE;
    private ColorUtils colorUtils;
    private DatabaseHelper dbHelper;
    private Events currentEvents;  // Holds the event to be edited
    private static final int MAP_REQUEST_CODE = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("OneTimeEventEditFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_one_time_event_edit, container, false);

        // Initialize UI components
        initializeUI(view);

        // Load color utility
        try {
            InputStream inputStream = requireContext().getAssets().open("colornames.json");
            colorUtils = new ColorUtils(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }



        // Set button click listeners
        setListeners(view);

        Bundle args = getArguments();
        if (args != null) {
            String eventId = args.getString("eventId");
            Log.d("OneTimeEventEditFragment", "Received Events ID: " + eventId);
            // Load event data if editing
            if (getArguments() != null) {
                loadEventData(eventId);
            }
        }

        eventLocationInput.setOnClickListener(v -> openMapForLocation());

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

    private void initializeUI(View view) {
        eventNameInput = view.findViewById(R.id.eventNameInput);
        eventDescriptionInput = view.findViewById(R.id.eventDescriptionInput);
        eventLocationInput = view.findViewById(R.id.eventLocationInput);
        eventDateInput = view.findViewById(R.id.eventDateInput);
        eventTimeInput = view.findViewById(R.id.eventTimeInput);
        eventEndTimeInput = view.findViewById(R.id.eventEndTimeInput);
        saveButton = view.findViewById(R.id.updateButton);
        colorPickerInput = view.findViewById(R.id.colorPickerInput);
        cancelButton = view.findViewById(R.id.editCancelButton);

        colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(0xff6200EE));
        colorPickerInput.setHint("Electric Violet By Default");
        colorPickerInput.setHintTextColor(Color.WHITE);
    }

    private void setListeners(View view ) {
        colorPickerInput.setOnClickListener(v -> openColorPicker());
        eventDateInput.setOnClickListener(v -> showDatePicker());
        eventTimeInput.setOnClickListener(v -> showTimePicker(eventTimeInput));
        eventEndTimeInput.setOnClickListener(v -> showTimePicker(eventEndTimeInput));
        saveButton.setOnClickListener(v -> updateEvent());
        cancelButton.setOnClickListener(v -> backToEventList());
    }

    private void loadEventData(String eventId) {
        Log.d("OneTimeEventEditFragment", "Loading event data for Events Name: " + eventId);

        dbHelper = new DatabaseHelper(getContext());
        currentEvents = dbHelper.getEventById(eventId);

        if (currentEvents != null) {
            Log.d("OneTimeEventEditFragment", "Events found: " + currentEvents.toString()); // Log the event details
            eventNameInput.setText(currentEvents.getName());
            eventDescriptionInput.setText(currentEvents.getDescription());
            eventLocationInput.setText(currentEvents.getLocation());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            eventDateInput.setText(dateFormat.format(currentEvents.getStartTime()));
            eventTimeInput.setText(timeFormat.format(currentEvents.getStartTime()));
            eventEndTimeInput.setText(timeFormat.format(currentEvents.getEndTime()));

            selectedColor = currentEvents.getColor();
            colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            colorPickerInput.setHint(colorUtils.getNearestColorName(selectedColor));
            setTextColorBasedOnContrast(selectedColor);

            Log.d("OneTimeEventEditFragment", "Events details populated in UI.");
        } else {
            Log.e("OneTimeEventEditFragment", "No event found for ID: " + eventId);
        }
    }


    private void openColorPicker() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(selectedColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(color -> {
                    String nearestColorName = colorUtils.getNearestColorName(color);
                    Toast.makeText(getContext(), "Color selected: #" + Integer.toHexString(color) + " (" + nearestColorName + ")", Toast.LENGTH_SHORT).show();
                })
                .setPositiveButton("OK", (dialog, color, allColors) -> {
                    selectedColor = color;
                    colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                    colorPickerInput.setHint(colorUtils.getNearestColorName(selectedColor));
                    setTextColorBasedOnContrast(selectedColor);
                })
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .build()
                .show();
    }

    private void showDatePicker() {
        long todayInMillis = MaterialDatePicker.todayInUtcMilliseconds();
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Events Date")
                .setSelection(todayInMillis)
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setStart(todayInMillis)
                        .setEnd(todayInMillis + 1000L * 60 * 60 * 24 * 365)
                        .build())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            eventDateInput.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
        });
    }

    private void showTimePicker(EditText timeInput) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTitleText("Select Time")
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                .build();

        timePicker.show(getParentFragmentManager(), "TIME_PICKER");
        timePicker.addOnPositiveButtonClickListener(v -> {
            timeInput.setText(String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute()));
        });
    }

    private void updateEvent() {
        String eventName = eventNameInput.getText().toString().trim();
        String eventDescription = eventDescriptionInput.getText().toString().trim();
        String eventLocation = eventLocationInput.getText().toString().trim();
        String eventDate = eventDateInput.getText().toString().trim();
        String eventTime = eventTimeInput.getText().toString().trim();
        String eventEndTime = eventEndTimeInput.getText().toString().trim();
        ColorStateList textColor = colorPickerInput.getBackgroundTintList();
        int selectedColor = textColor.getDefaultColor();

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventLocation.isEmpty() ||
                eventDate.isEmpty() || eventTime.isEmpty() || eventEndTime.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress dialog
        AlertDialog progressDialog = showCustomProgressDialog(getContext());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date startDateTime = dateFormat.parse(eventDate + " " + eventTime);
            Date endDateTime = dateFormat.parse(eventDate + " " + eventEndTime);

            if (endDateTime.equals(startDateTime) || endDateTime.before(startDateTime)) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "End time must be later than start time.", Toast.LENGTH_SHORT).show();
                return;
            }

            String sanitizedGoogleid = currentEvents.getId().replace("-", "").toLowerCase();

            currentEvents.setName(eventName);
            currentEvents.setDescription(eventDescription);
            currentEvents.setLocation(eventLocation);
            currentEvents.setStartTime(startDateTime);
            currentEvents.setEndTime(endDateTime);
            currentEvents.setColor(selectedColor);
            currentEvents.setGoogleEventId(sanitizedGoogleid);

            updateEventInGoogleCalendar(eventName, eventDescription, eventLocation, startDateTime, endDateTime, currentEvents, progressDialog);
        } catch (ParseException e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(getActivity(), "Invalid date or time format", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateEventInGoogleCalendar(String title, String description, String location, Date startDateTime, Date endDateTime, Events newEvents, AlertDialog progressDialog) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account == null) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "You need to sign in with Google first!", Toast.LENGTH_SHORT).show();
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
                .setId(newEvents.getGoogleEventId())
                .setSummary(title)
                .setDescription(description)
                .setLocation(location);

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
        event.setReminders(new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminders)));

        new Thread(() -> {
            try {
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                dbHelper.updateEvent(newEvents);

                Event updatedEvent = service.events().update("primary", event.getId(), event).execute();
                newEvents.setGoogleEventId(updatedEvent.getId());
                dbHelper.updateEventWithGoogleEventId(newEvents);

                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        backToEventList();
                        Toast.makeText(getActivity(), "Event updated in Google Calendar!", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed to update event in Google Calendar", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }






    private void setTextColorBasedOnContrast(int selectedColor) {
        double luminance = (0.299 * Color.red(selectedColor) + 0.587 * Color.green(selectedColor) + 0.114 * Color.blue(selectedColor)) / 255;
        colorPickerInput.setHintTextColor(luminance < 0.5 ? Color.WHITE : Color.BLACK);
    }

    private void backToEventList() {
        // Show the main event list view
        View view = getActivity().findViewById(R.id.mainAEL);
        view.setVisibility(View.VISIBLE);

        // Hide the edit event view
        View editView = getActivity().findViewById(R.id.editOneTImeEvent);
        editView.setVisibility(View.GONE);

        // Refresh the event list
        EventListActivity activity = (EventListActivity) getActivity();
        if (activity != null) {
            activity.refreshEventsForCurrentDate(); // Call the new public method
        }
    }

    private AlertDialog showCustomProgressDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.progress_dialog, null);

        // Change the text of descriptionTV
        TextView descriptionTextView = view.findViewById(R.id.descriptionTV);
        descriptionTextView.setText("Updating Event...");

        builder.setView(view);
        builder.setCancelable(false); // Prevent dismissing by tapping outside

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }




}
