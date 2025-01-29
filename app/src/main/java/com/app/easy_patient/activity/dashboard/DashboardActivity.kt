package com.app.easy_patient.activity.dashboard

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.postDelayed
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.*
import com.app.easy_patient.R
import com.app.easy_patient.activity.BaseActivity
import com.app.easy_patient.activity.NewMedicineActivity
import com.app.easy_patient.analytics.EasyPatientAnalytics
import com.app.easy_patient.databinding.ActivityDashboardNewBinding
import com.app.easy_patient.ktx.gone
import com.app.easy_patient.ktx.oneDayBeforeAppointmentNotificationTime
import com.app.easy_patient.ktx.show
import com.app.easy_patient.ktx.twoHrsBeforeAppointmentNotificationTime
import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.receiver.NotificationCancelReceiver
import com.app.easy_patient.ui.home.CallMedicineReminderWorker
import com.app.easy_patient.ui.home.CreateAppointmentNotification
import com.app.easy_patient.ui.home.CreateReminderNotification
import com.app.easy_patient.ui.home.DeleteReminderNotification
import com.app.easy_patient.util.Constants
import com.app.easy_patient.util.SoundService
import com.app.easy_patient.util.UIUpdater
import com.app.easy_patient.util.Utility
import com.app.easy_patient.worker.CreateMedicineReminderWorker
import com.app.easy_patient.worker.PeriodicMedicineReminderNotificationWorker
import com.app.easy_patient.wrappers.Resource
import dagger.android.AndroidInjection
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class DashboardActivity : BaseActivity() {

    private lateinit var bi: ActivityDashboardNewBinding
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navController: NavController
    private var currentDestinationId = R.id.homeFragment
    private lateinit var uiUpdater: UIUpdater
    lateinit var medicineCallback: ActivityResultLauncher<Intent>

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    @Inject
    lateinit var analytics: EasyPatientAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelProvider).get(DashboardViewModel::class.java)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_dashboard_new)
        bi.view = this@DashboardActivity
        bi.viewmodel = viewModel

        manageIntent()
        setupViews()
        initObserver()
    }

    private fun manageIntent() {

        if (intent.hasExtra(Constants.INTENT_KEYS.MEDICINE_REMINDER)) {
            viewModel.medicineReminder = intent.getParcelableExtra(Constants.INTENT_KEYS.MEDICINE_REMINDER)

            if (intent.hasExtra(Constants.INTENT_KEYS.STOP_REMINDER_SOUND)) {
                intent.extras?.get(Constants.INTENT_KEYS.STOP_REMINDER_SOUND)?.let {
                    if (it as Boolean) {
                        val intent = Intent(applicationContext, NotificationCancelReceiver::class.java)
                        intent.putExtra(NotificationCancelReceiver.NOTIFICATION_ID, viewModel.medicineReminder?.notificationId())
                        sendBroadcast(intent)
                    }
                }
            }

            viewModel.medicineReminder?.let { reminder ->
                analytics.medicineReminderAnalytics().logNotificationClicked(
                    time = reminder.reminderTime(),
                    name = reminder.name,
                    dosage = reminder.dosage,
                    notification = reminder.notification,
                    critical = reminder.critical
                )
            }
        }
    }

    override fun onDestroy() {
        uiUpdater.stopUpdates()
        super.onDestroy()
    }

    private fun setupViews() {
        updateMenu()

        setSupportActionBar(bi.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        medicineCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.loadLocalMedicines()
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bi.bottomNavView, navController)

        bi.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> navController.navigate(
                    R.id.homeFragment,
                    null,
                    navOptions()
                )

                R.id.medicineFragment -> navController.navigate(
                    R.id.medicineFragment,
                    null,
                    navOptions()
                )

                R.id.appointmentFragment -> navController.navigate(
                    R.id.appointmentFragment,
                    null,
                    navOptions()
                )

                R.id.moreFragment -> {
                    viewModel.swipeEnable(false)
                    navController.navigate(
                        R.id.moreFragment,
                        null,
                        navOptionsMenu()
                    )
                }
            }
            true
        }

        bi.bottomNavView.setOnItemReselectedListener {
            return@setOnItemReselectedListener
        }

        // Setting BottomNavigationView visibility for different screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestinationId = destination.id
            // Screens which show Bottom Navigation
            if (getScreenIdsWithBottomNavigation().contains(destination.id)) {
                showBottomNavigation()
            }
            // Screens which hide Bottom Navigation
            else {
                hideBottomNavigation()
            }

            showHideActionbar(getScreenIdsWithActionBar().contains(destination.id))
            setScreenTitle(destination.id)
            invalidateOptionsMenu()
        }

        uiUpdater = UIUpdater {
            viewModel.getAllMedicineReminders()
        }

        scheduleUpdateReminders()
    }

    private fun updateMenu() {
        val list = arrayListOf<com.app.easy_patient.model.kotlin.Menu>()
        list.add(
            com.app.easy_patient.model.kotlin.Menu(
                id = 1,
                name = getString(R.string.meal_plan_str)
            )
        )
        list.add(
            com.app.easy_patient.model.kotlin.Menu(
                id = 2,
                name = getString(R.string.prescriptions_str),
                notificationCount = 0
            )
        )
        list.add(
            com.app.easy_patient.model.kotlin.Menu(
                id = 3,
                name = getString(R.string.orientations)
            )
        )
        list.add(
            com.app.easy_patient.model.kotlin.Menu(
                id = 4,
                name = getString(R.string.document_str)
            )
        )
        viewModel._menuList.value = Resource.Success(list)
    }

    private fun initObserver() {
        viewModel.medicineList.observe(this) {
            if (it is Resource.Success) {
                uiUpdater.startUpdates()
                viewModel.createUpComingNotificationForUnKnownMedicine(it.data)
            }
        }

        viewModel.uiStates.observe(this) { state ->
            when (state) {
                is CreateReminderNotification -> Utility.setAlarm(applicationContext, state.medicineReminder)
                is CreateAppointmentNotification -> createAppointmentNotifications(state.appointment)
                is DeleteReminderNotification -> Utility.cancelAlarm(applicationContext, Utility.pendingIntent(applicationContext, state.medicineReminder, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE))
                is CallMedicineReminderWorker -> {
                    val data = Data.Builder().putInt("medicineId", state.medicine.id).build()
                    val work = OneTimeWorkRequest.Builder(CreateMedicineReminderWorker::class.java)
                        .setInputData(data).build()
                    WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                        CreateMedicineReminderWorker.TAG,
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        work
                    )
                }
            }
        }
    }

    private fun createAppointmentNotifications(appointment: Appointment) {
        val currentTime = Calendar.getInstance()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            currentTime.timeInMillis = SystemClock.elapsedRealtime()
        }
        // currentTime.add(Calendar.MINUTE, 5)

        val beforeOneDayTime = appointment.oneDayBeforeAppointmentNotificationTime()
        if (beforeOneDayTime > currentTime.timeInMillis) {
            Utility.setAppointmentAlarm(applicationContext,
                beforeOneDayTime,
                Utility.appointmentPendingIntent(applicationContext, appointment, requestCode = "${appointment.id}100".toInt())
            )
            analytics.appointmentAnalytics().logNotificationRegistered(
                appointmentId = appointment.id,
                notificationType = Appointment.ONE_DAY,
                appointmentTime = appointment.notify_at
            )
        }

        val before2hrsTime = appointment.twoHrsBeforeAppointmentNotificationTime()
        if (before2hrsTime > currentTime.timeInMillis) {
            Utility.setAppointmentAlarm(applicationContext,
                before2hrsTime,
                Utility.appointmentPendingIntent(applicationContext, appointment, requestCode = "${appointment.id}200".toInt())
            )
            analytics.appointmentAnalytics().logNotificationRegistered(
                appointmentId = appointment.id,
                notificationType = Appointment.TWO_HOURS,
                appointmentTime = appointment.date
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_add)?.let {
            it.isVisible = getScreenIdsWithAddButton().contains(currentDestinationId)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_add -> {
                onAddMedicineClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("ResourceType")
    fun navOptionsMenu(): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.transition.slide_in_right)
            .setExitAnim(R.transition.slide_out_left)
            .setPopEnterAnim(R.transition.slide_in_left)
            .setPopExitAnim(R.transition.slide_out_right)
            .setPopUpTo(navController.graph.startDestination, false)
            .build()
    }

    @SuppressLint("ResourceType")
    private fun navOptions(): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.transition.fade_in)
            .setExitAnim(R.transition.fade_out)
            .setPopEnterAnim(R.transition.fade_in)
            .setPopExitAnim(R.transition.fade_out)
            .setPopUpTo(navController.graph.startDestination, false)
            .build()
    }

    private fun showBottomNavigation() {
        bi.bottomNavView.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation() {
        bi.bottomNavView.visibility = View.GONE
    }

    private fun showHideActionbar(visibility: Boolean) {
        if (visibility)
            bi.toolbar?.show()
        else
            bi.toolbar?.gone()
    }

    private fun setScreenTitle(id: Int) {
        when (id) {
            R.id.medicineFragment -> title = getString(R.string.med_nav_str)
            R.id.appointmentFragment -> title = getString(R.string.appointments)
            R.id.orientationFragment -> title = getString(R.string.orientations)
            R.id.prescriptionFragment -> title = getString(R.string.prescriptions)
            R.id.mealPlanFragment -> title = getString(R.string.meal_plan_str)
            R.id.audioFragment -> title = getString(R.string.document_str)
            R.id.archiveAudioFragment -> title = getString(R.string.document_arc)
            R.id.profileFragment -> title = getString(R.string.profile_str)
            R.id.archivePrescriptionFragment -> title = getString(R.string.prescriptions_archive_str)
            R.id.archivedOrientationFragment -> title = getString(R.string.archived_orientation_str)
            R.id.archivedMealPlanFragment -> title = getString(R.string.archived_meal_plan_str)
            R.id.settingsFragment -> title = getString(R.string.settings)
            R.id.deleteAccountFragment -> title = getString(R.string.delete_account_sub_str)
        }
    }

    /**
     * Add all the IDs of the destination fragments in
     * which you want to show the Bottom Navigation View
     * component.
     */
    private fun getScreenIdsWithBottomNavigation() = listOf(
        R.id.homeFragment,
        R.id.medicineFragment,
        R.id.appointmentFragment
    )

    private fun getScreenIdsWithActionBar() = listOf(
        R.id.medicineFragment,
        R.id.appointmentFragment,
        R.id.orientationFragment,
        R.id.prescriptionFragment,
        R.id.mealPlanFragment,
        R.id.audioFragment,
        R.id.profileFragment,
        R.id.archivePrescriptionFragment,
        R.id.archivedOrientationFragment,
        R.id.archivedMealPlanFragment,
        R.id.settingsFragment,
        R.id.archiveAudioFragment,
        R.id.deleteAccountFragment
    )

    private fun getScreenIdsWithAddButton() = listOf(
        R.id.medicineFragment
    )

    private fun onAddMedicineClick() {
        val intent = Intent(this@DashboardActivity, NewMedicineActivity::class.java)
        intent.putExtra("flag", "new")
        medicineCallback.launch(intent)
    }

    override fun onBackPressed() {
        if (navController.graph.startDestination == navController.currentDestination?.id)
        {
            if (viewModel.backPressedOnce)
            {
                super.onBackPressed()
                return
            }

            viewModel.backPressedOnce = true
            showToast(this, getString(R.string.back_msg_str))

            Handler().postDelayed(2000) {
                viewModel.backPressedOnce = false
            }
        }
        else {
            super.onBackPressed()
            viewModel.swipeEnable(true)
        }
    }

    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        const val MEDICINE_REQUEST_CODE = 10110
    }

    private fun scheduleUpdateReminders() {
        val work = PeriodicWorkRequestBuilder<PeriodicMedicineReminderNotificationWorker>(10, TimeUnit.HOURS).addTag(PeriodicMedicineReminderNotificationWorker.TAG).build()
        WorkManager.getInstance(this@DashboardActivity).enqueueUniquePeriodicWork(PeriodicMedicineReminderNotificationWorker.TAG, ExistingPeriodicWorkPolicy.KEEP, work)
    }
}