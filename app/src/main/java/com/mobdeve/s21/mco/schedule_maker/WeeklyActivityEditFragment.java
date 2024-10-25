package com.mobdeve.s21.mco.schedule_maker;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class WeeklyActivityEditFragment extends  DialogFragment{
    private Button saveButton, cancelButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_activity_edit, container, false);

        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.editCancelBtn);

        saveButton.setOnClickListener(v -> dismiss()); // Close dialog and return
        cancelButton.setOnClickListener(v -> dismiss()); // Close dialog without saving

        return view;
    }
}

