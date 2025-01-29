package com.app.easy_patient.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "PrescriptionDetail")
data class PrescriptionListModel(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var type: String?,
    var title: String?,
    var patient_id: String?,
    var date: String?,
    var specialist: String?,
    var validity_revenue: String?,
    var period_validity: String?,
    var file: String?,
    var search_keywords: String?,
    var clinic_name: String?,
    var is_new: Boolean
): Parcelable