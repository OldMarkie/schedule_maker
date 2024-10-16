package com.mobdeve.s21.mco.schedule_maker;

import java.util.ArrayList;
import java.util.List;

public class DummyData {

    public static List<Event> getEvents() {
        List<Event> eventList = new ArrayList<>();
        eventList.add(new Event("Math Class", "Monday", "9:00 AM"));
        eventList.add(new Event("English Class", "Tuesday", "10:00 AM"));
        eventList.add(new Event("Gym Session", "Wednesday", "4:00 PM"));
        eventList.add(new Event("Doctor's Appointment", "Friday", "2:00 PM"));
        return eventList;
    }
}
