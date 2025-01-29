package com.app.easy_patient.model;

import java.util.ArrayList;

public class PostMedicineResponseModel {
    private String start_time;
    private String picture_path = null;
    private String picture_link = null;
    private String created_at;
    private String updated_at = null;
    private String deleted_at = null;
    private Integer id;
    private float fk_id_user;
    private String name;
    private String dosage;
    private String number_of_days;
    private String frequency;
    ArrayList< Integer > days_of_the_week = new ArrayList <> ();
    private String st_notification;
    private String st_critical;
    private String st_type = null;

    public ArrayList<Integer> getDays_of_the_week() {
        return days_of_the_week;
    }

    public void setDays_of_the_week(ArrayList<Integer> days_of_the_week) {
        this.days_of_the_week = days_of_the_week;
    }

    // Getter Methods

    public String getStart_time() {
        return start_time;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public String getPicture_link() {
        return picture_link;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public Integer getId() {
        return id;
    }

    public float getFk_id_user() {
        return fk_id_user;
    }

    public String getName() {
        return name;
    }

    public String getDosage() {
        return dosage;
    }

    public String getNumber_of_days() {
        return number_of_days;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getSt_notification() {
        return st_notification;
    }

    public String getSt_critical() {
        return st_critical;
    }

    public String getSt_type() {
        return st_type;
    }

    // Setter Methods

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }

    public void setPicture_link(String picture_link) {
        this.picture_link = picture_link;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFk_id_user(float fk_id_user) {
        this.fk_id_user = fk_id_user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setNumber_of_days(String number_of_days) {
        this.number_of_days = number_of_days;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setSt_notification(String st_notification) {
        this.st_notification = st_notification;
    }

    public void setSt_critical(String st_critical) {
        this.st_critical = st_critical;
    }

    public void setSt_type(String st_type) {
        this.st_type = st_type;
    }
}
