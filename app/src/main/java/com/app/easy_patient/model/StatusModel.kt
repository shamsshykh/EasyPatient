package com.app.easy_patient.model

import com.google.gson.annotations.SerializedName

class StatusModel {
    var status = false
    @SerializedName("sent")
    var isSent = false
}