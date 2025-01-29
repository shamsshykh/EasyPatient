package com.app.easy_patient.fragment

import com.app.easy_patient.fragment.base.BaseFragment
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.adapter.MedicinesAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.app.easy_patient.database.EasyPatientDatabase
import com.app.easy_patient.database.MedicineDetailDao
import com.app.easy_patient.model.kotlin.Medicine
import android.widget.LinearLayout
import android.app.ProgressDialog
import com.app.easy_patient.util.NetworkChecker
import android.os.Bundle
import com.app.easy_patient.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import com.app.easy_patient.activity.NewMedicineActivity
import com.app.easy_patient.network.GetDataService
import com.app.easy_patient.network.RetrofitClientInstance
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MedicinesFragment : BaseFragment() {
    var recyclerMedicine: RecyclerView? = null
    var mAdapter: MedicinesAdapter? = null
    var fabAdd: FloatingActionButton? = null
    private var db: EasyPatientDatabase? = null
    private var mMedicineDetailDao: MedicineDetailDao? = null
    var medicineDetailList: List<Medicine?> = ArrayList()
    var medicineListinDB: List<Medicine?> = ArrayList()
    var placeHolder: LinearLayout? = null
    var progressDialog: ProgressDialog? = null
    var networkChecker: NetworkChecker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_medicines, container, false)
        fabAdd = v.findViewById(R.id.fab)
        recyclerMedicine = v.findViewById(R.id.recycler_medicine)
        placeHolder = v.findViewById(R.id.ll_placeholder)
        val layoutManager = LinearLayoutManager(activity)
        recyclerMedicine?.layoutManager = layoutManager
        setHasOptionsMenu(true)
        networkChecker = NetworkChecker(activity)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.medicine_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                val intent = Intent(activity, NewMedicineActivity::class.java)
                intent.putExtra("flag", "new")
                startActivity(intent)
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initDefaultImages()
        progressDialog = ProgressDialog(activity, R.style.AppCompatAlertDialogStyle)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Loading...")
        db = EasyPatientDatabase.getDatabase(activity)
        mMedicineDetailDao = db?.medicineDetailDao()
        //        db.databaseWriteExecutor.execute(() -> {
//            medicineList = mMedicineDetailDao.getMedicineDetailData();
////            Log.e("Result_Size", String.valueOf(medicineList.size()));
//        });
        fabAdd!!.setOnClickListener { v: View? ->
            val intent = Intent(activity, NewMedicineActivity::class.java)
            intent.putExtra("flag", "new")
            startActivity(intent)
        }
        //        if (medicineDetailList.size() == 0)
//            loadMedicineList();
    }

    private fun initDefaultImages() {
        defaultImageMap["1"] = R.drawable.ic_med_1
        defaultImageMap["2"] = R.drawable.ic_med_2
        defaultImageMap["3"] = R.drawable.ic_med_3
        defaultImageMap["4"] = R.drawable.ic_med_4
        defaultImageMap["5"] = R.drawable.ic_med_5
        defaultImageMap["6"] = R.drawable.ic_med_6
        defaultImageMap["7"] = R.drawable.ic_med_7
        defaultImageMap["8"] = R.drawable.ic_med_8
        defaultImageMap["9"] = R.drawable.ic_med_9
        defaultImageMap["10"] = R.drawable.ic_med_10
        defaultImageMap["11"] = R.drawable.ic_med_11
        defaultImageMap["12"] = R.drawable.ic_med_12
    }

    override fun onResume() {
        super.onResume()
        //        if (medicineDetailList.size() == 0) {
        if (networkChecker!!.isConnectingNetwork) loadMedicineList()
        //        }else {
//            mAdapter = new MedicinesAdapter(getActivity(), medicineDetailList);
//            recyclerMedicine.setAdapter(mAdapter);
//            placeHolder.setVisibility(View.GONE);
//        }
    }

    private fun loadMedicineList() {
        progressDialog!!.show()
        val api = RetrofitClientInstance.getRetrofitInstance(activity).create(
            GetDataService::class.java
        )
        val loadOrientationResponse = api.medicines
        loadOrientationResponse.enqueue(object : Callback<ArrayList<Medicine?>> {
            override fun onResponse(
                call: Call<ArrayList<Medicine?>>,
                response: Response<ArrayList<Medicine?>>
            ) {
                if (response.isSuccessful) {
                    medicineDetailList = response.body()!!
                    if (medicineDetailList.size > 0) {
                        for (i in medicineDetailList.indices) {
                            val model = medicineDetailList[i]
                            EasyPatientDatabase.databaseWriteExecutor.execute {
                                val medicineDetail = mMedicineDetailDao!!.getMedicineDetail(
                                    model!!.id
                                )
                                if (medicineDetail == null) {
                                    val calFinishAlarmTime = Calendar.getInstance()
                                    val calNextAlarmTime = Calendar.getInstance()
                                    val calCurrentTime = Calendar.getInstance()
                                    val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    try {
                                        calNextAlarmTime.time = df.parse(model.start_time)
                                        calFinishAlarmTime.time = df.parse(model.start_time)
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                    calFinishAlarmTime.add(
                                        Calendar.DATE,
                                        model.number_of_days!! - 1
                                    )
                                    while (calCurrentTime.timeInMillis > calNextAlarmTime.timeInMillis) {
                                        calNextAlarmTime.add(
                                            Calendar.HOUR_OF_DAY,
                                            model.frequency!!
                                        )
                                    }
                                    // model.get(df.format(calNextAlarmTime.getTime()));
                                    mMedicineDetailDao!!.insertMedicineDetail(model)

                                    //Setting alarm for given start time
                                    if (calFinishAlarmTime.timeInMillis > calNextAlarmTime.timeInMillis) {
//                                        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
//                                        intent.putExtra("ID", model.getId());
//                                        intent.putExtra("MEDICINE_NAME", medicineDetail.getName());
//                                        intent.putExtra("FREQUENCY_UPDATE", medicineDetail.getFrequency());
//                                        intent.putExtra("NEXT_ALARM_TIME", medicineDetail.getNext_alarm_time());
//                                        intent.putExtra("START_TIME", medicineDetail.getStart_time());
//                                        intent.putExtra("ST_NOTIFICATION", medicineDetail.getSt_notification());
//                                        intent.putExtra("ST_CRITICAL", medicineDetail.getSt_critical());
//                                        intent.putExtra("DAY_OF_THE_WEEK", medicineDetail.getDays_of_the_week());
//                                        intent.putExtra("NUMBER_DAYS", medicineDetail.getNumber_of_days());
//                                        PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(), model.getId(), intent, 0);
//                                        setAlarm(getActivity(), calNextAlarmTime.getTimeInMillis(), pIntent);
                                    }
                                }
                            }
                        }
                        mAdapter = MedicinesAdapter(activity, medicineDetailList)
                        recyclerMedicine!!.adapter = mAdapter
                        placeHolder!!.visibility = View.GONE
                    } else placeHolder!!.visibility = View.VISIBLE
                } else placeHolder!!.visibility = View.VISIBLE
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<ArrayList<Medicine?>>, t: Throwable) {
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    companion object {
        @JvmField
        var defaultImageMap = HashMap<String, Int>()
        private fun setAlarm(context: Context, time: Long, pendingIntent: PendingIntent) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            ) else alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }
}