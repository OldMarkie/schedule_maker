package com.mobdeve.s21.mco.schedule_maker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventDetailsDialogFragment extends DialogFragment{

    private TextView eventName, eventTime, eventRecurrence, eventDescription, eventLocation;
    private Button editButton, deleteButton;

    private Event event;
    private OnEventActionListener listener;

    public interface OnEventActionListener {
        void onEventDelete(Event event);
        void onEventEdit(Event event);
    }

    public static EventDetailsDialogFragment newInstance(Event event, OnEventActionListener listener) {
        EventDetailsDialogFragment fragment = new EventDetailsDialogFragment();
        fragment.event = event;
        fragment.listener = listener;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate custom layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_event_details, null);

        eventName = view.findViewById(R.id.eventName);
        eventTime = view.findViewById(R.id.eventTime);
        eventRecurrence = view.findViewById(R.id.eventRecurrence);
        eventDescription = view.findViewById(R.id.eventDescription); // Initialize eventDescription
        eventLocation = view.findViewById(R.id.eventLocation); // Initialize eventLocation
        editButton = view.findViewById(R.id.editButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        // Set event details
        eventName.setText(event.getName());

        // Get user preference for 24-hour or 12-hour format
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("ThemePref", getContext().MODE_PRIVATE);
        boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);

        // Set the correct time format based on preference
        SimpleDateFormat timeFormat;
        if (is24HourFormat) {
            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());  // 24-hour format
        } else {
            timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());  // 12-hour AM/PM format
        }

        eventTime.setText(timeFormat.format(event.getStartTime()) + " - " + timeFormat.format(event.getEndTime()));

        // Show if it's a weekly or one-time event
        if (event.isWeekly()) {
            eventRecurrence.setText("Weekly Event");
        } else {
            eventRecurrence.setText("One-time Event");
        }

        // Set event description and location
        eventDescription.setText(event.getDescription()); // Assuming getDescription() method exists in Event class
        eventLocation.setText(event.getLocation()); // Assuming getLocation() method exists in Event class

        // Handle the edit button click
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEventEdit(event); // Notify listener for edit action
                dismiss();
            }
        });

        // Handle the delete button click
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEventDelete(event); // Notify listener for delete action
                dismiss();
            }
        });

        // Set the view and build the dialog
        builder.setView(view)
                .setTitle("Event Details")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

}
