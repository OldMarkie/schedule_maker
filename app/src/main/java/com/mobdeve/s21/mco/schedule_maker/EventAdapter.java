package com.mobdeve.s21.mco.schedule_maker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;
        public TextView eventDay;
        public TextView eventTime;

        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.eventName);
            eventDay = view.findViewById(R.id.eventDay);
            eventTime = view.findViewById(R.id.eventTime);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDay.setText(event.getDay());
        holder.eventTime.setText(event.getTime());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
