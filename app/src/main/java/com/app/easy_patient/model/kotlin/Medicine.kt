package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.easy_patient.ktx.stringToDate
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.*

@Parcelize
@Entity(tableName = "Medicine")
data class Medicine(
    @PrimaryKey(autoGenerate = false)
    @Expose val id: Int,
    @Expose var name: String?,
    @Expose var dosage: String?,
    @Expose var start_time: String?,
    @Expose var next_alarm_time: String?,
    @Expose var number_of_days: Int?,
    @Expose var frequency: Int?,
    @Expose var days_of_the_week: String?,
    @Expose var st_notification: @RawValue Any?,
    @Expose var st_critical: @RawValue Any?,
    @Expose var st_type: String?,
    @Expose var picture_path: String?,
    @Expose var picture_link: String?,
    @Expose var default_icon: String?,
    @Expose var created_at: String?
): Parcelable

fun Medicine.isNew(): Boolean {
    val calendar = Calendar.getInstance()
    start_time?.let {
        calendar.time = it.stringToDate()
        calendar.add(Calendar.DAY_OF_YEAR, number_of_days ?: 0)
    }
    return calendar.timeInMillis > Calendar.getInstance().timeInMillis
}

fun Medicine.medicineReminderId(timeInMillis: Long): String {
    return id.toString() + "_" + timeInMillis
}