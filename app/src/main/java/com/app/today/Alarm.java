package com.app.today;

import java.io.Serializable;
import java.util.List;

class Alarm implements Serializable {
    public String id;
    public List<Integer> days;
    public String label;
    public String time;

    Alarm () {

    }

    Alarm(String id, List<Integer> days, String label, String time) {
        this.id = id;
        this.days = days;
        this.label = label;
        this.time = time;
    }

    String getId() {
        return id;
    }

    List<Integer> getDays() {
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

    void setDays(List<Integer> days) {
        this.days = days;
    }

    void setLabel(String label) {
        this.label = label;
    }
    void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id='" + id + '\'' +
                ", days=" + days +
                ", label='" + label + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
