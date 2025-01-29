package com.app.easy_patient.activity.appointment_detail

import androidx.lifecycle.ViewModel
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.model.kotlin.Appointment
import javax.inject.Inject

class AppointmentDetailViewModel @Inject constructor(private val repository: EasyPatientRepo): ViewModel() {

    lateinit var appointment: Appointment

    var isFromNotification = false

    fun getAppointmentData(appointmentId: Int): Appointment? {
        appointment = repository.getAppointment(appointmentId)!!
        return appointment
    }
}