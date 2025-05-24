package com.example.shadowfox

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "ShadowFoxUserSession"

        // All Shared Preferences Keys
        private const val IS_LOGGED_IN = "IsLoggedIn"

        // User name (make variable public to access from outside)
        const val KEY_EMAIL = "email"

        // Email address (make variable public to access from outside)
        const val KEY_PASSWORD = "password" // Storing password in SharedPreferences is not recommended for production apps
    }

    /**
     * Create login session
     */
    fun createLoginSession(email: String, password: String) {
        // This function is called when a user successfully logs in.
        // It confirms their login status.
        editor.putBoolean(IS_LOGGED_IN, true)
        // It's good practice to also re-affirm the email for the current session,
        // though the primary storage of credentials happens at signup.
        editor.putString(KEY_EMAIL, email) // Storing email for the active session
        // Storing password again here is not strictly necessary if createUser did it,
        // but ensures the session reflects the credentials used for this specific login.
        // However, the main point is that logoutUser() should not clear these.
        editor.putString(KEY_PASSWORD, password)
        editor.commit()
    }

    /**
     * Create new user - for signup
     */
    fun createUser(email: String, password: String) {
        // For simplicity, we are just storing it.
        // In a real app, you might want to check if user already exists or store it more securely.
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)
        editor.commit() // Commit the changes for user creation
    }


    /**
     * Get stored session data
     */
    fun getUserDetails(): HashMap<String, String?> {
        val user = HashMap<String, String?>()
        user[KEY_EMAIL] = prefs.getString(KEY_EMAIL, null)
        user[KEY_PASSWORD] = prefs.getString(KEY_PASSWORD, null)
        return user
    }

    /**
     * Clear session details
     */
    fun logoutUser() {
        // Clear only the login status, not the stored credentials
        editor.putBoolean(IS_LOGGED_IN, false)
        editor.commit()

        // After logout, redirect user to Login Activity
        // This part should be handled in the activity where logout is called
        // For example:
        // val i = Intent(_context, LoginActivity::class.java)
        // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // _context.startActivity(i)
    }

    /**
     * Quick check for login
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }
}