package com.app.today;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.HashSet;
import java.util.Set;

public class CalendarContentResolver {
    public static final String[] FIELDS = {
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE
    };
    public static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
    ContentResolver contentResolver;
    Set<String> calendars = new HashSet<String>();

    public CalendarContentResolver(Context context) {
        contentResolver = context.getContentResolver();
    }

    public Set<String> getCalendars() {
        Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                String name = cursor.getString(0);
                String displayName = cursor.getString(1);
                String color = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
                Boolean selected = !cursor.getString(3).equals("0");
                calendars.add(displayName);
            }
        } catch (AssertionError ex) {
        }
        return calendars;
    }
}