package com.app.easy_patient.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.easy_patient.database.OrientationListModel
import java.util.ArrayList

@Dao
interface OrientationDetailDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrientationDetail(appointmentDetail: ArrayList<OrientationListModel?>?)

    @get:Query("SELECT * FROM OrientationDetail")
    val orientationDetailData: List<OrientationListModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrientationItem(orientationItem: OrientationListModel)

    @Query("DELETE FROM OrientationDetail WHERE id = :id")
    fun deleteOrientationItem(id: Int): Int

    @Query("SELECT * FROM OrientationDetail WHERE id= :id")
    fun getOrientationById(id: String): OrientationListModel?
}