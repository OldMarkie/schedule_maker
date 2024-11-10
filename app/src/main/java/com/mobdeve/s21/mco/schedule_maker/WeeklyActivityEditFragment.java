package com.mobdeve.s21.mco.schedule_maker;

import static android.app.Activity.RESULT_OK;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private  String oldNameHolder;
    private static final int MAP_REQUEST_CODE = 2;

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
        dbHelper = new DatabaseHelper(getContext());

        Bundle args = getArguments();

        String eventName = args.getString("eventName");
        Log.d("OneTimeEventEditFragment", "Received Event Name: " + eventName);


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

        setupAlreadyChecked(eventName);
        fillUpDetails(eventName);
        setupTimeInputExisting(eventName);


        activityLocationInput.setOnClickListener(v -> openMapForLocation());

        updateButton.setOnClickListener(v -> updateActivity());
        cancelButton.setOnClickListener(v -> backToEventList());

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


    private void setupTimeInputExisting(String eventName) {
        // Fetch the list of start and end times (as Date objects)
        List<Date> timeDetails = dbHelper.getWeeklyTime(eventName);

        // Define SimpleDateFormat to format time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        // Iterate over the time details to set them into respective EditTexts for each day
        for (int i = 0; i < timeDetails.size(); i += 2) {
            Date startTime = timeDetails.get(i);   // Start time as Date object
            Date endTime = timeDetails.get(i + 1); // End time as Date object

            // Get the day of the week from the start time
            String dayOfWeek = getDayOfWeekFromDate(startTime);

            // Format the start and end times into a readable format
            String formattedStartTime = timeFormat.format(startTime);
            String formattedEndTime = timeFormat.format(endTime);

            // Set the formatted time into the respective EditTexts for the corresponding day
            switch (dayOfWeek) {
                case "Sunday":
                    sundayStartTimeInput.setText(formattedStartTime);
                    sundayEndTimeInput.setText(formattedEndTime);
                    break;
                case "Monday":
                    mondayStartTimeInput.setText(formattedStartTime);
                    mondayEndTimeInput.setText(formattedEndTime);
                    break;
                case "Tuesday":
                    tuesdayStartTimeInput.setText(formattedStartTime);
                    tuesdayEndTimeInput.setText(formattedEndTime);
                    break;
                case "Wednesday":
                    wednesdayStartTimeInput.setText(formattedStartTime);
                    wednesdayEndTimeInput.setText(formattedEndTime);
                    break;
                case "Thursday":
                    thursdayStartTimeInput.setText(formattedStartTime);
                    thursdayEndTimeInput.setText(formattedEndTime);
                    break;
                case "Friday":
                    fridayStartTimeInput.setText(formattedStartTime);
                    fridayEndTimeInput.setText(formattedEndTime);
                    break;
                case "Saturday":
                    saturdayStartTimeInput.setText(formattedStartTime);
                    saturdayEndTimeInput.setText(formattedEndTime);
                    break;
                default:
                    // If no matching day is found
                    break;
            }
        }
    }

    private String getDayName(int dayIndex) {
        // Map day index to day name
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return daysOfWeek[dayIndex - 1];
    }

    private String getDayOfWeekFromDate(Date date) {
        // Use Calendar to get the day of the week from Date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);  // Set the Date object

        // Get the day of the week index (1 = Sunday, 2 = Monday, ..., 7 = Saturday)
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        return getDayName(dayIndex);
    }



    private void fillUpDetails(String eventName) {
        List<String> weeklyDetails = dbHelper.getWeeklyDetails(eventName);

        String activityName = weeklyDetails.get(0); // Name of the activity
        String description = weeklyDetails.get(1);  // Description of the activity
        String location = weeklyDetails.get(2);     // Location of the activity
        int colorInt = Integer.parseInt(weeklyDetails.get(3)); // Color (Hex string, e.g. #FF5733)

        oldNameHolder = activityName;

        activityNameInput.setText(activityName);
        activityDescriptionInput.setText(description);
        activityLocationInput.setText(location);
        colorPickerInput.setBackgroundTintList(ColorStateList.valueOf(colorInt));
        colorPickerInput.setText(colorUtils.getNearestColorName(colorInt));
        setTextColorBasedOnContrast(colorInt);
    }

    private void setupAlreadyChecked(String eventName) {
        List<Integer> daysOfWeek = dbHelper.getDaysOfWeekForEvent(eventName);
        for (Integer day : daysOfWeek) {
            switch (day) {
                case 1:
                    checkMonday.setChecked(true);
                    break;
                case 2:
                    checkTuesday.setChecked(true);
                    break;
                case 3:
                    checkWednesday.setChecked(true);
                    break;
                case 4:
                    checkThursday.setChecked(true);
                    break;
                case 5:
                    checkFriday.setChecked(true);
                    break;
                case 6:
                    checkSaturday.setChecked(true);
                    break;
                case 7:
                    checkSunday.setChecked(true);
                    break;
                default:
                    // Handle unexpected day values if necessary
                    break;
            }
        }
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
            dbHelper.deleteEvent(oldNameHolder);

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
            backToEventList();

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
        Log.d("Event", "dayWeek value: " + dayWeek);

        // Ensure dayWeek is not null or invalid (set a default value if necessary)
        if (dayWeek < 1 || dayWeek > 7) {
            Log.w("Event", "Invalid dayWeek value. Setting to default (0 - Monday).");
            dayWeek = 1;  // Default to Sunday (or handle this appropriately)
        }

        if (startTime != null && endTime != null) {

            // Iterate through each week for the next year (52 weeks)
            for (int i = 0; i < 52; i++) {
                // Create the start and end Date for the event instance
                Calendar eventDate = (Calendar) baseDate.clone();
                eventDate.add(Calendar.WEEK_OF_YEAR, i); // Move to the correct week

                Date eventStartDate = combineDateAndTime(eventDate, startTime);
                Date eventEndDate = combineDateAndTime(eventDate, endTime);

                Log.d("Event", "Event " + (i + 1) + " - Start: " + eventStartDate + ", End: " + eventEndDate);

                // Check for conflicts
                if (dbHelper.isTimeConflict(eventStartDate, eventEndDate)) {
                    Toast.makeText(getActivity(), "Event time conflict for week " + (i + 1) + "!", Toast.LENGTH_SHORT).show();
                    continue; // Optionally continue to check the next week
                }

                String counter = UUID.randomUUID().toString();
                // Create the Event object
                Event event = new Event(counter, name, description, location, eventStartDate, eventEndDate, true, eventColor, dayWeek);

                // Save the Event object to the database
                boolean success = dbHelper.addEvent(event);
                if (!success) {
                    Toast.makeText(getContext(), "Failed to save event for week " + (i + 1), Toast.LENGTH_SHORT).show();
                    Log.e("Event", "Failed to save event for week " + (i + 1));
                } else {
                    Log.d("Event", "Event saved for week " + (i + 1));
                }
            }
            Toast.makeText(getContext(), "Weekly Event saved for a year!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Invalid start or end time!", Toast.LENGTH_SHORT).show();
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
