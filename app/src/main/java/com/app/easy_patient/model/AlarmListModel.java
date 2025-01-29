package com.app.easy_patient.model;

import java.io.Serializable;

public class AlarmListModel implements Serializable {
    private int id;
    private String title;
    private String time;
    private String name;
    private String start_time;
    private String next_alarm_time;
    private int number_of_days;
    private int frequency;
    private String st_notification;
    private String st_critical;
    private boolean isDelete;
    private boolean isToday;
    private boolean isComplete;
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getNext_alarm_time() {
        return next_alarm_time;
    }

    public void setNext_alarm_time(String next_alarm_time) {
        this.next_alarm_time = next_alarm_time;
    }

    public int getNumber_of_days() {
        return number_of_days;
    }

    public void setNumber_of_days(int number_of_days) {
        this.number_of_days = number_of_days;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getSt_notification() {
        return st_notification;
    }

    public void setSt_notification(String st_notification) {
        this.st_notification = st_notification;
    }

    public String getSt_critical() {
        return st_critical;
    }

    public void setSt_critical(String st_critical) {
        this.st_critical = st_critical;
    }

}
