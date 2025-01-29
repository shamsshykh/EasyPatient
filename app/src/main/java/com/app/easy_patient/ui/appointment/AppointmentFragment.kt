package com.app.easy_patient.ui.appointment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easy_patient.activity.appointment_detail.AppointmentDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.databinding.FragmentAppointmentBinding
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.ui.home.adapter.AppointmentTabType
import com.app.easy_patient.ui.home.adapter.HomeAdapter
import com.app.easy_patient.util.AppConstants

class AppointmentFragment : BaseFragment() {

    private lateinit var bi: FragmentAppointmentBinding
    private lateinit var viewModel: DashboardViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)
        bi = FragmentAppointmentBinding.inflate(inflater, container, false).apply {
            appointmentAdapter = HomeAdapter(onItemClick = ::onAppointmentSelected, type = AppointmentTabType)
            recyclerViewAppointment.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = appointmentAdapter
            }
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

    private fun setupViews() {
        viewModel.loadAppointments()
    }

    private fun initObserver() {

    }

    private fun onAppointmentSelected(appointment: Appointment) {
        val intent = Intent(context, AppointmentDetailActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEYS.APPOINTMENT_ID, appointment.id)
        startActivity(intent)
    }

}