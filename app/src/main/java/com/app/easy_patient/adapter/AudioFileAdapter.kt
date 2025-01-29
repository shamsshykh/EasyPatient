package com.app.easy_patient.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.R
import com.app.easy_patient.database.MealPlanModel
import com.app.easy_patient.databinding.AudioFileItemLayoutBinding
import com.app.easy_patient.ktx.gone
import com.app.easy_patient.ktx.show
import com.app.easy_patient.ktx.toDuration
import com.app.easy_patient.model.kotlin.AudioFile
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class AudioFileAdapter(
    private val onClickAction : (AudioFile) -> Unit
) :
    BaseRecyclerViewAdapter<AudioFileAdapter.AudioFileViewHolder, AudioFile>() {

    private var selectedAudioPos: Int = -1
    private var simpleExoPlayerList: MutableList<SimpleExoPlayer> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioFileViewHolder {
        val binding = AudioFileItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AudioFileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioFileViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, position)
        }
    }

    inner class AudioFileViewHolder(private val itemBinding: AudioFileItemLayoutBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(audioFile: AudioFile, position: Int) {
            prepareMediaPlayer(itemBinding, audioFile)
            updateTextViews(itemBinding, audioFile)
            itemBinding.executePendingBindings()
        }
    }

    private fun prepareMediaPlayer(itemBinding: AudioFileItemLayoutBinding, audioFile: AudioFile) {
        lateinit var simpleExoPlayer: SimpleExoPlayer
        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(itemBinding.root.context, Util.getUserAgent(itemBinding.root.context, "easy-patient"))

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
            MediaItem.fromUri(audioFile.file!!))

        val mediaSourceFactory: MediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

        simpleExoPlayer = SimpleExoPlayer.Builder(itemBinding.root.context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        simpleExoPlayer.addMediaSource(mediaSource)
        simpleExoPlayer.prepare()

        simpleExoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.i("EASY_PATIENT_onPlaybackStateChanged", "onPlaybackStateChanged: $playbackState")
                if (playbackState == Player.STATE_ENDED) {
                    updateViews(itemBinding, false)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                updateViews(itemBinding, isPlaying)
                if (isPlaying) onClickAction.invoke(audioFile)
            }
        })
        itemBinding.playerView.player = simpleExoPlayer
        simpleExoPlayerList.add(simpleExoPlayer)
    }

    private fun updateTextViews(itemBinding: AudioFileItemLayoutBinding, audioFile: AudioFile) {
        val audioName = itemBinding.playerView.findViewById<TextView>(R.id.txtTitle)
        val audioDuration = itemBinding.playerView.findViewById<TextView>(R.id.txtDuration)

        audioName.text = audioFile.file_name
        audioDuration.text = audioFile.toDuration()
    }

    private fun updateViews(itemBinding: AudioFileItemLayoutBinding, isPlaying: Boolean) {
        val audioName = itemBinding.playerView.findViewById<TextView>(R.id.txtTitle)
        val audioDuration = itemBinding.playerView.findViewById<TextView>(R.id.txtDuration)
        val exoProgress = itemBinding.playerView.findViewById<PreviewTimeBar>(R.id.exo_progress)

        if (isPlaying) {
            audioName.setTextColor(itemBinding.root.context.getColor(android.R.color.black))
            audioDuration.setTextColor(itemBinding.root.context.getColor(android.R.color.black))
            exoProgress.show()
        } else {
            audioName.setTextColor(itemBinding.root.context.getColor(R.color.colorText))
            audioDuration.setTextColor(itemBinding.root.context.getColor(R.color.colorText))
            exoProgress.gone()
        }

    }

    fun getPlayerList(): List<SimpleExoPlayer> {
        return simpleExoPlayerList
    }
}