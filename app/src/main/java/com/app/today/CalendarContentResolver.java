package com.app.today;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.app.today.Event;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarContentResolver extends MainActivity {
    public CalendarContentResolver(Context context) { getCalendar(context); }
    private void getCalendar(Context context) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), new String[] { "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null, null, null);

        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        //calendar.clear();
        try {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                //Add a condition to display end times if they don't equal null

                nameOfEvent.add(cursor.getString(1));
                startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                //endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                descriptions.add(cursor.getString(2));

                /*Event event = new Event(cursor.getString(1), getDate(Long.parseLong(cursor.getString(3))), "", cursor.getString(2));
                Log.i("new event title", event.getTitle());
                calendar.add(event);*/
                cursor.moveToNext();
            }
        } catch(NullPointerException e) {
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        } finally { cursor.close(); }
    }
    private static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("d/M HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    protected static boolean compareDate(String date) { // --> method to determine if a calendar event is happening on the current date
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
        if (new Date().after(strDate))
            return true;
        return false;
    }
}