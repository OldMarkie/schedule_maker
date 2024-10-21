package com.mobdeve.s21.mco.schedule_maker;

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

public class OneTimeEventFragment extends Fragment {

    private EditText eventNameInput;
    private EditText eventDescriptionInput;
    private EditText eventLocationInput;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one_time_event, container, false);

        eventNameInput = view.findViewById(R.id.eventNameInput);
        eventDescriptionInput = view.findViewById(R.id.eventDescriptionInput);
        eventLocationInput = view.findViewById(R.id.eventLocationInput);
        saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String eventName = eventNameInput.getText().toString();
            String eventDescription = eventDescriptionInput.getText().toString();
            String eventLocation = eventLocationInput.getText().toString();

            // Handle event saving logic here (e.g., save to database)

            Toast.makeText(getActivity(), "One-Time Event Saved!", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed(); // Go back to the previous screen
        });

        return view;
    }
}
