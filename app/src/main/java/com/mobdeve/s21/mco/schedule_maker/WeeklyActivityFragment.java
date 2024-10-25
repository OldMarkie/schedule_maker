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
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeeklyActivityFragment extends Fragment {

    private EditText activityNameInput;
    private EditText activityDescriptionInput;
    private EditText activityLocationInput;
    private CheckBox checkMonday, checkTuesday, checkWednesday, checkThursday, checkFriday, checkSaturday, checkSunday;
    private EditText mondayStartTimeInput, mondayEndTimeInput;
    private EditText tuesdayStartTimeInput, tuesdayEndTimeInput;
    private EditText wednesdayStartTimeInput, wednesdayEndTimeInput;
    private EditText thursdayStartTimeInput, thursdayEndTimeInput;
    private EditText fridayStartTimeInput, fridayEndTimeInput;
    private EditText saturdayStartTimeInput, saturdayEndTimeInput;
    private EditText sundayStartTimeInput, sundayEndTimeInput;
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

    private void toggleTimeInputs(EditText startInput, EditText endInput, boolean isChecked) {
        int visibility = isChecked ? View.VISIBLE : View.GONE;
        startInput.setVisibility(visibility);
        endInput.setVisibility(visibility);
        if (isChecked) {
            startInput.setOnClickListener(v -> showTimePicker(startInput));
            endInput.setOnClickListener(v -> showTimePicker(endInput));
        }
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
                createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, mondayStartTimeInput, mondayEndTimeInput);
            }
            if (checkTuesday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, tuesdayStartTimeInput, tuesdayEndTimeInput);
            }
            if (checkWednesday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, wednesdayStartTimeInput, wednesdayEndTimeInput);
            }
            if (checkThursday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, thursdayStartTimeInput, thursdayEndTimeInput);
            }
            if (checkFriday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, fridayStartTimeInput, fridayEndTimeInput);
            }
            if (checkSaturday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, saturdayStartTimeInput, saturdayEndTimeInput);
            }
            if (checkSunday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                createWeeklyEvent(activityName, activityDescription, activityLocation, baseDate, sundayStartTimeInput, sundayEndTimeInput);
            }

            // Show a message to the user
            Toast.makeText(getActivity(), "Events saved successfully!", Toast.LENGTH_SHORT).show();

            // Clear the back stack to return to EventActivity
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // After saving, show the hint text view again
            ((EventActivity) getActivity()).showHintTextView();
            ((EventActivity) getActivity()).showAddWeeklyActivityButton();

        } else {
            // Handle one-time event case if needed
            Toast.makeText(getActivity(), "Please select at least one day for the weekly event.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createWeeklyEvent(String name, String description, String location, Calendar baseDate, EditText startTimeInput, EditText endTimeInput) {
        Date startTime = parseTime(startTimeInput.getText().toString());
        Date endTime = parseTime(endTimeInput.getText().toString());

        if (startTime != null && endTime != null) {
            // Combine the base date with the event time (hour and minute)
            Calendar startEventCalendar = Calendar.getInstance();
            startEventCalendar.set(Calendar.YEAR, baseDate.get(Calendar.YEAR));
            startEventCalendar.set(Calendar.MONTH, baseDate.get(Calendar.MONTH));
            startEventCalendar.set(Calendar.DAY_OF_MONTH, baseDate.get(Calendar.DAY_OF_MONTH));
            startEventCalendar.set(Calendar.HOUR_OF_DAY, startTime.getHours());
            startEventCalendar.set(Calendar.MINUTE, startTime.getMinutes());

            Calendar endEventCalendar = Calendar.getInstance();
            endEventCalendar.set(Calendar.YEAR, baseDate.get(Calendar.YEAR));
            endEventCalendar.set(Calendar.MONTH, baseDate.get(Calendar.MONTH));
            endEventCalendar.set(Calendar.DAY_OF_MONTH, baseDate.get(Calendar.DAY_OF_MONTH));
            endEventCalendar.set(Calendar.HOUR_OF_DAY, endTime.getHours());
            endEventCalendar.set(Calendar.MINUTE, endTime.getMinutes());

            // Try to add the event and check for conflict
            if (!DummyData.addEvent(new Event(name, description, location, startEventCalendar.getTime(), endEventCalendar.getTime(), true))) {
                Toast.makeText(getActivity(), "Event time conflict for " + baseDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, getResources().getConfiguration().locale), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Invalid time for " + baseDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, getResources().getConfiguration().locale), Toast.LENGTH_SHORT).show();
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
}
