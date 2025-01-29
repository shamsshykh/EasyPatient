package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "AudioFile")
data class AudioFile(
    @PrimaryKey(autoGenerate = false)
    @Expose val id: Int?,
    @Expose val file_name: String?,
    @Expose val duration: String?,
    @Expose val file: String?,
    @Expose val date: String?,
    @Expose val is_new: Boolean = false,
    var isSelected: Boolean = false
    ) : Parcelable
