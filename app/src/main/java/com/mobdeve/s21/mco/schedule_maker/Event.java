package com.mobdeve.s21.mco.schedule_maker;

import java.util.Date;

public class Event {
    private String name;
    private Date dateTime;
    private boolean isWeekly;

    public Event(String name, Date dateTime, boolean isWeekly) {
        this.name = name;
        this.dateTime = dateTime;
        this.isWeekly = isWeekly;
    }

    public String getName() {
        return name;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public boolean isWeekly() {
        return isWeekly;
    }

    public void setWeekly(boolean weekly) {
        isWeekly = weekly;
    }
}
