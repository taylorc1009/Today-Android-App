package com.app.today.BusinessLayer;

import java.util.Date;

public class Event {
    //Calendar event, holds the information to show the user

    private String title;
    private String description;
    private String duration;
    /*private Date begin;
    private Date end;
    private boolean allDay;*/

    Event(String title, String description, String duration) { // Date begin, Date end, boolean allDay
        this.title = title;
        this.description = description;
        this.duration = duration;
        /*this.begin = begin;
        this.end = end;
        this.allDay = !allDay; //Inverted here because it's inverted in the calendar, for some reason */
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDuration() { return duration; }
    /*public Date getBegin() { return begin; }
    public Date getEnd() { return end; }
    public boolean isAllDay() { return allDay; }*/
}
