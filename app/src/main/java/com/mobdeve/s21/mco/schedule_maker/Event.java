package com.mobdeve.s21.mco.schedule_maker;

import java.util.Date;
import java.util.UUID;

public class Event {
    private String id;           // Unique identifier for the event
    private String name;
    private String description;
    private String location;
    private Date startTime;      // Start time of the event
    private Date endTime;        // End time of the event
    private boolean isWeekly;     // Indicates if the event is recurring weekly

    // Constructor to initialize an event with a generated ID
    public Event(String name, String description, String location, Date startTime, Date endTime, boolean isWeekly) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWeekly = isWeekly;
    }

    // Constructor to initialize an event with a given ID (for editing purposes)
    public Event(String id, String name, String description, String location, Date startTime, Date endTime, boolean isWeekly) {
        this.id = id; // Use provided ID
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWeekly = isWeekly;
    }

    public String getId() {
        return id; // Getter for ID
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
