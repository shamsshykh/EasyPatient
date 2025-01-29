package com.app.easy_patient.ui.audio_archive

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easy_patient.R
import com.app.easy_patient.activity.audio_detail.AudioDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.adapter.AudioAdapter
import com.app.easy_patient.databinding.FragmentArchiveAudioBinding
import com.app.easy_patient.fragment.ArchiveBottomSheetDialog
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.model.kotlin.Audio
import com.app.easy_patient.util.AppConstants
import com.app.easy_patient.util.ArchiveCallback

class ArchiveAudioFragment : BaseFragment() {

    lateinit var bi: FragmentArchiveAudioBinding
    lateinit var viewModel: DashboardViewModel
    lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)
        bi = FragmentArchiveAudioBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            audioAdapter = AudioAdapter(::onAudioSelected, ::onUnArchiveClick,false)
            recyclerViewAppointment.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = audioAdapter
            }
            lifecycleOwner = viewLifecycleOwner
        }
        return bi.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        initObserver()
    }

    private fun setupViews() {
        progressDialog = ProgressDialog(context, R.style.AppCompatAlertDialogStyle)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.loading_str))

        viewModel.getUnArchiveAudios(loader = true)
    }

    private fun initObserver() {
        viewModel.progress.observe(viewLifecycleOwner) { show ->
            if (show) progressDialog.show() else progressDialog.dismiss()
        }
    }

    private fun onAudioSelected(audio: Audio) {
        if (audio.isNew)
            viewModel.updateCount("audio", audio.audio_files!![0].id!!)

        val intent = Intent(context, AudioDetailActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEYS.AUDIO_ID, audio.id)
        startActivity(intent)
    }

    private fun onUnArchiveClick(audio: Audio) {
        val dialog = ArchiveBottomSheetDialog(ArchiveBottomSheetDialog.RECOMMENDATION_TYPE,
            object : ArchiveCallback {
                override fun performArchive() {
                    viewModel.unArchiveAudio(audio)
                }
            })
        dialog.show((activity as DashboardActivity).supportFragmentManager, ArchiveBottomSheetDialog.NAME)
    }
}