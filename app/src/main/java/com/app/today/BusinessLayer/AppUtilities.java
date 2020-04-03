package com.app.today.BusinessLayer;

import android.app.Activity;
import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AppUtilities {
    //I was going to make a collection of date utilities for the calendar and alarms, but this was the only method I needed across both
    public static Calendar buildTime(int hour, int minute, int second, int millis) {
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

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch(NullPointerException e) {
            Log.e("? could not close keyboard", e.toString());
        }
    }
}