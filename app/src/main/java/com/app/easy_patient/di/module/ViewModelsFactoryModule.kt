package com.app.easy_patient.di.module

import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.di.factories.AppViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelsFactoryModule {

    @Binds
    fun bindAppViewModelFactory(factory: AppViewModelProviderFactory) : ViewModelProvider.Factory
}