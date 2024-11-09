package com.mobdeve.s21.mco.schedule_maker;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class WeeklyActivityEditFragment extends Fragment {

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
    private Button updateButton, colorPickerInput, cancelButton;
    private int selectedColor = 0xFFFFFFFF; // Default color is white
    private ColorUtils colorUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_activity_edit, container, false);

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
        updateButton = view.findViewById(R.id.updateButton);
        colorPickerInput = view.findViewById(R.id.colorPickerInput);
        cancelButton = view.findViewById(R.id.editCancelBtn);
        updateButton.setOnClickListener(v -> updateActivity());

        // Open the color picker dialog when the color input field is clicked
        colorPickerInput.setOnClickListener(v -> openColorPicker());


        colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(0xff6200EE));
        colorPickerInput.setHint("Electric Violet By Default");
        colorPickerInput.setHintTextColor(Color.WHITE);


        Bundle args = getArguments();

        String eventId = args.getString("eventId");
        Log.d("OneTimeEventEditFragment", "Received Event ID: " + eventId);


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

        updateButton.setOnClickListener(v -> updateActivity());
        cancelButton.setOnClickListener(v -> backToEventList());

        return view;
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

    private void updateActivity() {
        String activityName = activityNameInput.getText().toString().trim();
        String activityDescription = activityDescriptionInput.getText().toString().trim();
        String activityLocation = activityLocationInput.getText().toString().trim();

        boolean isWeekly = checkMonday.isChecked() || checkTuesday.isChecked() || checkWednesday.isChecked() ||
                checkThursday.isChecked() || checkFriday.isChecked() || checkSaturday.isChecked() || checkSunday.isChecked();

        if (isWeekly) {
            Calendar baseDate = Calendar.getInstance();

            if (checkMonday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
               updateWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        mondayStartTimeInput, mondayEndTimeInput ,1);
            }
            if (checkTuesday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
               updateWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        tuesdayStartTimeInput, tuesdayEndTimeInput, 2);
            }
            if (checkWednesday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
               updateWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        wednesdayStartTimeInput, wednesdayEndTimeInput, 3);
            }
            if (checkThursday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
               updateWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        thursdayStartTimeInput, thursdayEndTimeInput, 4);
            }
            if (checkFriday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
               updateWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        fridayStartTimeInput, fridayEndTimeInput, 5);
            }
            if (checkSaturday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
               updateWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        saturdayStartTimeInput, saturdayEndTimeInput, 6);
            }
            if (checkSunday.isChecked()) {
                baseDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
               updateWeeklyEventToDatabase(activityName, activityDescription, activityLocation, baseDate,
                        sundayStartTimeInput, sundayEndTimeInput, 7);
            }

            Toast.makeText(getActivity(), "Events saved successfully!", Toast.LENGTH_SHORT).show();

            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // After saving, show the hint text view again
            ((EventActivity) getActivity()).showHintTextView();
            ((EventActivity) getActivity()).showAddWeeklyActivityButton();

        } else {
            Toast.makeText(getActivity(), "Please select at least one day for the weekly event.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWeeklyEventToDatabase(String activityName, String activityDescription, String activityLocation, Calendar baseDate, EditText startTimeInput, EditText endTimeInput, int dayWeek) {
        String startTimeText = startTimeInput.getText().toString().trim();
        String endTimeText = endTimeInput.getText().toString().trim();

        if (startTimeText.isEmpty() || endTimeText.isEmpty()) {
            Toast.makeText(getActivity(), "Start and end times must be filled.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse start and end times
        Date startTime = parseTime(startTimeText);
        Date endTime = parseTime(endTimeText);

        if (startTime == null || endTime == null) {
            Toast.makeText(getActivity(), "Invalid time format. Use HH:mm.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Combine the date with the start and end times
        Date combinedStartTime = combineDateAndTime(baseDate, startTime);
        Date combinedEndTime = combineDateAndTime(baseDate, endTime);

        // Save to database (implement DatabaseHelper's update or insert method as needed)
        boolean success = dbHelper.updateWeeklyActivityForDay(
                activityName, dayWeek,
                combinedStartTime.getTime(), combinedEndTime.getTime(),activityLocation, activityDescription, selectedColor
        );

        if (success) {
            Toast.makeText(getActivity(), "Activity updated successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to update activity.", Toast.LENGTH_SHORT).show();
        }
    }



    // Toggle visibility of EditTexts based on CheckBox state
    private void toggleEditTextVisibility(EditText startTimeInput, EditText endTimeInput, boolean isChecked) {
        if (isChecked) {
            startTimeInput.setVisibility(View.VISIBLE);
            endTimeInput.setVisibility(View.VISIBLE);
        } else {
            startTimeInput.setVisibility(View.GONE);
            endTimeInput.setVisibility(View.GONE);
        }
    }

    // Set initial visibility for EditTexts
    private void setEditTextVisibility() {
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
