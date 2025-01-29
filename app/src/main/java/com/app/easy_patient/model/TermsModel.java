package com.app.easy_patient.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermsModel {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private Message message;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("endpoint")
    @Expose
    private String endpoint;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    public class Message {

        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

    }
}
