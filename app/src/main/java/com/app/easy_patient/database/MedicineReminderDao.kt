package com.app.easy_patient.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.easy_patient.model.kotlin.MedicineReminder
import com.app.easy_patient.model.kotlin.MedicineStatus
import androidx.room.Update




@Dao
interface MedicineReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicineReminder(medicineReminder: MedicineReminder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicineReminders(medicineReminder: List<MedicineReminder>)

    @Query("SELECT * FROM MedicineReminder")
    suspend fun medicineReminderList(): MutableList<MedicineReminder>

    @Query("SELECT * FROM MedicineReminder WHERE medicineId = :medicineId")
    fun medicineReminderList(medicineId: Int): List<MedicineReminder>

    @Query("SELECT * FROM MedicineReminder WHERE medicineId = :medicineId AND time >= :time ORDER BY time ASC LIMIT 24")
    fun medicineReminderListForNotification(medicineId: Int, time: Long): MutableList<MedicineReminder>

    @Query("DELETE FROM MedicineReminder WHERE medicineId = :medicineId")
    fun deleteAllMedicineReminders(medicineId: Int)

    @Query("UPDATE MedicineReminder SET status=:medicineStatus WHERE id = :medicineReminderId")
    fun updateStatus(medicineStatus: MedicineStatus, medicineReminderId: Int)

    @Query("UPDATE MedicineReminder SET time=:updatedTime WHERE id = :medicineReminderId")
    fun updateTime(updatedTime: Long, medicineReminderId: Int)

    @Update
    fun update(medicineReminder: MedicineReminder)

    @Query("SELECT * FROM MedicineReminder WHERE id = :reminderId")
    fun medicineReminderById(reminderId: String): MedicineReminder?

    @Query("UPDATE MedicineReminder SET name=:name, dosage=:dosage, notification=:notification, pictureLink=:pictureLink, defaultIcon=:defaultIcon, critical=:critical WHERE medicineId = :medicineId")
    fun updateMedicineReminderExceptTime(medicineId: Int,
                     name: String,
                     dosage: String,
                     notification: Boolean,
                     pictureLink: String?,
                     defaultIcon: Int,
                     critical: Boolean)
}