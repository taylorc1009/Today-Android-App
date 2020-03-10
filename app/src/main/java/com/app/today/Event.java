package com.app.today;

import java.util.Date;

class Event {
    //Calendar event, holds the information to show the user

    private String title;
    private Date begin;
    private Date end;
    private boolean allDay;

    Event(String title, Date begin, Date end, boolean allDay) {
        this.title = title;
        this.begin = begin;
        this.end = end;
        this.allDay = !allDay; //Inverted because it's inverted in the calendar for some reason
    }

    String getTitle() { return title; }
    Date getBegin() { return begin; }
    Date getEnd() { return end; }
    boolean isAllDay() { return allDay; }
}
