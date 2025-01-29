package com.app.easy_patient.activity.audio_detail

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.R
import com.app.easy_patient.activity.BaseActivity
import com.app.easy_patient.adapter.AudioFileAdapter
import com.app.easy_patient.databinding.ActivityAudioDetailBinding
import com.app.easy_patient.model.kotlin.AudioFile
import com.app.easy_patient.util.AppConstants
import dagger.android.AndroidInjection
import javax.inject.Inject

class AudioDetailActivity : BaseActivity() {
    lateinit var bi: ActivityAudioDetailBinding
    lateinit var viewModel: AudioDetailViewModel
    private lateinit var audioFilesAdapter : AudioFileAdapter

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelProvider).get(AudioDetailViewModel::class.java)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_audio_detail)
        bi.view = this
        bi.viewmodel = viewModel
        bi.audio = viewModel.getAudioById(id = audioId())
        audioFilesAdapter = AudioFileAdapter( :: onClick)
        bi.audioFileAdapter = audioFilesAdapter
        bi.executePendingBindings()
        setContentView(bi.root)

        bi.swipeRefresh.setOnRefreshListener {
            viewModel.getAudioById(id = audioId()).apply {
                bi.audio = this
                bi.swipeRefresh.isRefreshing = false
            }

        }

        setupViews()
    }


    private fun onClick(audio: AudioFile) {
        viewModel.updateCount("audio",audio.id!!)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViews() {
        setSupportActionBar(bi.toolbar)
        title = getString(R.string.document_str)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun audioId(): Int {
        intent?.let {
            return it.getIntExtra(AppConstants.INTENT_KEYS.AUDIO_ID, 0)
        }
        return 0
    }

    override fun onDestroy() {
        audioFilesAdapter.getPlayerList().forEach {
            it.playWhenReady = false
        }
        super.onDestroy()
    }
}