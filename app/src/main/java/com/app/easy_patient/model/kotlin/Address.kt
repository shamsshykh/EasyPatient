package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "address")
data class Address(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Expose val street: String?,
    @Expose val number: String?,
    @Expose val neighborhood: String?,
    @Expose val complement: String?,
    @Expose val city_name: String?,
    @Expose val zip_code: String?,
    @Expose val uf: String,
) : Parcelable
