package com.example.conferenceregistration

import android.app.Application
import com.example.conferenceregistration.data.local.database.ConferenceDatabase
import com.example.conferenceregistration.data.repository.ParticipantRepository

/**
 * Application class for dependency initialization.
 * Provides database and repository instances.
 */
class ConferenceApplication : Application() {

    val database: ConferenceDatabase by lazy {
        ConferenceDatabase.getDatabase(this)
    }

    val repository: ParticipantRepository by lazy {
        ParticipantRepository(database.participantDao())
    }
}
