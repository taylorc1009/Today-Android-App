package com.app.today;

import java.util.Date;

public class Event {
    private String title;
    private Date begin;
    private Date end;
    private boolean allDay;

    Event(String title, Date begin, Date end, boolean allDay) {
        this.title = title;
        this.begin = begin;
        this.end = end;
        this.allDay = !allDay;
    }

    String getTitle() { return title; }
    Date getBegin() { return begin; }
    Date getEnd() { return end; }
    boolean isAllDay() { return allDay; }

    @Override
    public String toString() {
        return "Event2{" +
                "title='" + title + '\'' +
                ", begin=" + begin +
                ", end=" + end +
                ", allDay=" + allDay +
                '}';
    }
}
