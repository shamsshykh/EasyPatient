package com.app.easy_patient.ui.audio

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easy_patient.R
import com.app.easy_patient.activity.PrescriptionDetailActivity
import com.app.easy_patient.activity.audio_detail.AudioDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.adapter.AudioAdapter
import com.app.easy_patient.databinding.FragmentAudioBinding
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.ktx.gone
import com.app.easy_patient.ktx.show
import com.app.easy_patient.model.kotlin.Audio
import com.app.easy_patient.util.AppConstants

class AudioFragment : BaseFragment() {
    lateinit var bi: FragmentAudioBinding
    lateinit var viewModel: DashboardViewModel
    lateinit var progressDialog: ProgressDialog
    lateinit var searchItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)
        bi = FragmentAudioBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            audioAdapter = AudioAdapter(::onAudioSelected, :: onArchiveClick, true)
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

        bi.searchViewLayout.imgCloseSearchView.setOnClickListener {
            bi.searchViewLayout.llSearchView.gone()
            searchItem.isVisible = true
        }
    }

    private fun initObserver() {
        viewModel.progress.observe(viewLifecycleOwner) { show ->
            if (show) progressDialog.show() else progressDialog.dismiss()
        }
    }

    private fun onAudioSelected(audio: Audio) {
        viewModel.updateCount("audio", audio.audio_files!![0].id!!)
        viewModel.updateCount.observe(viewLifecycleOwner){
            if (it){
                val intent = Intent(context, AudioDetailActivity::class.java)
                intent.putExtra(AppConstants.INTENT_KEYS.AUDIO_ID, audio.id)
                startActivity(intent)
            }
        }
    }

    private fun onArchiveClick(audio: Audio) {
        viewModel.archiveAudioDocument(audio)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.orientation_fragment_menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        searchItem.isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                item.isVisible = false
                bi.searchViewLayout.llSearchView.show()
            }
            R.id.action_archive -> findNavController()
                .navigate(R.id.action_audioFragment_to_archiveAudioFragment)
            else -> super.onOptionsItemSelected(item)
        }
        return true

    }
}