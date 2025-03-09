package com.connectly_cm.Connectly_CM.sendMailUsingConnectedInboxAcc.dto;

import java.util.List;

public class Timewindow {
    private String start;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    private String end;
    private List<String> days;

    public boolean contains(String currentDay) {
        return days.contains(currentDay);
    }

    @Override
    public String toString() {
        return "Timewindow{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", days=" + (days != null ? days.toString() : "[]") +
                '}';
    }

}
