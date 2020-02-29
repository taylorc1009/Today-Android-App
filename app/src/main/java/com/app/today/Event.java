package com.app.today;

import android.icu.util.Calendar;

class Event {
    private String title;
    //private Calendar startDate;
    private String startTimeStr;
    private String endTimeStr;

    Event(String title, String startTimeStr, String endTimeStr) {
        this.title = title;
        //this.startDate = startDate;
        this.startTimeStr = startTimeStr;
        this.endTimeStr = endTimeStr;
    }

    String getTitle() { return title; }
    //Calendar getStartDate() { return startDate; }
    String getStartTimeStr() { return startTimeStr; }
    String getEndTimeStr() { return endTimeStr; }
}
