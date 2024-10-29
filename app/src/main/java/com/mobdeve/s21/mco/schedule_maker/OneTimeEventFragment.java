package com.mobdeve.s21.mco.schedule_maker;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
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
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;


import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
                .setTitleText("Select Event Date")
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
        String eventName = eventNameInput.getText().toString().trim();
        String eventDescription = eventDescriptionInput.getText().toString().trim();
        String eventLocation = eventLocationInput.getText().toString().trim();
        String eventDate = eventDateInput.getText().toString().trim();
        String eventTime = eventTimeInput.getText().toString().trim();
        String eventEndTime = eventEndTimeInput.getText().toString().trim();
        String color = colorPickerInput.getText().toString().trim();

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventLocation.isEmpty() ||
                eventDate.isEmpty() || eventTime.isEmpty() || eventEndTime.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Combine date and time into Date objects
        String startDateTimeString = eventDate + " " + eventTime;
        String endDateTimeString = eventDate + " " + eventEndTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date eventStartDateTime;
        Date eventEndDateTime;

        try {
            eventStartDateTime = dateFormat.parse(startDateTimeString);
            eventEndDateTime = dateFormat.parse(endDateTimeString);

            // Check if end time is not equal to start time and end time is after start time
            if (eventEndDateTime.equals(eventStartDateTime) || eventEndDateTime.before(eventStartDateTime)) {
                Toast.makeText(getActivity(), "End time must be later than start time.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(getActivity(), "Invalid date or time format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the color from colorPickerInput or use a default if it's empty
        // Get the ColorStateList from the colorPickerInput
        ColorStateList colorStateList = colorPickerInput.getBackgroundTintList();
        int eventColor;

        // Check if the ColorStateList is null
        if (colorStateList == null) {
            colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(0xff6200EE));
            colorStateList = colorPickerInput.getBackgroundTintList();
            eventColor = colorStateList.getDefaultColor();
        } else {
            // Get the default color from the ColorStateList
            eventColor = colorStateList.getDefaultColor();

            // Optionally, if you want to show the color as a string format
            String colorString = String.format("#%06X", (0xFFFFFF & eventColor));

            // If you need to validate the format (not necessary in this context)
            try {
                // This block may not be needed if you trust the colorStateList to be valid
                eventColor = Color.parseColor(colorString);
            } catch (IllegalArgumentException e) {
                Toast.makeText(getActivity(), "Invalid color format", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create and save new Event
        Event newEvent = new Event(eventName, eventDescription, eventLocation, eventStartDateTime, eventEndDateTime, false, eventColor);
        if (!DummyData.addEvent(newEvent)) {
            Toast.makeText(getActivity(), "Event time conflict!", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(), "One-Time Event Saved!", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ((EventActivity) getActivity()).showHintTextView();
        ((EventActivity) getActivity()).showAddOneTimeEventButton();
    }
}
