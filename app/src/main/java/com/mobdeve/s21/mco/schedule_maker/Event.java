package com.mobdeve.s21.mco.schedule_maker;

import java.util.Date;

public class Event {
    private String name;
    private String description;
    private String location;
    private Date dateTime;  // Date and time for the event
    private boolean isWeekly; // Indicates if the event is recurring weekly

    public Event(String name, String description, String location, Date dateTime, boolean isWeekly) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.isWeekly = isWeekly;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public boolean isWeekly() {
        return isWeekly;
    }
}
