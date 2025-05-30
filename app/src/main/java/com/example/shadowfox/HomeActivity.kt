package com.example.shadowfox

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.shadowfox.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import android.app.Activity

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        private const val PROFILE_ACTIVITY_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        sessionManager = SessionManager(this)

        setupNavigationDrawer()
        updateNavHeader()

        binding.buttonGoToWeatherForecast.setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        binding.fabGoToCalculator.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun setupNavigationDrawer() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun updateNavHeader() {
        val userDetails = sessionManager.getUserDetails()
        val email = userDetails[SessionManager.KEY_EMAIL]
        val displayName = userDetails[SessionManager.KEY_DISPLAY_NAME] ?: email?.substringBefore('@') ?: "User"

        binding.textViewWelcomeHome.text = "Welcome, $displayName!"

        val headerView = binding.navView.getHeaderView(0)
        val navHeaderEmailTextView = headerView.findViewById<TextView>(R.id.textViewNavHeaderEmail)
        val navHeaderNameTextView = headerView.findViewById<TextView>(R.id.textViewNavHeaderName)

        navHeaderEmailTextView.text = email ?: "user@example.com"
        navHeaderNameTextView.text = displayName
    }

    override fun onResume() {
        super.onResume()
        // Update nav header in case user details changed (e.g., from ProfileActivity)
        // This is a simpler way than onActivityResult if ProfileActivity just finishes.
        // If ProfileActivity sends a specific result, onActivityResult is better.
        updateNavHeader()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivityForResult(intent, PROFILE_ACTIVITY_REQUEST_CODE)
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_logout -> {
                sessionManager.logoutUser()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val profileUpdated = data?.getBooleanExtra("profile_updated", false) ?: false
                if (profileUpdated) {
                    updateNavHeader()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}