package com.app.easy_patient.ui.audio_archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.easy_patient.data.repository.EasyPatientRepo
import com.app.easy_patient.model.kotlin.Audio
import com.app.easy_patient.wrappers.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArchiveAudioViewModel @Inject constructor(
    private val repository: EasyPatientRepo
): ViewModel() {
    private val _audioList = MutableLiveData<Resource<List<Audio>>>()
    val audioList: LiveData<Resource<List<Audio>>>
        get() = _audioList

    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean>
        get() = _progress

    init {
       getUnArchiveAudios()
    }

    private fun getUnArchiveAudios() {
        viewModelScope.launch {
            repository.getArchiveAudios().collect { resource ->
                _progress.value = resource is Resource.Loading
                _audioList.value = resource
            }
        }
    }

    fun unArchiveAudio(audio: Audio) {
        viewModelScope.launch {
            repository.unArchiveAudioDocument(audio.id!!).collect { resource ->
                _progress.value = resource is Resource.Loading
                if (resource is Resource.Success)
                    getUnArchiveAudios()
            }
        }
    }
}