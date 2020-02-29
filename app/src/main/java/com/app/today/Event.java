package com.app.today;

import android.icu.util.Calendar;

class Event {
    private String title;
    private Calendar startDate;
    private String startDateStr;
    private String endDateStr;
    private String description;

    Event(String title, String description, Calendar startDate, String startDateStr, String endDateStr) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.startDateStr = startDateStr;
        this.endDateStr = endDateStr;
    }
    Event(String title, Calendar startDate, String startDateStr) {
        this.title = title;
        this.startDate = startDate;
        this.startDateStr = startDateStr;
    }
    Event(String title, Calendar startDate, String startDateStr, String endDateStr) {
        this.title = title;
        this.startDate = startDate;
        this.startDateStr = startDateStr;
        this.endDateStr = endDateStr;
    }
    Event(String title, String description, Calendar startDate, String startDateStr) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.startDateStr = startDateStr;
    }

    String getTitle() { return title; }
    Calendar getStartDate() { return startDate; }
    String getStartDateStr() { return startDateStr; }
    String getEndDateStr() { return endDateStr; }
    String getDescription() { return description; }
}
