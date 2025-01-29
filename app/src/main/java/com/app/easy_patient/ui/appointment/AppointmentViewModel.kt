package com.app.easy_patient.ui.appointment

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.wrappers.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppointmentViewModel @Inject constructor(
    private val repository: EasyPatientRepo,
    private val preferences: SharedPreferences): ViewModel() {

    val appointmentList = MutableLiveData<Resource<List<Appointment>>>()
}