package com.app.easy_patient.model;

public class MedicinesModel {
    private String title;

    private int id;

    private String name;

    private String dosage;

    private String start_time;

    private int number_of_days;

    private int frequency;

    private String days_of_the_week;

    private String st_notification;

    private String st_critical;

    private String st_type;

    private String picture;

    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setDosage(String dosage){
        this.dosage = dosage;
    }
    public String getDosage(){
        return this.dosage;
    }
    public void setStart_time(String start_time){
        this.start_time = start_time;
    }
    public String getStart_time(){
        return this.start_time;
    }
    public void setNumber_of_days(int number_of_days){
        this.number_of_days = number_of_days;
    }
    public int getNumber_of_days(){
        return this.number_of_days;
    }
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }
    public int getFrequency(){
        return this.frequency;
    }
    public void setDays_of_the_week(String days_of_the_week){
        this.days_of_the_week = days_of_the_week;
    }
    public String getDays_of_the_week(){
        return this.days_of_the_week;
    }
    public void setSt_notification(String st_notification){
        this.st_notification = st_notification;
    }
    public String getSt_notification(){
        return this.st_notification;
    }
    public void setSt_critical(String st_critical){
        this.st_critical = st_critical;
    }
    public String getSt_critical(){
        return this.st_critical;
    }
    public void setSt_type(String st_type){
        this.st_type = st_type;
    }
    public String getSt_type(){
        return this.st_type;
    }
    public void setPicture(String picture){
        this.picture = picture;
    }
    public String getPicture(){
        return this.picture;
    }
}
