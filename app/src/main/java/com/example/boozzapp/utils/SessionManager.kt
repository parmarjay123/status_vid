package com.example.boozzapp.utils

import android.content.Context

class SessionManager(context: Context) {

    var APP_KEY = context.packageName.replace("\\.".toRegex(), "_").toLowerCase()
    val sharedPreferences = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE)
    private val PREF_SESSION_START_TIME = "session_start_time"
    private val SESSION_TIMEOUT = 30 * 60 * 1000 // 30 minutes

    fun isNewSession(): Boolean {
        val sessionStartTime = sharedPreferences.getLong(PREF_SESSION_START_TIME, 0)
        val currentTime = System.currentTimeMillis()
        return currentTime - sessionStartTime > SESSION_TIMEOUT
    }

    fun updateSessionStartTime() {
        val currentTime = System.currentTimeMillis()
        sharedPreferences.edit().putLong(PREF_SESSION_START_TIME, currentTime).apply()
    }
}