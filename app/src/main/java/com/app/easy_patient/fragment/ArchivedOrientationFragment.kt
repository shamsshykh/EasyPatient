package com.app.easy_patient.fragment

import com.app.easy_patient.util.ListUpdate
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.adapter.OrientationsAdapter
import com.app.easy_patient.database.OrientationListModel
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
import android.util.Log
import android.view.*
import com.app.easy_patient.network.RetrofitClientInstance
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.activity.OrientationDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.fragment.base.BaseFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ArchivedOrientationFragment : BaseFragment(), ListUpdate {
    var placeholder: LinearLayout? = null
    var llSearchView: ConstraintLayout? = null
    var mContext: Context? = null
    private var orientationsRecycler: RecyclerView? = null
    var mAdapter: OrientationsAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    var orientationList = ArrayList<OrientationListModel>()
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
        val view = inflater.inflate(R.layout.fragment_archived_orientation, container, false)
        mContext = activity
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)

        placeholder = view.findViewById(R.id.ll_placeholder)
        orientationsRecycler = view.findViewById(R.id.recycler_orientations_activity)
        llSearchView = view.findViewById(R.id.ll_search_view)
        imgClose = view.findViewById(R.id.img_close_search_view)
        etSearchView = view.findViewById(R.id.et_search_view)
        setHasOptionsMenu(true)

        // use a linear layout manager
        layoutManager = LinearLayoutManager(mContext)
        orientationsRecycler?.layoutManager = layoutManager
        mAdapter = OrientationsAdapter(mContext!!, orientationList, true, this, ::onClickAction)
        orientationsRecycler?.adapter = mAdapter
        return view
    }


    private fun onClickAction(model: OrientationListModel, archive: Boolean) {
        if (model.is_new)
            viewModel.updateCount("orientations", model.id)

        val intent = Intent(context, OrientationDetailActivity::class.java)
        intent.putExtra("orientation_detail", model)
        intent.putExtra("flag", archive)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle)
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage(getString(R.string.loading_str))
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
        // ((NavigationDrawerState)mContext).hideNavigationDrawer(getString(R.string.archived_orientation_str));
        loadArchiveList()
    }

    private fun loadArchiveList() {
        progressDialog!!.show()
        val loadOrientationResponse = api!!.orientationsArchive
        loadOrientationResponse.enqueue(object : Callback<ArrayList<OrientationListModel>?> {
            override fun onResponse(
                call: Call<ArrayList<OrientationListModel>?>,
                response: Response<ArrayList<OrientationListModel>?>
            ) {
                if (response.isSuccessful) {
                    orientationList.clear()
                    orientationList.addAll(response.body()!!)
                    mAdapter!!.notifyDataSetChanged()
                    updateUI(orientationList.size)
                }
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<ArrayList<OrientationListModel>?>, t: Throwable) {
                if (progressDialog!!.isShowing) {
                    progressDialog!!.dismiss()
                }
            }
        })
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

    override fun onStop() {
        super.onStop()
        // ((NavigationDrawerState)mContext).showNavigationDrawer("Orientations");
    }

    override fun updateUI(size: Int, boolean: Boolean) {
        if (size == 0) {
            placeholder!!.visibility = View.VISIBLE
            orientationsRecycler!!.visibility = View.GONE
        } else {
            placeholder!!.visibility = View.GONE
            orientationsRecycler!!.visibility = View.VISIBLE
        }
    }
}