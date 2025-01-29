package com.app.easy_patient.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.easy_patient.R
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.analytics.EasyPatientAnalytics
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.ktx.*
import com.app.easy_patient.model.kotlin.MedicineReminder
import com.app.easy_patient.util.*
import dagger.android.AndroidInjection
import javax.inject.Inject


class MedicineReminderReceiver : BroadcastReceiver() {
    lateinit var notificationManager: NotificationManager
    lateinit var context: Context

    @Inject
    lateinit var repo: EasyPatientRepo

    @Inject
    lateinit var analytics: EasyPatientAnalytics

    override fun onReceive(k1: Context, intent: Intent) {
        AndroidInjection.inject(this, k1)
        Log.e("setNotification>>>", "OnReceived")
        context = k1.applicationContext

        val medicineReminder = repo.getMedicineReminderById(intent.getStringExtra("id")!!)
        if (medicineReminder != null) {
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (medicineReminder.notification) {
                showNotification(context, medicineReminder)
                analytics.medicineReminderAnalytics().logNotificationGenerated(
                    time = medicineReminder.reminderTime(),
                    name = medicineReminder.name,
                    dosage = medicineReminder.dosage,
                    notification = true,
                    critical = medicineReminder.critical
                )
            }

            if (medicineReminder.notification || medicineReminder.critical) {
                stopSoundService()
                playNotificationSound(medicineReminder)
            }

            if (medicineReminder.critical && Settings.canDrawOverlays(context)) {
                val intent = Intent(context, DashboardActivity::class.java)
                intent.putExtra(Constants.INTENT_KEYS.MEDICINE_REMINDER, medicineReminder)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_USER_ACTION
                context.startActivity(intent)
            }
        }
    }

    private fun showNotification(context: Context, medicineReminder: MedicineReminder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(context.notificationChannel(medicineReminder))
        }
        val intent = Intent(context, DashboardActivity::class.java)
        intent.putExtra(Constants.INTENT_KEYS.MEDICINE_REMINDER, medicineReminder)
        intent.putExtra(Constants.INTENT_KEYS.STOP_REMINDER_SOUND, true)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        Log.e("setNotification>>>", "setNotification")
        val contentIntent = PendingIntent.getActivity(
            context,
            medicineReminder.notificationId(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, if (medicineReminder.critical) CHANNEL_ID_HIGH_PRIO else CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_notification_icon)
            .setContentTitle(medicineReminder.name)
            .setContentText(context.getString(R.string.medicine_notification_sub_title_str, medicineReminder.dosage))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(Notification.CATEGORY_ALARM)
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setDeleteIntent(deletePendingIntent(medicineReminder))
            .setSilent(true)
            .apply {
                setLargeNotificationIcon(
                    id = medicineReminder.notificationId(),
                    notificationManager = notificationManager,
                    model = if (!medicineReminder.pictureLink.isNullOrEmpty()) medicineReminder.pictureLink else medicineReminder.defaultIcon.toString().medicineNotificationIcon(),
                    context = context
                )
            }
        notificationManager.notify(medicineReminder.notificationId(), builder.build())
    }

    private fun playNotificationSound(medicineReminder: MedicineReminder) {
        val intent = Intent(context, SoundService::class.java)
        intent.putExtra(Constants.INTENT_KEYS.MEDICINE_REMINDER, medicineReminder)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context!!.startForegroundService(intent)
        } else {
            context!!.startService(intent)
        }
    }

    private fun stopSoundService() {
        val service = Intent(context, SoundService::class.java)
        context.stopService(service)
    }

    private fun deletePendingIntent(medicineReminder: MedicineReminder): PendingIntent {
        val intent = Intent(context, NotificationCancelReceiver::class.java)
        intent.putExtra(NotificationCancelReceiver.NOTIFICATION_ID, medicineReminder.notificationId())
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT);
    }
}