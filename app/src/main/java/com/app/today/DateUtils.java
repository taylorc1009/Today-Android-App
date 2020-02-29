package com.app.today;

import android.icu.util.Calendar;

class DateUtils {
    static Calendar buildTime(int hour, int minute, int second) {
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(System.currentTimeMillis());
        day.set(Calendar.HOUR_OF_DAY, hour);
        day.set(Calendar.MINUTE, minute);
        day.set(Calendar.SECOND, second);
        return day;
    }
}