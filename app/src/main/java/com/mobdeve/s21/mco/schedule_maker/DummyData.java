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
            eventDate.setTime(event.getStartTime());

            if (event.isWeekly()) {
                if (eventDate.get(Calendar.DAY_OF_WEEK) == selectedDate.get(Calendar.DAY_OF_WEEK)) {
                    filteredEvents.add(event);
                }
            }
        }

        // Sort by start time
        Collections.sort(filteredEvents, Comparator.comparing(Event::getStartTime));
        return filteredEvents;
    }

    // Return events for a specific date (including recurring weekly events)
    public static List<Event> getEventsForDate(Date date) {
        List<Event> filteredEvents = new ArrayList<>();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(date);

        for (Event event : eventList) {
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTime(event.getStartTime());

            if (event.isWeekly()) {
                if (eventDate.get(Calendar.DAY_OF_WEEK) == selectedDate.get(Calendar.DAY_OF_WEEK)) {
                    filteredEvents.add(event);
                }
            } else {
                if (isSameDay(eventDate, selectedDate)) {
                    filteredEvents.add(event);
                }
            }
        }

        // Sort by start time
        Collections.sort(filteredEvents, Comparator.comparing(Event::getStartTime));
        return filteredEvents;
    }

    // Helper method to check if two Calendar objects represent the same day
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Method to check if an event overlaps with existing events
    public static boolean isEventTimeConflict(Date startTime, Date endTime) {
        for (Event event : eventList) {
            if (startTime.before(event.getEndTime()) && endTime.after(event.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    // Method to add an event (one-time or weekly)
    public static boolean addEvent(Event event) {
        if (isEventTimeConflict(event.getStartTime(), event.getEndTime())) {
            return false;
        }
        eventList.add(event);
        return true;
    }

    // Method to delete an event
    public static void deleteEvent(Event event) {
        eventList.remove(event);
    }

    // Return all events sorted by start time
    public static List<Event> getEvents() {
        Collections.sort(eventList, Comparator.comparing(Event::getStartTime));
        return eventList;
    }

    // Method to update an existing event
    public static void updateEvent(Event event) {
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getId().equals(event.getId())) {
                eventList.set(i, event);
                break;
            }
        }
    }
}
