package com.app.today.BusinessLayer;

import java.util.Date;

public class Event {
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

    public String getTitle() { return title; }
    public Date getBegin() { return begin; }
    public Date getEnd() { return end; }
    public boolean isAllDay() { return allDay; }
}
