package com.mobdeve.s21.mco.schedule_maker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DummyData {

    private static List<Events> eventsList = new ArrayList<>();

    // Return events for a specific weekday (including recurring weekly events)
    public static List<Events> getEventsForWeekDay(Date weekDay) {
        List<Events> filteredEvents = new ArrayList<>();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(weekDay);

        for (Events events : eventsList) {
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTime(events.getStartTime());

            if (events.isWeekly()) {
                if (eventDate.get(Calendar.DAY_OF_WEEK) == selectedDate.get(Calendar.DAY_OF_WEEK)) {
                    filteredEvents.add(events);
                }
            }
        }

        // Sort by start time
        Collections.sort(filteredEvents, Comparator.comparing(Events::getStartTime));
        return filteredEvents;
    }

    // Return events for a specific date (including recurring weekly events)
    public static List<Events> getEventsForDate(Date date) {
        List<Events> filteredEvents = new ArrayList<>();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(date);

        for (Events events : eventsList) {
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTime(events.getStartTime());

            if (events.isWeekly()) {
                if (eventDate.get(Calendar.DAY_OF_WEEK) == selectedDate.get(Calendar.DAY_OF_WEEK)) {
                    filteredEvents.add(events);
                }
            } else {
                if (isSameDay(eventDate, selectedDate)) {
                    filteredEvents.add(events);
                }
            }
        }

        // Sort by start time
        Collections.sort(filteredEvents, Comparator.comparing(Events::getStartTime));
        return filteredEvents;
    }

    // Helper method to check if two Calendar objects represent the same day
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Method to check if an event overlaps with existing events
    public static boolean isEventTimeConflict(Date startTime, Date endTime) {
        for (Events events : eventsList) {
            if (startTime.before(events.getEndTime()) && endTime.after(events.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    // Method to add an events (one-time or weekly)
    public static boolean addEvent(Events events) {
        if (isEventTimeConflict(events.getStartTime(), events.getEndTime())) {
            return false;
        }
        eventsList.add(events);
        return true;
    }

    // Method to delete an events
    public static void deleteEvent(Events events) {
        eventsList.remove(events);
    }

    // Return all events sorted by start time
    public static List<Events> getEvents() {
        Collections.sort(eventsList, Comparator.comparing(Events::getStartTime));
        return eventsList;
    }

    // Method to update an existing events
    public static void updateEvent(Events events) {
        for (int i = 0; i < eventsList.size(); i++) {
            if (eventsList.get(i).getId().equals(events.getId())) {
                eventsList.set(i, events);
                break;
            }
        }
    }
}
