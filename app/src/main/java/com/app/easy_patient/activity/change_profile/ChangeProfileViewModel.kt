package com.app.easy_patient.activity.change_profile

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.model.kotlin.Status
import com.app.easy_patient.model.kotlin.UploadImage
import com.app.easy_patient.util.AppConstants.PREF_KEYS.GENDER
import com.app.easy_patient.util.AppConstants.PREF_KEYS.IMAGE_PATH
import com.app.easy_patient.wrappers.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class ChangeProfileViewModel @Inject constructor(
    private val repository: EasyPatientRepo,
    private val preferences: SharedPreferences
    ): ViewModel() {

    private var _uploadProfileResponse = MutableLiveData<Resource<UploadImage>>()
    var uploadProfileResponse: LiveData<Resource<UploadImage>> = _uploadProfileResponse

    private var _deleteProfileResponse = MutableLiveData<Resource<Status>>()
    var deleteProfileResponse: LiveData<Resource<Status>> = _deleteProfileResponse

    fun getGender(): String {
        val gender = preferences.getString(GENDER, null)
        return gender ?: ""
    }

    fun uploadProfilePhoto(imgFile: File) {
        _uploadProfileResponse.value = Resource.Loading
        val filePart = MultipartBody.Part.createFormData(
            "file",
            imgFile.absolutePath,
            RequestBody.create("image/jpeg".toMediaTypeOrNull(), imgFile)
        )

        viewModelScope.launch {
            repository.uploadPhoto(filePart).collect { resource ->
                _uploadProfileResponse.value = resource
            }
        }
    }

    fun updateImagePath(location: String?) {
        val editor = preferences.edit()
        editor.putString(IMAGE_PATH, location)
        editor.commit()
    }

    fun userImage() = preferences.getString(IMAGE_PATH, "")

    fun deleteProfilePhoto() {
        _deleteProfileResponse.value = Resource.Loading
        viewModelScope.launch {
            repository.deletePhoto().collect { resource ->
                _deleteProfileResponse.value = resource
            }
        }
    }

}