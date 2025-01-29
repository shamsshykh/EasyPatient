package com.app.easy_patient.database

import androidx.room.*
import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.model.kotlin.Medicine

@Dao
interface MedicineDetailDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMedicineDetail(medicineDetail: Medicine): Long

    @Update
    fun updateMedicineDetail(medicineDetail: Medicine)

    @get:Query("SELECT * FROM Medicine")
    val medicineDetailData: List<Medicine>

    @Query("SELECT * FROM Medicine WHERE id= :id")
    fun getMedicineDetail(id: Int): Medicine?

    @Query("UPDATE Medicine SET next_alarm_time= :alarmTime WHERE id = :id")
    fun updateAlarmTime(alarmTime: String, id: Int): Int

    @Delete
    fun deleteMedicineEntry(medicineDetail: Medicine)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMedicines(medicines: List<Medicine>)
}