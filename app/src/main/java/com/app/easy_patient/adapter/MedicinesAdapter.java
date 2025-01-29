package com.app.easy_patient.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.app.easy_patient.R;
import com.app.easy_patient.activity.NewMedicineActivity;
import com.app.easy_patient.database.EasyPatientDatabase;
import com.app.easy_patient.database.MedicineDetailDao;
import com.app.easy_patient.ktx.ImageViewExtensionKt;
import com.app.easy_patient.ktx.TheKtxKt;
import com.app.easy_patient.model.kotlin.Medicine;
import com.app.easy_patient.util.NetworkChecker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static com.app.easy_patient.fragment.MedicinesFragment.defaultImageMap;

public class MedicinesAdapter extends RecyclerView.Adapter<MedicinesAdapter.ViewHolder> {
    Context context;
    List<Medicine> medicineList;
    ProgressDialog progressDialog;
    NetworkChecker networkChecker;
    private EasyPatientDatabase db;
    private MedicineDetailDao mMedicineDetailDao;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgNotifications, imgMedicine;
        TextView tvMedicineName, tvDosage, tvDaysOfWeek, tvNextDose;

        public ViewHolder(View itemView) {
            super(itemView);
            imgMedicine = itemView.findViewById(R.id.img_medicine);
            // imgMedicineCustom = itemView.findViewById(R.id.medicine_image_custom);
            // llCustomImage = itemView.findViewById(R.id.ll_custom_image);
            // imgDelete = itemView.findViewById(R.id.img_delete);
            imgNotifications = itemView.findViewById(R.id.img_notifications);
            tvMedicineName = itemView.findViewById(R.id.tv_medicine_name);
            tvDosage = itemView.findViewById(R.id.tv_dosage);
            tvDaysOfWeek = itemView.findViewById(R.id.tv_days_of_week);
            tvNextDose = itemView.findViewById(R.id.tv_next_dose);
        }
    }

    public MedicinesAdapter(Context context, List medicineList) {
        this.context = context;
        this.medicineList = medicineList;
        networkChecker = new NetworkChecker(context);
        db = EasyPatientDatabase.getDatabase(context);
        mMedicineDetailDao = db.medicineDetailDao();
        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.loading_str));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.medicine_adapter_layout_new, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Medicine listData = medicineList.get(position);
        if (listData.getPicture_link() != null && !listData.getPicture_link().isEmpty()) {
            ImageViewExtensionKt.loadCircularImage(holder.imgMedicine, listData.getPicture_link(), 1, R.color.colorPrimary, R.drawable.ic_med_1);
        } else if ((listData.getDefault_icon() != null) && !listData.getDefault_icon().equals("0")) {
            holder.imgMedicine.setImageResource(defaultImageMap.get(listData.getDefault_icon()));
        } else {
            holder.imgMedicine.setImageResource(defaultImageMap.get("1"));
        }
        holder.tvMedicineName.setText(listData.getName());
        holder.tvDosage.setText(listData.getDosage());
        StringBuilder selectedDayNameString = getSelectedDayString(listData.getDays_of_the_week());
        holder.tvDaysOfWeek.setText(selectedDayNameString);
        holder.tvNextDose.setText(convertDate(listData.getStart_time()));
        if (TheKtxKt.critical(listData))
            holder.imgNotifications.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(v -> {
            if (networkChecker.isConnectingNetwork()) {
                Intent intent = new Intent(context, NewMedicineActivity.class);
                intent.putExtra("ID", listData.getId());
                intent.putExtra("flag", "edit");
                intent.putExtra("MEDICINE_DETAIL", listData);
                context.startActivity(intent);
            } else
                Toast.makeText(context, context.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        });
        /*holder.imgDelete.setOnClickListener(v -> {
            if (networkChecker.isConnectingNetwork())
                deleteConfirmation(position);
            else
                Toast.makeText(context, context.getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        });*/
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public StringBuilder getSelectedDayString(String dayOfWeek) {
        StringBuilder sb = new StringBuilder();
        String[] daysArray = dayOfWeek.split(",");
        if (daysArray.length == 7)
            sb.append(context.getString(R.string.every_day_str));
        else {
            for (int i = 0; i < daysArray.length; i++) {
                if (i == daysArray.length - 1)
                    sb.append(mapToDayString(daysArray[i].trim()));
                else
                    sb.append(mapToDayString(daysArray[i].trim()) + ", ");
            }
        }
        return sb;
    }

    public String mapToDayString(String day) {
        String returnDay = null;
        switch (day.trim()) {
            case "1":
                returnDay = context.getString(R.string.monday_str);
                break;
            case "2":
                returnDay = context.getString(R.string.tuesday_str);
                break;
            case "3":
                returnDay = context.getString(R.string.wednesday_str);
                break;
            case "4":
                returnDay = context.getString(R.string.thursday_str);
                break;
            case "5":
                returnDay = context.getString(R.string.friday_str);
                break;
            case "6":
                returnDay = context.getString(R.string.saturday_str);
                break;
            case "7":
                returnDay = context.getString(R.string.sunday_str);
                break;
        }
        return returnDay;
    }

    private String convertDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String month, dd, year, hour, min;
        Date date = new Date();
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        hour = checkDigit(cal.get(Calendar.HOUR_OF_DAY));
        min = checkDigit(cal.get(Calendar.MINUTE));

        SimpleDateFormat formatterDate = new SimpleDateFormat("dd MMM yyyy", new Locale( "pt" , "BR" ));
        String formattedDate = formatterDate.format(date);
        String formattedTime = hour + "h" + min;
        return context.getString(R.string.next_dose_date_time_str, formattedDate, formattedTime);
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
