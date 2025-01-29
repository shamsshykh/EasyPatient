package com.app.easy_patient.di.module

import android.app.Application
import com.app.easy_patient.database.EasyPatientDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun getDatabase(application: Application): EasyPatientDatabase = EasyPatientDatabase.getDatabase(application.applicationContext)

}