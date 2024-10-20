package com.mobdeve.s21.mco.schedule_maker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DummyData {

    private static List<Event> eventList = new ArrayList<>();

    // Return the list of events, including recurring weekly ones, filtered and sorted
    public static List<Event> getEvents() {
        if (eventList.isEmpty()) {
            loadExampleSchedules();
        }
        return filterAndSortEvents();
    }

    // Load some example schedules
    private static void loadExampleSchedules() {
        Calendar calendar = Calendar.getInstance();

        // Add one-time and weekly events here...
    }

    // Add event
    public static void addEvent(Event event) {
        eventList.add(event);
    }

    // Method to delete an event
    public static void deleteEvent(Event event) {
        eventList.remove(event);
    }

    // Filter and include weekly events
    private static List<Event> filterAndSortEvents() {
        List<Event> allEvents = new ArrayList<>(eventList);
        Date currentDate = new Date();

        // Generate weekly events if needed
        List<Event> weeklyEvents = new ArrayList<>();
        for (Event event : eventList) {
            if (event.isWeekly()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(event.getDateTime());
                while (calendar.getTime().before(currentDate)) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                }
                // Add future weekly events
                while (calendar.getTime().before(new Date(currentDate.getTime() + (7L * 24 * 60 * 60 * 1000)))) {  // 1 week ahead
                    weeklyEvents.add(new Event(event.getName(), calendar.getTime(), true));
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                }
            }
        }
        allEvents.addAll(weeklyEvents);

        // Filter out past events
        List<Event> upcomingEvents = new ArrayList<>();
        for (Event event : allEvents) {
            if (event.getDateTime().after(currentDate)) {
                upcomingEvents.add(event);
            }
        }

        // Sort by date
        Collections.sort(upcomingEvents, Comparator.comparing(Event::getDateTime));

        return upcomingEvents;
    }

    // Return events for a specific date (including recurring weekly events)
    public static List<Event> getEventsForDate(Date date) {
        List<Event> filteredEvents = new ArrayList<>();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);

        for (Event event : eventList) {
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(event.getDateTime());

            // Check if the event is weekly and falls on the same day of the week
            if (event.isWeekly()) {
                while (calendar2.getTime().before(date)) {
                    calendar2.add(Calendar.WEEK_OF_YEAR, 1);
                }
            }

            // Match the day
            if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                    calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)) {
                filteredEvents.add(event);
            }
        }

        return filteredEvents;
    }
}
