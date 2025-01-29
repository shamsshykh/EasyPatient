package com.app.easy_patient.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.di.factories.ChildWorkerFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class UpdateMedicineReminderWorker(val context: Context,
                                    params: WorkerParameters,
                                    private val repository: EasyPatientRepo
): Worker(context, params) {
    companion object {
        const val TAG = "UpdateMedicineReminderWorker"
    }
    override fun doWork(): Result {
        val medicineID = inputData.getInt("medicineId", 0)
        if (medicineID > 0) {
            GlobalScope.launch {
                updateMedicineReminders(medicineID)
            }
            return Result.success()
        }
        return Result.failure()
    }

    private suspend fun updateMedicineReminders(medicineID: Int) {
        val medicine = repository.getMedicineById(medicineID)
        medicine?.let {
            repository.updateMedicineReminders(it)
        }
    }

    class Factory @Inject constructor(
        private val repository: Provider<EasyPatientRepo>
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return UpdateMedicineReminderWorker(
                appContext,
                params,
                repository.get()
            )
        }
    }
}