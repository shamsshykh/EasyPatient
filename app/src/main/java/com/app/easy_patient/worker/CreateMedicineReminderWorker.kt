package com.app.easy_patient.worker

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.di.factories.ChildWorkerFactory
import com.app.easy_patient.ktx.stringToDate
import com.app.easy_patient.ktx.toMedicineReminder
import com.app.easy_patient.model.kotlin.MedicineStatus
import com.app.easy_patient.util.Utility
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class CreateMedicineReminderWorker(val context: Context,
                                   params: WorkerParameters,
                                   private val repository: EasyPatientRepo
): Worker(context, params) {
    companion object {
        const val TAG = "CreateMedicineReminderWorker"
    }
    override fun doWork(): Result {
        val medicineID = inputData.getInt("medicineId", 0)
        if (medicineID > 0) {
            GlobalScope.launch {
                createMedicineReminders(medicineID)
                createNotifications(medicineID)
            }
            return Result.success()
        }
        return Result.failure()
    }

    private suspend fun createMedicineReminders(medicineId: Int) {
        repository.getMedicineById(medicineId)?.let { medicine ->
            val durationType = medicine.days_of_the_week!!.split(",").toTypedArray()[1].toInt()
            val frequencyType = medicine.days_of_the_week!!.split(",").toTypedArray()[0].toInt()

            if (frequencyType > 2 || durationType > 2) {
                return
            }

            val startDateCalendar = Calendar.getInstance()
            startDateCalendar.time = medicine.start_time!!.stringToDate()
            startDateCalendar.set(Calendar.SECOND, 0)
            startDateCalendar.set(Calendar.MILLISECOND, 0)

            val endDateCalendar = Calendar.getInstance()
            endDateCalendar.time = medicine.start_time!!.stringToDate()
            startDateCalendar.set(Calendar.SECOND, 0)
            startDateCalendar.set(Calendar.MILLISECOND, 0)

            when (durationType) {
                0 -> endDateCalendar.add(Calendar.DAY_OF_YEAR, medicine.number_of_days!!)
                1 -> endDateCalendar.add(Calendar.WEEK_OF_YEAR, medicine.number_of_days!!)
                2 -> endDateCalendar.add(Calendar.MONTH, medicine.number_of_days!!)
            }
            do {
                Log.i("Medicine_Reminder", DashboardActivity.dateFormat.format(startDateCalendar.time))
                repository.insertMedicineReminder(
                    medicine.toMedicineReminder(
                        status = if (startDateCalendar.timeInMillis > Calendar.getInstance().timeInMillis) MedicineStatus.Future else MedicineStatus.Missed,
                        timeInMillis = startDateCalendar.timeInMillis
                    )
                )

                when (frequencyType) {
                    0 -> startDateCalendar.add(Calendar.HOUR_OF_DAY, medicine.frequency!!)
                    1 -> startDateCalendar.add(Calendar.DAY_OF_YEAR, medicine.frequency!!)
                    2 -> startDateCalendar.add(Calendar.WEEK_OF_YEAR, medicine.frequency!!)
                }
            } while (
                startDateCalendar.timeInMillis <= endDateCalendar.timeInMillis
            )
        }
    }

    private suspend fun createNotifications(medicineId: Int) {
        val result =  repository.medicineRemindersForNotification(medicineId)
        result.forEach { reminder ->
            Log.i(TAG, DashboardActivity.dateFormat.format(reminder.reminderTime()))
            if (reminder.reminderTime() > Calendar.getInstance().timeInMillis)
                Utility.setAlarm(context.applicationContext, reminder)
        }
    }

    class Factory @Inject constructor(
        private val repository: Provider<EasyPatientRepo>
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return CreateMedicineReminderWorker(
                appContext,
                params,
                repository.get()
            )
        }
    }
}