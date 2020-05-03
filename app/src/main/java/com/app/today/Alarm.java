package com.app.today;

import java.io.Serializable;

public class Alarm implements Serializable {
    //Used to store the alarms data for/from the database
    //Alarm doesn't need to store the time in millis as the AlarmManager does this,
    //so I'm only going to store the data needed for retrieving the alarm and display data

    private String id;
    private String days;
    private String label;
    private String time;

    Alarm() {} //Firebase serializer requires a non-parameter constructor

    Alarm(String id, String days, String label, String time) {
        this.id = id;
        this.days = days;
        this.label = label;
        this.time = time;
    }

    //These must be public in order to allow Firebase to access the values
    public String getId() {
        return id;
    }
    public String getDays() {
        return days;
    }
    public String getLabel() {
        return label;
    }
    public String getTime() {
        return time;
    }
}
