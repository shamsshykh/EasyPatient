package com.app.easy_patient.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.easy_patient.R;
import com.app.easy_patient.util.LangContextWrapper;
import com.app.easy_patient.util.MyDialogCloseListener;
import com.app.easy_patient.util.WheelView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;


public class BottomSheetFrequency extends BottomSheetDialogFragment {
    String hours[] = new String[] {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16",
    "17","18","19","20","21","22","23","24"};
    String days[] = new String[] {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16",
            "17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
    String weeks[] = new String[] {"1","2","3","4","5","6","7","8","9","10"};
    Button btnOk;
    ImageView imgClose;
    String selectedValue = "1";
    TextView tvHour;
    int type = -1;

    public BottomSheetFrequency(int type) {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bottom_sheet_frequency, container, false);
        WheelView wva = v.findViewById(R.id.main_wv);
        btnOk=v.findViewById(R.id.btn_ok);
        imgClose = v.findViewById(R.id.img_close);
        tvHour = v.findViewById(R.id.tv_hour);
        wva.setOffset(1);
        switch (type) {
            case 0:
                wva.setItems(Arrays.asList(hours));
                tvHour.setText(getString(R.string.hour_str));
                break;
            case 1:
                wva.setItems(Arrays.asList(days));
                tvHour.setText(getString(R.string.day_str));
                break;
            case 2:
                wva.setItems(Arrays.asList(weeks));
                tvHour.setText(getString(R.string.week_str));
                break;

        }

        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                selectedValue = item;
                if(selectedValue.equalsIgnoreCase("1")) {
                    switch (type) {
                        case 0: tvHour.setText(getString(R.string.hour_str)); break;
                        case 1: tvHour.setText(getString(R.string.day_str)); break;
                        case 2: tvHour.setText(getString(R.string.week_str)); break;
                    }

                } else {
                    switch (type) {
                        case 0: tvHour.setText(getString(R.string.hours_str)); break;
                        case 1: tvHour.setText(getString(R.string.days_str)); break;
                        case 2: tvHour.setText(getString(R.string.weeks_str)); break;
                    }
                }
                Log.d("TAG", "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnOk.setOnClickListener(v -> {
            Activity activity = getActivity();

            ((MyDialogCloseListener)activity).handleFrequencyDialogClose(Integer.parseInt(selectedValue), type);
            dismiss();
        });

        imgClose.setOnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(LangContextWrapper.Companion.wrap(context, "pt-rBR"));
    }
}
