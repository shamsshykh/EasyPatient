package com.app.easy_patient.di.module

import com.app.easy_patient.activity.appointment_detail.AppointmentDetailActivity
import com.app.easy_patient.activity.audio_detail.AudioDetailActivity
import com.app.easy_patient.activity.change_profile.ChangeProfileActivity
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.activity.onboarding.OnBoardingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun bindDashboardActivity(): DashboardActivity

    @ContributesAndroidInjector
    abstract fun bindChangeProfileActivity(): ChangeProfileActivity

    @ContributesAndroidInjector
    abstract fun bindOnBoardingActivity(): OnBoardingActivity

    @ContributesAndroidInjector
    abstract fun bindAppointmentDetailActivity(): AppointmentDetailActivity

    @ContributesAndroidInjector
    abstract fun bindAudioDetailActivity(): AudioDetailActivity
}