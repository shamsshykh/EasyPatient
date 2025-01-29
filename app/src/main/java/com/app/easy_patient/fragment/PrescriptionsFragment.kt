package com.app.easy_patient.fragment

import androidx.navigation.fragment.findNavController
import com.app.easy_patient.util.ListUpdate
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.adapter.PrescriptionsAdapter
import com.app.easy_patient.database.PrescriptionListModel
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.os.Bundle
import com.app.easy_patient.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextWatcher
import android.text.Editable
import android.view.*
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.app.easy_patient.activity.PrescriptionDetailActivity
import com.app.easy_patient.activity.dashboard.DashboardViewModel
import com.app.easy_patient.fragment.base.BaseFragment
import com.app.easy_patient.wrappers.Resource
import java.util.*

class PrescriptionsFragment : BaseFragment(), ListUpdate {
    var placeholder: LinearLayout? = null
    var llSearchView: ConstraintLayout? = null
    var mContext: Context? = null
    private var priscriptionsRecycler: RecyclerView? = null
    var mAdapter: PrescriptionsAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    var prescriptionList = ArrayList<PrescriptionListModel>()
    var searchItem: MenuItem? = null
    var imgClose: ImageView? = null
    var etSearchView: EditText? = null


    private lateinit var viewModel: DashboardViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_prescriptions, container, false)

        viewModel = ViewModelProvider(requireActivity(), viewModelProvider).get(DashboardViewModel::class.java)
        placeholder = view.findViewById(R.id.ll_placeholder)
        llSearchView = view.findViewById(R.id.ll_search_view)
        imgClose = view.findViewById(R.id.img_close_search_view)
        etSearchView = view.findViewById(R.id.et_search_view)
        priscriptionsRecycler = view.findViewById(R.id.recycler_prescriptions_activity)
        setHasOptionsMenu(true)
        layoutManager = LinearLayoutManager(mContext)
        priscriptionsRecycler?.layoutManager = layoutManager
        mAdapter = PrescriptionsAdapter(mContext!!, prescriptionList, false, this, :: onClickAction)
        priscriptionsRecycler?.adapter = mAdapter

        return view
    }



    private fun onClickAction(model : PrescriptionListModel, archive : Boolean) {
        viewModel.updateCount("prescriptions", model.id)
        viewModel.updateCount.observe(viewLifecycleOwner){
            if (it){
                val intent = Intent(context, PrescriptionDetailActivity::class.java)
                intent.putExtra("prescription_detail", model)
                intent.putExtra("flag", archive)
                startActivity(intent)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        initObserver()
    }

    private fun initObserver(){
        viewModel.prescriptionList.observe(viewLifecycleOwner) {
            if (it is Resource.Success){
                prescriptionList.clear()
                prescriptionList.addAll(it.data)
                updateUI(prescriptionList.size)
                mAdapter!!.notifyDataSetChanged()
            } else {
                updateUI(0 , false)
            }
        }
    }
    override fun updateUI(size: Int, update: Boolean) {
        if (size > 0) {
            placeholder!!.visibility = View.GONE
            priscriptionsRecycler!!.visibility = View.VISIBLE
        } else {
            placeholder!!.visibility = View.VISIBLE
            priscriptionsRecycler!!.visibility = View.GONE
        }

        if (update) {
            viewModel.loadPrescriptions()
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
                .navigate(R.id.action_prescriptionFragment_to_archivePrescriptionFragment)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}