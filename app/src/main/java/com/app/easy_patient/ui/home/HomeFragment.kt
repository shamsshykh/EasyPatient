package com.app.easy_patient.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easy_patient.R
import com.app.easy_patient.activity.NewMedicineActivity
import com.app.easy_patient.activity.appointment_detail.AppointmentDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.analytics.EasyPatientAnalytics
import com.app.easy_patient.databinding.FragmentHomeBinding
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.ktx.loadCircularImage
import com.app.easy_patient.ktx.setDrawableImage
import com.app.easy_patient.model.kotlin.*
import com.app.easy_patient.ui.home.adapter.*
import com.app.easy_patient.ui.home.dialog.MedicineDialogFragment
import com.app.easy_patient.ui.home.dialog.NotificationDialogFragment
import com.app.easy_patient.util.AppConstants
import com.app.easy_patient.util.Constants
import com.app.easy_patient.util.SharedPrefs
import com.app.easy_patient.util.Utility
import com.app.easy_patient.wrappers.Resource
import javax.inject.Inject

class HomeFragment : BaseFragment() {

    private lateinit var bi: FragmentHomeBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var notificationAdapter: HomeAdapter<Notification>
    private lateinit var medicineReminderAdapter: HomeAdapter<MedicineReminder>
    private lateinit var menuAdapter: HomeAdapter<Menu>
    private lateinit var appointmentAdapter: HomeAdapter<Appointment>
    private lateinit var appointmentAdapterDays: HomeAdapter<Appointment>

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var analytics: EasyPatientAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)
        bi = FragmentHomeBinding.inflate(inflater, container, false).apply {
            view = this@HomeFragment
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        if (Constants.UpdateCount){
            viewModel.loadMenu()
            Constants.UpdateCount = false
        }

        return bi.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        initObserver()
    }

    private fun setupViews() {
        val fullName = sharedPrefs.name.split(" ")
        bi.txtUsername.text = getString(R.string.username_str, if (fullName.isNotEmpty()) fullName[0] else sharedPrefs.name)

        val imgPath: String? = sharedPrefs.imagePath
        if (imgPath != null) {
            bi.imgProfile.loadCircularImage(model = imgPath)
        } else {
            if (sharedPrefs.gender == "m")
                bi.imgProfile.setDrawableImage(R.drawable.ic_profile_pic_male_borderless)
            else
                bi.imgProfile.setDrawableImage(R.drawable.profile_pic_female_borderless)
        }

        notificationAdapter = HomeAdapter(onItemClick = ::onNotificationItemClick, type = NotificationType)
        bi.notificationAdapter = notificationAdapter
        bi.rvNotification.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = notificationAdapter
        }

        medicineReminderAdapter = HomeAdapter(onItemClick = ::onMedItemClick, type = MedicineType)
        bi.medicineAdapter = medicineReminderAdapter
        bi.rvMedicine.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = medicineReminderAdapter
        }

        menuAdapter = HomeAdapter(onItemClick = ::onMenuItemClick, type = MenuType)
        bi.menuAdapter = menuAdapter
        bi.rvMenu.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = menuAdapter
        }

        appointmentAdapter = HomeAdapter(onItemClick = ::onAppointmentItemClick, type = AppointmentType)
        bi.appointmentAdapter = appointmentAdapter
        bi.rvAppointment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = appointmentAdapter
        }


        appointmentAdapterDays = HomeAdapter(onItemClick = ::onAppointmentItemClick, type = AppointmentType)
        bi.appointmentDaysAdapter = appointmentAdapterDays
        bi.rvAppointmentDays.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = appointmentAdapterDays
        }

        bi.executePendingBindings()
    }

    private fun initObserver() {
        viewModel.medicineReminderList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    if (viewModel.medicineReminder != null) {
                        val item = resource.data.firstOrNull { it.id == viewModel.medicineReminder?.id }
                        item?.let {
                            onMedItemClick(it)
                            analytics.medicineReminderAnalytics().logReminderDialogOpened(
                                time = it.reminderTime(),
                                name = it.name,
                                dosage = it.dosage,
                                notification = it.notification,
                                critical = it.critical
                            )
                        }
                        viewModel.medicineReminder = null
                    }
                }
            }
        }
    }

    private fun onNotificationItemClick(notification: Notification) {
        val bundle = Bundle()
        bundle.putParcelable(NotificationDialogFragment.TAG, notification)
        findNavController().navigate(R.id.action_homeFragment_to_notificationDialogFragment, bundle)
    }

    private fun onMedItemClick(medicineReminder: MedicineReminder) {
        if (medicineReminder.status == MedicineStatus.UpComing ||
            medicineReminder.status == MedicineStatus.Missed) {

            val bundle = Bundle()
            bundle.putParcelable(MedicineDialogFragment.TAG, medicineReminder)
            findNavController().navigate(R.id.action_homeFragment_to_medicineReminderDialogFragment, bundle)

        } else if (medicineReminder.status == MedicineStatus.Future) {

            val medicine = viewModel.getMedicineById(medicineReminder.medicineId)
            if (medicine != null) {
                val intent = Intent(context, NewMedicineActivity::class.java)
                intent.putExtra("ID", medicine.id)
                intent.putExtra("flag", "edit")
                intent.putExtra("MEDICINE_DETAIL", medicine)
                (activity as DashboardActivity).medicineCallback.launch(intent)
            }
        }
    }

    private fun onMenuItemClick(menu: Menu) {
        when (menu.id) {
            1 -> findNavController().navigate(R.id.action_homeFragment_to_mealPlanFragment ,
                null,
                (activity as DashboardActivity).navOptionsMenu())

            2 -> findNavController().navigate(R.id.action_homeFragment_to_prescriptionFragment ,
                null,
                (activity as DashboardActivity).navOptionsMenu())

            3 -> findNavController().navigate(R.id.action_homeFragment_to_orientationFragment ,
                null,
                (activity as DashboardActivity).navOptionsMenu())

            4 -> findNavController().navigate(R.id.action_homeFragment_to_audioFragment ,
                null,
                (activity as DashboardActivity).navOptionsMenu())
        }
    }

    private fun onAppointmentItemClick(appointment: Appointment) {
        val intent = Intent(context, AppointmentDetailActivity::class.java)
        intent.putExtra(AppConstants.INTENT_KEYS.APPOINTMENT_ID, appointment.id)
        startActivity(intent)
    }

    fun onProfileClick() {
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onAddMedicalClick() {
        val intent = Intent(context, NewMedicineActivity::class.java)
        intent.putExtra("flag", "new")
        (activity as DashboardActivity).medicineCallback.launch(intent)
    }
}