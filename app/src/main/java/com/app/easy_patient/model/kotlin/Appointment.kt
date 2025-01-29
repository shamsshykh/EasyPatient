package com.app.easy_patient.model.kotlin

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

/*{
        "id": 10470,
        "schedule_status_id": 2,
        "schedule_status_name": "Agendamento confirmado",
        "type": "Teleconsulta",
        "clinic": "Normal",
        "clinic_logo": "https://easy-health.app/assets/images/avatar/avatar-18.svg",
        "type_image": "avatar",
        "color_image": "lightblue",
        "date": "2021-11-22 22:30:00",
        "end_date": "2021-11-22 23:00:00",
        "weekday_name": "Monday",
        "specialist": "Pro",
        "address": {
            "street": "Rua Sao Nicolau",
            "number": "877",
            "neighborhood": "Santa Maria Goretti",
            "complement": "401",
            "city_name": "Porto Alegre",
            "zip_code": "91030-230",
            "uf": "RS"
        },
        "latitude": "-30.006051",
        "longitude": "-51.17688",
        "whatsapp": true,
        "whatsapp_value": "(48) 99632-3424",
        "phone_prefix": null,
        "phone": "(51) 3361-1302",
        "email": "cibelermallmann@gmail.com",
        "notify_at": "2021-11-21 22:30:00"
    }*/

@Parcelize
@Entity(tableName = "Appointment")
data class Appointment(
    @PrimaryKey(autoGenerate = false)
    @Expose val id: Int,
    @Expose val schedule_status_id: Int,
    @Expose val schedule_status_name: String?,
    @Expose val type: String?,
    @Expose val type_image: String?,
    @Expose val clinic: String?,
    @Expose var date: String?,
    @Expose val end_date: String?,
    @Expose val weekday_name: String?,
    @Expose val specialist: String?,
    @Expose val address: Address?,
    @Expose val latitude: String?,
    @Expose val longitude: String?,
    @Expose val whatsapp: Boolean?,
    @Expose val whatsapp_value: String?,
    @Expose val phone_prefix: String?,
    @Expose val phone: String?,
    @Expose val clinic_logo: String?,
    @Expose val email: String?,
    @Expose val notify_at: String?,
    @Expose val color_image: String?,
): Parcelable {
    var isToday: Boolean = false

    companion object {
        const val TWO_HOURS = "2 Hours before appointment notification"
        const val ONE_DAY = "1 Day before appointment notification"
    }
}