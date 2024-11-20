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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LatestScheduleFragment extends Fragment {

    private TextView latestSchedule;
    private TextView eventDescription;
    private TextView eventLocation;
    private TextView upcomingOrNon;
    private TextView eventTime;
    private TextView descTitle;
    private TextView locTitle;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_latest_schedule, container, false);

        // Initialize TextViews
        latestSchedule = view.findViewById(R.id.latestSchedule);
        upcomingOrNon = view.findViewById(R.id.UpcomingOrNon);
        eventTime = view.findViewById(R.id.eventTimeTV);



        // Load the latest schedule
        loadLatestSchedule();

        return view;
    }

    private void loadLatestSchedule() {
        dbHelper = new DatabaseHelper(requireContext());
        List<Events> events = dbHelper.getAllEvents();  // Fetch the events from a data source

        if (!events.isEmpty()) {
            Events nextEvents = events.get(0);  // Assuming this is sorted by date

            // Retrieve time format preference from SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ThemePref", requireContext().MODE_PRIVATE);
            boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);

            // Choose the appropriate time format
            String timePattern = is24HourFormat ? "HH:mm" : "hh:mm a";
            SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern, Locale.getDefault());

            // Get start time
            Date startTime = nextEvents.getStartTime();

            // Create a Calendar instance and set the time
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);

            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE"); // Full name of the day

            // Get the day of the week
            String dayOfWeek = dayFormat.format(startTime); // Get the full name of the day


            // Set text to the TextViews
            upcomingOrNon.setText("Upcoming");
            latestSchedule.setText(nextEvents.getName());
            eventTime.setText(timeFormat.format(nextEvents.getStartTime()) + " - " + timeFormat.format(nextEvents.getEndTime()) + "\n (" + dayOfWeek +")" );

        } else {
            upcomingOrNon.setText("No Schedule Stored");
            latestSchedule.setText("");
            eventTime.setText("");
        }
    }

}
