package com.app.today;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarContentResolver {
    private Calendar today = Calendar.getInstance();
    List<Event> getCalendar(Context context) {
        Cursor cursor = queryCalendar(context);
        List<Event> calendar = new ArrayList<>();
        try {
            assert cursor != null;
            while (cursor.moveToNext()) {
                //Add a condition to display end time if it doesn't equal null

                Calendar day = Calendar.getInstance();
                day.setTimeInMillis(Long.parseLong(cursor.getString(3)));
                Event event = new Event(cursor.getString(1), day, getDateString(Long.parseLong(cursor.getString(3))));
                Log.i("new event", event.getTitle() + ", " + cursor.getString(3));
                calendar.add(event);
                cursor.moveToNext();
            }
        } catch(NullPointerException e) {
            //e.printStackTrace();
        } finally {
            assert cursor != null;
            cursor.close();
        }
        Log.i("!!! returned calendar", "get it?");
        return calendar;
    }
    private static String getDateString(long milliSeconds) {
        String format = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    boolean compareDate(Calendar day, String title) { // --> method to determine if a calendar event is happening on the current date
        today.setTime(new Date());
        Log.i("? day of year comparison of " + title, String.valueOf(today.get(Calendar.DAY_OF_YEAR)) + " == " + String.valueOf(day.get(Calendar.DAY_OF_YEAR)));
        return today.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR);
    }
    private Cursor queryCalendar(Context context) {
        Calendar dayStart = buildTime(0, 0, 0);
        Calendar dayEnd = buildTime(23, 59, 59);
        String instances = "instances/when/" + dayStart + "/" + dayEnd;
        return context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), new String[] { "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null, null, "dtstart DESC");
    }
    private Calendar buildTime(int hour, int minute, int second) {
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(System.currentTimeMillis());
        day.set(Calendar.HOUR_OF_DAY, hour);
        day.set(Calendar.MINUTE, minute);
        day.set(Calendar.SECOND, second);
        return day;
    }
}