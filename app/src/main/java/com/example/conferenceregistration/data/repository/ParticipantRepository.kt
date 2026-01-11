package com.example.conferenceregistration.data.repository

import com.example.conferenceregistration.data.local.dao.ParticipantDao
import com.example.conferenceregistration.data.local.entity.Participant

/**
 * Repository class that abstracts data operations.
 * Acts as a single source of truth for participant data.
 */
class ParticipantRepository(private val participantDao: ParticipantDao) {

    suspend fun registerParticipant(participant: Participant) {
        participantDao.insertParticipant(participant)
    }

    suspend fun findParticipantById(userId: Int): Participant? {
        return participantDao.getParticipantById(userId)
    }

    suspend fun isUserIdTaken(userId: Int): Boolean {
        return participantDao.isUserIdExists(userId)
    }
}
