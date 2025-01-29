package com.app.easy_patient.ui.home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easy_patient.R
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.databinding.NotificationDialogBottomSheetBinding
import com.app.easy_patient.model.kotlin.Notification
import com.app.easy_patient.model.kotlin.SuggestedMedicine
import com.app.easy_patient.ui.home.adapter.HomeAdapter
import com.app.easy_patient.ui.home.adapter.HomeAdapterType
import com.app.easy_patient.ui.home.adapter.NotificationMedicineType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationDialogFragment: BottomSheetDialogFragment() {

    private lateinit var bi: NotificationDialogBottomSheetBinding
    lateinit var notificationItem: Notification
    lateinit var suggestedMedAdapter: HomeAdapter<SuggestedMedicine>
    var sugggestedMedList: MutableList<SuggestedMedicine> = mutableListOf()

    companion object {
        const val TAG = "NotificationDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let { bundle ->
            notificationItem = bundle.get(TAG) as Notification
            sugggestedMedList.addAll(notificationItem.suggestedMedicines!!)
        }
        suggestedMedAdapter = HomeAdapter(type = NotificationMedicineType)
        bi = NotificationDialogBottomSheetBinding.inflate(inflater, container, false).apply {
            view = this@NotificationDialogFragment
            notification = notificationItem
            suggestedMedicineAdapter = suggestedMedAdapter
            lifecycleOwner = viewLifecycleOwner
        }
        return bi.root
    }

    fun onCancelClick() {
        dismiss()
    }

    fun onOpenClick() {
        findNavController().navigate(R.id.action_notificationDialogFragment_to_medicineFragment,
            null,
            (activity as DashboardActivity).navOptionsMenu())
        dismiss()
    }
}