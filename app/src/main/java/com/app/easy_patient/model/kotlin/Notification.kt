package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val id: Int,
    val title: String,
    val name: String,
    val desc: String,
    val suggestedMedicines: List<SuggestedMedicine>? = null): Parcelable