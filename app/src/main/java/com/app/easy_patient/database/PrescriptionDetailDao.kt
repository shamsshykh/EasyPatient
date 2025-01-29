package com.app.easy_patient.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PrescriptionDetailDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPrescriptionDetail(prescriptionDetail: List<PrescriptionListModel>)

    @get:Query("SELECT * FROM PrescriptionDetail")
    val prescriptionDetailData: List<PrescriptionListModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPrescriptionItem(prescriptionItem: PrescriptionListModel)

    @Query("DELETE FROM PrescriptionDetail WHERE id = :id")
    fun deleteItem(id: Int): Int

    @Query("SELECT * FROM PrescriptionDetail WHERE id= :id")
    fun getPrescriptionById(id: Int): PrescriptionListModel?
}