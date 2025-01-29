package com.app.easy_patient.model;

public class UserRegistrationModel {
    private boolean created;

    private boolean exists;

    public void setCreated(boolean created){
        this.created = created;
    }
    public boolean getCreated(){
        return this.created;
    }
    public void setExists(boolean exists){
        this.exists = exists;
    }
    public boolean getExists(){
        return this.exists;
    }
}
