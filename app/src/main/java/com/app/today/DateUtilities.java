package com.app.today;

import android.app.Activity;
import android.content.Context;
import android.icu.util.Calendar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        /*View view = context.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }*/
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}