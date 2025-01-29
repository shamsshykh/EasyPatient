package com.app.easy_patient.activity.appointment_detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ResolveInfo
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.R
import com.app.easy_patient.activity.BaseActivity
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.analytics.EasyPatientAnalytics
import com.app.easy_patient.databinding.ActivityAppointmentDetailBinding
import com.app.easy_patient.model.kotlin.Appointment
import com.app.easy_patient.util.AppConstants
import com.app.easy_patient.util.GpsUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.android.AndroidInjection
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


class AppointmentDetailActivity : BaseActivity() {

    lateinit var bi: ActivityAppointmentDetailBinding
    lateinit var viewModel: AppointmentDetailViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val perms = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    var currentLocation: Location? = null


    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    @Inject
    lateinit var analytics: EasyPatientAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        viewModel = ViewModelProvider(this, viewModelProvider).get(AppointmentDetailViewModel::class.java)
        bi = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail)
        bi.view = this
        bi.viewmodel = viewModel
        bi.appointment = viewModel.getAppointmentData(appointmentId = appointmentId())
        bi.executePendingBindings()
        setContentView(bi.root)

        setupViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViews() {
        setSupportActionBar(bi.toolbar)
        title = getString(R.string.appointments)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        viewModel.isFromNotification = intent.getBooleanExtra(AppConstants.INTENT_KEYS.IS_FROM_NOTIFICATION, false)

        if (viewModel.isFromNotification) {
            val requestCode = intent.getIntExtra(AppConstants.INTENT_KEYS.APPOINTMENT_NOTIFICATION_REQUEST_CODE, 100)
            analytics.appointmentAnalytics().logNotificationClicked(
                appointmentId = viewModel.appointment.id,
                notificationType = if (requestCode == "${viewModel.appointment.id}100".toInt())
                    Appointment.ONE_DAY
                else
                    Appointment.TWO_HOURS,
                appointmentTime = if (requestCode == "${viewModel.appointment.id}100".toInt())
                    viewModel.appointment.notify_at
                else
                    viewModel.appointment.date
            )
        }

        if (EasyPermissions.hasPermissions(this, *perms)) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }
    }

    fun onShowRouteClick(appointment: Appointment) {
        requestLocationPermission()
    }

    fun onPhoneClick(appointment: Appointment) {
        appointment.phone?.let {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$it")
            startActivity(intent)
        }
    }

    fun onWhatsappClick(appointment: Appointment) {
        appointment.whatsapp_value?.let {
            val url = "https://api.whatsapp.com/send?phone=$it"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
    }

    fun onEmailClick(appointment: Appointment) {
        appointment.email?.let {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(it))
            emailIntent.type = "text/plain"
            val pm = packageManager
            val matches = pm.queryIntentActivities(emailIntent, 0)
            var best: ResolveInfo? = null
            for (info in matches) if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase()
                    .contains("gmail")
            ) best = info
            if (best != null) emailIntent.setClassName(
                best.activityInfo.packageName,
                best.activityInfo.name
            )
            startActivity(emailIntent)
        }

    }

    private fun appointmentId(): Int {
        intent?.let {
            return it.getIntExtra(AppConstants.INTENT_KEYS.APPOINTMENT_ID, 0)
        }
        return 0
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(AppConstants.REQUEST_CODE_KEYS.REQUEST_LOCATION_PERMISSION)
    fun requestLocationPermission() {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            showClinicLocationOnMap()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_message_str),
                AppConstants.REQUEST_CODE_KEYS.REQUEST_LOCATION_PERMISSION,
                *perms
            )
        }
    }

    private fun showClinicLocationOnMap() {
        GpsUtils(this).turnGPSOn { isGPSEnable -> // turn on GPS
            if (isGPSEnable) {
                if (currentLocation != null) {
                    showAddressOnMap()
                } else {
                    fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener {
                        currentLocation = it
                        showAddressOnMap()
                    }
                }
            } else {
                showToast(this, getString(R.string.current_location_not_available_str))
            }
        }
    }

    private fun showAddressOnMap() {
        val appointment = viewModel.appointment
        val uri =
            "http://maps.google.com/maps?saddr=${currentLocation?.latitude},${currentLocation?.longitude},&daddr=${appointment.latitude ?: ""},${appointment.longitude ?: ""}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1001) {
            showClinicLocationOnMap()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (viewModel.isFromNotification) {
            val intent = Intent(this@AppointmentDetailActivity, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            startActivity(intent)
        }
    }
}