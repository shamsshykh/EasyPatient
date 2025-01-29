package com.app.easy_patient.ui.medicine

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.R
import com.app.easy_patient.activity.NewMedicineActivity
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.databinding.FragmentMedicineBinding
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.model.kotlin.Medicine
import com.app.easy_patient.ui.medicine.adapter.MedicineAdapter
import com.app.easy_patient.wrappers.Resource
import com.google.android.gms.maps.model.Dash

class MedicineFragment : BaseFragment() {

    private lateinit var bi: FragmentMedicineBinding
    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)
        // Inflate the layout for this fragment
        bi = FragmentMedicineBinding.inflate(inflater, container, false).apply {
            view = this@MedicineFragment
            adapter = MedicineAdapter(::onMedicineItemClick)
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
    }

    private fun initObserver() {

    }

    fun addMedicineClick() {
        val intent = Intent(context, NewMedicineActivity::class.java)
        intent.putExtra("flag", "new")
        (activity as DashboardActivity).medicineCallback.launch(intent)
    }

    private fun onMedicineItemClick(medicine: Medicine, pos: Int) {
        val intent = Intent(context, NewMedicineActivity::class.java)
        intent.putExtra("ID", medicine.id)
        intent.putExtra("flag", "edit")
        intent.putExtra("MEDICINE_DETAIL", medicine)
        (activity as DashboardActivity).medicineCallback.launch(intent)
    }
}