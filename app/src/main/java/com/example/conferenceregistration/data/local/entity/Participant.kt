package com.example.conferenceregistration.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a conference participant.
 * Maps directly to the 'participants' table in Room database.
 */
@Entity(tableName = "participants")
data class Participant(
    @PrimaryKey
    val userId: Int,
    val fullName: String,
    val title: String,
    val registrationType: Int,
    val photoPath: String?
)
