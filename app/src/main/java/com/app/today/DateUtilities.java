package com.app.today;

import android.icu.util.Calendar;

class DateUtilities {
    //I was going to make a collection of date utilities for the calendar and alarms, but this was the only method I needed across both
    static Calendar buildTime(int hour, int minute, int second, int millis) {
        //Create an instance of the exact current time in milliseconds
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(System.currentTimeMillis());

        //Alter said time using the parameters to build a time instance the system can use for calculations
        day.set(Calendar.HOUR_OF_DAY, hour);
        day.set(Calendar.MINUTE, minute);
        day.set(Calendar.SECOND, second);
        day.set(Calendar.MILLISECOND, millis);

        return day;
    }
}