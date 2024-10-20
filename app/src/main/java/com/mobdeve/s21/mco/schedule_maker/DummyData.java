package com.mobdeve.s21.mco.schedule_maker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DummyData {

    private static List<Event> eventList = new ArrayList<>();

    public static List<Event> getEvents() {
        if (eventList.isEmpty()) {
            loadExampleSchedules();
        }
        filterAndSortEvents();
        return eventList;
    }

    private static void loadExampleSchedules() {
        Calendar calendar = Calendar.getInstance();

        // Weekly Event: Gym Session every Monday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        eventList.add(new Event("Gym Session", calendar.getTime(), true));

        // Non-weekly events...
        calendar.set(2024, Calendar.OCTOBER, 23, 17, 0);
        eventList.add(new Event("Doctor Appointment", calendar.getTime(), false));
    }

    private static void filterAndSortEvents() {
        Date currentDate = new Date();
        List<Event> upcomingEvents = new ArrayList<>();

        for (Event event : eventList) {
            // If event is after the current date or is a weekly event
            if (event.getDateTime().after(currentDate) || event.isWeekly()) {
                upcomingEvents.add(event);
            }
        }

        // Sort the events by date
        Collections.sort(upcomingEvents, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                return event1.getDateTime().compareTo(event2.getDateTime());
            }
        });

        eventList = upcomingEvents;
    }

}
