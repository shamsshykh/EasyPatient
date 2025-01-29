package com.app.easy_patient.di.module

import androidx.work.WorkerFactory
import com.app.easy_patient.di.factories.AppWorkerProviderFactory
import dagger.Binds
import dagger.Module

@Module
interface WorkerFactoryModule {
    @Binds
    fun bindWorkManagerFactory(appWorkerProviderFactory: AppWorkerProviderFactory): WorkerFactory
}