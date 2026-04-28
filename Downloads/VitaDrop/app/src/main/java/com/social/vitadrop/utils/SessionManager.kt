package com.social.vitadrop.utils

import android.content.Context

import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("vita_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ROLE = "user_role"
        private const val KEY_UID = "user_uid"
        private const val KEY_LOGIN = "is_logged_in"
    }

    // ✅ SAVE SESSION
    fun saveUserSession( role: String) {
        prefs.edit()
            .putString(KEY_ROLE, role)
            .putBoolean(KEY_LOGIN, true)
            .apply()
    }

    // ✅ GET ROLE
    fun getUserRole(): String {
        return prefs.getString(KEY_ROLE, "") ?: ""
    }

    // ✅ GET UID
    fun getUserId(): String {
        return prefs.getString(KEY_UID, "") ?: ""
    }

    // ✅ CHECK LOGIN STATUS
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_LOGIN, false)
    }

    // 🚀 CLEAR SESSION (LOGOUT)
    fun clearSession() {
        prefs.edit()
            .clear()
            .apply()
    }
}