package com.mobdeve.s21.mco.schedule_maker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ScheduleMaker.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_IS_WEEKLY = "is_weekly";
    private static final String COLUMN_COLOR = "color";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EVENTS + "("
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_START_TIME + " INTEGER, "
                + COLUMN_END_TIME + " INTEGER, "
                + COLUMN_IS_WEEKLY + " INTEGER, "
                + COLUMN_COLOR + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public boolean addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, event.getId());
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_LOCATION, event.getLocation());
        values.put(COLUMN_START_TIME, event.getStartTime().getTime());
        values.put(COLUMN_END_TIME, event.getEndTime().getTime());
        values.put(COLUMN_IS_WEEKLY, event.isWeekly() ? 1 : 0);
        values.put(COLUMN_COLOR, event.getColor());

        long result = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return result != -1;
    }

    public List<Event> getEventsForDate(Date date) {
        List<Event> events = new ArrayList<>();
        Set<String> uniqueWeeklyEventIds = new HashSet<>(); // To track unique weekly events
        SQLiteDatabase db = this.getReadableDatabase();

        // Format date to match with your stored event dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dateFormat.format(date);

        // Query to retrieve all events, could also add WHERE clause to filter by date if necessary
        Cursor cursor = db.query(TABLE_EVENTS, null, null, null, null, null, COLUMN_START_TIME);

        while (cursor.moveToNext()) {
            Event event = createEventFromCursor(cursor);

            // Check if the event is for the specified date
            if (isEventOnDate(event, dateString)) {
                if (event.isWeekly()) {
                    // Use a unique identifier for weekly events
                    String uniqueId = event.getName(); // Adjust this based on your identifier logic
                    if (!uniqueWeeklyEventIds.contains(uniqueId)) {
                        uniqueWeeklyEventIds.add(uniqueId);
                        events.add(event); // Add only unique weekly events
                    }
                } else {
                    // Add one-time events directly
                    events.add(event);
                }
            }
        }
        cursor.close();
        db.close();
        return events;
    }

    // Helper method to determine if an event is on the specified date
    private boolean isEventOnDate(Event event, String dateString) {
        // Format the event's start time to compare with the given date
        SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String eventDateString = eventDateFormat.format(event.getStartTime()); // Assuming getStartTime returns a Date object
        return eventDateString.equals(dateString);
    }


    private Event createEventFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
        Date startTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
        Date endTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
        boolean isWeekly = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_WEEKLY)) == 1;
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COLOR));

        return new Event(name, description, location, startTime, endTime, isWeekly, color);
    }

    public void deleteEvent(String eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COLUMN_ID + "=?", new String[]{eventId});
        db.close();
    }

    public void updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_LOCATION, event.getLocation());
        values.put(COLUMN_START_TIME, event.getStartTime().getTime());
        values.put(COLUMN_END_TIME, event.getEndTime().getTime());
        values.put(COLUMN_IS_WEEKLY, event.isWeekly() ? 1 : 0);
        values.put(COLUMN_COLOR, event.getColor());

        db.update(TABLE_EVENTS, values, COLUMN_ID + "=?", new String[]{event.getId()});
        db.close();
    }

    public boolean isTimeConflict(Date startTime, Date endTime) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to find overlapping events
        String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE "
                + COLUMN_START_TIME + " < ? AND " + COLUMN_END_TIME + " > ?";
        String[] args = {
                String.valueOf(endTime.getTime()),  // End time of the new event
                String.valueOf(startTime.getTime())  // Start time of the new event
        };

        Cursor cursor = db.rawQuery(query, args);
        boolean conflictExists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return conflictExists;
    }

    public boolean eventExists(String eventName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_NAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{eventName});
        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();
        return exists;
    }

}
