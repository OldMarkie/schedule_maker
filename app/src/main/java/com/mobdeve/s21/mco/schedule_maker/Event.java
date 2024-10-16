package com.mobdeve.s21.mco.schedule_maker;

public class Event {
    private String name;
    private String day;
    private String time;

    public Event(String name, String day, String time) {
        this.name = name;
        this.day = day;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
