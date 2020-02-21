package com.app.today;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarContentResolver extends MainActivity {
    public CalendarContentResolver(Context context) { getCalendar(context); }
    private void getCalendar(Context context) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), new String[] { "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null, null, null);
        try { cursor.moveToFirst(); }
        catch(NullPointerException e) { Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show(); }
        /*nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();*/
        calendar.clear();
        try {
            String[] eventInfo = new String[3];
            while (cursor.moveToNext()) {
                /*nameOfEvent.add(cursor.getString(1));
                startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                //endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                descriptions.add(cursor.getString(2));*/
                eventInfo[0] = cursor.getString(1);
                eventInfo[1] = getDate(Long.parseLong(cursor.getString(3)));
                //endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                eventInfo[2] = cursor.getString(2);
                calendar.put(calendar.size(), eventInfo);
                cursor.moveToNext();
            }
        } catch(NullPointerException e) {

            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        finally { cursor.close(); }
    }
    private static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("d/M HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    protected static boolean compareDate(String date) { // --> method to determine if a calendar event is happening on the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = new Date();
        try {
            strDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (new Date().after(strDate)) //strDate.getTime())
            return true;
        return false;
    }
}