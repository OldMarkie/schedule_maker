package com.mobdeve.s21.mco.schedule_maker;

import java.util.Date;

public class Event {
    private String name;
    private String description;
    private String location;
    private Date startTime;  // Start time of the event
    private Date endTime;    // End time of the event
    private boolean isWeekly; // Indicates if the event is recurring weekly

    public Event(String name, String description, String location, Date startTime, Date endTime, boolean isWeekly) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public boolean isWeekly() {
        return isWeekly;
    }
}
