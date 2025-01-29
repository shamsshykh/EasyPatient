package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuggestedMedicine(
    val id: Int,
    val name: String,
    val date: String,
    val daysOfWeek: String,
    val dosage: String
): Parcelable
