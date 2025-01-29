package com.app.easy_patient.ui.sidemenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.easy_patient.data.repository.EasyPatientRepo
import kotlinx.coroutines.launch
import javax.inject.Inject

class SideMenuViewModel @Inject constructor(
    private val repository: EasyPatientRepo
): ViewModel() {


}