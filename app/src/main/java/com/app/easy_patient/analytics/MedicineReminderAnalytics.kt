package com.app.easy_patient.analytics

import com.app.easy_patient.ktx.dateString
import com.app.easy_patient.ktx.toStringFormat
import com.app.easy_patient.util.SharedPrefs
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

interface MedicineReminderAnalytics {
    fun logNotificationGenerated(
        time: Long,
        name: String,
        dosage: String,
        notification: Boolean,
        critical: Boolean
    )

    fun logNotificationClicked(
        time: Long,
        name: String,
        dosage: String,
        notification: Boolean,
        critical: Boolean
    )

    fun logReminderDialogOpened(
        time: Long,
        name: String,
        dosage: String,
        notification: Boolean,
        critical: Boolean
    )
}

class MedicineReminderAnalyticsImpl @Inject constructor(
    private val localPref: SharedPrefs
): MedicineReminderAnalytics {

    private val analytics = Firebase.analytics

    override fun logNotificationGenerated(
        time: Long,
        name: String,
        dosage: String,
        notification: Boolean,
        critical: Boolean
    ) {
        analytics.logEvent("MEDICINE_REMINDER_NOTIFICATION_GENERATED") {
            param("user", localPref.email)
            param("time", time.dateString())
            param("dosage", dosage)
            param("notification", notification.toStringFormat())
            param("critical", critical.toStringFormat())
        }
    }

    override fun logNotificationClicked(
        time: Long,
        name: String,
        dosage: String,
        notification: Boolean,
        critical: Boolean
    ) {
        analytics.logEvent("MEDICINE_REMINDER_NOTIFICATION_CLICKED") {
            param("user", localPref.email)
            param("time", time.dateString())
            param("dosage", dosage)
            param("notification", notification.toStringFormat())
            param("critical", critical.toStringFormat())
        }
    }

    override fun logReminderDialogOpened(
        time: Long,
        name: String,
        dosage: String,
        notification: Boolean,
        critical: Boolean
    ) {
        analytics.logEvent("MEDICINE_REMINDER_DIALOG_OPENED") {
            param("user", localPref.email)
            param("time", time.dateString())
            param("dosage", dosage)
            param("notification", notification.toStringFormat())
            param("critical", critical.toStringFormat())
        }
    }

}