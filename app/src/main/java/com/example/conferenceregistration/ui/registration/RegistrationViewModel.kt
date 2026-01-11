package com.example.conferenceregistration.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conferenceregistration.data.local.entity.Participant
import com.example.conferenceregistration.data.repository.ParticipantRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Registration screen.
 * Manages UI state and handles business logic.
 */
class RegistrationViewModel(
    private val repository: ParticipantRepository
) : ViewModel() {

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    private val _registrationState = MutableLiveData<RegistrationState>(RegistrationState.Idle)
    val registrationState: LiveData<RegistrationState> = _registrationState

    private val _photoPath = MutableLiveData<String?>()
    val photoPath: LiveData<String?> = _photoPath

    fun setPhotoPath(path: String?) {
        _photoPath.value = path
    }

    fun registerParticipant(
        userId: Int,
        fullName: String,
        title: String,
        registrationType: Int
    ) {
        if (userId <= 0) {
            _registrationState.value = RegistrationState.Error("Please enter a valid User ID")
            return
        }
        if (fullName.isBlank()) {
            _registrationState.value = RegistrationState.Error("Please enter your full name")
            return
        }
        if (_photoPath.value == null) {
            _registrationState.value = RegistrationState.Error("Please take a profile photo")
            return
        }

        _registrationState.value = RegistrationState.Loading

        viewModelScope.launch {
            try {
                if (repository.isUserIdTaken(userId)) {
                    _registrationState.value = RegistrationState.Error("User ID already exists")
                    return@launch
                }

                val participant = Participant(
                    userId = userId,
                    fullName = fullName,
                    title = title,
                    registrationType = registrationType,
                    photoPath = _photoPath.value
                )
                repository.registerParticipant(participant)
                _registrationState.value = RegistrationState.Success
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(
                    e.message ?: "Registration failed"
                )
            }
        }
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
        _photoPath.value = null
    }
}
