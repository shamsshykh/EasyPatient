package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Audio")
data class Audio(
    @PrimaryKey(autoGenerate = false)
    @Expose val id: Int?,
    @Expose val receive_date: String?,
    @Expose val specialist: String?,
    @Expose val clinic_name: String?,
    @Expose val date_attendance: String?,
    @Expose val audio_files: List<AudioFile>?
    ) : Parcelable {
        var isNew = audio_files!![0].is_new
    }