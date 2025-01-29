package com.app.easy_patient.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity (tableName = "OrientationDetail")
public class OrientationListModel implements Serializable {
    @PrimaryKey
    @NonNull
    private int id;
    private String title;
    private String patient_id;
    private String date;
    private String specialist;
    private String search_keywords;
    private String file;
    private String clinic_name;
    public boolean is_new = false;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getSearch_keywords() {
        return search_keywords;
    }

    public void setSearch_keywords(String search_keywords) {
        this.search_keywords = search_keywords;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public void setClinic_name(String clinic_name) {
        this.clinic_name = clinic_name;
    }
}
