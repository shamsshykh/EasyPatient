package com.app.easy_patient.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.app.easy_patient.ktx.toNotificationTime
import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.model.kotlin.Medicine
import com.app.easy_patient.model.kotlin.MedicineReminder
import com.app.easy_patient.receiver.AppointmentReceiver
import com.app.easy_patient.receiver.BootReceiver
import com.app.easy_patient.receiver.MedicineReminderReceiver

object Utility {

    @JvmStatic
    fun setAlarm(context: Context, medicineReminder: MedicineReminder) {
        setupBootReceiver(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntent(context, medicineReminder, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(medicineReminder.reminderTime(), null), pendingIntent)
        /*else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, medicineReminder.toNotificationTime(), pendingIntent)*/
        else
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, medicineReminder.toNotificationTime(), pendingIntent)
    }

    @JvmStatic
    fun setAppointmentAlarm(context: Context, time: Long, pendingIntent: PendingIntent) {
        setupBootReceiver(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(time, null), pendingIntent)
        /*else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingIntent)*/
        else
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingIntent)
    }

    @JvmStatic
    fun cancelAlarm(context: Context, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun setRepeatingAlarm(
        context: Context,
        triggerAtMillis: Long,
        intervalMillis: Long,
        pendingIntent: PendingIntent
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            intervalMillis,
            pendingIntent
        )
    }

    @JvmStatic
    fun pendingIntent(context: Context, medicineReminder: MedicineReminder, flag: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT): PendingIntent {
        val intent = Intent(context, MedicineReminderReceiver::class.java)
        intent.putExtra("id", medicineReminder.id)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)

        return PendingIntent.getBroadcast(
            context,
            medicineReminder.notificationId(),
            intent,
            flag
        )
    }

    fun appointmentPendingIntent(context: Context, appointment: Appointment, flag: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, requestCode: Int): PendingIntent {
        val intent = Intent(context, AppointmentReceiver::class.java)
        intent.putExtra("id", appointment.id)
        intent.putExtra("requestCode", requestCode)

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            flag
        )
    }

    @JvmStatic
    fun setupBootReceiver(context: Context, state: Int) {
        //enable receiver
        val receiver = ComponentName(context, BootReceiver::class.java)
        val pm: PackageManager = context.packageManager

        pm.setComponentEnabledSetting(
            receiver,
            state,
            PackageManager.DONT_KILL_APP
        )
    }

    @JvmStatic
    fun hasSchedulePermission(context: Context): Boolean{
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}