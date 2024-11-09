package com.mobdeve.s21.mco.schedule_maker;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private FragmentActivity activity; // To manage fragment transactions for the dialog
    private EventDetailsDialogFragment.OnEventActionListener listener;
    private boolean is24HourFormat;

    public EventAdapter(List<Event> eventList, FragmentActivity activity, EventDetailsDialogFragment.OnEventActionListener listener,
                        boolean is24HourFormat) {
        this.eventList = eventList;
        this.activity = activity;
        this.listener = listener;
        this.is24HourFormat = is24HourFormat;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }


    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event currentEvent = eventList.get(position);
        Log.d("Event", "Event details: " + currentEvent.toString());

        // Set event name
        holder.eventName.setText(currentEvent.getName());

        // Format and set event start time and end time
        String startTime = formatTime(currentEvent.getStartTime());
        String endTime = formatTime(currentEvent.getEndTime());
        holder.eventTime.setText(startTime + " - " + endTime);

        // Set recurrence type or any other field if needed
        holder.eventRecurrence.setText(currentEvent.isWeekly() ? "Weekly" : "One-time");

        int eventColor = currentEvent.getColor();
        Log.d("EventAdapter", "Event color (HEX): " + Integer.toHexString(eventColor));

        // Check if the event color is valid (non-transparent)
        if (eventColor == 0x000000 || eventColor == 0xFFFFFFFF) {
            Log.w("EventAdapter", "Event color is invalid (black or white). Color: " + Integer.toHexString(eventColor));
        }

        // Ensure detailsBackground is properly accessed
        if (holder.detailsBackground != null) {
            Log.d("EventAdapter", "detailsBackground: " + holder.detailsBackground);
            // Try using setBackgroundColor() directly if setCardBackgroundColor() doesn't work
            holder.detailsBackground.setBackgroundColor(eventColor);
            Log.d("EventAdapter", "Set background color to: " + Integer.toHexString(eventColor));
        } else {
            Log.e("EventAdapter", "detailsBackground is null for " + currentEvent.getName());
        }

        // Set text color for visibility against the background color
        int textColor = (isDarkMode(eventColor)) ? 0xFFFFFFFF : 0xFF000000; // White text for dark bg, black for light bg
        holder.eventName.setTextColor(textColor);
        holder.eventTime.setTextColor(textColor);
        holder.eventRecurrence.setTextColor(textColor);

        // Set click listener to show event details
        holder.itemView.setOnClickListener(v -> {
            EventDetailsDialogFragment dialog = EventDetailsDialogFragment.newInstance(currentEvent, listener);
            dialog.show(activity.getSupportFragmentManager(), "eventDetails");
        });
    }

    // Helper method for formatting time with respect to 12-hour or 24-hour format
    private String formatTime(Date timeInMillis) {
        SimpleDateFormat sdf;
        if (is24HourFormat) {
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());  // 24-hour format
        } else {
            sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());  // 12-hour format
        }
        return sdf.format(timeInMillis);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public TextView eventTime; // This was previously named `eventDate`
        public TextView eventRecurrence;
        public CardView detailsBackground;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventTime = itemView.findViewById(R.id.eventTime); // Rename `eventDate` to `eventTime`
            eventRecurrence = itemView.findViewById(R.id.eventRecurrence); // Add `eventRecurrence` if it's in your layout
            detailsBackground = itemView.findViewById(R.id.detailsBackground);
        }
    }
    // Method to determine if a color is dark
    private boolean isDarkMode(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        // Calculate the brightness using the luminance formula
        double brightness = (0.299 * r + 0.587 * g + 0.114 * b);
        return brightness < 128; // Returns true if dark
    }
}
