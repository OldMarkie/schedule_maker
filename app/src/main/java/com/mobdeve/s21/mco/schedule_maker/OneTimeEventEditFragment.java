package com.mobdeve.s21.mco.schedule_maker;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OneTimeEventEditFragment extends Fragment {

    private EditText eventNameInput, eventDescriptionInput, eventLocationInput;
    private EditText eventDateInput, eventTimeInput, eventEndTimeInput;
    private Button saveButton, colorPickerInput, cancelButton;
    private int selectedColor = Color.WHITE;
    private ColorUtils colorUtils;
    private DatabaseHelper dbHelper;
    private Event currentEvent;  // Holds the event to be edited

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
            Log.d("OneTimeEventEditFragment", "Received Event ID: " + eventId);
            // Load event data if editing
            if (getArguments() != null) {
                loadEventData(eventId);
            }
        }



        return view;
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
        Log.d("OneTimeEventEditFragment", "Loading event data for Event Name: " + eventId);

        dbHelper = new DatabaseHelper(getContext());
        currentEvent = dbHelper.getEventById(eventId);

        if (currentEvent != null) {
            Log.d("OneTimeEventEditFragment", "Event found: " + currentEvent.toString()); // Log the event details
            eventNameInput.setText(currentEvent.getName());
            eventDescriptionInput.setText(currentEvent.getDescription());
            eventLocationInput.setText(currentEvent.getLocation());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            eventDateInput.setText(dateFormat.format(currentEvent.getStartTime()));
            eventTimeInput.setText(timeFormat.format(currentEvent.getStartTime()));
            eventEndTimeInput.setText(timeFormat.format(currentEvent.getEndTime()));

            selectedColor = currentEvent.getColor();
            colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
            colorPickerInput.setHint(colorUtils.getNearestColorName(selectedColor));
            setTextColorBasedOnContrast(selectedColor);

            Log.d("OneTimeEventEditFragment", "Event details populated in UI.");
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
                .setTitleText("Select Event Date")
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date startDateTime = dateFormat.parse(eventDate + " " + eventTime);
            Date endDateTime = dateFormat.parse(eventDate + " " + eventEndTime);

            if (endDateTime.equals(startDateTime) || endDateTime.before(startDateTime)) {
                Toast.makeText(getActivity(), "End time must be later than start time.", Toast.LENGTH_SHORT).show();
                return;
            }

            currentEvent.setName(eventName);
            currentEvent.setDescription(eventDescription);
            currentEvent.setLocation(eventLocation);
            currentEvent.setStartTime(startDateTime);
            currentEvent.setEndTime(endDateTime);
            currentEvent.setColor(selectedColor);

            dbHelper.updateEvent(currentEvent);

            Toast.makeText(getActivity(), "Event updated successfully!", Toast.LENGTH_SHORT).show();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
            backToEventList();

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Invalid date or time format", Toast.LENGTH_SHORT).show();
        }
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





}
