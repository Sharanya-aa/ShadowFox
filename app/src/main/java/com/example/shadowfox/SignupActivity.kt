package com.example.shadowfox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sessionManager = SessionManager(this)

        val editTextEmailSignup: EditText = findViewById(R.id.editTextEmailSignup)
        val editTextPasswordSignup: EditText = findViewById(R.id.editTextPasswordSignup)
        val editTextConfirmPasswordSignup: EditText = findViewById(R.id.editTextConfirmPasswordSignup)
        val buttonSignup: Button = findViewById(R.id.buttonSignup)
        val textViewGoToLogin: TextView = findViewById(R.id.textViewGoToLogin)

        buttonSignup.setOnClickListener {
            val email = editTextEmailSignup.text.toString().trim()
            val password = editTextPasswordSignup.text.toString().trim()
            val confirmPassword = editTextConfirmPasswordSignup.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Basic email validation (can be more complex)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Basic password strength (e.g., minimum length)
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Store user details using SessionManager
            sessionManager.createUser(email, password)
            sessionManager.createLoginSession(email, password) // Log in the user after signup

            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()

            // Navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        textViewGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: finish SignupActivity so user can't go back to it from Login
        }
    }
}