package com.app.today;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

class CalendarContentResolver {
    //Used to query the calendar and return the results
    static List<Event> getCalendar(Context context) {
        List<Event> events = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        HashSet<String> calendarIds = new HashSet<>(); //Using a HashSet to ensure no duplicate results

        //Try to get a list of calendar events
        //Otherwise return null to the UI thread so it knows this method failed
        //Should only happen if the user denied calendar permissions
        try {
            //Stores the query results in a cursor for us to iterate through
            Cursor cursor = resolver.query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);
            assert cursor != null;
            //Detects if the calendar even returned anything
            if(cursor.getCount() > 0)
                while (cursor.moveToNext())
                    //Adds the results of the query to the HashSet
                    calendarIds.add(cursor.getString(0));
            cursor.close();

            //If the query didn't return an empty result, continue
            //Else return the empty list so the UI thread knows there are none
            if(!calendarIds.isEmpty()) {
                //This resource identifier queries the events we previously got for instances we ask for, in this case I'm asking for today's events
                Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
                //Uses my buildTime method to create an instance of the specified times of today, start and end
                Calendar dayStart = AppUtilities.buildTime(0, 0, 0, 0);
                Calendar dayEnd = AppUtilities.buildTime(23, 59, 59, 999);
                //Appends the identifier, telling it we only want results in the time range specified
                ContentUris.appendId(builder, dayStart.getTimeInMillis());
                ContentUris.appendId(builder, dayEnd.getTimeInMillis());
                //Finally we query the calendar for instances, using the URI, for the fields "title", "begin", "end", and "allDay"
                //in ASC (oldest to newest) order
                cursor = resolver.query(builder.build(), new String[]{ "title", "description", "begin", "end", "allDay" }, null, null, "startDay ASC, startMinute ASC");

                //Now we iterate through the results and store them in a list to be returned
                assert cursor != null;
                for (String ignored : calendarIds) {
                    if (cursor.getCount() > 0) {
                        if (cursor.moveToFirst()) {
                            do {
                                String duration, description;

                                if((description = cursor.getString(1)).equals(""))
                                    description = "Sorry! This event has no description provided…";

                                if(!cursor.getString(4).equals("0"))
                                    duration = "ALL DAY";
                                else
                                    duration = getTimeString(new Date(cursor.getLong(2)).getTime()) + "-" + getTimeString(new Date(cursor.getLong(3)).getTime());

                                Event event = new Event(cursor.getString(0), description, duration);
                                events.add(event);

                                Log.i("! calendar event found", event.getTitle() + ", " + event.getDuration() + ", description: \"" + event.getDescription() + "\"");
                            } while (cursor.moveToNext());
                        }
                    }
                }
                cursor.close();
            }
            return events;
        } catch(AssertionError | Exception e ) {
            Log.e("? Calendar exception", e.toString());
            return null;
        }
    }

    //Converts the time of a calendar instance to a String value, used for displaying the time to the user
    private static String getTimeString(long milliSeconds) {
        //Defines the format we want
        String format = "HH:mm";
        //Applies it to our time formatter
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        //Create a time instance which the system understands for it to convert to a String
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        //Convert the time to a string and return it
        return formatter.format(calendar.getTime());
    }
}