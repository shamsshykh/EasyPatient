package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class Status(
    @Expose val status: Boolean,
    @Expose val sent: Boolean,
) : Parcelable