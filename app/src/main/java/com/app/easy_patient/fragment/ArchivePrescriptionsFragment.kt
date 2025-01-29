package com.app.easy_patient.fragment

import com.app.easy_patient.util.ListUpdate
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.adapter.PrescriptionsAdapter
import com.app.easy_patient.database.PrescriptionListModel
import com.app.easy_patient.network.GetDataService
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.os.Bundle
import com.app.easy_patient.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextWatcher
import android.text.Editable
import android.view.*
import com.app.easy_patient.network.RetrofitClientInstance
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.activity.PrescriptionDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.fragment.base.BaseFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ArchivePrescriptionsFragment : BaseFragment(), ListUpdate {
    var placeholder: LinearLayout? = null
    var llSearchView: ConstraintLayout? = null
    var mContext: Context? = null
    private var priscriptionsRecycler: RecyclerView? = null
    var mAdapter: PrescriptionsAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    var prescriptionList = ArrayList<PrescriptionListModel>()
    var api: GetDataService? = null
    var progressDialog: ProgressDialog? = null
    var searchItem: MenuItem? = null
    var imgClose: ImageView? = null
    var etSearchView: EditText? = null

    private lateinit var viewModel: DashboardViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_archive_prescriptions, container, false)
        mContext = activity
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)

        placeholder = view.findViewById(R.id.ll_placeholder)
        priscriptionsRecycler = view.findViewById(R.id.recycler_prescriptions_activity)
        llSearchView = view.findViewById(R.id.ll_search_view)
        imgClose = view.findViewById(R.id.img_close_search_view)
        etSearchView = view.findViewById(R.id.et_search_view)
        setHasOptionsMenu(true)
        // use a linear layout manager
        layoutManager = LinearLayoutManager(mContext)
        priscriptionsRecycler?.setLayoutManager(layoutManager)
        mAdapter = PrescriptionsAdapter(mContext!!, prescriptionList, true, this,:: onClickAction)
        priscriptionsRecycler?.setAdapter(mAdapter)
        return view
    }


    private fun onClickAction(model: PrescriptionListModel, archive: Boolean) {
        if (model.is_new)
            viewModel.updateCount("prescriptions", model.id)

        val intent = Intent(context, PrescriptionDetailActivity::class.java)
        intent.putExtra("prescription_detail", model)
        intent.putExtra("flag", archive)
        startActivity(intent)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Loading...")
        etSearchView!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                // TODO Auto-generated method stub
                if (mAdapter != null) {
                    if (etSearchView!!.text.toString() == null || etSearchView!!.text.toString()
                            .isEmpty()
                        || etSearchView!!.text.toString().equals("null", ignoreCase = true)
                    ) {
                        mAdapter!!.filter("")
                    } else {
                        val text = etSearchView!!.text.toString().toLowerCase(Locale.getDefault())
                        mAdapter!!.filter(text)
                    }
                }
            }

            override fun beforeTextChanged(
                arg0: CharSequence, arg1: Int,
                arg2: Int, arg3: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(
                arg0: CharSequence, arg1: Int, arg2: Int,
                arg3: Int
            ) {
                // TODO Auto-generated method stub
            }
        })
        imgClose!!.setOnClickListener { v: View? ->
            llSearchView!!.visibility = View.GONE
            searchItem!!.isVisible = true
        }
        api = RetrofitClientInstance.getRetrofitInstance(mContext).create(
            GetDataService::class.java
        )
        // ((NavigationDrawerState) mContext).hideNavigationDrawer(getString(R.string.archived_prescription_str));
        loadArchiveList()
    }

    private fun loadArchiveList() {
        progressDialog!!.show()
        val loadOrientationResponse = api!!.prescriptionsArchive
        loadOrientationResponse.enqueue(object : Callback<ArrayList<PrescriptionListModel>?> {
            override fun onResponse(
                call: Call<ArrayList<PrescriptionListModel>?>,
                response: Response<ArrayList<PrescriptionListModel>?>
            ) {
                if (response.isSuccessful) {
                    prescriptionList.clear()
                    prescriptionList.addAll(response.body()!!)
                    mAdapter!!.notifyDataSetChanged()
                }
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
                updateUI(prescriptionList.size)
            }

            override fun onFailure(call: Call<ArrayList<PrescriptionListModel>?>, t: Throwable) {
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // ((NavigationDrawerState) mContext).showNavigationDrawer(getString(R.string.prescriptions_str));
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.archived_orientation_fragment_menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                item.isVisible = false
                llSearchView!!.visibility = View.VISIBLE
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun updateUI(size: Int, update: Boolean) {
        if (size == 0) {
            placeholder!!.visibility = View.VISIBLE
            priscriptionsRecycler!!.visibility = View.GONE
        } else {
            placeholder!!.visibility = View.GONE
            priscriptionsRecycler!!.visibility = View.VISIBLE
        }
    }
}