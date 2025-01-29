package com.app.easy_patient.di.module

import androidx.work.ListenableWorker
import com.app.easy_patient.di.factories.ChildWorkerFactory
import com.app.easy_patient.worker.CreateMedicineReminderWorker
import com.app.easy_patient.worker.PeriodicMedicineReminderNotificationWorker
import com.app.easy_patient.worker.UpdateMedicineReminderWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
interface WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(CreateMedicineReminderWorker::class)
    fun bindMedicineReminderWorker(factory: CreateMedicineReminderWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(PeriodicMedicineReminderNotificationWorker::class)
    fun bindPeriodicMedicineReminderNotificationWorker(factory: PeriodicMedicineReminderNotificationWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(UpdateMedicineReminderWorker::class)
    fun bindUpdateMedicineReminderWorker(factory: UpdateMedicineReminderWorker.Factory): ChildWorkerFactory
}