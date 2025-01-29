package com.app.easy_patient.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.app.easy_patient.R;
import com.app.easy_patient.ktx.TheKtxKt;
import com.app.easy_patient.util.LangContextWrapper;
import com.app.easy_patient.util.MyDialogCloseListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.app.easy_patient.activity.NewMedicineActivity.selectedDays;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomSheetDOW extends BottomSheetDialogFragment {
    private Button btnOK;
    private ImageView imgClose;
    private CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8;
    private Calendar startDateCalendar;
    private boolean isEdit = false;
    public BottomSheetDOW(boolean isEdit, String startTime) {
        this.isEdit = isEdit;
        startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTime(TheKtxKt.stringToDate(startTime));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dow, container, false);
        btnOK = view.findViewById(R.id.btn_ok);
        imgClose = view.findViewById(R.id.img_close);
        cb1 = view.findViewById(R.id.radio1);
        cb2 = view.findViewById(R.id.radio2);
        cb3 = view.findViewById(R.id.radio3);
        cb4 = view.findViewById(R.id.radio4);
        cb5 = view.findViewById(R.id.radio5);
        cb6 = view.findViewById(R.id.radio6);
        cb7 = view.findViewById(R.id.radio7);
        cb8 = view.findViewById(R.id.radio8);

        cb1.setOnClickListener(v -> {
            if (((CheckBox) v).isChecked())
                setAllCheckedChange(true);
            else
                setAllCheckedChange(false);
        });
        return view;
    }

    private void updateCurrentDay() {
        int day = startDateCalendar.get(Calendar.DAY_OF_WEEK);
        cb2.setChecked(day == 2);
        cb3.setChecked(day == 3);
        cb4.setChecked(day == 4);
        cb5.setChecked(day == 5);
        cb6.setChecked(day == 6);
        cb7.setChecked(day == 7);
        cb8.setChecked(day == 1);
    }

    private void setAllCheckedChange(boolean b) {
        cb2.setChecked(b);
        cb3.setChecked(b);
        cb4.setChecked(b);
        cb5.setChecked(b);
        cb6.setChecked(b);
        cb7.setChecked(b);
        cb8.setChecked(b);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cb2.setChecked(selectedDays.get("Monday"));
        cb3.setChecked(selectedDays.get("Tuesday"));
        cb4.setChecked(selectedDays.get("Wednesday"));
        cb5.setChecked(selectedDays.get("Thursday"));
        cb6.setChecked(selectedDays.get("Friday"));
        cb7.setChecked(selectedDays.get("Saturday"));
        cb8.setChecked(selectedDays.get("Sunday"));
        btnOK.setOnClickListener(v -> {
            selectedDays.clear();
            selectedDays.put("Monday", cb2.isChecked());
            selectedDays.put("Tuesday", cb3.isChecked());
            selectedDays.put("Wednesday", cb4.isChecked());
            selectedDays.put("Thursday", cb5.isChecked());
            selectedDays.put("Friday", cb6.isChecked());
            selectedDays.put("Saturday", cb7.isChecked());
            selectedDays.put("Sunday", cb8.isChecked());
            Activity activity = getActivity();

            ((MyDialogCloseListener)activity).handleDOWDialogClose();
            dismiss();
        });

        imgClose.setOnClickListener(v -> {
            dismiss();
        });

        if (!isEdit)
            updateCurrentDay();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(LangContextWrapper.Companion.wrap(context, "pt-rBR"));
    }
}
