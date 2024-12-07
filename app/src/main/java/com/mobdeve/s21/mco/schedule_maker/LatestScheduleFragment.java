// LatestScheduleFragment.java
package com.mobdeve.s21.mco.schedule_maker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LatestScheduleFragment extends Fragment {

    private TextView latestSchedule;
    private TextView eventDescription;
    private TextView eventLocation;
    private TextView upcomingOrNon;
    private TextView eventTime;
    private TextView descTitle;
    private TextView locTitle;
    private DatabaseHelper dbHelper;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_latest_schedule, container, false);

        // Initialize TextViews
        latestSchedule = view.findViewById(R.id.latestSchedule);
        upcomingOrNon = view.findViewById(R.id.UpcomingOrNon);
        eventTime = view.findViewById(R.id.eventTimeTV);

        // Load the latest schedule asynchronously
        loadLatestSchedule();

        return view;
    }

    private void loadLatestSchedule() {
        dbHelper = new DatabaseHelper(requireContext());

        executor.execute(() -> {
            Events nextEvent = dbHelper.getLatestEvent(); // Fetch the latest event from the database

            if (nextEvent != null) {
                // Retrieve time format preference
                SharedPreferences sharedPreferences = requireContext()
                        .getSharedPreferences("ThemePref", requireContext().MODE_PRIVATE);
                boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);

                // Format time and day
                String timePattern = is24HourFormat ? "HH:mm" : "hh:mm a";
                SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern, Locale.getDefault());
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

                Date startTime = nextEvent.getStartTime();
                Date endTime = nextEvent.getEndTime();

                // Fallback to ensure no null values
                if (startTime == null || endTime == null) {
                    setNoScheduleText();
                    return;
                }

                String dayOfWeek = dayFormat.format(startTime);
                String formattedStartTime = timeFormat.format(startTime);
                String formattedEndTime = timeFormat.format(endTime);

                // Post updates to the UI on the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    upcomingOrNon.setText("Upcoming");
                    latestSchedule.setText(nextEvent.getName());
                    eventTime.setText(formattedStartTime + " - " + formattedEndTime + "\n (" + dayOfWeek + ")");
                });
            } else {
                // Post "No Schedule" updates to the UI on the main thread
                new Handler(Looper.getMainLooper()).post(this::setNoScheduleText);
            }
        });
    }

    private void setNoScheduleText() {
        upcomingOrNon.setText("No Schedule Stored");
        latestSchedule.setText("");
        eventTime.setText("");
    }


}
