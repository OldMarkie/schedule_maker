package com.mobdeve.s21.mco.schedule_maker;

import android.app.DatePickerDialog;
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

        dateInput.setOnClickListener(v -> showDatePickerDialog());
        timeInput.setOnClickListener(v -> showTimePickerDialog(timeInput));
        endTimeInput.setOnClickListener(v -> showTimePickerDialog(endTimeInput));

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

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    // Update the calendar and set the selected date in the EditText
                    calendar.set(year, month, dayOfMonth);
                    dateInput.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText timeEditText) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    // Update the calendar and set the selected time in the EditText
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    timeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }
}
