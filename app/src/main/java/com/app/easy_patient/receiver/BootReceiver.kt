package com.app.easy_patient.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.ktx.stringToDate
import com.app.easy_patient.ktx.toNotificationTime
import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.model.kotlin.Medicine
import com.app.easy_patient.model.kotlin.MedicineReminder
import com.app.easy_patient.util.Utility
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var repo: EasyPatientRepo

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // create medicine reminders for medicines after device restart
            GlobalScope.launch {
                val medicineReminders = repo.getMedicineReminders()
                createMedicineReminders(context.applicationContext, medicineReminders)
            }

            // create appointments notification after device restart
            val appointments = repo.getLocalAppointments()
            if (!appointments.isNullOrEmpty()) {
                createAppointmentsNotification(context.applicationContext, appointments)
            }
        }
    }

    private fun createMedicineReminders(context: Context, medicineReminders: List<MedicineReminder>) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 10)

        medicineReminders.forEach { medicineReminder ->
            if (medicineReminder.reminderTime() > calendar.timeInMillis)
                Utility.setAlarm(context, medicineReminder)
        }
    }

    private fun createAppointmentsNotification(context: Context, appointments: List<Appointment>) {
        appointments.forEach { appointment ->
            val appointmentTime = Calendar.getInstance()
            appointmentTime.time = DashboardActivity.dateFormat.parse(appointment.date)

            val beforeOneDayTime = Calendar.getInstance()
            beforeOneDayTime.time = DashboardActivity.dateFormat.parse(appointment.notify_at)

            val before2hrsTime = Calendar.getInstance()
            before2hrsTime.time = DashboardActivity.dateFormat.parse(appointment.date)
            before2hrsTime.add(Calendar.HOUR_OF_DAY, -2)

            val currentTime = Calendar.getInstance()

            if (currentTime.timeInMillis < beforeOneDayTime.timeInMillis) {
                Utility.setAppointmentAlarm(context,
                    beforeOneDayTime.timeInMillis,
                    Utility.appointmentPendingIntent(context, appointment, requestCode = (appointment.id + 100))
                )
            }

            if (currentTime.timeInMillis < before2hrsTime.timeInMillis) {
                Utility.setAppointmentAlarm(context,
                    before2hrsTime.timeInMillis,
                    Utility.appointmentPendingIntent(context, appointment, requestCode = (appointment.id + 200))
                )
            }
        }
    }
}