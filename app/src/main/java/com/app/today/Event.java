package com.app.today;

public class Event {
    private String title;
    private String startDate;
    private String endDate;
    private String description;

    public Event(String title, String startDate, String endDate, String description) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return title + ", " + startDate + ", " + endDate + ", " + description;
    }
}
