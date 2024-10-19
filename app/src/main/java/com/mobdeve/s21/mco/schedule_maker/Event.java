package com.mobdeve.s21.mco.schedule_maker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    private String name;
    private Date dateTime; // Use a legitimate Date object

    public Event(String name, Date dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public Date getDateTime() {
        return dateTime;
    }

    // Helper method to format the Date object into a readable string
    public String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");
        return dateFormat.format(dateTime);
    }
}
