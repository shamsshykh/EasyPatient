package com.app.easy_patient.model;

public class ImageUploadModel {
    private boolean upload;
    private String mimetype;
    private String location;
    private String key;
    private String etag;


    // Getter Methods

    public boolean getUpload() {
        return upload;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getLocation() {
        return location;
    }

    public String getKey() {
        return key;
    }

    public String getEtag() {
        return etag;
    }

    // Setter Methods

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
