package com.app.easy_patient.ui.delete_account

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.app.easy_patient.R
import com.app.easy_patient.activity.LoginActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.databinding.FragmentDeleteAccountBinding
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.ktx.showToast
import com.app.easy_patient.util.SharedPrefs
import com.app.easy_patient.util.Utility
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeleteAccountFragment : BaseFragment() {

    private lateinit var bi: FragmentDeleteAccountBinding
    private lateinit var viewModel: DeleteAccountViewModel
    private lateinit var parentViewModel: DashboardViewModel
    private lateinit var sharedPrefs: SharedPrefs
    lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelProvider).get(DeleteAccountViewModel::class.java)
        parentViewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)

        bi = FragmentDeleteAccountBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return bi.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        initObserver()
    }

    private fun initObserver() {
        viewModel.uiStates.observe(viewLifecycleOwner) { state ->
            progressDialog.dismiss()
            when (state) {
                LogoutUser -> logoutUser()
                InvalidCredentials -> requireContext().showToast(getString(R.string.invalid_user_or_pass_str))
                Failure -> requireContext().showToast(getString(R.string.server_detail_error_str))
            }
        }
    }

    private fun setupViews() {
        progressDialog = ProgressDialog(requireContext(), R.style.AppCompatAlertDialogStyle)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.loading_str))

        sharedPrefs = SharedPrefs(requireContext())
        bi.etEmail.setText(sharedPrefs.email)

        bi.btnLogin.setOnClickListener {
            if (bi.etEmail.text.isEmpty()) {
                bi.etEmail.error = getString(R.string.enter_email_str)
                return@setOnClickListener
            }

            if (bi.etPassword.text.isEmpty()) {
                bi.etEmail.error = getString(R.string.enter_email_str)
                return@setOnClickListener
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.app_name)
                .setMessage(R.string.delete_account_msg_str)
                .setNegativeButton(R.string.cancel_str) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.delete_medicine_str) { dialog, _ ->
                    dialog.dismiss()
                    progressDialog.show()
                    viewModel.deleteUserAccount(bi.etEmail.text.toString().trim(), bi.etPassword.text.toString())
                }
                .show()
        }
    }

    private fun logoutUser() {
        WorkManager.getInstance(requireContext().applicationContext).cancelAllWork()
        sharedPrefs.deleteAll()
        parentViewModel.cancelAllPendingAlarms()
        Utility.setupBootReceiver(requireContext().applicationContext, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        parentViewModel.logoutUser()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        activity?.finish()
    }
}