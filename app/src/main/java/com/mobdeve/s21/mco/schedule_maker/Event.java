package com.mobdeve.s21.mco.schedule_maker;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Event implements Serializable {
    private String id;           // Unique identifier for the event
    private String name;
    private String description;
    private String location;
    private Date startTime;
    private Date endTime;
    private boolean isWeekly;
    private int color;
    private int dayWeek;


    public Event(String id, String name, String description, String location, Date startTime, Date endTime, boolean isWeekly,int color, int dayWeek) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWeekly = isWeekly;
        this.color = color;
        this.dayWeek = dayWeek;
    }


//    public Event(String name, String description, String location, Date startTime, Date endTime, boolean isWeekly, int dayWeek, color) {
//        this.id = UUID.randomUUID().toString(); // Generate a unique ID
//        this.name = name;
//        this.description = description;
//        this.location = location;
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.isWeekly = isWeekly;
//        this.color = color;
//        this.dayWeek = -1;
//    }

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

    public int getColor() {
        return color;
    }

    public int getDayWeek() {
        return dayWeek;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setWeekly(boolean weekly) {
        isWeekly = weekly;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setDayWeek(int dayWeek) {
        this.dayWeek = dayWeek;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isWeekly=" + isWeekly +
                ", color=" + color +
                ", dayWeek=" + dayWeek +
                '}';
    }


}
