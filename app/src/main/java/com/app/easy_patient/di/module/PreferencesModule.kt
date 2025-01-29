package com.app.easy_patient.di.module

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.app.easy_patient.util.AppConstants.PREF_KEYS.DB_NAME
import com.app.easy_patient.util.Preferences
import com.app.easy_patient.util.SharedPrefs
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(app: Application): SharedPreferences = app.getSharedPreferences(DB_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideStoredPreferences(pref: SharedPreferences): Preferences = Preferences(pref)

    @Singleton
    @Provides
    fun provideLocalPreferences(app: Application): SharedPrefs = SharedPrefs(app)

}