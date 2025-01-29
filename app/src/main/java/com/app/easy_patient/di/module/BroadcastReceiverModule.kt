package com.app.easy_patient.di.module

import com.app.easy_patient.receiver.AppointmentReceiver
import com.app.easy_patient.receiver.BootReceiver
import com.app.easy_patient.receiver.MedicineReminderReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverModule {

    @ContributesAndroidInjector
    abstract fun bindBootBroadcastReceiver(): BootReceiver

    @ContributesAndroidInjector
    abstract fun bindMedicineReminderBroadcastReceiver(): MedicineReminderReceiver

    @ContributesAndroidInjector
    abstract fun bindAppointmentBroadcastReceiver(): AppointmentReceiver
}