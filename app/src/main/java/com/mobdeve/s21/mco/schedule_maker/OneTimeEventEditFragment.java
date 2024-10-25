package com.mobdeve.s21.mco.schedule_maker;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OneTimeEventEditFragment extends DialogFragment {
    private Button saveButton, cancelButton;
    private EditText dateInput, timeInput, endTimeInput;
    private OnFragmentInteractionListener listener;
    private Calendar calendar;

    public void setDismissListener(OnFragmentInteractionListener listener) {
        this.listener = listener;
    }

    public interface OnFragmentInteractionListener {
        void onDialogDismissed();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_time_event_edit, container, false);

        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.editCancelButton); // Make sure this ID matches your XML
        dateInput = view.findViewById(R.id.eventDateInput);
        timeInput = view.findViewById(R.id.eventTimeInput);
        endTimeInput = view.findViewById(R.id.eventEndTimeInput);

        calendar = Calendar.getInstance(); // Get the current date and time

        dateInput.setOnClickListener(v -> showDatePicker());
        timeInput.setOnClickListener(v -> showTimePicker(timeInput));
        endTimeInput.setOnClickListener(v -> showTimePicker(endTimeInput));

        saveButton.setOnClickListener(v -> {
            // Add any additional logic for saving the event here
            listener.onDialogDismissed();
            dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            listener.onDialogDismissed();
            dismiss();
        });

        return view;
    }

    private void showDatePicker() {
        // Get today's date in milliseconds
        long todayInMillis = MaterialDatePicker.todayInUtcMilliseconds();

        // Create a MaterialDatePicker with constraints
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Event Date")
                .setSelection(todayInMillis) // Start with today's date selected
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setEnd(Long.MAX_VALUE) // Optional: No end date limit, allows selection of any future date
                        .build())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
            dateInput.setText(formattedDate);
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
}
