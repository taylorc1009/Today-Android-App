package com.app.today;

import android.icu.util.Calendar;

class DateUtilities {
    static Calendar buildTime(int hour, int minute, int second, int millis) {
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(System.currentTimeMillis());
        day.set(Calendar.HOUR_OF_DAY, hour);
        day.set(Calendar.MINUTE, minute);
        day.set(Calendar.SECOND, second);
        day.set(Calendar.MILLISECOND, millis);
        return day;
    }
}