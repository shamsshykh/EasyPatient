package com.app.easy_patient.model;

public class UserDetailModel {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String username;
    private String language;
    private String name;
    private String birth_date;
    private String cpf = null;
    private String rg = null;
    private String telephone = null;
    private String zipcode = null;
    private String state_id;
    private String state_name;
    private String state_code;
    private String state_belongs_to_country_code;
    private String city_id;
    private String city_name;
    private String street = null;
    private String home_number = null;
    private String neighborhood = null;
    private String address_extra_details = null;
    private String picture;
    private String gender;
    private String device;
    private String created_at;


    // Getter Methods

    public String getUsername() {
        return username;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRg() {
        return rg;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getState_id() {
        return state_id;
    }

    public String getState_name() {
        return state_name;
    }

    public String getState_code() {
        return state_code;
    }

    public String getState_belongs_to_country_code() {
        return state_belongs_to_country_code;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getStreet() {
        return street;
    }

    public String getHome_number() {
        return home_number;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getAddress_extra_details() {
        return address_extra_details;
    }

    public String getPicture() {
        return picture;
    }

    public String getGender() {
        return gender;
    }

    public String getDevice() {
        return device;
    }

    public String getCreated_at() {
        return created_at;
    }

    // Setter Methods


    public void setUsername(String username) {
        this.username = username;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public void setState_belongs_to_country_code(String state_belongs_to_country_code) {
        this.state_belongs_to_country_code = state_belongs_to_country_code;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHome_number(String home_number) {
        this.home_number = home_number;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setAddress_extra_details(String address_extra_details) {
        this.address_extra_details = address_extra_details;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
