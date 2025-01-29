package com.app.easy_patient.ui.home

import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.model.kotlin.Medicine
import com.app.easy_patient.model.kotlin.MedicineReminder

sealed class HomeState

data class DeleteReminderNotification(val medicineReminder: MedicineReminder): HomeState()
data class CreateReminderNotification(val medicineReminder: MedicineReminder): HomeState()
data class CreateAppointmentNotification(val appointment: Appointment): HomeState()
data class CallMedicineReminderWorker(val medicine: Medicine): HomeState()
