package com.example.conferenceregistration.ui.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conferenceregistration.data.local.entity.Participant
import com.example.conferenceregistration.data.repository.ParticipantRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Verification screen.
 * Handles participant lookup and verification logic.
 */
class VerificationViewModel(
    private val repository: ParticipantRepository
) : ViewModel() {

    sealed class VerificationState {
        object Idle : VerificationState()
        object Loading : VerificationState()
        data class Found(val participant: Participant) : VerificationState()
        object NotFound : VerificationState()
        data class Error(val message: String) : VerificationState()
    }

    private val _verificationState = MutableLiveData<VerificationState>(VerificationState.Idle)
    val verificationState: LiveData<VerificationState> = _verificationState

    fun verifyParticipant(userIdText: String) {
        val userId = userIdText.toIntOrNull()
        if (userId == null || userId <= 0) {
            _verificationState.value = VerificationState.Error("Please enter a valid User ID")
            return
        }

        _verificationState.value = VerificationState.Loading

        viewModelScope.launch {
            try {
                val participant = repository.findParticipantById(userId)
                if (participant != null) {
                    _verificationState.value = VerificationState.Found(participant)
                } else {
                    _verificationState.value = VerificationState.NotFound
                }
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error(
                    e.message ?: "Verification failed"
                )
            }
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
    }
}
