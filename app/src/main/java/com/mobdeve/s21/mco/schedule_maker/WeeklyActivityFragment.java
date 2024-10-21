package com.mobdeve.s21.mco.schedule_maker;

import android.app.TimePickerDialog;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeeklyActivityFragment extends Fragment {

    private EditText activityNameInput;
    private EditText activityDescriptionInput;
    private EditText activityLocationInput;
    private CheckBox checkMonday, checkTuesday, checkWednesday, checkThursday, checkFriday, checkSaturday, checkSunday;
    private EditText mondayTimeInput, tuesdayTimeInput, wednesdayTimeInput, thursdayTimeInput, fridayTimeInput, saturdayTimeInput, sundayTimeInput;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_activity, container, false);

        activityNameInput = view.findViewById(R.id.activityNameInput);
        activityDescriptionInput = view.findViewById(R.id.activityDescriptionInput);
        activityLocationInput = view.findViewById(R.id.activityLocationInput);
        checkMonday = view.findViewById(R.id.checkMonday);
        mondayTimeInput = view.findViewById(R.id.mondayTimeInput);
        checkTuesday = view.findViewById(R.id.checkTuesday);
        tuesdayTimeInput = view.findViewById(R.id.tuesdayTimeInput);
        checkWednesday = view.findViewById(R.id.checkWednesday);
        wednesdayTimeInput = view.findViewById(R.id.wednesdayTimeInput);
        checkThursday = view.findViewById(R.id.checkThursday);
        thursdayTimeInput = view.findViewById(R.id.thursdayTimeInput);
        checkFriday = view.findViewById(R.id.checkFriday);
        fridayTimeInput = view.findViewById(R.id.fridayTimeInput);
        checkSaturday = view.findViewById(R.id.checkSaturday);
        saturdayTimeInput = view.findViewById(R.id.saturdayTimeInput);
        checkSunday = view.findViewById(R.id.checkSunday);
        sundayTimeInput = view.findViewById(R.id.sundayTimeInput);
        saveButton = view.findViewById(R.id.saveButton);

        // Initially set all time inputs to be gone
        setTimeInputVisibility();

        // Set up listeners to show/hide time input fields based on checkbox state
        setupCheckBoxListeners();

        saveButton.setOnClickListener(v -> {
            saveActivity();
        });

        return view;
    }

    private void setupCheckBoxListeners() {
        checkMonday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mondayTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                mondayTimeInput.setOnClickListener(v -> showTimePicker(mondayTimeInput));
            }
        });
        checkTuesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tuesdayTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                tuesdayTimeInput.setOnClickListener(v -> showTimePicker(tuesdayTimeInput));
            }
        });
        checkWednesday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            wednesdayTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                wednesdayTimeInput.setOnClickListener(v -> showTimePicker(wednesdayTimeInput));
            }
        });
        checkThursday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            thursdayTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                thursdayTimeInput.setOnClickListener(v -> showTimePicker(thursdayTimeInput));
            }
        });
        checkFriday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fridayTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                fridayTimeInput.setOnClickListener(v -> showTimePicker(fridayTimeInput));
            }
        });
        checkSaturday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saturdayTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                saturdayTimeInput.setOnClickListener(v -> showTimePicker(saturdayTimeInput));
            }
        });
        checkSunday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sundayTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (isChecked) {
                sundayTimeInput.setOnClickListener(v -> showTimePicker(sundayTimeInput));
            }
        });
    }

    private void showTimePicker(final EditText timeInput) {
        // Get current time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create and show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (view, selectedHour, selectedMinute) -> {
                    // Format and set the time in the EditText
                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeInput.setText(formattedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveActivity() {
        String activityName = activityNameInput.getText().toString().trim();
        String activityDescription = activityDescriptionInput.getText().toString().trim();
        String activityLocation = activityLocationInput.getText().toString().trim();

        boolean isWeekly = checkMonday.isChecked() || checkTuesday.isChecked() || checkWednesday.isChecked() ||
                checkThursday.isChecked() || checkFriday.isChecked() || checkSaturday.isChecked() || checkSunday.isChecked();

        if (isWeekly) {
            // Create a Calendar instance to set the base date
            Calendar baseDate = Calendar.getInstance();
            int currentWeekday = baseDate.get(Calendar.DAY_OF_WEEK); // Get today's weekday

            // Iterate through each day of the week and save events accordingly
            if (checkMonday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                Event newEvent = createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, mondayTimeInput);
                if (newEvent != null) {
                    DummyData.addEvent(newEvent);
                }
            }
            if (checkTuesday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                Event newEvent = createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, tuesdayTimeInput);
                if (newEvent != null) {
                    DummyData.addEvent(newEvent);
                }
            }
            if (checkWednesday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                Event newEvent = createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, wednesdayTimeInput);
                if (newEvent != null) {
                    DummyData.addEvent(newEvent);
                }
            }
            if (checkThursday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                Event newEvent = createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, thursdayTimeInput);
                if (newEvent != null) {
                    DummyData.addEvent(newEvent);
                }
            }
            if (checkFriday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                Event newEvent = createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, fridayTimeInput);
                if (newEvent != null) {
                    DummyData.addEvent(newEvent);
                }
            }
            if (checkSaturday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                Event newEvent = createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, saturdayTimeInput);
                if (newEvent != null) {
                    DummyData.addEvent(newEvent);
                }
            }
            if (checkSunday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                Event newEvent = createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, sundayTimeInput);
                if (newEvent != null) {
                    DummyData.addEvent(newEvent);
                }
            }

            // Show a message to the user
            Toast.makeText(getActivity(), "Events saved successfully!", Toast.LENGTH_SHORT).show();
            // Go back to the previous fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            // Handle one-time event case if needed
            Toast.makeText(getActivity(), "Please select at least one day for the weekly event.", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to create a weekly event based on the selected time
    private Event createWeeklyEvent(String name, String description, String location, Calendar baseDate, EditText timeInput) {
        Date eventTime = parseTime(timeInput.getText().toString());
        if (eventTime != null) {
            // Combine the base date with the event time (hour and minute)
            Calendar eventCalendar = Calendar.getInstance();
            eventCalendar.set(Calendar.YEAR, baseDate.get(Calendar.YEAR));
            eventCalendar.set(Calendar.MONTH, baseDate.get(Calendar.MONTH));
            eventCalendar.set(Calendar.DAY_OF_MONTH, baseDate.get(Calendar.DAY_OF_MONTH));
            eventCalendar.set(Calendar.HOUR_OF_DAY, eventTime.getHours());
            eventCalendar.set(Calendar.MINUTE, eventTime.getMinutes());

            return new Event(name, description, location, eventCalendar.getTime(), true); // true for weekly events
        } else {
            Toast.makeText(getActivity(), "Invalid time for " + baseDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, getResources().getConfiguration().locale), Toast.LENGTH_SHORT).show();
            return null; // return null if the time was invalid
        }
    }

    private Date parseTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setTimeInputVisibility() {
        mondayTimeInput.setVisibility(View.GONE);
        tuesdayTimeInput.setVisibility(View.GONE);
        wednesdayTimeInput.setVisibility(View.GONE);
        thursdayTimeInput.setVisibility(View.GONE);
        fridayTimeInput.setVisibility(View.GONE);
        saturdayTimeInput.setVisibility(View.GONE);
        sundayTimeInput.setVisibility(View.GONE);
    }
}
