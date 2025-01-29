package com.app.easy_patient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.app.easy_patient.R
import com.app.easy_patient.util.ArchiveCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class ArchiveBottomSheetDialog(val type: Int, val callback: ArchiveCallback): BottomSheetDialogFragment() {

    companion object {
        const val NAME = "ARCHIVE_DIALOG"
        const val MEAL_PLAN_TYPE = 0
        const val PRESCRIPTION_TYPE = 1
        const val ORIENTATION_TYPE = 2
        const val RECOMMENDATION_TYPE = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.archive_bottom_sheet_dialog, container, false)
        val desc = view.findViewById<TextView>(R.id.txtDesc)
        val okay = view.findViewById<Button>(R.id.btnAccept)
        val cancel = view.findViewById<Button>(R.id.btnCancel)

        when (type) {
            MEAL_PLAN_TYPE -> desc.text = getString(R.string.meal_plan_archive_msg)
            PRESCRIPTION_TYPE -> desc.text = getString(R.string.prescription_archive_msg)
            ORIENTATION_TYPE -> desc.text = getString(R.string.orientation_archive_msg)
            RECOMMENDATION_TYPE -> desc.text = getString(R.string.recommendation_archive_msg)
        }

        okay.setOnClickListener {
            callback.performArchive()
            dismiss()
        }

        cancel.setOnClickListener {
            dismiss()
        }

        return view
    }
}