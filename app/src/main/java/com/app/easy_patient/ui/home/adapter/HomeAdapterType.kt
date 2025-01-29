package com.app.easy_patient.ui.home.adapter

sealed class HomeAdapterType

object NotificationType: HomeAdapterType()
object MenuType: HomeAdapterType()
object MedicineType: HomeAdapterType()
object AppointmentType: HomeAdapterType()
object NotificationMedicineType: HomeAdapterType()
object AppointmentTabType: HomeAdapterType()
