package com.social.vitadrop.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    // applicationContext avoids holding on to an Activity
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("vita_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ROLE = "user_role"
        private const val KEY_UID = "user_uid"
        private const val KEY_LOGIN = "is_logged_in"
    }

    // SAVE SESSION (called once after a successful, role-verified login)
    fun saveUserSession(uid: String, role: String) {
        prefs.edit()
            .putString(KEY_UID, uid)
            .putString(KEY_ROLE, role)
            .putBoolean(KEY_LOGIN, true)
            .apply()
    }

    // GET ROLE
    fun getUserRole(): String =
        prefs.getString(KEY_ROLE, "") ?: ""

    // GET UID
    fun getUserId(): String =
        prefs.getString(KEY_UID, "") ?: ""

    // CHECK LOGIN STATUS
    fun isLoggedIn(): Boolean =
        prefs.getBoolean(KEY_LOGIN, false) && getUserRole().isNotBlank()

    // LOGOUT (also call FirebaseAuth.getInstance().signOut() wherever you log out)
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}