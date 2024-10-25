package com.mobdeve.s21.mco.schedule_maker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.text.SimpleDateFormat;
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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class OneTimeEventFragment extends Fragment {

    private EditText eventNameInput;
    private EditText eventDescriptionInput;
    private EditText eventLocationInput;
    private EditText eventDateInput;
    private EditText eventTimeInput;
    private EditText eventEndTimeInput; // Added for end time
    private Button saveButton;

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
        eventEndTimeInput = view.findViewById(R.id.eventEndTimeInput); // Initialize end time input
        saveButton = view.findViewById(R.id.saveButton);

        // Set up date picker dialog
        eventDateInput.setOnClickListener(v -> showDatePicker());

        // Set up time picker dialogs
        eventTimeInput.setOnClickListener(v -> showTimePicker(eventTimeInput));
        eventEndTimeInput.setOnClickListener(v -> showTimePicker(eventEndTimeInput)); // End time picker

        saveButton.setOnClickListener(v -> saveEvent());

        return view;
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    eventDateInput.setText(formattedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(EditText timeInput) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (view, selectedHour, selectedMinute) -> {
                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeInput.setText(formattedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveEvent() {
        String eventName = eventNameInput.getText().toString().trim();
        String eventDescription = eventDescriptionInput.getText().toString().trim();
        String eventLocation = eventLocationInput.getText().toString().trim();
        String eventDate = eventDateInput.getText().toString().trim();
        String eventTime = eventTimeInput.getText().toString().trim();
        String eventEndTime = eventEndTimeInput.getText().toString().trim(); // Get end time

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventLocation.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty() || eventEndTime.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Combine date and time into a single Date object for start and end
        String startDateTimeString = eventDate + " " + eventTime;
        String endDateTimeString = eventDate + " " + eventEndTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date eventStartDateTime;
        Date eventEndDateTime;

        try {
            eventStartDateTime = dateFormat.parse(startDateTimeString);
            eventEndDateTime = dateFormat.parse(endDateTimeString);
        } catch (ParseException e) {
            Toast.makeText(getActivity(), "Invalid date or time format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Event object with startTime and endTime
        Event newEvent = new Event(eventName, eventDescription, eventLocation, eventStartDateTime, eventEndDateTime, false); // isWeekly is false for one-time events

        // Save the event using DummyData and check for conflict
        if (!DummyData.addEvent(newEvent)) {
            Toast.makeText(getActivity(), "Event time conflict!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Notify user
        Toast.makeText(getActivity(), "One-Time Event Saved!", Toast.LENGTH_SHORT).show();

        // Clear the back stack to return to EventActivity
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        ((EventActivity) getActivity()).showHintTextView();
        ((EventActivity) getActivity()).showAddOneTimeEventButton();
    }

}
