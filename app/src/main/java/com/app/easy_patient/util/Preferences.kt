package com.app.easy_patient.util

import android.content.SharedPreferences
import com.app.easy_patient.util.AppConstants.PREF_KEYS.LOGIN_STATUS
import com.app.easy_patient.util.AppConstants.PREF_KEYS.TOKEN
import javax.inject.Inject
import javax.inject.Singleton

class Preferences @Inject constructor(private val preferences: SharedPreferences) {

    fun isUserLogin(): Boolean {
        return preferences.getBoolean(LOGIN_STATUS, false)
    }

    fun token(): String {
        val token = preferences.getString(TOKEN, "")
        return token ?: ""
    }
}