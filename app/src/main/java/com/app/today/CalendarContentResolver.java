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

public class CalendarContentResolver {
    List<Event> getCalendar(Context context) {
        List<Event> events = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        HashSet<String> calendarIds = new HashSet<>();

        try {
            Cursor cursor = resolver.query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);
            assert cursor != null;
            if(cursor.getCount() > 0)
                while (cursor.moveToNext())
                    calendarIds.add(cursor.getString(0));
            cursor.close();
        } catch(AssertionError | Exception ex) {
            ex.printStackTrace();
            return null;
        }
        if(!calendarIds.isEmpty()) {
            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
            Calendar dayStart = DateUtilities.buildTime(0, 0, 0, 0);
            Calendar dayEnd = DateUtilities.buildTime(23, 59, 59, 999);
            ContentUris.appendId(builder, dayStart.getTimeInMillis());
            ContentUris.appendId(builder, dayEnd.getTimeInMillis());
            Cursor cursor = resolver.query(builder.build(), new String[]{ "title", "begin", "end", "allDay" }, null, null, "startDay ASC, startMinute ASC");
            assert cursor != null;
            for (String ignored : calendarIds) {
                if (cursor.getCount() > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            Event event = new Event(cursor.getString(0), new Date(cursor.getLong(1)), new Date(cursor.getLong(2)), cursor.getString(3).equals("0"));
                            Log.i("! event found", event.toString() + ", " + getTimeString(event.getBegin().getTime()));
                            events.add(event);
                        } while (cursor.moveToNext());
                    }
                }
            }
            cursor.close();
        }
        return events;
    }
    String getTimeString(long milliSeconds) {
        String format = "HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}