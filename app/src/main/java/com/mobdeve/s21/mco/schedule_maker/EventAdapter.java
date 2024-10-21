package com.mobdeve.s21.mco.schedule_maker;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private FragmentActivity activity; // To manage fragment transactions for the dialog
    private EventDetailsDialogFragment.OnEventActionListener listener;

    public EventAdapter(List<Event> eventList, FragmentActivity activity, EventDetailsDialogFragment.OnEventActionListener listener) {
        this.eventList = eventList;
        this.activity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        final Event event = eventList.get(position);
        holder.eventName.setText(event.getName());

        // Get user preference for 24-hour or 12-hour format
        SharedPreferences sharedPreferences = activity.getSharedPreferences("ThemePref", MODE_PRIVATE);
        boolean is24HourFormat = sharedPreferences.getBoolean("is24HourFormat", false);

        // Set the correct time format based on preference
        SimpleDateFormat timeFormat;
        if (is24HourFormat) {
            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        }

        // Format and set only the time in the eventTime TextView
        holder.eventTime.setText(timeFormat.format(event.getDateTime()));

        // Set the recurrence text (e.g., Weekly or One-time)
        if (event.isWeekly()) {
            holder.eventRecurrence.setText("Weekly");
        } else {
            holder.eventRecurrence.setText("One-time");
        }

        // Set click listener to show event details
        holder.itemView.setOnClickListener(v -> {
            EventDetailsDialogFragment dialog = EventDetailsDialogFragment.newInstance(event, listener);
            dialog.show(activity.getSupportFragmentManager(), "eventDetails");
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public TextView eventTime; // This was previously named `eventDate`
        public TextView eventRecurrence;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventTime = itemView.findViewById(R.id.eventTime); // Rename `eventDate` to `eventTime`
            eventRecurrence = itemView.findViewById(R.id.eventRecurrence); // Add `eventRecurrence` if it's in your layout
        }
    }
}
