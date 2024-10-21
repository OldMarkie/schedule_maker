package com.mobdeve.s21.mco.schedule_maker;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LatestScheduleFragment extends Fragment {

    private TextView latestSchedule;
    private TextView eventDescription;
    private TextView eventLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_latest_schedule, container, false);

        // Initialize TextViews
        latestSchedule = view.findViewById(R.id.latestSchedule);
        eventDescription = view.findViewById(R.id.eventDescription);  // Initialize new TextView
        eventLocation = view.findViewById(R.id.eventLocation);        // Initialize new TextView

        // Load the latest schedule
        loadLatestSchedule();

        return view;
    }

    private void loadLatestSchedule() {
        List<Event> events = DummyData.getEvents();  // Fetch the events from a data source

        if (!events.isEmpty()) {
            Event nextEvent = events.get(0);  // Assuming this is sorted by date

            // Retrieve time format preference from SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ThemePref", requireContext().MODE_PRIVATE);
            boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);

            // Choose the appropriate time format
            String timePattern = is24HourFormat ? "HH:mm" : "hh:mm a";
            SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern, Locale.getDefault());

            // Set text to the TextViews
            latestSchedule.setText("Next: " + nextEvent.getName() + " at " + timeFormat.format(nextEvent.getDateTime()));
            eventDescription.setText("Description: " + nextEvent.getDescription());
            eventLocation.setText("Location: " + nextEvent.getLocation());
        } else {
            latestSchedule.setText("No upcoming schedule");
            eventDescription.setText("");
            eventLocation.setText("");
        }
    }

}
