package com.app.easy_patient.di.module

import com.app.easy_patient.util.SoundService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract fun bindSoundService(): SoundService
}