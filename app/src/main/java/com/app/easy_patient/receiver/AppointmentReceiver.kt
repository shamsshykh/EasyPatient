package com.app.easy_patient.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.easy_patient.R
import com.app.easy_patient.activity.appointment_detail.AppointmentDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.analytics.EasyPatientAnalytics
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.ktx.appointmentTime
import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.util.AppConstants
import com.app.easy_patient.util.Utility
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject

class AppointmentReceiver : BroadcastReceiver() {
    private var CHANNEL_ID = "APPOINTMENT_CHANNEL_ID_EASY_PATIENT"
    private lateinit var notificationManager: NotificationManager
    private lateinit var calendar: Calendar

    @Inject
    lateinit var repo: EasyPatientRepo

    @Inject
    lateinit var analytics: EasyPatientAnalytics

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val appointment = repo.getAppointment(intent.getIntExtra("id", 0))
        val requestCode = intent.getIntExtra("requestCode", 100)
        if (appointment != null) {
            showNotification(context.applicationContext, appointment, requestCode)
            addNotificationGeneratedAnalytics(appointment, requestCode)
        }
    }

    private fun addNotificationGeneratedAnalytics(appointment: Appointment, requestCode: Int) {
        analytics.appointmentAnalytics()
            .logNotificationGenerated(
                appointmentId = appointment.id,
                notificationType = if (requestCode == "${appointment.id}100".toInt())
                    Appointment.ONE_DAY
                else
                    Appointment.TWO_HOURS,
                appointmentTime = if (requestCode == "${appointment.id}100".toInt())
                    appointment.notify_at
                else
                    appointment.date
            )
    }

    private fun showNotification(context: Context, appointment: Appointment, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = "This Channel is used to display notifications"
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.enableLights(true)
            channel.enableVibration(false)
            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(context, AppointmentDetailActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEYS.APPOINTMENT_ID, appointment.id)
        intent.putExtra(AppConstants.INTENT_KEYS.APPOINTMENT_NOTIFICATION_REQUEST_CODE, requestCode)
        intent.putExtra(AppConstants.INTENT_KEYS.IS_FROM_NOTIFICATION, true)
        Log.e("setNotification>>>", "setNotification")
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_notification_icon)
            .apply {
                if (requestCode == "${appointment.id}100".toInt()) {
                    setContentTitle(context.getString(R.string.appointment_notification_title_24hrs_str))
                    setContentText(context.getString(R.string.appointment_notification_desc_24hrs_str))
                } else {
                    setContentTitle(context.getString(R.string.appointment_notification_title_2hrs_str))
                    setContentText(context.getString(R.string.appointment_notification_desc_2hrs_str))
                }
            }
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(Notification.CATEGORY_ALARM)
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_SOUND)

        notificationManager!!.notify(requestCode, builder.build())
    }
}