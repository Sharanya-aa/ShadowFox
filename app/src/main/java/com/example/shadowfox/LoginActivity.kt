package com.example.shadowfox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // If user is already logged in, navigate to MainActivity
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return // Finish this activity so user can't go back to it
        }

        val editTextEmailLogin: EditText = findViewById(R.id.editTextEmailLogin)
        val editTextPasswordLogin: EditText = findViewById(R.id.editTextPasswordLogin)
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val textViewGoToSignup: TextView = findViewById(R.id.textViewGoToSignup)

        buttonLogin.setOnClickListener {
            val email = editTextEmailLogin.text.toString().trim()
            val password = editTextPasswordLogin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Retrieve stored user details
            val storedUserDetails = sessionManager.getUserDetails()
            val storedEmail = storedUserDetails[SessionManager.KEY_EMAIL]
            val storedPassword = storedUserDetails[SessionManager.KEY_PASSWORD]

            if (email == storedEmail && password == storedPassword) {
                sessionManager.createLoginSession(email, password) // Re-affirm login session
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        textViewGoToSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}