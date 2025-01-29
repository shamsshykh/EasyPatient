package com.app.easy_patient.ui.home.dialog

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.databinding.MedicineDialogBottomSheetBinding
import com.app.easy_patient.ktx.gone
import com.app.easy_patient.ktx.show
import com.app.easy_patient.model.kotlin.MedicineReminder
import com.app.easy_patient.model.kotlin.MedicineStatus
import com.app.easy_patient.receiver.NotificationCancelReceiver
import com.app.easy_patient.util.Utility
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import java.util.*
import javax.inject.Inject

class MedicineDialogFragment: BottomSheetDialogFragment() {

    lateinit var bi: MedicineDialogBottomSheetBinding
    lateinit var medicineReminderItem: MedicineReminder
    lateinit var viewModel: DashboardViewModel

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    companion object {
        const val TAG = "MedicineDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)

        arguments?.let { bundle ->
            medicineReminderItem = bundle.get(TAG) as MedicineReminder
        }
        bi = MedicineDialogBottomSheetBinding.inflate(inflater, container, false).apply {
            view = this@MedicineDialogFragment
            medicineReminder = medicineReminderItem
        }
        return bi.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        bi.apply {
            medicineReminder?.apply {
                when (status) {
                    MedicineStatus.UpComing -> {
                        btnCancel.setOnClickListener {
                            firstLayout.gone()
                            secondLayout.show()
                        }
                        imgCancel.setOnClickListener {
                            dismiss()
                        }
                    }
                    MedicineStatus.Missed -> {
                        btnCancel.setOnClickListener {
                            removeNotification(notificationId())

                            firstLayout.gone()
                            secondLayout.show()
                        }

                        imgCancel.setOnClickListener {
                            removeNotification(notificationId())

                            dismiss()
                        }
                    }
                    else -> {
                        btnCancel.setOnClickListener {
                            removeNotification(notificationId())

                            dismiss()
                        }
                    }
                }
            }

            btnAccept.setOnClickListener {
                medicineReminderItem?.let { data ->
                    removeNotification(data.notificationId())
                    viewModel.updateMedicineReminderStatus(status = MedicineStatus.Taken, medicineReminder = data)
                }

                dismiss()
            }
        }
    }

    fun onTimeClick(value: Int) {
        if (medicineReminderItem.reminderTime() > Calendar.getInstance().timeInMillis)
            Utility.cancelAlarm(context!!.applicationContext, Utility.pendingIntent(context!!.applicationContext, medicineReminderItem, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE))

        viewModel.updateMedicineReminderTime(minutes = value, medicineReminder = medicineReminderItem)

        dismiss()
    }

    private fun removeNotification(notificationId: Int) {
        val intent = Intent(context, NotificationCancelReceiver::class.java)
        intent.putExtra(NotificationCancelReceiver.NOTIFICATION_ID, notificationId)
        requireActivity().sendBroadcast(intent)
    }
}