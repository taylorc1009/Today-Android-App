package com.app.today;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CalendarContentResolver {
    //private Calendar today = Calendar.getInstance();
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
                Log.i("! event found", event.getTitle() + ", time in milliseconds = " + cursor.getString(2));
                cursor.moveToNext();
            }
        } catch(NullPointerException e) {
            //e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
        }
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
        today.setTimeInMillis(System.currentTimeMillis());
        Log.i(" - DAY_OF_YEAR", today.get(Calendar.DAY_OF_YEAR) + ", " + day.get(Calendar.DAY_OF_YEAR));
        return today.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR);
    }*/
    private Cursor queryCalendar(Context context) {
        //used to get the start and end times of of today in milliseconds
        Calendar dayStart = DateUtils.buildTime(0, 0, 0);
        Calendar dayEnd = DateUtils.buildTime(23, 59, 59);
        String WHERE = "dtstart >= '" + dayStart.getTimeInMillis() + "' AND dtstart <= '" + dayEnd.getTimeInMillis() + "'";
        return context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), new String[] { "title", "dtstart", "dtend" }, WHERE, null, "dtstart DESC");
    }
}