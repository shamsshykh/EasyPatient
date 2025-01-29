package com.app.easy_patient.model;

public class ResetPasswordModel {
    private boolean changed;
    private boolean found;
    private boolean expired;


    // Getter Methods

    public boolean getChanged() {
        return changed;
    }

    public boolean getFound() {
        return found;
    }

    public boolean getExpired() {
        return expired;
    }

    // Setter Methods

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
