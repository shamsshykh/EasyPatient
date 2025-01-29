package com.app.easy_patient.di.module

import com.app.easy_patient.data.remote.EasyPatientApiService
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.data.repository.EasyPatientRepoImpl
import com.app.easy_patient.database.EasyPatientDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun generateRepository(apiService: EasyPatientApiService, database: EasyPatientDatabase): EasyPatientRepo {
        return EasyPatientRepoImpl(apiService, database)
    }
}