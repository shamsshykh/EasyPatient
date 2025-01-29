package com.app.easy_patient.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.app.easy_patient.BuildConfig
import com.app.easy_patient.R
import com.app.easy_patient.ktx.critical
import com.app.easy_patient.model.kotlin.Medicine
import com.app.easy_patient.model.kotlin.MedicineReminder

const val CHANNEL_ID_HIGH_PRIO = "${BuildConfig.APPLICATION_ID}.HIGH"
const val CHANNEL_ID = "${BuildConfig.APPLICATION_ID}.DEFAULT"
const val CHANNEL_SERVICE_ID = "${BuildConfig.APPLICATION_ID}.SERVICE"

@RequiresApi(Build.VERSION_CODES.O)
fun Context.notificationChannel( medicine: MedicineReminder): NotificationChannel {
    return NotificationChannel(
        if (medicine.critical) CHANNEL_ID_HIGH_PRIO else CHANNEL_ID,
        getString(R.string.channel_name),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "This Channel is used to display notifications"
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        enableLights(true)
        enableVibration(false)
        vibrationPattern = longArrayOf(0, 1000, 500, 1000)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.notificationChannel(): NotificationChannel {
    return NotificationChannel(
        CHANNEL_SERVICE_ID,
        getString(R.string.channel_name),
        NotificationManager.IMPORTANCE_MIN,
    ).apply {
        setShowBadge(false)
    }
}

