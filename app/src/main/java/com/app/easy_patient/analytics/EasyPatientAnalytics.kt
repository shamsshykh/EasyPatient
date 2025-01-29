package com.app.easy_patient.analytics

import javax.inject.Inject

interface EasyPatientAnalytics {
    fun appointmentAnalytics(): AppointmentAnalytics
    fun medicineReminderAnalytics(): MedicineReminderAnalytics
}

class EasyPatientAnalyticsImpl @Inject constructor(
    private val appointmentAnalytics: AppointmentAnalytics,
    private val medicineReminderAnalytics: MedicineReminderAnalytics
): EasyPatientAnalytics {
    override fun appointmentAnalytics(): AppointmentAnalytics {
        return appointmentAnalytics
    }

    override fun medicineReminderAnalytics(): MedicineReminderAnalytics {
        return medicineReminderAnalytics
    }

}