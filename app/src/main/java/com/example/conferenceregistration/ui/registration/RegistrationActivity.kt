package com.example.conferenceregistration.ui.registration

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.conferenceregistration.ConferenceApplication
import com.example.conferenceregistration.R
import com.example.conferenceregistration.databinding.ActivityRegistrationBinding
import com.example.conferenceregistration.util.Constants
import com.example.conferenceregistration.util.ViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Registration Activity - Module A.
 * Handles participant registration with camera capture.
 */
class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var viewModel: RegistrationViewModel

    private var currentPhotoPath: String? = null
    private lateinit var photoUri: Uri

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, R.string.error_camera_permission, Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.setPhotoPath(currentPhotoPath)
            binding.ivProfilePhoto.setImageURI(photoUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupUI()
        observeViewModel()
    }

    private fun setupViewModel() {
        val app = application as ConferenceApplication
        val factory = ViewModelFactory(app.repository)
        viewModel = ViewModelProvider(this, factory)[RegistrationViewModel::class.java]
    }

    private fun setupUI() {
        val titleAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Constants.TITLE_OPTIONS
        )
        titleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTitle.adapter = titleAdapter

        binding.btnConferenceInfo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.CONFERENCE_URL))
            startActivity(intent)
        }

        binding.ivProfilePhoto.setOnClickListener {
            checkCameraPermissionAndCapture()
        }

        binding.cardPhoto.setOnClickListener {
            checkCameraPermissionAndCapture()
        }

        binding.btnRegister.setOnClickListener {
            registerParticipant()
        }
    }

    private fun checkCameraPermissionAndCapture() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        try {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(photoUri)
        } catch (e: Exception) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun registerParticipant() {
        val userId = binding.etUserId.text.toString().toIntOrNull() ?: 0
        val fullName = binding.etFullName.text.toString()
        val title = binding.spinnerTitle.selectedItem.toString()
        val registrationType = when (binding.rgRegistrationType.checkedRadioButtonId) {
            R.id.rbFull -> Constants.REGISTRATION_TYPE_FULL
            R.id.rbStudent -> Constants.REGISTRATION_TYPE_STUDENT
            R.id.rbNone -> Constants.REGISTRATION_TYPE_NONE
            else -> Constants.REGISTRATION_TYPE_NONE
        }

        viewModel.registerParticipant(userId, fullName, title, registrationType)
    }

    private fun observeViewModel() {
        viewModel.registrationState.observe(this) { state ->
            when (state) {
                is RegistrationViewModel.RegistrationState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is RegistrationViewModel.RegistrationState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, R.string.success_registration, Toast.LENGTH_SHORT).show()
                    clearForm()
                }
                is RegistrationViewModel.RegistrationState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }

    private fun clearForm() {
        binding.etUserId.text?.clear()
        binding.etFullName.text?.clear()
        binding.spinnerTitle.setSelection(0)
        binding.rgRegistrationType.check(R.id.rbFull)
        binding.ivProfilePhoto.setImageResource(R.drawable.ic_camera_placeholder)
        viewModel.resetState()
    }
}
