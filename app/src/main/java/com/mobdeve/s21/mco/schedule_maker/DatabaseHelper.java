package com.mobdeve.s21.mco.schedule_maker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * DatabaseHelper is a subclass of SQLiteOpenHelper that manages database creation,
 * version management, and basic CRUD operations for the application. This class is
 * responsible for creating, opening, and managing the SQLite database and its schema.
 * It includes methods for handling SQL queries such as insert, update, delete, and select.
 *
 * The database contains various tables that hold information related to the application's
 * functionality, and this helper class ensures that the database operations are efficient,
 * consistent, and secure.
 */

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
    private static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    private static final String COLUMN_GOOGLE_ID = "google_id";

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
                + COLUMN_COLOR + " INTEGER,"
                + COLUMN_DAY_OF_WEEK + " INTEGER,"
                + COLUMN_GOOGLE_ID + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public boolean addEvent(Events events) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, events.getId());
        values.put(COLUMN_NAME, events.getName());
        values.put(COLUMN_DESCRIPTION, events.getDescription());
        values.put(COLUMN_LOCATION, events.getLocation());
        values.put(COLUMN_START_TIME, events.getStartTime().getTime());
        values.put(COLUMN_END_TIME, events.getEndTime().getTime());
        values.put(COLUMN_IS_WEEKLY, events.isWeekly() ? 1 : 0);
        values.put(COLUMN_DAY_OF_WEEK, events.getDayWeek());
        values.put(COLUMN_COLOR, events.getColor());
        values.put(COLUMN_GOOGLE_ID, events.getGoogleEventId());

        long result = db.insert(TABLE_EVENTS, null, values);
        return result != -1;

    }

    public List<Events> getEventsForDate(Date date) {
        List<Events> events = new ArrayList<>();
        Set<String> uniqueWeeklyEventIds = new HashSet<>(); // To track unique weekly events
        SQLiteDatabase db = this.getReadableDatabase();

        // Format date to match with your stored event dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dateFormat.format(date);

        // Query to retrieve all events, could also add WHERE clause to filter by date if necessary
        Cursor cursor = db.query(TABLE_EVENTS, null, null, null, null, null, COLUMN_START_TIME);

        while (cursor.moveToNext()) {
            Events event = createEventFromCursor(cursor);

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
        return events;
    }

    // Helper method to determine if an events is on the specified date
    private boolean isEventOnDate(Events events, String dateString) {
        // Format the events's start time to compare with the given date
        SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String eventDateString = eventDateFormat.format(events.getStartTime()); // Assuming getStartTime returns a Date object
        return eventDateString.equals(dateString);
    }


    // Helper method to create an Events object from the cursor
    private Events createEventFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
        Date startTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
        Date endTime = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
        boolean isWeekly = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_WEEKLY)) == 1;
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COLOR));
        int dayOfWeek = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAY_OF_WEEK));
        String googleEventId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GOOGLE_ID));

        return new Events(id, name, description, location, startTime, endTime, isWeekly, color, dayOfWeek, googleEventId);
    }

    public void deleteEvent(String eventName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, retrieve the event by its name to check if it's weekly
        Cursor cursor = db.query(TABLE_EVENTS, null, COLUMN_NAME + "=?", new String[]{eventName}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Loop through all instances with the same name
            do {
                Events events = createEventFromCursor(cursor);

                // Check if the events is weekly
                if (events.isWeekly()) {
                    // If it's a weekly events, delete all instances by its name
                    db.delete(TABLE_EVENTS, COLUMN_NAME + "=?", new String[]{events.getName()});
                    break; // Exit the loop after deletion
                } else {
                    // If it's not a weekly events, delete it by its name
                    db.delete(TABLE_EVENTS, COLUMN_NAME + "=?", new String[]{events.getName()});
                }
            } while (cursor.moveToNext());
        }

        // Close the cursor and database
        if (cursor != null) {
            cursor.close();
        }
    }



    public void updateEvent(Events events) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, events.getName());
        values.put(COLUMN_DESCRIPTION, events.getDescription());
        values.put(COLUMN_LOCATION, events.getLocation());
        values.put(COLUMN_START_TIME, events.getStartTime().getTime());
        values.put(COLUMN_END_TIME, events.getEndTime().getTime());
        values.put(COLUMN_IS_WEEKLY, events.isWeekly() ? 1 : 0);
        values.put(COLUMN_COLOR, events.getColor());

        db.update(TABLE_EVENTS, values, COLUMN_ID + "=?", new String[]{events.getId()});
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
        

        return conflictExists;
    }

    public boolean eventExists(String eventName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_NAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{eventName});
        boolean exists = cursor.getCount() > 0;

        cursor.close();
        
        return exists;
    }

    @SuppressLint("Range")
    public Events getEventById(String eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Events events = null;

        String query = "SELECT * FROM events WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{eventId});

        // Check if cursor is not null and has results
        if (cursor != null) {
            Log.d("DatabaseHelper", "Query executed: " + query + " with ID: " + eventId);

            if (cursor.moveToFirst()) {
                // Retrieve each field using the correct column names
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id")); // Get the ID
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                long startTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow("start_time"));
                long endTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow("end_time"));
                int color = cursor.getInt(cursor.getColumnIndexOrThrow("color"));
                int dayOfWeek = cursor.getInt(cursor.getColumnIndexOrThrow("day_of_week"));
                String googleEventId = cursor.getString(cursor.getColumnIndexOrThrow("google_id"));

                // Convert start and end times from milliseconds to Date objects
                Date startDate = new Date(startTimeMillis);
                Date endDate = new Date(endTimeMillis);

                // Create the Events object with the ID
                events = new Events(id, name, description, location, startDate, endDate, false, color, dayOfWeek, googleEventId); // Include ID

                Log.d("DatabaseHelper", "Events found: " + events.toString());
            } else {
                Log.e("DatabaseHelper", "No events found for ID: " + eventId);
            }

            cursor.close();
        } else {
            Log.e("DatabaseHelper", "Cursor is null.");
        }

        
        return events;
    }

    // Update a specific instance of a weekly activity for a specific day of the week
    public boolean updateWeeklyActivityForDay(String eventId, int dayOfWeek, long newStartTime, long newEndTime, String newLocation, String newDescription, int newColor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_START_TIME, newStartTime);
        values.put(COLUMN_END_TIME, newEndTime);
        values.put(COLUMN_LOCATION, newLocation);
        values.put(COLUMN_DESCRIPTION, newDescription);
        values.put(COLUMN_COLOR, newColor);

        // Update the specific instance of the weekly activity
        int rowsAffected = db.update(TABLE_EVENTS, values, COLUMN_ID + "=? AND " + COLUMN_DAY_OF_WEEK + "=?",
                new String[]{eventId, String.valueOf(dayOfWeek)});
        

        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public List<Integer> getDaysOfWeekForEvent(String eventName) {
        List<Integer> daysOfWeek = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DAY_OF_WEEK + " FROM events WHERE name = ?", new String[]{eventName});

        if (cursor.moveToFirst()) {
            do {
                daysOfWeek.add(cursor.getInt(cursor.getColumnIndex(COLUMN_DAY_OF_WEEK)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        

        return daysOfWeek;
    }

    public List<String> getWeeklyDetails(String eventName) {
        List<String> weeklyDetails = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        // Define your database query (use the correct column names from your database)
        String query = "SELECT " + COLUMN_NAME + ", "
                + COLUMN_DESCRIPTION + ", "
                + COLUMN_LOCATION + ", "
                + COLUMN_COLOR
                + " FROM " + TABLE_EVENTS
                + " WHERE " + COLUMN_NAME + " = ?";

        try {
            // Open the database in read mode
            db = this.getReadableDatabase();

            // Execute the query and get a cursor to iterate over the result set
            cursor = db.rawQuery(query, new String[]{eventName});

            // Check if there are results and move the cursor to the first result
            if (cursor.moveToFirst()) {
                // Retrieve each detail and add it to the list
                String activityName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
                int colorInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COLOR));


                // Add details to the list
                weeklyDetails.add(activityName);
                weeklyDetails.add(description);
                weeklyDetails.add(location);
                weeklyDetails.add(String.valueOf(colorInt));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the cursor and database to avoid memory leaks
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                
            }
        }

        // Return the list containing the event details
        return weeklyDetails;
    }

    public List<Date> getWeeklyTime(String eventName) {
        List<Date> timeDetails = new ArrayList<>();  // List to store startTime and endTime as Date objects
        SQLiteDatabase db = null;
        Cursor cursor = null;

        // Define your database query to fetch start and end time for a specific event
        String query = "SELECT " + COLUMN_START_TIME + ", " + COLUMN_END_TIME
                + " FROM " + TABLE_EVENTS
                + " WHERE " + COLUMN_NAME + " = ?";  // Using event name as the filter

        try {
            // Open the database in read mode
            db = this.getReadableDatabase();

            // Execute the query and get a cursor to iterate over the result set
            cursor = db.rawQuery(query, new String[]{eventName});

            // Check if there are results and move the cursor to the first result
            if (cursor != null && cursor.moveToFirst()) {
                // Loop through all rows in the cursor
                do {
                    // Retrieve the start and end time as Date objects
                    long startTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_TIME));
                    long endTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_TIME));

                    // Convert the long values (milliseconds) into Date objects
                    Date startTime = new Date(startTimeMillis);
                    Date endTime = new Date(endTimeMillis);

                    // Add the start and end time to the list
                    timeDetails.add(startTime);
                    timeDetails.add(endTime);
                } while (cursor.moveToNext());  // Continue to the next row if available
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the cursor and database to avoid memory leaks
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                
            }
        }

        // Return the list containing the start and end times as Date objects
        return timeDetails;
    }

    public boolean updateWeeklyActivityForWeek(String name, int dayOfWeek, long startTime, long endTime, String location, String description, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_START_TIME, startTime);
        contentValues.put(COLUMN_END_TIME, endTime);
        contentValues.put(COLUMN_LOCATION, location);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_COLOR, color);

        // Debugging logs
        Log.d("UpdateDebug", "Updating activity - Name: " + name + ", Day: " + dayOfWeek +
                ", Start: " + startTime + ", End: " + endTime + ", Location: " + location +
                ", Description: " + description + ", Color: " + color);

        // Perform the update
        int rowsAffected = db.update(TABLE_EVENTS, contentValues, COLUMN_NAME + " = ? AND " + COLUMN_DAY_OF_WEEK + " = ?",
                new String[]{name, String.valueOf(dayOfWeek)});

        // Log the result
        Log.d("UpdateDebug", "Rows affected: " + rowsAffected);

        
        return rowsAffected > 0;
    }

    @SuppressLint("Range")
    public long getEventStartTime(String name, int dayOfWeek) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{COLUMN_START_TIME},
                COLUMN_NAME + " = ? AND " + COLUMN_DAY_OF_WEEK + " = ?",
                new String[]{name, String.valueOf(dayOfWeek)}, null, null, null);

        long startTime = -1;
        if (cursor.moveToFirst()) {
            startTime = cursor.getLong(cursor.getColumnIndex(COLUMN_START_TIME));
        }
        cursor.close();
        return startTime;
    }

    @SuppressLint("Range")
    public long getEventEndTime(String name, int dayOfWeek) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, new String[]{COLUMN_END_TIME},
                COLUMN_NAME + " = ? AND " + COLUMN_DAY_OF_WEEK + " = ?",
                new String[]{name, String.valueOf(dayOfWeek)}, null, null, null);

        long endTime = -1;
        if (cursor.moveToFirst()) {
            endTime = cursor.getLong(cursor.getColumnIndex(COLUMN_END_TIME));
        }
        cursor.close();
        return endTime;
    }

    public boolean deleteEventInstancesForDay(String name, int dayOfWeek) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Log the incoming parameters
        Log.d("DeleteDebug", "Deleting instances for activity: " + name + ", Day: " + dayOfWeek);

        // Perform the delete operation
        int rowsDeleted = db.delete(TABLE_EVENTS,
                COLUMN_NAME + " = ? AND " + COLUMN_DAY_OF_WEEK + " = ?",
                new String[]{name, String.valueOf(dayOfWeek)});

        // Log the number of rows deleted
        Log.d("DeleteDebug", "Rows deleted: " + rowsDeleted);

        

        return rowsDeleted > 0;
    }

    public void deletePastEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        long currentTime = System.currentTimeMillis();
        db.delete(TABLE_EVENTS, COLUMN_END_TIME + " < ?", new String[]{String.valueOf(currentTime)});

    }

    public void updateEventWithGoogleEventId(Events event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_GOOGLE_ID, event.getGoogleEventId()); // Ensure the column is correct

        // Log the values being updated for debugging
        Log.d("DatabaseHelper", "Updating Google Event ID for Event: " + event.getName() + ", ID: " + event.getGoogleEventId());

        int rowsAffected = db.update(TABLE_EVENTS, values, COLUMN_ID + " = ?", new String[]{event.getId()});

        if (rowsAffected > 0) {
            Log.d("DatabaseHelper", "Event updated successfully in the database.");
        } else {
            Log.e("DatabaseHelper", "Failed to update event in the database. Event not found.");
        }
    }

    public List<String> getGoogleEventIdsForRecurringEvent(String eventName) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> googleEventIds = new ArrayList<>();
        Cursor cursor = db.query(TABLE_EVENTS,
                new String[]{COLUMN_GOOGLE_ID},
                COLUMN_NAME + "=?",
                new String[]{eventName},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String googleEventId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GOOGLE_ID));
                if (googleEventId != null && !googleEventId.isEmpty()) {
                    googleEventIds.add(googleEventId);
                }
            }
            cursor.close();
        }
        
        return googleEventIds;
    }

    public List<String> getGoogleEventIdsForEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> googleEventIds = new ArrayList<>();
        Cursor cursor = db.query(TABLE_EVENTS, // table name
                new String[]{COLUMN_GOOGLE_ID},   // columns to retrieve
                null,                             // no WHERE clause
                null,                             // no arguments for WHERE clause
                null,                             // no GROUP BY
                null,                             // no HAVING
                null);                            // no ORDER BY


        if (cursor != null) {
            while (cursor.moveToNext()) {
                String googleEventId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GOOGLE_ID));
                if (googleEventId != null && !googleEventId.isEmpty()) {
                    googleEventIds.add(googleEventId);
                }
            }
            cursor.close();
        }
        
        return googleEventIds;
    }



    public List<Events> getWeeklyEvents() {
        List<Events> weeklyEvents = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get all weekly events from the database
        String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COLUMN_IS_WEEKLY + " = 1";
        Cursor cursor = db.rawQuery(query, null);

        // Iterate through the results
        while (cursor.moveToNext()) {
            // Create an event from the cursor
            Events event = createEventFromCursor(cursor);
            // Add the event to the list of weekly events
            weeklyEvents.add(event);
        }

        // Close cursor and database
        cursor.close();
        

        return weeklyEvents;
    }


    public void clearAllEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EVENTS);
    }

    public List<Events> getAllEvents() {
        List<Events> allEvents = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get all events from the database
        String query = "SELECT * FROM " + TABLE_EVENTS;
        Cursor cursor = db.rawQuery(query, null);

        // Iterate through the results
        while (cursor.moveToNext()) {
            // Create an event from the cursor
            Events event = createEventFromCursor(cursor);
            // Add the event to the list of all events
            allEvents.add(event);
        }

        cursor.close();
        return allEvents;
    }

    public Events getLatestEvent() {
        SQLiteDatabase db = this.getReadableDatabase();
        Events event = null;

        // Query to get the latest event based on start_time
        String query = "SELECT * FROM " + TABLE_EVENTS + " WHERE start_time >= ? ORDER BY start_time ASC LIMIT 1";
        String currentTime = String.valueOf(System.currentTimeMillis());

        Cursor cursor = db.rawQuery(query, new String[]{currentTime});

        if (cursor.moveToFirst()) {
            // Extract all required fields from the cursor
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
            long startTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow("start_time"));
            long endTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow("end_time"));
            boolean isWeekly = cursor.getInt(cursor.getColumnIndexOrThrow("is_weekly")) == 1;
            int color = cursor.getInt(cursor.getColumnIndexOrThrow("color"));
            int dayOfWeek = cursor.getInt(cursor.getColumnIndexOrThrow("day_of_week"));
            String googleEventId = cursor.getString(cursor.getColumnIndexOrThrow("google_id"));

            // Create a new Events object
            event = new Events(
                    id,
                    name,
                    description,
                    location,
                    new Date(startTimeMillis),
                    new Date(endTimeMillis),
                    isWeekly,
                    color,
                    dayOfWeek,
                    googleEventId
            );
        }

        cursor.close();
        return event;
    }


}
