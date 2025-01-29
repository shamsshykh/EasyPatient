package com.app.easy_patient.ui.medicine

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.model.kotlin.Medicine
import com.app.easy_patient.wrappers.Resource
import javax.inject.Inject

class MedicineViewModel @Inject constructor(
    private val repository: EasyPatientRepo
): ViewModel() {

    val medicineList = MutableLiveData<Resource<List<Medicine>>>()

}