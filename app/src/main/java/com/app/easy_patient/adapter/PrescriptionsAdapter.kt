package com.app.easy_patient.adapter

import com.app.easy_patient.database.PrescriptionListModel
import com.app.easy_patient.util.ListUpdate
import androidx.recyclerview.widget.RecyclerView
import android.app.ProgressDialog
import android.content.Context
import com.app.easy_patient.util.NetworkChecker
import com.app.easy_patient.database.EasyPatientDatabase
import com.app.easy_patient.database.PrescriptionDetailDao
import com.app.easy_patient.model.StatusModel
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import com.app.easy_patient.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.Intent
import android.service.autofill.OnClickAction
import android.view.View
import com.app.easy_patient.activity.PrescriptionDetailActivity
import android.widget.Toast
import com.app.easy_patient.network.GetDataService
import com.app.easy_patient.network.RetrofitClientInstance
import com.app.easy_patient.fragment.ArchiveBottomSheetDialog
import com.app.easy_patient.util.ArchiveCallback
import com.app.easy_patient.activity.dashboard.DashboardActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PrescriptionsAdapter(
    var context: Context,
    var prescriptionList: ArrayList<PrescriptionListModel>,
    archive: Boolean,
    listUpdate: ListUpdate,
    private val onClickAction : (PrescriptionListModel, Boolean) -> Unit
) : RecyclerView.Adapter<PrescriptionsAdapter.ViewHolder>() {
    private val prescriptionListFilter = ArrayList<PrescriptionListModel>()
    var archive: Boolean
    var progressDialog: ProgressDialog? = null
    var networkChecker: NetworkChecker
    private val db: EasyPatientDatabase
    private val mPrescriptionDetailDao: PrescriptionDetailDao
    private val listUpdate: ListUpdate
    var OrientationArchiveResponse: Call<StatusModel>? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnArchiveStatus: MaterialButton
        var txtType: TextView
        var tvDate: TextView
        var tvSpecialist: TextView
//        var tvValidity: TextView
        var tvMonth: TextView
        var txtStatus: TextView
        var txtNew: TextView

        init {
            btnArchiveStatus = itemView.findViewById(R.id.btn_archive_status)
            tvDate = itemView.findViewById(R.id.tv_date)
            tvSpecialist = itemView.findViewById(R.id.tv_specialist)
//            tvValidity = itemView.findViewById(R.id.tv_validity)
            txtType = itemView.findViewById(R.id.txtType)
            tvMonth = itemView.findViewById(R.id.tv_month)
            txtStatus = itemView.findViewById(R.id.txtStatus)
            txtNew = itemView.findViewById(R.id.txtNew)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        /*if (archive)
            listItem = layoutInflater.inflate(R.layout.adapter_prescriptions_archive_layout, parent, false);
        else
            listItem = layoutInflater.inflate(R.layout.adapter_prescriptions_layout, parent, false);*/
        val listItem: View =
            layoutInflater.inflate(R.layout.adapter_prescriptions_layout_new, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listData = prescriptionList[position]
        holder.txtType.text = listData.type
        holder.tvMonth.text = context.getString(R.string.received_on_new_str, listData.title)
        if (listData.clinic_name != null) {
            holder.tvDate.text = listData.clinic_name
        }
        holder.tvSpecialist.text = context.getString(R.string.doctor_str, listData.specialist)
//        if (listData.validity_revenue != null) {
//            holder.tvValidity.visibility = View.VISIBLE
//            holder.tvValidity.setPrescriptionStatus(listData.validity_revenue!!)
//        }
        if (archive) {
            holder.btnArchiveStatus.text = context.getString(R.string.dearchive_btn_st)
            holder.btnArchiveStatus.icon = context.getDrawable(R.drawable.ic_unarchive)
        } else {
            holder.btnArchiveStatus.text = context.getString(R.string.ocultar_str)
            holder.btnArchiveStatus.icon = context.getDrawable(R.drawable.ic_archive)
        }
        holder.itemView.setOnClickListener { v: View? ->
            onClickAction.invoke(listData,archive)
        }
        holder.btnArchiveStatus.setOnClickListener {
            if (networkChecker.isConnectingNetwork) postArchiveStatus(position) else Toast.makeText(
                context, context.resources.getString(R.string.no_internet), Toast.LENGTH_SHORT
            ).show()
        }
        holder.txtNew.visibility = if (listData.is_new) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return prescriptionList.size
    }

    private fun postArchiveStatus(position: Int) {
        val api = RetrofitClientInstance.getRetrofitInstance(context).create(
            GetDataService::class.java
        )
        if (archive) {
            val dialog = ArchiveBottomSheetDialog(
                ArchiveBottomSheetDialog.PRESCRIPTION_TYPE,
                object : ArchiveCallback {
                    override fun performArchive() {
                        OrientationArchiveResponse =
                            api.putPrescriptionsAvailable(prescriptionList[position].id)
                        request(position)
                    }
                })
            dialog.show(
                (context as DashboardActivity).supportFragmentManager,
                ArchiveBottomSheetDialog.NAME
            )
        } else {
            OrientationArchiveResponse = api.postPrescriptionsArchive(prescriptionList[position].id)
            request(position)
        }
    }

    private fun request(position: Int) {
        progressDialog = ProgressDialog(context, R.style.AppCompatAlertDialogStyle)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage(context.getString(R.string.loading_str))
        progressDialog!!.show()
        OrientationArchiveResponse!!.enqueue(object : Callback<StatusModel> {
            override fun onResponse(call: Call<StatusModel>, response: Response<StatusModel>) {
                if (response.isSuccessful) {
                    val status = response.body()!!.status
                    if (status) {
                        val model = prescriptionList[position]
                        if (archive) {
                            EasyPatientDatabase.databaseWriteExecutor.execute {
                                mPrescriptionDetailDao.insertPrescriptionItem(
                                    model
                                )
                            }
                            //    Toast.makeText(context,context.getResources().getString(R.string.unarchived_successfully),Toast.LENGTH_SHORT).show();
                        } else {
                            EasyPatientDatabase.databaseWriteExecutor.execute {
                                mPrescriptionDetailDao.deleteItem(
                                    model.id
                                )
                            }
                            //   Toast.makeText(context,context.getResources().getString(R.string.archived_successfully),Toast.LENGTH_SHORT).show();
                        }
                        prescriptionList.removeAt(position)
                        listUpdate.updateUI(prescriptionList.size)
                        notifyDataSetChanged()
                    } else Toast.makeText(
                        context,
                        context.getString(R.string.error_str),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.server_detail_error_str),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<StatusModel>, t: Throwable) {
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        prescriptionList.clear()
        if (charText.length == 0) {
            prescriptionList.addAll(prescriptionListFilter)
        } else {
            for (wp in prescriptionListFilter) {
                if (wp.search_keywords != null) {
                    val items = wp.search_keywords!!.split("\\s*,\\s*").toTypedArray()
                    for (i in items.indices) {
                        if (items[i].toLowerCase(Locale.getDefault())
                                .contains(charText)
                        ) prescriptionList.add(wp)
                    }
                }
                //                if ((wp.getTitle().toLowerCase(Locale.getDefault()).contains(charText) ||
//                        wp.getSpecialist().toLowerCase(Locale.getDefault()).contains(charText))) {
//                    prescriptionList.add(wp);
//                }
            }
        }
        notifyDataSetChanged()
        listUpdate.updateUI(prescriptionList.size)
    }

    private fun convertDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val month: String
        val dd: String
        val year: String
        var hour: String
        var min: String
        var date: Date? = Date()
        try {
            date = inputFormat.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal = Calendar.getInstance()
        cal.time = date
        month = checkDigit(cal[Calendar.MONTH] + 1)
        dd = checkDigit(cal[Calendar.DATE])
        year = checkDigit(cal[Calendar.YEAR])
        return "$dd/$month/$year"
    }

    private fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    init {
        prescriptionListFilter.addAll(prescriptionList)
        this.archive = archive
        networkChecker = NetworkChecker(context)
        db = EasyPatientDatabase.getDatabase(context)
        mPrescriptionDetailDao = db.prescriptionDetailDao()
        this.listUpdate = listUpdate
    }
}