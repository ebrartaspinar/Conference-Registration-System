package com.example.conferenceregistration.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.conferenceregistration.data.repository.ParticipantRepository
import com.example.conferenceregistration.ui.registration.RegistrationViewModel
import com.example.conferenceregistration.ui.verification.VerificationViewModel

/**
 * Factory class to create ViewModels with dependencies.
 */
class ViewModelFactory(
    private val repository: ParticipantRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
                RegistrationViewModel(repository) as T
            }
            modelClass.isAssignableFrom(VerificationViewModel::class.java) -> {
                VerificationViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
