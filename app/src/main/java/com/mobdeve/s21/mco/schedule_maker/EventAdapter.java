package com.mobdeve.s21.mco.schedule_maker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

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
        holder.eventDate.setText(event.getFormattedDate());

        // Set click listener to show event details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDetailsDialogFragment dialog = EventDetailsDialogFragment.newInstance(event, listener);
                dialog.show(activity.getSupportFragmentManager(), "eventDetails");
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public TextView eventDate;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
        }
    }
}
