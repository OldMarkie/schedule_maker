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

public class WeeklyActivityFragment extends Fragment {

    private EditText activityNameInput;
    private EditText activityDescriptionInput;
    private EditText activityLocationInput;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_activity, container, false);

        activityNameInput = view.findViewById(R.id.activityNameInput);
        activityDescriptionInput = view.findViewById(R.id.activityDescriptionInput);
        activityLocationInput = view.findViewById(R.id.activityLocationInput);
        saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String activityName = activityNameInput.getText().toString();
            String activityDescription = activityDescriptionInput.getText().toString();
            String activityLocation = activityLocationInput.getText().toString();

            // Handle activity saving logic here (e.g., save to database)

            Toast.makeText(getActivity(), "Weekly Activity Saved!", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed(); // Go back to the previous screen
        });

        return view;
    }
}
