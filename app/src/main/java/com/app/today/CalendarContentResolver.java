package com.app.today;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarContentResolver {
    //public CalendarContentResolver(Context context) { getCalendar(context); }
    List<Event> getCalendar(Context context) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), new String[] { "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null, null, null);

        /*nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();*/
        List<Event> calendar = new ArrayList<>();
        try {
            assert cursor != null;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                //Add a condition to display end time if it doesn't equal null

                /*nameOfEvent.add(cursor.getString(1));
                startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                //endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                descriptions.add(cursor.getString(2));*/

                Event event = new Event(cursor.getString(1), getDate(Long.parseLong(cursor.getString(3))), "", cursor.getString(2));
                Log.i("new event", event.getTitle() + ", " + event.getStartDate());
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
    private static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("d/M HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    boolean compareDate(String date) { // --> method to determine if a calendar event is happening on the current date
        /*java.util.Calendar validationDate = java.util.Calendar.getInstance();
        validationDate.setTime(new Date());
        validationDate.set(*/
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = new Date();
        //date = sdf.format(date);
        try {
            strDate = sdf.parse(date); //unparsable date (date and time is included)
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date().after(strDate);
    }
}