package com.app.easy_patient.activity.onboarding

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.app.easy_patient.util.AppConstants
import javax.inject.Inject

class OnBoardingViewModel @Inject constructor(
    private val preferences: SharedPreferences
    ): ViewModel() {

    fun loginStatus(): Boolean {
        return preferences.getBoolean(AppConstants.PREF_KEYS.LOGIN_STATUS, false)
    }
}