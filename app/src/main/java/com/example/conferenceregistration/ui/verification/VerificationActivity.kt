package com.example.conferenceregistration.ui.verification

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.conferenceregistration.ConferenceApplication
import com.example.conferenceregistration.R
import com.example.conferenceregistration.data.local.entity.Participant
import com.example.conferenceregistration.databinding.ActivityVerificationBinding
import com.example.conferenceregistration.util.RegistrationType
import com.example.conferenceregistration.util.ViewModelFactory
import java.io.File

/**
 * Verification Activity - Module B.
 * Handles participant verification and displays results with color-coded backgrounds.
 */
class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding
    private lateinit var viewModel: VerificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupViewModel() {
        val app = application as ConferenceApplication
        val factory = ViewModelFactory(app.repository)
        viewModel = ViewModelProvider(this, factory)[VerificationViewModel::class.java]
    }

    private fun setupUI() {
        binding.btnVerify.setOnClickListener {
            val userId = binding.etSearchUserId.text.toString()
            viewModel.verifyParticipant(userId)
        }

        binding.cardResult.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.verificationState.observe(this) { state ->
            when (state) {
                is VerificationViewModel.VerificationState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnVerify.isEnabled = false
                }
                is VerificationViewModel.VerificationState.Found -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.isEnabled = true
                    showFoundResult(state.participant)
                }
                is VerificationViewModel.VerificationState.NotFound -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.isEnabled = true
                    showNotFoundResult()
                }
                is VerificationViewModel.VerificationState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnVerify.isEnabled = true
                }
            }
        }
    }

    private fun showFoundResult(participant: Participant) {
        binding.cardResult.visibility = View.VISIBLE

        binding.tvResultName.text = participant.fullName
        binding.tvResultTitle.text = participant.title
        binding.tvResultType.text = when (participant.registrationType) {
            1 -> getString(R.string.full_registration)
            2 -> getString(R.string.student_registration)
            3 -> getString(R.string.no_registration)
            else -> "Unknown"
        }

        participant.photoPath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(path)
                binding.ivResultPhoto.setImageBitmap(bitmap)
            }
        }

        val regType = RegistrationType.fromValue(participant.registrationType)
        val backgroundColor = ContextCompat.getColor(this, regType.colorResId)
        binding.cardResult.setCardBackgroundColor(backgroundColor)
        binding.mainLayout.setBackgroundColor(backgroundColor)
    }

    private fun showNotFoundResult() {
        binding.cardResult.visibility = View.VISIBLE

        binding.tvResultName.text = getString(R.string.user_not_found)
        binding.tvResultTitle.text = ""
        binding.tvResultType.text = getString(R.string.no_participant)
        binding.ivResultPhoto.setImageResource(R.drawable.ic_camera_placeholder)

        val redColor = ContextCompat.getColor(this, android.R.color.holo_red_light)
        binding.cardResult.setCardBackgroundColor(redColor)
        binding.mainLayout.setBackgroundColor(redColor)
    }
}
