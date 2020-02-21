package com.app.today;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
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
        catch(NullPointerException e) { Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();}
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        try {
            while (cursor.moveToNext()) {
                nameOfEvent.add(cursor.getString(1));
                startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                //endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                descriptions.add(cursor.getString(2));
                cursor.moveToNext();
            }
            if (compareDate(startDates.get(0)))
                event1Txt.setText("Coming up soon: " + nameOfEvent.get(0) + ", " + startDates.get(0));
            else
                event1Txt.setText(nameOfEvent.get(0) + ", " + startDates.get(0));
            if (compareDate(startDates.get(1)))
                event2Txt.setText("Coming up soon: " + nameOfEvent.get(1) + ", " + startDates.get(1));
            else
                event2Txt.setText(nameOfEvent.get(1) + ", " + startDates.get(1));
            if (compareDate(startDates.get(2)))
                event3Txt.setText("Coming up soon: " + nameOfEvent.get(2) + ", " + startDates.get(2));
            else
                event3Txt.setText(nameOfEvent.get(2) + ", " + startDates.get(2));
        } catch(NullPointerException e) {
            //// Hide table if null
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
    private static boolean compareDate(String date) { // --> method to determine if a calendar event is happening on the current date
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