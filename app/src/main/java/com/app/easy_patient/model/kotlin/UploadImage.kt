package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadImage(
    @Expose val upload: Boolean,
    @Expose val mimetype: String,
    @Expose val location: String,
    @Expose val key: String,
    @Expose val etag: String
) : Parcelable