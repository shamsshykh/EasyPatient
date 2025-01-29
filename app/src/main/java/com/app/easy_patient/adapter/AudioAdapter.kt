package com.app.easy_patient.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.databinding.AudioItemLayoutBinding
import com.app.easy_patient.model.kotlin.Audio

class AudioAdapter(
    val onItemClick: (audio: Audio) -> Unit,
    val onArchiveUnArchiveClick: (audio: Audio) -> Unit,
    val isArchive : Boolean
) :
    BaseRecyclerViewAdapter<AudioAdapter.AudioViewHolder, Audio>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val binding = AudioItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AudioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class AudioViewHolder(private val itemBinding: AudioItemLayoutBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(audio: Audio) {
            itemBinding.audio = audio
            itemBinding.archive = isArchive
            itemBinding.apply {
                rootItemLayout.setOnClickListener {
                    onItemClick(audio)
                }
                btnArchiveStatus.setOnClickListener {
                    onArchiveUnArchiveClick(audio)
                }
            }
            itemBinding.executePendingBindings()
        }
    }
}