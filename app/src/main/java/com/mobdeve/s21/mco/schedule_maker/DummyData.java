package com.mobdeve.s21.mco.schedule_maker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DummyData {

    private static List<Event> eventList = new ArrayList<>();

    // Return events for a specific weekday (including recurring weekly events)
    public static List<Event> getEventsForWeekDay(Date weekDay) {
        List<Event> filteredEvents = new ArrayList<>();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(weekDay);

        for (Event event : eventList) {
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTime(event.getDateTime());

            if (event.isWeekly()) {
                // Weekly recurring events: match the day of the week
                if (eventDate.get(Calendar.DAY_OF_WEEK) == selectedDate.get(Calendar.DAY_OF_WEEK)) {
                    filteredEvents.add(event);  // If the event recurs on this weekday, add it to the list
                }
            } else {
                // One-time events: match the exact date
                if (isSameDay(eventDate, selectedDate)) {
                    filteredEvents.add(event);
                }
            }
        }

        return filteredEvents;
    }

    // Return events for a specific date (including recurring weekly events)
    public static List<Event> getEventsForDate(Date date) {
        List<Event> filteredEvents = new ArrayList<>();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(date);

        for (Event event : eventList) {
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTime(event.getDateTime());

            if (event.isWeekly()) {
                // For weekly events, check if the day of the week matches
                if (eventDate.get(Calendar.DAY_OF_WEEK) == selectedDate.get(Calendar.DAY_OF_WEEK)) {
                    filteredEvents.add(event);
                }
            } else {
                // For one-time events, check if the exact date matches
                if (isSameDay(eventDate, selectedDate)) {
                    filteredEvents.add(event);
                }
            }
        }

        return filteredEvents;
    }

    // Helper method to check if two Calendar objects represent the same day
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Method to add an event (one-time or weekly)
    public static void addEvent(Event event) {
        eventList.add(event);
    }

    // Method to delete an event
    public static void deleteEvent(Event event) {
        eventList.remove(event);
    }

    // Return all events (one-time and weekly events) sorted by date
    public static List<Event> getEvents() {
        // Sort events by date
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return e1.getDateTime().compareTo(e2.getDateTime());  // Sort in ascending order by date
            }
        });
        return eventList;
    }
}
