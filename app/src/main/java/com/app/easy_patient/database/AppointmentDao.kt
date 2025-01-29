package com.app.easy_patient.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.easy_patient.model.kotlin.Appointment

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAppointments(appointments: List<Appointment>)

    @get:Query("SELECT * FROM Appointment")
    val getAllAppointments: List<Appointment>

    @Query("SELECT * FROM Appointment WHERE id= :id")
    fun getAppointmentById(id: Int): Appointment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)
}
