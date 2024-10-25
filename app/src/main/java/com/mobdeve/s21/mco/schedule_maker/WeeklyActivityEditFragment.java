package com.mobdeve.s21.mco.schedule_maker;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeeklyActivityEditFragment extends DialogFragment {
    private Button saveButton, cancelButton;
    private OnFragmentInteractionListener listener;

    // Add CheckBox and EditText references
    private CheckBox checkMonday, checkTuesday, checkWednesday, checkThursday, checkFriday, checkSaturday, checkSunday;
    private EditText mondayStartTimeInput, mondayEndTimeInput;
    private EditText tuesdayStartTimeInput, tuesdayEndTimeInput;
    private EditText wednesdayStartTimeInput, wednesdayEndTimeInput;
    private EditText thursdayStartTimeInput, thursdayEndTimeInput;
    private EditText fridayStartTimeInput, fridayEndTimeInput;
    private EditText saturdayStartTimeInput, saturdayEndTimeInput;
    private EditText sundayStartTimeInput, sundayEndTimeInput;

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
        View view = inflater.inflate(R.layout.fragment_weekly_activity_edit, container, false);

        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.editCancelBtn);

        // Initialize CheckBoxes
        checkMonday = view.findViewById(R.id.checkMonday);
        checkTuesday = view.findViewById(R.id.checkTuesday);
        checkWednesday = view.findViewById(R.id.checkWednesday);
        checkThursday = view.findViewById(R.id.checkThursday);
        checkFriday = view.findViewById(R.id.checkFriday);
        checkSaturday = view.findViewById(R.id.checkSaturday);
        checkSunday = view.findViewById(R.id.checkSunday);

        // Initialize EditTexts
        mondayStartTimeInput = view.findViewById(R.id.mondayStartTimeInput);
        mondayEndTimeInput = view.findViewById(R.id.mondayEndTimeInput);
        tuesdayStartTimeInput = view.findViewById(R.id.tuesdayStartTimeInput);
        tuesdayEndTimeInput = view.findViewById(R.id.tuesdayEndTimeInput);
        wednesdayStartTimeInput = view.findViewById(R.id.wednesdayStartTimeInput);
        wednesdayEndTimeInput = view.findViewById(R.id.wednesdayEndTimeInput);
        thursdayStartTimeInput = view.findViewById(R.id.thursdayStartTimeInput);
        thursdayEndTimeInput = view.findViewById(R.id.thursdayEndTimeInput);
        fridayStartTimeInput = view.findViewById(R.id.fridayStartTimeInput);
        fridayEndTimeInput = view.findViewById(R.id.fridayEndTimeInput);
        saturdayStartTimeInput = view.findViewById(R.id.saturdayStartTimeInput);
        saturdayEndTimeInput = view.findViewById(R.id.saturdayEndTimeInput);
        sundayStartTimeInput = view.findViewById(R.id.sundayStartTimeInput);
        sundayEndTimeInput = view.findViewById(R.id.sundayEndTimeInput);

        // Set initial visibility
        setEditTextVisibility();

        // Set onCheckedChangeListeners for CheckBoxes
        checkMonday.setOnCheckedChangeListener((buttonView, isChecked) -> toggleEditTextVisibility(mondayStartTimeInput, mondayEndTimeInput, isChecked));
        checkTuesday.setOnCheckedChangeListener((buttonView, isChecked) -> toggleEditTextVisibility(tuesdayStartTimeInput, tuesdayEndTimeInput, isChecked));
        checkWednesday.setOnCheckedChangeListener((buttonView, isChecked) -> toggleEditTextVisibility(wednesdayStartTimeInput, wednesdayEndTimeInput, isChecked));
        checkThursday.setOnCheckedChangeListener((buttonView, isChecked) -> toggleEditTextVisibility(thursdayStartTimeInput, thursdayEndTimeInput, isChecked));
        checkFriday.setOnCheckedChangeListener((buttonView, isChecked) -> toggleEditTextVisibility(fridayStartTimeInput, fridayEndTimeInput, isChecked));
        checkSaturday.setOnCheckedChangeListener((buttonView, isChecked) -> toggleEditTextVisibility(saturdayStartTimeInput, saturdayEndTimeInput, isChecked));
        checkSunday.setOnCheckedChangeListener((buttonView, isChecked) -> toggleEditTextVisibility(sundayStartTimeInput, sundayEndTimeInput, isChecked));

        saveButton.setOnClickListener(v -> {
            listener.onDialogDismissed();
            dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            listener.onDialogDismissed();
            dismiss();
        });

        return view;
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
}
