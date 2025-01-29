package com.app.easy_patient.activity.audio_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.model.kotlin.Audio
import com.app.easy_patient.model.kotlin.AudioFile
import com.app.easy_patient.util.Constants
import com.app.easy_patient.wrappers.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioDetailViewModel @Inject constructor(
    private val repository: EasyPatientRepo
): ViewModel() {

    private var _audioFiles = MutableLiveData<Resource<List<AudioFile>>>()
    val audioFiles: LiveData<Resource<List<AudioFile>>> = _audioFiles

    fun getAudioById(id: Int): Audio {
        val audio = repository.getAudio(id)
        audio?.audio_files?.let {
            _audioFiles.value = Resource.Success(it)
        }
        return audio!!
    }


    fun updateCount(type : String, id: Int){
        viewModelScope.launch {
            repository.updateCount(type,id).collect {
                if (it is Resource.Success){
                    Constants.UpdateCount = true
                }
            }
        }
    }
}