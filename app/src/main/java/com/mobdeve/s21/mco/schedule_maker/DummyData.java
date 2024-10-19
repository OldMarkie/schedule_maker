package com.mobdeve.s21.mco.schedule_maker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DummyData {

    private static List<Event> eventList = new ArrayList<>();

    // Return the list of events, filtered and sorted
    public static List<Event> getEvents() {
        // Initialize with some example schedules if the list is empty
        if (eventList.isEmpty()) {
            loadExampleSchedules();
        }
        // Remove past events and sort upcoming events
        filterAndSortEvents();
        return eventList;
    }

    // Load some example schedules
    private static void loadExampleSchedules() {
        Calendar calendar = Calendar.getInstance();

        // Example 1: Math Class
        calendar.set(2024, Calendar.OCTOBER, 20, 9, 0);  // Date: October 20, 2024, 9:00 AM
        eventList.add(new Event("Math Class", calendar.getTime()));

        // Example 2: English Class
        calendar.set(2024, Calendar.OCTOBER, 21, 10, 30);  // Date: October 21, 2024, 10:30 AM
        eventList.add(new Event("English Class", calendar.getTime()));

        // Example 3: Doctor Appointment
        calendar.set(2024, Calendar.OCTOBER, 22, 14, 0);  // Date: October 22, 2024, 2:00 PM
        eventList.add(new Event("Doctor Appointment", calendar.getTime()));

        // Example 4: Gym Session
        calendar.set(2024, Calendar.OCTOBER, 23, 17, 0);  // Date: October 23, 2024, 5:00 PM
        eventList.add(new Event("Gym Session", calendar.getTime()));

        // Example 5: Team Meeting
        calendar.set(2024, Calendar.OCTOBER, 24, 11, 0);  // Date: October 24, 2024, 11:00 AM
        eventList.add(new Event("Team Meeting", calendar.getTime()));

        // Example 6: Dinner with Friends
        calendar.set(2024, Calendar.OCTOBER, 25, 19, 30);  // Date: October 25, 2024, 7:30 PM
        eventList.add(new Event("Dinner with Friends", calendar.getTime()));

        // Example 7: Project Deadline
        calendar.set(2024, Calendar.OCTOBER, 26, 23, 59);  // Date: October 26, 2024, 11:59 PM
        eventList.add(new Event("Project Deadline", calendar.getTime()));
    }

    // Filter out past events and sort the remaining events by date
    private static void filterAndSortEvents() {
        Date currentDate = new Date();  // Get the current date and time

        // Remove past events (events whose date is before the current date and time)
        List<Event> upcomingEvents = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getDateTime().after(currentDate)) {  // Only keep future events
                upcomingEvents.add(event);
            }
        }
        eventList = upcomingEvents;

        // Sort events by date (ascending order)
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                return event1.getDateTime().compareTo(event2.getDateTime());  // Ascending order
            }
        });
    }
}
