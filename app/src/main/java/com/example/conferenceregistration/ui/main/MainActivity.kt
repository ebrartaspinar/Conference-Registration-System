package com.example.conferenceregistration.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.conferenceregistration.databinding.ActivityMainBinding
import com.example.conferenceregistration.ui.registration.RegistrationActivity
import com.example.conferenceregistration.ui.verification.VerificationActivity

/**
 * Main Activity - Navigation hub for the application.
 * Provides access to Registration and Verification screens.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegistration.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        binding.btnVerification.setOnClickListener {
            startActivity(Intent(this, VerificationActivity::class.java))
        }
    }
}
