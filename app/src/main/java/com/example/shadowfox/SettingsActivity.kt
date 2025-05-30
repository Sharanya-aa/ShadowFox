package com.example.shadowfox

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shadowfox.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val PREFS_NAME = "AppSettings"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val KEY_TEMP_UNIT_IS_CELSIUS = "temp_unit_celsius"
        // Default to Celsius
        fun isCelsiusPreferred(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            return prefs.getBoolean(KEY_TEMP_UNIT_IS_CELSIUS, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        loadSettings()

        binding.buttonSaveChanges.setOnClickListener {
            saveSettings()
            Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show()
            // Potentially finish() or provide other feedback
        }
    }

    private fun loadSettings() {
        val notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        val tempUnitIsCelsius = sharedPreferences.getBoolean(KEY_TEMP_UNIT_IS_CELSIUS, true) // Default to Celsius

        binding.switchNotifications.isChecked = notificationsEnabled
        if (tempUnitIsCelsius) {
            binding.radioButtonCelsius.isChecked = true
        } else {
            binding.radioButtonFahrenheit.isChecked = true
        }
    }

    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, binding.switchNotifications.isChecked)
        editor.putBoolean(KEY_TEMP_UNIT_IS_CELSIUS, binding.radioButtonCelsius.isChecked)
        editor.apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}