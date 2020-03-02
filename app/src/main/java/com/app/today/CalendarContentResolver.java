package com.app.today;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarContentResolver {
    List<Event> getCalendar(Context context) {
        Cursor cursor = queryCalendar(context);
        List<Event> calendar = new ArrayList<>();
        try {
            assert cursor != null;
            while (cursor.moveToNext()) {
                Event event;
                if(cursor.getString(2) == null)
                    event = new Event(cursor.getString(0), getTimeString(Long.parseLong(cursor.getString(1))), null);
                else
                    event = new Event(cursor.getString(0), getTimeString(Long.parseLong(cursor.getString(1))), getTimeString(Long.parseLong(cursor.getString(2))));
                calendar.add(event);
                Log.i("! event: " + event.getTitle() + ", time in milliseconds = " + cursor.getString(1), DateUtilities.buildTime(0, 0, 0).getTimeInMillis() + " <= " + cursor.getString(1) + " <= " + DateUtilities.buildTime(23, 59, 59).getTimeInMillis() + " ?");
                cursor.moveToNext();
            }
        } catch(NullPointerException e) {
            //e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
        }
        getCalendars(context);
        return calendar;
    }
    private static String getTimeString(long milliSeconds) {
        String format = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    /*boolean compareDate(Calendar day) { // --> method to determine if a calendar event is happening on the current date
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        Log.i(" - DAY_OF_YEAR", today.get(Calendar.DAY_OF_YEAR) + ", " + day.get(Calendar.DAY_OF_YEAR));
        return today.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR);
    }*/
    private Cursor queryCalendar(Context context) {
        //used to get the start and end times of of today in milliseconds
        Calendar dayStart = DateUtilities.buildTime(0, 0, 0);
        Calendar dayEnd = DateUtilities.buildTime(23, 59, 59);
        String WHERE = "dtstart >= " + dayStart.getTimeInMillis() + " AND dtstart <= " + dayEnd.getTimeInMillis();
        return context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), new String[] { "title", "dtstart", "dtend" }, WHERE, null, "dtstart ASC");
    }

    /*private static final String[] FIELDS = {
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE
    };
    private static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
    private ContentResolver contentResolver;
    //Set<String> calendars = new HashSet<String>();
    private void getCalendars(Context context) {
        contentResolver = context.getContentResolver();
        // Fetch a list of all calendars sync'd with the device and their display names
        Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null);
        try {
            assert cursor != null;
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    /*String name = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    // This is actually a better pattern:
                    String color = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
                    Boolean selected = !cursor.getString(3).equals("0");
                    calendars.add(displayName);
                    Log.i("/calendars fields", cursor.getString(2));
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace(); }
        finally {
            assert cursor != null;
            cursor.close(); }
        //return calendars;
    }*/
    private void getCalendars(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        // Fetch a list of all calendars synced with the device, their display names and whether the
        //(new String[] { "_id", "displayName", "selected"})
        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();

        try
        {
            System.out.println("Count="+cursor.getCount());
            if(cursor.getCount() > 0)
            {
                System.out.println("the control is just inside of the cursor.count loop");
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    Boolean selected = !cursor.getString(2).equals("0");

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
                    calendarIds.add(_id);
                }
            }
        }
        catch(AssertionError ex)
        {
            ex.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        // For each calendar, display all the events from the previous week to the end of next week.
        for (String id : calendarIds) {
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
            //Uri.Builder builder = Uri.parse("content://com.android.calendar/calendars").buildUpon();
            long now = new Date().getTime();

            ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 10000);
            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[]{"title", "begin", "end", "allDay"}, null, null, "startDay ASC, startMinute ASC");

            System.out.println("eventCursor count=" + eventCursor.getCount());
            if (eventCursor.getCount() > 0) {

                if (eventCursor.moveToFirst()) {
                    do {
                        Object mbeg_date, beg_date, beg_time, end_date, end_time;

                        final String title = eventCursor.getString(0);
                        final Date begin = new Date(eventCursor.getLong(1));
                        final Date end = new Date(eventCursor.getLong(2));
                        final Boolean allDay = !eventCursor.getString(3).equals("0");

                        /*  System.out.println("Title: " + title + " Begin: " + begin + " End: " + end +
                                    " All Day: " + allDay);

                        System.out.println("Title:" + title);
                        System.out.println("Begin:" + begin);
                        System.out.println("End:" + end);
                        System.out.println("All Day:" + allDay);*/
                        Log.i("/instances when", title + ", " + begin + ", " + end + ", " + allDay);

                    } while(eventCursor.moveToNext());
                }
            }
        }
    }
}