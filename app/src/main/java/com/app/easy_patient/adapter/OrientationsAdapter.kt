package com.app.easy_patient.adapter

import com.app.easy_patient.database.OrientationListModel
import com.app.easy_patient.util.ListUpdate
import androidx.recyclerview.widget.RecyclerView
import android.app.ProgressDialog
import android.content.Context
import com.app.easy_patient.util.NetworkChecker
import com.app.easy_patient.database.EasyPatientDatabase
import com.app.easy_patient.database.OrientationDetailDao
import com.app.easy_patient.model.StatusModel
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import com.app.easy_patient.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.app.easy_patient.activity.OrientationDetailActivity
import android.widget.Toast
import com.app.easy_patient.network.GetDataService
import com.app.easy_patient.network.RetrofitClientInstance
import com.app.easy_patient.fragment.ArchiveBottomSheetDialog
import com.app.easy_patient.util.ArchiveCallback
import com.app.easy_patient.activity.dashboard.DashboardActivity
import com.app.easy_patient.database.MealPlanModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OrientationsAdapter(
    var context: Context,
    var orientationList: ArrayList<OrientationListModel>,
    archive: Boolean,
    listUpdate: ListUpdate,
    private val onClickAction : (OrientationListModel, Boolean) -> Unit
) : RecyclerView.Adapter<OrientationsAdapter.ViewHolder>() {
    private val orientationListFilter = ArrayList<OrientationListModel>()
    var archive: Boolean
    var progressDialog: ProgressDialog? = null
    var networkChecker: NetworkChecker
    private val db: EasyPatientDatabase
    private val mOrientationDetailDao: OrientationDetailDao
    private val listUpdate: ListUpdate
    var OrientationArchiveResponse: Call<StatusModel>? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnArchiveStatus: MaterialButton
        var tvTitle: TextView
        var clinicName: TextView
        var tvSpecialist: TextView
        var imgNav: ImageView
        var txtNew: TextView

        init {
            btnArchiveStatus = itemView.findViewById(R.id.btn_archive_status)
            tvTitle = itemView.findViewById(R.id.tv_title)
            clinicName = itemView.findViewById(R.id.clinicName)
            tvSpecialist = itemView.findViewById(R.id.tv_specialist)
            imgNav = itemView.findViewById(R.id.imgNav)
            txtNew = itemView.findViewById(R.id.txtNew)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val listItem: View
        val layoutInflater = LayoutInflater.from(parent.context)
        listItem = layoutInflater.inflate(R.layout.adapter_orientation_layout_new, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listData = orientationList[position]
        if (archive) {
            holder.btnArchiveStatus.icon = context.getDrawable(R.drawable.ic_unarchive)
            holder.btnArchiveStatus.text = context.getString(R.string.dearchive_btn_st)
        } else {
            holder.btnArchiveStatus.icon = context.getDrawable(R.drawable.ic_archive)
            holder.btnArchiveStatus.text = context.getString(R.string.archive_btn_str)
        }
        holder.tvTitle.text = context.getString(R.string.received_on_new_str, listData.title)
        holder.clinicName.text = listData.clinic_name
        holder.tvSpecialist.text = context.getString(R.string.doctor_str, listData.specialist)
        holder.itemView.setOnClickListener {
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
        return orientationList.size
    }

    private fun postArchiveStatus(position: Int) {
        val api = RetrofitClientInstance.getRetrofitInstance(context).create(
            GetDataService::class.java
        )
        if (archive) {
            val dialog = ArchiveBottomSheetDialog(
                ArchiveBottomSheetDialog.ORIENTATION_TYPE,
                object : ArchiveCallback {
                    override fun performArchive() {
                        OrientationArchiveResponse =
                            api.putOrientationsAvailable(orientationList[position].id)
                        request(position)
                    }
                })
            dialog.show(
                (context as DashboardActivity).supportFragmentManager,
                ArchiveBottomSheetDialog.NAME
            )
        } else {
            OrientationArchiveResponse = api.postOrientationsArchive(orientationList[position].id)
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
                        if (status) {
                            val model = orientationList[position]
                            if (archive) {
                                EasyPatientDatabase.databaseWriteExecutor.execute {
                                    mOrientationDetailDao.insertOrientationItem(
                                        model
                                    )
                                }
                                // Toast.makeText(context,context.getResources().getString(R.string.unarchived_successfully),Toast.LENGTH_SHORT).show();
                            } else {
                                EasyPatientDatabase.databaseWriteExecutor.execute {
                                    mOrientationDetailDao.deleteOrientationItem(
                                        model.id
                                    )
                                }
                                // Toast.makeText(context,context.getResources().getString(R.string.archived_successfully),Toast.LENGTH_SHORT).show();
                            }
                        }
                        orientationList.removeAt(position)
                        notifyDataSetChanged()
                        listUpdate.updateUI(orientationList.size)
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
        orientationList.clear()
        if (charText.length == 0) {
            orientationList.addAll(orientationListFilter)
        } else {
            for (wp in orientationListFilter) {
                if (wp.search_keywords != null) {
                    val items = wp.search_keywords.split("\\s*,\\s*").toTypedArray()
                    for (i in items.indices) {
                        if (items[i].toLowerCase(Locale.getDefault())
                                .contains(charText)
                        ) orientationList.add(wp)
                    }
                }

//                if ((wp.getTitle().toLowerCase(Locale.getDefault()).contains(charText)||
//                        wp.getSpecialist().toLowerCase(Locale.getDefault()).contains(charText))) {
//                    orientationList.add(wp);
//                }
            }
        }
        notifyDataSetChanged()
        listUpdate.updateUI(orientationList.size)
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

    fun checkDigit(number: Int): String {
        return if (number <= 9) "0$number" else number.toString()
    }

    init {
        orientationListFilter.addAll(orientationList)
        this.archive = archive
        networkChecker = NetworkChecker(context)
        db = EasyPatientDatabase.getDatabase(context)
        mOrientationDetailDao = db.orientationDetailDao()
        this.listUpdate = listUpdate
    }
}