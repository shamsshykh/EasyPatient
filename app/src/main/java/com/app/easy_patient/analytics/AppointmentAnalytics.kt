package com.app.easy_patient.analytics

import com.app.easy_patient.util.SharedPrefs
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

interface AppointmentAnalytics {

    fun logNotificationRegistered(
        appointmentId: Int,
        notificationType: String,
        appointmentTime: String?
    )

    fun logNotificationGenerated(
        appointmentId: Int,
        notificationType: String,
        appointmentTime: String?
    )

    fun logNotificationClicked(
        appointmentId: Int,
        notificationType: String,
        appointmentTime: String?
    )
}

class AppointmentAnalyticsImpl @Inject constructor(
    private val localPref: SharedPrefs
): AppointmentAnalytics {
    private val analytics = Firebase.analytics
    override fun logNotificationRegistered(
        appointmentId: Int,
        notificationType: String,
        appointmentTime: String?
    ) {
        analytics.logEvent("APPOINTMENT_NOTIFICATION_REGISTERED") {
            param("user", localPref.email)
            param("appointmentId", appointmentId.toString())
            param("notificationType", notificationType)
            appointmentTime?.let {
                param("appointmentTime", appointmentTime)
            }
        }
    }

    override fun logNotificationGenerated(
        appointmentId: Int,
        notificationType: String,
        appointmentTime: String?
    ) {
        analytics.logEvent("APPOINTMENT_NOTIFICATION_GENERATED") {
            param("user", localPref.email)
            param("appointmentId", appointmentId.toString())
            param("notificationType", notificationType)
            appointmentTime?.let {
                param("appointmentTime", appointmentTime)
            }
        }
    }

    override fun logNotificationClicked(
        appointmentId: Int,
        notificationType: String,
        appointmentTime: String?
    ) {
        analytics.logEvent("APPOINTMENT_NOTIFICATION_CLICKED") {
            param("user", localPref.email)
            param("appointmentId", appointmentId.toString())
            param("notificationType", notificationType)
            appointmentTime?.let {
                param("appointmentTime", appointmentTime)
            }
        }
    }
}