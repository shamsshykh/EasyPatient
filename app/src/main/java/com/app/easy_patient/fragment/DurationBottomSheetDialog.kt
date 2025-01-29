package com.app.easy_patient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.app.easy_patient.R
import com.app.easy_patient.util.DurationCallback
import com.app.easy_patient.util.WheelView
import com.app.easy_patient.util.WheelView.OnWheelViewListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class DurationBottomSheetDialog(val callback: DurationCallback): BottomSheetDialogFragment() {
    private var durationType = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.duration_bottom_sheet_dialog, container, false)
        var options = resources.getStringArray(R.array.duration_array)
        val wheelView = view.findViewById<WheelView>(R.id.main_wv)
        wheelView.offset = 1
        wheelView.setItems(options.toList())
        wheelView.onWheelViewListener = object : OnWheelViewListener() {
            override fun onSelected(selectedIndex: Int, item: String) {
                durationType = selectedIndex - 1
            }
        }
        val okay = view.findViewById<Button>(R.id.btn_ok)
        val cancel = view.findViewById<ImageView>(R.id.img_close)

        okay.setOnClickListener {
            callback.duration(durationType)
            dismiss()
        }

        cancel.setOnClickListener {
            dismiss()
        }

        return view
    }
}