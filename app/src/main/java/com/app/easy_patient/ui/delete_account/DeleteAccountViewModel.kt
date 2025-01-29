package com.app.easy_patient.ui.delete_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.wrappers.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteAccountViewModel @Inject constructor(
    private val repository: EasyPatientRepo
): ViewModel() {

    private var _uiStates = MutableLiveData<DeleteAccountStates>()
    val uiStates: LiveData<DeleteAccountStates> = _uiStates

    fun deleteUserAccount(userName: String, password: String) {
        viewModelScope.launch {
           repository.deleteUserAccount(userName, password).collect { resource ->
               when (resource) {
                   is Resource.Success -> if (resource.data.deleted) _uiStates.value = LogoutUser else InvalidCredentials
                   is Resource.Error -> _uiStates.value = Failure
               }
           }
        }
    }
}