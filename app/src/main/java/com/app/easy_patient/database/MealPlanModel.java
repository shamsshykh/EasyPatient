package com.app.easy_patient.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Meal_Plan_Detail")
public class MealPlanModel implements Serializable {
    @PrimaryKey
    @NonNull
    private Integer id;
    private String title;
    private Integer patientId;
    private String date;
    private String specialist;
    private String file;
    private String search_keywords;
    private String clinic_name;
    public boolean is_new = false;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public String getTitle() {
        if (title == null)
            return "";
        else
            return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getDate() {
        if (date == null)
            return "";
        else
            return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSpecialist() {
        if (specialist == null)
            return "";
        else
            return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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
