package com.mobdeve.s21.mco.schedule_maker;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Events implements Serializable {
    private String id;           // Unique identifier for the event
    private String name;
    private String description;
    private String location;
    private Date startTime;
    private Date endTime;
    private boolean isWeekly;
    private int color;
    private int dayWeek;
    private String googleEventId;


    public Events(String id, String name, String description, String location, Date startTime, Date endTime, boolean isWeekly, int color, int dayWeek) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWeekly = isWeekly;
        this.color = color;
        this.dayWeek = dayWeek;
        this.googleEventId = "";
    }

    public Events(String id, String name, String description, String location, Date startTime, Date endTime, boolean isWeekly, int color, int dayWeek, String googleEventId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWeekly = isWeekly;
        this.color = color;
        this.dayWeek = dayWeek;
        this.googleEventId = googleEventId;
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

    public int getColor() {
        return color;
    }

    public int getDayWeek() {
        return dayWeek;
    }

    public String getGoogleEventId() {
        return googleEventId;
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

    public void setGoogleEventId(String googleEventId) {this.googleEventId = googleEventId; }

    @NonNull
    @Override
    public String toString() {
        return "Events{" +
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
