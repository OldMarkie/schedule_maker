package com.mobdeve.s21.mco.schedule_maker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecurringEventManager {

    // List to hold recurring events, you can replace this with database handling logic
    private static List<RecurringEvent> recurringEvents = new ArrayList<>();

    // Method to add a recurring event
    public static void addRecurringEvent(RecurringEvent event) {
        recurringEvents.add(event);
    }

    // Method to extend the events for the next period (e.g., 6 months)
    public static void extendEventForNextPeriod(Calendar baseDate, Calendar newEndDate) {
        for (RecurringEvent event : recurringEvents) {
            if (event.getEndTime().before(baseDate.getTime())) {
                // Extend the end date of the event for the next 6 months or period
                event.setEndTime(newEndDate.getTime());
            }
        }
    }

    // Method to retrieve all recurring events
    public static List<RecurringEvent> getRecurringEvents() {
        return recurringEvents;
    }

    public class RecurringEvent extends Event {

        // Additional properties for recurring events
        private int recurrenceInterval; // Interval in weeks for the recurrence

        public RecurringEvent(String name, String description, String location, Date startTime, Date endTime, boolean isWeekly, int recurrenceInterval) {
            super(name, description, location, startTime, endTime, isWeekly);
            this.recurrenceInterval = recurrenceInterval;
        }

        public int getRecurrenceInterval() {
            return recurrenceInterval;
        }

        public void setRecurrenceInterval(int recurrenceInterval) {
            this.recurrenceInterval = recurrenceInterval;
        }
    }

}
