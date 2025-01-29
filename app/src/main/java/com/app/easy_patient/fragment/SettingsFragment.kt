package com.app.easy_patient.fragment

import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.app.easy_patient.R
import com.app.easy_patient.activity.TermsActivity
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.util.SharedPrefs


class SettingsFragment : BaseFragment() {
    private var switchNotifyMedicine: SwitchCompat? = null
    private var switchNotifySchedule: SwitchCompat? = null
    private var switchRealtimeSchedule: SwitchCompat? = null
    private var switchScreenNotifyMedicine: SwitchCompat? = null
    private var sharedPrefs: SharedPrefs? = null
    private var tvTermsCondition: TextView? = null
    private var permissionCallback: ActivityResultLauncher<Intent>? = null
    private var realTimePermissionCallback: ActivityResultLauncher<Intent>? = null
    private lateinit var deleteAccountTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        switchNotifyMedicine = view.findViewById(R.id.switch_notify_medicine)
        switchScreenNotifyMedicine = view.findViewById(R.id.switch_second_notify_medicine)
        switchRealtimeSchedule = view.findViewById(R.id.switch_realtime_schedule)
        switchNotifySchedule = view.findViewById(R.id.switch_notify_schedule)
        tvTermsCondition = view.findViewById(R.id.tv_terms_condition_link)
        deleteAccountTxt = view.findViewById(R.id.tv_delete_account)
        sharedPrefs = SharedPrefs(activity)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchNotifyMedicine!!.isChecked = sharedPrefs!!.notifyMedicineStatus
        switchNotifySchedule!!.isChecked = sharedPrefs!!.notifyScheduleStatus
        switchScreenNotifyMedicine!!.isChecked = Settings.canDrawOverlays(activity)
        switchRealtimeSchedule!!.isChecked = isRealTimeNotificationPermissionEnabled()

        switchNotifyMedicine!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            sharedPrefs!!.notifyMedicineStatus = isChecked
        }
        switchNotifySchedule!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            sharedPrefs!!.notifyScheduleStatus = isChecked
        }
        switchScreenNotifyMedicine!!.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity?.packageName}")
            )
            permissionCallback!!.launch(intent)
        }
        tvTermsCondition!!.setOnClickListener { v: View? ->
            val intent = Intent(activity, TermsActivity::class.java)
            intent.putExtra("FromSettings", true)
            startActivity(intent)
        }
        switchRealtimeSchedule!!.setOnCheckedChangeListener { compoundButton, isChecked ->
            realTimePermission()
        }

        deleteAccountTxt.setOnClickListener {
            this.findNavController()
                .navigate(R.id.action_settingFragment_to_deleteAccountFragment)
        }
    }

    private fun realTimePermission() {
        val intent = Intent()
        val packageName: String = requireContext().packageName
        val pm = requireContext().getSystemService(POWER_SERVICE) as PowerManager?
        if (!pm!!.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
        } else {
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        }
        realTimePermissionCallback!!.launch(intent)
    }

    private fun isRealTimeNotificationPermissionEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = requireContext().packageName
            val pm = requireContext().getSystemService(POWER_SERVICE) as PowerManager
            return pm.isIgnoringBatteryOptimizations(packageName)
        }
        return true
    }

    private fun permissionResult() {
        permissionCallback = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            switchScreenNotifyMedicine!!.isChecked = Settings.canDrawOverlays(
                activity
            )
        }

        realTimePermissionCallback = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            switchRealtimeSchedule!!.isChecked = isRealTimeNotificationPermissionEnabled()
        }
    }
}