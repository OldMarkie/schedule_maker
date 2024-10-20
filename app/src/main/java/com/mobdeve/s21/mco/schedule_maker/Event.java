package com.mobdeve.s21.mco.schedule_maker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a");
        return dateFormat.format(dateTime);
    }

    public boolean occursInWeek(Date startOfWeek, Date endOfWeek) {
        Calendar eventCalendar = Calendar.getInstance();
        eventCalendar.setTime(dateTime);

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startOfWeek);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endOfWeek);

        if (isWeekly) {
            return eventCalendar.get(Calendar.DAY_OF_WEEK) >= startCalendar.get(Calendar.DAY_OF_WEEK)
                    && eventCalendar.get(Calendar.DAY_OF_WEEK) <= endCalendar.get(Calendar.DAY_OF_WEEK);
        } else {
            return dateTime.after(startOfWeek) && dateTime.before(endOfWeek);
        }
    }
}
