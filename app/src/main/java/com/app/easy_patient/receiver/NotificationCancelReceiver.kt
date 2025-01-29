package com.app.easy_patient.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.easy_patient.util.SoundService

class NotificationCancelReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "notification_id"
    }

    lateinit var notificationManager: NotificationManager
    var notificationId = -1

    override fun onReceive(context: Context?, intent: Intent?) {

        notificationId = intent?.extras?.getInt(NOTIFICATION_ID) ?: notificationId

        context?.let {
            notificationManager = it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationId == -1)
                notificationManager.cancelAll()
            else
                notificationManager.cancel(notificationId)

            val soundService = Intent(it.applicationContext, SoundService::class.java)
            it.stopService(soundService)
        }
    }
}