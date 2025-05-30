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
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_USER_BIO = "user_bio"
    }

    /**
     * Create login session
     */
    fun createLoginSession(email: String, password: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(KEY_EMAIL, email)
        // It's generally better not to store/re-store password during login session creation
        // if it's already stored at signup. The main purpose here is to set IS_LOGGED_IN.
        // editor.putString(KEY_PASSWORD, password) // Consider removing if password stored securely at signup
        editor.commit()
    }

    /**
     * Create new user - for signup
     */
    fun createUser(email: String, password: String) {
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password) // Ensure this is handled securely in a real app
        // Initialize display name from email part
        editor.putString(KEY_DISPLAY_NAME, email.substringBefore('@'))
        editor.putString(KEY_USER_BIO, "") // Initialize empty bio
        editor.commit()
    }

    /**
     * Update user profile data (Display Name and Bio)
     */
    fun updateUserProfile(displayName: String, bio: String) {
        editor.putString(KEY_DISPLAY_NAME, displayName)
        editor.putString(KEY_USER_BIO, bio)
        editor.commit()
    }


    /**
     * Get stored session data
     */
    fun getUserDetails(): HashMap<String, String?> {
        val user = HashMap<String, String?>()
        user[KEY_EMAIL] = prefs.getString(KEY_EMAIL, null)
        user[KEY_PASSWORD] = prefs.getString(KEY_PASSWORD, null) // For login check, not for display
        user[KEY_DISPLAY_NAME] = prefs.getString(KEY_DISPLAY_NAME, prefs.getString(KEY_EMAIL, "")?.substringBefore('@'))
        user[KEY_USER_BIO] = prefs.getString(KEY_USER_BIO, "")
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