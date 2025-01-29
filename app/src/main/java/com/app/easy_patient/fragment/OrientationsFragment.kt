package com.app.easy_patient.fragment

import androidx.navigation.fragment.findNavController
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
import com.app.easy_patient.database.EasyPatientDatabase
import com.app.easy_patient.database.OrientationDetailDao
import com.app.easy_patient.util.NetworkChecker
import android.os.Bundle
import com.app.easy_patient.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.TextView
import android.view.inputmethod.EditorInfo
import android.text.TextWatcher
import android.text.Editable
import com.app.easy_patient.network.RetrofitClientInstance
import android.os.AsyncTask
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.activity.MealPlanDetailActivity
import com.app.easy_patient.activity.OrientationDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.database.MealPlanModel
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.wrappers.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OrientationsFragment : BaseFragment(), ListUpdate {
    var placeholder: LinearLayout? = null
    var llSearchView: ConstraintLayout? = null
    var mContext: Context? = null
    private var orientationsRecycler: RecyclerView? = null
    var mAdapter: OrientationsAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    var orientationList = ArrayList<OrientationListModel>()
    var searchItem: MenuItem? = null
    var imgClose: ImageView? = null
    var etSearchView: EditText? = null
    private lateinit var viewModel: DashboardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_orientations, container, false)
        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)
        placeholder = view.findViewById(R.id.ll_placeholder)
        llSearchView = view.findViewById(R.id.ll_search_view)
        imgClose = view.findViewById(R.id.img_close_search_view)
        etSearchView = view.findViewById(R.id.et_search_view)
        orientationsRecycler = view.findViewById(R.id.recycler_orientations_activity)
        setHasOptionsMenu(true)
        layoutManager = LinearLayoutManager(mContext)
        orientationsRecycler?.layoutManager = layoutManager
        mAdapter = OrientationsAdapter(mContext!!, orientationList, false, this, :: onClickAction)
        orientationsRecycler?.adapter = mAdapter
        return view
    }


    private fun onClickAction(model : OrientationListModel, archive : Boolean) {
        viewModel.updateCount("orientations", model.id)
        viewModel.updateCount.observe(viewLifecycleOwner){
            if (it){
                val intent = Intent(context, OrientationDetailActivity::class.java)
                intent.putExtra("orientation_detail", model)
                intent.putExtra("flag", archive)
                startActivity(intent)
            }
        }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
          etSearchView!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
                return@setOnEditorActionListener true
            }
            false
        }
        etSearchView!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                // TODO Auto-generated method stub
                if (mAdapter != null) {
                    if (etSearchView!!.text.toString() == null || etSearchView!!.text.toString()
                            .isEmpty()
                        || etSearchView!!.text.toString().equals("null", ignoreCase = true)
                    ) {
                        mAdapter!!.filter("")
                        mAdapter!!.notifyDataSetChanged()
                    } else {
                        val text = etSearchView!!.text.toString().toLowerCase(Locale.getDefault())
                        mAdapter!!.filter(text)
                        mAdapter!!.notifyDataSetChanged()
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

        initObserver()

    }

    private fun initObserver(){
        viewModel.orientationList.observe(viewLifecycleOwner){
            if (it is Resource.Success){
                orientationList.clear()
                orientationList.addAll(it.data)
                updateUI(orientationList.size)
                mAdapter!!.notifyDataSetChanged()
            } else {
                updateUI(0, false)
            }
        }
    }



    override fun updateUI(size: Int, update: Boolean) {
        if (size > 0) {
            placeholder!!.visibility = View.GONE
            orientationsRecycler!!.visibility = View.VISIBLE
        } else {
            placeholder!!.visibility = View.VISIBLE
            orientationsRecycler!!.visibility = View.GONE
        }

        if (update) {
            viewModel.loadOrientations()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.orientation_fragment_menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                item.isVisible = false
                llSearchView!!.visibility = View.VISIBLE
            }
            R.id.action_archive -> this.findNavController()
                .navigate(R.id.action_orientationFragment_to_archivedOrientationFragment)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}