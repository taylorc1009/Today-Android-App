package com.app.today;

import java.io.Serializable;
import java.util.List;

class Alarm implements Serializable {
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
    void setId(String id) {
        this.id = id;
    }
    void setDays(String days) {
        this.days = days;
    }
    void setLabel(String label) {
        this.label = label;
    }
    void setTime(String time) {
        this.time = time;
    }
}
