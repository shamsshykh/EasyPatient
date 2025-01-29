package com.app.easy_patient.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.di.factories.ChildWorkerFactory
import com.app.easy_patient.util.Utility
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class PeriodicMedicineReminderNotificationWorker(val context: Context,
                                                  params: WorkerParameters,
                                                  private val repository: EasyPatientRepo
): Worker(context, params){

    companion object {
        const val TAG = "PeriodicMedicineReminderNotificationWorker"
    }
    override fun doWork(): Result {

        GlobalScope.launch {
            val medicines = repository.getLocalMedicines()
            medicines.forEach { medicine ->
                val reminders = repository.medicineRemindersForNotification(medicine.id)
                reminders.forEach { reminder ->
                    if (reminder.reminderTime() > Calendar.getInstance().timeInMillis)
                        Utility.setAlarm(context.applicationContext, reminder)
                }
            }
        }
        return Result.success()
    }

    class Factory @Inject constructor(
        private val repository: Provider<EasyPatientRepo>
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return PeriodicMedicineReminderNotificationWorker(
                appContext,
                params,
                repository.get()
            )
        }
    }
}