package com.app.easy_patient.model.kotlin.converter

import androidx.room.TypeConverter
import com.app.easy_patient.model.kotlin.Address
import com.app.easy_patient.model.kotlin.AudioFile
import com.app.easy_patient.model.kotlin.MedicineStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converter {

    val gson = Gson()

    @TypeConverter
    fun addressToString(address: Address): String {
        return gson.toJson(address)
    }

    @TypeConverter
    fun stringToAddress(addressString: String): Address {
        val objectType = object : TypeToken<Address>() {}.type
        return gson.fromJson(addressString, objectType)
    }

    @TypeConverter
    fun audioFileToString(audioFile: List<AudioFile>): String {
        return gson.toJson(audioFile)
    }

    @TypeConverter
    fun stringToAudioFile(audioFileString: String): List<AudioFile> {
        val objectType = object : TypeToken<List<AudioFile>>() {}.type
        return gson.fromJson(audioFileString, objectType)
    }

    @TypeConverter
    fun restoreList(listOfString: String): ArrayList<Int> {
        return Gson().fromJson(listOfString, object : TypeToken<ArrayList<Int>>() {}.type)
    }

    @TypeConverter
    fun saveList(listOfString: ArrayList<Int>): String {
        return Gson().toJson(listOfString)
    }

    @TypeConverter
    fun restoreInt(obj: Any?): Int {
        if (obj is Int)
            return obj
        else if (obj is String && !obj.isNullOrEmpty())
            return obj.toInt()
        return 0
    }

    @TypeConverter
    fun saveInt(value: Int): Any? {
        return value
    }

    @TypeConverter
    fun toMedicineReminderStatus(value: Int) = enumValues<MedicineStatus>()[value]

    @TypeConverter
    fun fromMedicineReminderStatus(value: MedicineStatus) = value.ordinal
}