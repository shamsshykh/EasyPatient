package com.app.easy_patient.fragment

import com.app.easy_patient.fragment.base.BaseFragment
import android.widget.LinearLayout
import com.app.easy_patient.network.GetDataService
import android.app.ProgressDialog
import com.app.easy_patient.model.AppointmentsListModel
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.adapter.AppointmentsAdapter
import com.app.easy_patient.database.EasyPatientDatabase
import com.app.easy_patient.util.NetworkChecker
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.app.easy_patient.R
import com.app.easy_patient.network.RetrofitClientInstance
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import com.app.easy_patient.receiver.AppointmentReceiver
import android.app.PendingIntent
import android.os.AsyncTask
import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AppointmentsFragment : BaseFragment() {
    var placeholder: LinearLayout? = null
    var mContext: Context? = null
    var api: GetDataService? = null
    var progressDialog: ProgressDialog? = null
    var appointmentList = ArrayList<AppointmentsListModel>()
    var appointmentsRecycler: RecyclerView? = null
    var mAdapter: AppointmentsAdapter? = null
    private var db: EasyPatientDatabase? = null

    // private AppointmentDetailDao mAppointmentDetailDao;
    var networkChecker: NetworkChecker? = null
    var df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_appointments, container, false)
        placeholder = v.findViewById(R.id.ll_placeholder)
        appointmentsRecycler = v.findViewById(R.id.recycler_appointments)
        networkChecker = NetworkChecker(activity)
        db = EasyPatientDatabase.getDatabase(activity)
        // mAppointmentDetailDao = db.appointmentDetailDao();
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        progressDialog = ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage(getString(R.string.loading_str))
        api = RetrofitClientInstance.getRetrofitInstance(mContext).create(
            GetDataService::class.java
        )
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(mContext)
        appointmentsRecycler!!.layoutManager = layoutManager
    }

    private fun loadAppointmentsList() {
        progressDialog!!.show()
        val loadOrientationResponse = api!!.appointments
        loadOrientationResponse.enqueue(object : Callback<ArrayList<AppointmentsListModel>> {
            override fun onResponse(
                call: Call<ArrayList<AppointmentsListModel>>,
                response: Response<ArrayList<AppointmentsListModel>>
            ) {
                if (response.isSuccessful) {
//                    Log.e("response", String.valueOf(response.body().size()));
                    appointmentList = response.body()!!
                    if (appointmentList.size > 0) {
                        for (i in appointmentList.indices) {
                            val model = appointmentList[i]
                            EasyPatientDatabase.databaseWriteExecutor.execute {

                                // AppointmentsListModel appointmentDetail = mAppointmentDetailDao.getAppointmentDetail(model.getId());
                                val appointmentDetail: AppointmentsListModel? = null
                                if (appointmentDetail == null) {
                                    // mAppointmentDetailDao.insertAppointmentDetail(model);
                                    val calAppointmentTime = Calendar.getInstance()
                                    val calNotifyTime = Calendar.getInstance()
                                    val calCurrentTime = Calendar.getInstance()
                                    try {
                                        calAppointmentTime.time = df.parse(model.date)
                                        calNotifyTime.time = df.parse(model.notify_at)
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                    if (calCurrentTime.timeInMillis < calAppointmentTime.timeInMillis) {
                                        val intent =
                                            Intent(mContext, AppointmentReceiver::class.java)
                                        intent.putExtra("ID", model.id)

                                        // Appointment Time
                                        val firstAlarmId = System.currentTimeMillis().toInt()
                                        val p1Intent = PendingIntent.getBroadcast(
                                            mContext,
                                            firstAlarmId,
                                            intent,
                                            0
                                        )
                                        setAlarm(
                                            mContext,
                                            calAppointmentTime.timeInMillis,
                                            p1Intent
                                        )

                                        // 24 hours before notify time
                                        val secondAlarmId = System.currentTimeMillis()
                                            .toInt()
                                        val p2Intent = PendingIntent.getBroadcast(
                                            mContext,
                                            secondAlarmId,
                                            intent,
                                            0
                                        )
                                        calNotifyTime.add(Calendar.HOUR_OF_DAY, -24)
                                        setAlarm(mContext, calNotifyTime.timeInMillis, p2Intent)

                                        // 2 hours before notify time
                                        val thirdAlarmId = System.currentTimeMillis().toInt()
                                        val p3Intent = PendingIntent.getBroadcast(
                                            mContext,
                                            thirdAlarmId,
                                            intent,
                                            0
                                        )
                                        calNotifyTime.add(Calendar.HOUR_OF_DAY, 22)
                                        setAlarm(mContext, calNotifyTime.timeInMillis, p3Intent)
                                    }
                                }
                            }
                        }
                        mAdapter = AppointmentsAdapter(mContext, appointmentList)
                        appointmentsRecycler!!.adapter = mAdapter
                        placeholder!!.visibility = View.GONE
                    } else placeholder!!.visibility = View.VISIBLE
                }
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<ArrayList<AppointmentsListModel>>, t: Throwable) {
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //        if (appointmentList.size() == 0) {
        if (networkChecker!!.isConnectingNetwork) loadAppointmentsList() else AsyncTaskAppointmentList().execute()
        //        }else {
//            mAdapter = new AppointmentsAdapter(mContext, appointmentList);
//            appointmentsRecycler.setAdapter(mAdapter);
//        }
    }

    internal inner class AsyncTaskAppointmentList : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (appointmentList.size > 0) {
                mAdapter = AppointmentsAdapter(mContext, appointmentList)
                appointmentsRecycler!!.adapter = mAdapter
                placeholder!!.visibility = View.GONE
            } else placeholder!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): Void? {
            TODO("Not yet implemented")
        }
    }

    companion object {
        private fun setAlarm(context: Context?, time: Long, pendingIntent: PendingIntent) {
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            ) else alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }
}