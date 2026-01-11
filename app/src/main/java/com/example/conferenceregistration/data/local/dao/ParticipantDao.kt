package com.example.conferenceregistration.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.conferenceregistration.data.local.entity.Participant

/**
 * Data Access Object for Participant entity.
 * Defines database operations using Room annotations.
 */
@Dao
interface ParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: Participant)

    @Query("SELECT * FROM participants WHERE userId = :userId")
    suspend fun getParticipantById(userId: Int): Participant?

    @Query("SELECT EXISTS(SELECT 1 FROM participants WHERE userId = :userId)")
    suspend fun isUserIdExists(userId: Int): Boolean

    @Query("SELECT * FROM participants")
    suspend fun getAllParticipants(): List<Participant>
}
