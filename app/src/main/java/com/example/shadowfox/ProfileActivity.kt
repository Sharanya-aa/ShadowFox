package com.example.shadowfox

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shadowfox.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"

        sessionManager = SessionManager(this)
        val userDetails = sessionManager.getUserDetails()
        val email = userDetails[SessionManager.KEY_EMAIL]
        val currentDisplayName = userDetails[SessionManager.KEY_DISPLAY_NAME] ?: email?.substringBefore('@') ?: "User"
        val currentBio = userDetails[SessionManager.KEY_USER_BIO] ?: ""

        binding.textViewProfileTitle.text = "$currentDisplayName's Profile"
        binding.editTextDisplayName.setText(currentDisplayName)
        binding.textViewUserEmail.text = email ?: "Not available"
        binding.editTextBio.setText(currentBio)


        binding.buttonSaveProfile.setOnClickListener {
            val newDisplayName = binding.editTextDisplayName.text.toString().trim()
            val newBio = binding.editTextBio.text.toString().trim()

            if (newDisplayName.isEmpty()) {
                binding.editTextDisplayName.error = "Display name cannot be empty"
                return@setOnClickListener
            }

            sessionManager.updateUserProfile(newDisplayName, newBio)
            Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()

            // Set result to indicate profile was updated
            val resultIntent = Intent()
            resultIntent.putExtra("profile_updated", true)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}