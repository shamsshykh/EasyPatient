
package com.app.easy_patient.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicinesResponseModel {

    private String startTime;
    private Object picturePath;
    private Object pictureLink;
    private String createdAt;
    private Object updatedAt;
    private Object deletedAt;
    private Integer id;
    private Integer fkIdUser;
    private String name;
    private String dosage;
    private String numberOfDays;
    private String frequency;
    private List<Object> daysOfTheWeek = null;
    private String stNotification;
    private String stCritical;
    private Object stType;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Object getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(Object picturePath) {
        this.picturePath = picturePath;
    }

    public Object getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(Object pictureLink) {
        this.pictureLink = pictureLink;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFkIdUser() {
        return fkIdUser;
    }

    public void setFkIdUser(Integer fkIdUser) {
        this.fkIdUser = fkIdUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(String numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<Object> getDaysOfTheWeek() {
        return daysOfTheWeek;
    }

    public void setDaysOfTheWeek(List<Object> daysOfTheWeek) {
        this.daysOfTheWeek = daysOfTheWeek;
    }

    public String getStNotification() {
        return stNotification;
    }

    public void setStNotification(String stNotification) {
        this.stNotification = stNotification;
    }

    public String getStCritical() {
        return stCritical;
    }

    public void setStCritical(String stCritical) {
        this.stCritical = stCritical;
    }

    public Object getStType() {
        return stType;
    }

    public void setStType(Object stType) {
        this.stType = stType;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
