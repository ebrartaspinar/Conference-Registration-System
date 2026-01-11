package com.example.conferenceregistration.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.conferenceregistration.data.local.dao.ParticipantDao
import com.example.conferenceregistration.data.local.entity.Participant

/**
 * Room Database class.
 * Singleton pattern ensures only one database instance exists.
 */
@Database(
    entities = [Participant::class],
    version = 1,
    exportSchema = false
)
abstract class ConferenceDatabase : RoomDatabase() {

    abstract fun participantDao(): ParticipantDao

    companion object {
        @Volatile
        private var INSTANCE: ConferenceDatabase? = null

        fun getDatabase(context: Context): ConferenceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConferenceDatabase::class.java,
                    "conference_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
