package com.mobdeve.s21.mco.schedule_maker;

import android.app.Dialog;
import android.content.DialogInterface;
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

public class EventDetailsDialogFragment extends DialogFragment {

    private TextView eventName, eventDate;
    private Button editButton, deleteButton;

    private Event event;
    private OnEventActionListener listener;

    // Use this interface to handle edit and delete actions in the host activity
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
        eventDate = view.findViewById(R.id.eventDate);
        editButton = view.findViewById(R.id.editButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        // Set event details
        eventName.setText(event.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());
        eventDate.setText(dateFormat.format(event.getDateTime()));

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
