package com.app.easy_patient.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.easy_patient.model.kotlin.Audio

@Dao
interface AudioDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAudios(audios: List<Audio>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAudio(audios: Audio)

    @get:Query("SELECT * FROM Audio")
    val getAllAudios: List<Audio>

    @Query("SELECT * FROM Audio WHERE id= :id")
    fun getAudioById(id: Int): Audio?

    @Query("DELETE FROM Audio WHERE id = :id")
    fun deleteAudioDocument(id: Int)
}