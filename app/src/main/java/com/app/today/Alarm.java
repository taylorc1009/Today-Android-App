package com.app.today;

import java.io.Serializable;

class Alarm implements Serializable {
    //Used to store the alarms data for/from the database
    //Alarm doesn't need to store the time in millis as the AlarmManager does this,
    //so I'm only going to store the data needed for retrieving the alarm and display data

    //Attributes must be public for Firebase serialization
    public String id;
    public String days;
    public String label;
    public String time;

    Alarm() {} //Firebase serializer requires a non-parameter constructor

    Alarm(String id, String days, String label, String time) {
        this.id = id;
        this.days = days;
        this.label = label;
        this.time = time;
    }

    String getId() {
        return id;
    }
    String getDays() {
        return days;
    }
    String getLabel() {
        return label;
    }
    String getTime() {
        return time;
    }
}
