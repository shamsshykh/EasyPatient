package com.app.easy_patient.activity.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.R
import com.app.easy_patient.activity.BaseActivity
import com.app.easy_patient.activity.LoginActivity
import com.app.easy_patient.activity.dashboard.DashboardActivity
import dagger.android.AndroidInjection
import javax.inject.Inject


class OnBoardingActivity : BaseActivity() {

    private lateinit var viewModel: OnBoardingViewModel
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory

    val perms = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.POST_NOTIFICATIONS,
    )

    private lateinit var realTimePermissionCallback: ActivityResultLauncher<Intent>
    private lateinit var fullScreenPermissionCallback: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.onboarding_activity)
        statusBar(R.color.white)

        viewModel = ViewModelProvider(this, viewModelProvider).get(OnBoardingViewModel::class.java)

        if (viewModel.loginStatus()) {
            startActivity(Intent(this@OnBoardingActivity, DashboardActivity::class.java))
            finish()
        } else {
            var allowed = true
            perms.forEach {
                if (ContextCompat.checkSelfPermission(this@OnBoardingActivity, it) != PackageManager.PERMISSION_GRANTED) {
                    allowed = false
                    return@forEach
                }
            }
            if (allowed) {
                launchLoginScreen()
            }
        }

        realTimePermissionCallback = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                fullScreenPermissionCallback.launch(intent)
            } else {
                launchLoginScreen()
            }
        }

        fullScreenPermissionCallback = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            launchLoginScreen()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!isRealTimeNotificationPermissionEnabled()) {
            realTimePermission()
        } else if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            fullScreenPermissionCallback.launch(intent)
        } else {
            launchLoginScreen()
        }
    }

    private fun launchLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun realTimePermission() {
        val intent = Intent()
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
        } else {
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        }
        realTimePermissionCallback.launch(intent)
    }

    private fun isRealTimeNotificationPermissionEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            return pm.isIgnoringBatteryOptimizations(packageName)
        }
        return true
    }

}
