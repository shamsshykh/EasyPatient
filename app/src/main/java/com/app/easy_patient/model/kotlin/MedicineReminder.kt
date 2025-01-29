package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type


@Parcelize
@Entity(tableName = "MedicineReminder")
data class MedicineReminder(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    val medicineId: Int,
    var time: Long,
    val name: String,
    val dosage: String,
    val notification: Boolean,
    var status: MedicineStatus,
    var pictureLink: String?,
    var defaultIcon: Int = 1,
    var duration: Int,
    val critical: Boolean,
    var reminderType: String = "pending",
    var eventId: String? = null,
    var iosNotificationId: String? = null,
    var updatedTime: Long? = null
): Parcelable {
    fun reminderTime(): Long {
        return updatedTime?.let { updatedTime } ?: time
    }

    fun setUpdateTime(time: Long) {
        updatedTime = time
    }

    fun notificationId(): Int {
        val time = reminderTime().toString().subSequence(3, 8)
        return "$medicineId$time".toInt()
    }
}

@JsonAdapter(MedicineStatus.Serializer::class)
enum class MedicineStatus(val value: Int) {
    Missed(value = 1),
    Taken(value = 2),
    UpComing(value = 3),
    Future(value = 4);

    internal class Serializer : JsonSerializer<MedicineStatus>, JsonDeserializer<MedicineStatus> {
        override fun serialize(
            src: MedicineStatus,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return context.serialize(src.value)
        }

        private fun getMedicineStatusByValue(newVal: Int): MedicineStatus {
            for (medStatus in values()) if (medStatus.value == newVal) return medStatus
            return Missed
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext?
        ): MedicineStatus {
            return try {
                getMedicineStatusByValue(json.asNumber.toInt())
            } catch (e: JsonParseException) {
                return Missed
            }
        }
    }
}
