package com.app.easy_patient.ui.sidemenu

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.WorkManager
import com.app.easy_patient.R
import com.app.easy_patient.activity.LoginActivity
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.databinding.FragmentSideMenuBinding
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.ktx.loadCircularImage
import com.app.easy_patient.ktx.setDrawableImage
import com.app.easy_patient.ktx.stringToDate
import com.app.easy_patient.util.SharedPrefs
import com.app.easy_patient.util.Utility
import com.app.easy_patient.worker.PeriodicMedicineReminderNotificationWorker
import java.util.*

class SideMenuFragment : BaseFragment() {

    private lateinit var bi: FragmentSideMenuBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)
        bi = FragmentSideMenuBinding.inflate(inflater, container, false).apply {
            view = this@SideMenuFragment
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
        sharedPrefs = SharedPrefs(context)
        bi.txtUserName.text = sharedPrefs.name
        bi.txtUserEmail.text = sharedPrefs.email

        val imgPath: String? = sharedPrefs.imagePath
        if (imgPath != null) {
            bi.imgUser.loadCircularImage(model = imgPath, borderSize = 1f, borderColor = R.color.colorPrimary)
        } else {
            if (sharedPrefs.gender == "m")
                bi.imgUser.setDrawableImage(R.drawable.ic_profile_pic_male)
            else
                bi.imgUser.setDrawableImage(R.drawable.profile_pic_female)
        }
    }

    private fun initObserver() {
        viewModel.medicineNotification.observe(viewLifecycleOwner) { medicineReminders ->
            medicineReminders.forEach { reminder ->
                if (reminder.reminderTime() > Calendar.getInstance().timeInMillis)
                    Utility.cancelAlarm(requireContext().applicationContext, Utility.pendingIntent(requireContext().applicationContext, reminder, flag = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            }
        }

        viewModel.appointmentNotification.observe(viewLifecycleOwner) { appointment ->
            val beforeOneDayTime = Calendar.getInstance()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                beforeOneDayTime.timeInMillis = SystemClock.elapsedRealtime()
            }
            beforeOneDayTime.time = DashboardActivity.dateFormat.parse(appointment.notify_at)
            beforeOneDayTime.add(Calendar.HOUR_OF_DAY, -24)

            val before2hrsTime = Calendar.getInstance()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                before2hrsTime.timeInMillis = SystemClock.elapsedRealtime()
            }
            before2hrsTime.time = DashboardActivity.dateFormat.parse(appointment.date)
            before2hrsTime.add(Calendar.HOUR_OF_DAY, -2)

            val currentTime = Calendar.getInstance()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                currentTime.timeInMillis = SystemClock.elapsedRealtime()
            }

            if (beforeOneDayTime.timeInMillis > currentTime.timeInMillis)
                Utility.cancelAlarm(requireContext().applicationContext, Utility.appointmentPendingIntent(requireContext().applicationContext, appointment, flag = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE, requestCode = "${appointment.id}100".toInt()))

            if (before2hrsTime.timeInMillis > currentTime.timeInMillis)
                Utility.cancelAlarm(requireContext().applicationContext, Utility.appointmentPendingIntent(requireContext().applicationContext, appointment, flag = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE, requestCode = "${appointment.id}200".toInt()))
        }
    }

    fun onBackClick() {
        (activity as DashboardActivity).onBackPressed()
    }

    fun onHomeClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_homeFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onUserNameClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_profileFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onAppointmentClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_appointmentFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onMedicineClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_medicineFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onPrescriptionClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_prescriptionFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onOrientationClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_orientationFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onMealPlanClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_mealPlanFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onRecommendationsClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_audioFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onSettingsClick() {
        viewModel.swipeEnable(true)
        findNavController().navigate(R.id.action_moreFragment_to_settingsFragment ,
            null,
            (activity as DashboardActivity).navOptionsMenu())
    }

    fun onLogoutClick() {
        WorkManager.getInstance(requireContext().applicationContext).cancelAllWork()
        sharedPrefs.deleteAll()
        viewModel.cancelAllPendingAlarms()
        Utility.setupBootReceiver(requireContext().applicationContext, PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        viewModel.logoutUser()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        activity?.finish()
    }
}