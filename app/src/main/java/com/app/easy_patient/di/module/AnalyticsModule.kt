package com.app.easy_patient.di.module

import com.app.easy_patient.analytics.*
import com.app.easy_patient.util.SharedPrefs
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AnalyticsModule {

    @Singleton
    @Provides
    fun medicineReminderAnalytics(sharedPrefs: SharedPrefs): MedicineReminderAnalytics {
        return MedicineReminderAnalyticsImpl(sharedPrefs)
    }

    @Singleton
    @Provides
    fun appointmentAnalytics(sharedPrefs: SharedPrefs): AppointmentAnalytics {
        return AppointmentAnalyticsImpl(sharedPrefs)
    }

    @Singleton
    @Provides
    fun easyPatientAnalytics(
        medicineReminderAnalytics: MedicineReminderAnalytics,
        appointmentAnalytics: AppointmentAnalytics
    ): EasyPatientAnalytics {
        return EasyPatientAnalyticsImpl(
            appointmentAnalytics,
            medicineReminderAnalytics
        )
    }
}