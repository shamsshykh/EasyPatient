package com.app.easy_patient.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity (tableName = "AppointmentDetail")
public class AppointmentsListModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String type;
    private String clinic;
    private String date;
    private String specialist;
    private String address;
    private String latitude;
    private String longitude;
    private String whatsapp;
    private String phone_prefix;
    private String phone;
    private String clinic_logo;
    private String email;
    private String notify_at;

    public String getWhatsapp_value() {
        return whatsapp_value;
    }

    public void setWhatsapp_value(String whatsapp_value) {
        this.whatsapp_value = whatsapp_value;
    }

    private String whatsapp_value;

    public String getClinic_logo() {
        return clinic_logo;
    }

    public void setClinic_logo(String clinic_logo) {
        this.clinic_logo = clinic_logo;
    }

    private Integer schedule_status_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getPhone_prefix() {
        return phone_prefix;
    }

    public void setPhone_prefix(String phone_prefix) {
        this.phone_prefix = phone_prefix;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSchedule_status_id() {
        return schedule_status_id;
    }

    public void setSchedule_status_id(Integer schedule_status_id) {
        this.schedule_status_id = schedule_status_id;
    }

    public String getNotify_at() {
        return notify_at;
    }

    public void setNotify_at(String notifyAt) {
        this.notify_at = notify_at;
    }
}
