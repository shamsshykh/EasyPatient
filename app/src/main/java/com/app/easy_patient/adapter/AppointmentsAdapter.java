package com.app.easy_patient.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.easy_patient.R;
import com.app.easy_patient.activity.AppointmentsDetailActivity;
import com.app.easy_patient.model.AppointmentsListModel;
import com.app.easy_patient.util.NetworkChecker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {
    Context context;
    ArrayList<AppointmentsListModel> appointmentList;
    NetworkChecker networkChecker;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //        public ImageView image_download;
        TextView tvMonth, tvDate, tvDay, tvType, tvClinic, tvSpecialist, tvTime;
        ImageView imgAppointmentStatus;

        public ViewHolder(View itemView) {
            super(itemView);
//            image_download = itemView.findViewById(R.id.img_3);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvType = itemView.findViewById(R.id.tv_type);
            tvClinic = itemView.findViewById(R.id.tv_clinic);
            tvSpecialist = itemView.findViewById(R.id.tv_specialist);
            tvTime = itemView.findViewById(R.id.tv_time);
            imgAppointmentStatus = itemView.findViewById(R.id.img_appointment_status);
    }
    }

    public AppointmentsAdapter(Context context, ArrayList appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        networkChecker = new NetworkChecker(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.adapter_appointments_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Date date = new Date();
        AppointmentsListModel listData = appointmentList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(listData.getDate());
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        if (date != null) {
            holder.tvMonth.setText(DateFormat.format("MMMM", date).toString().toUpperCase());
            holder.tvDate.setText(DateFormat.format("dd", date));
            holder.tvDay.setText(DateFormat.format("EE", date).toString().toUpperCase());
            holder.tvTime.setText(DateFormat.format("HH", date)+"h"+DateFormat.format("mm", date));
        }
        if(appointmentList.get(position).getSchedule_status_id()==5)
            holder.imgAppointmentStatus.setImageResource(R.drawable.ic_cancel);
        else if(appointmentList.get(position).getSchedule_status_id()==1)
            holder.imgAppointmentStatus.setImageResource(R.drawable.ic_check_default);
        holder.tvType.setText(listData.getType());
        holder.tvClinic.setText(listData.getClinic());
        holder.tvSpecialist.setText(listData.getSpecialist());
//        holder.card.setCardBackgroundColor(Color.parseColor("#EDFFF2"));

        holder.itemView.setOnClickListener(v -> {
            if (networkChecker.isConnectingNetwork()) {
                Intent intent = new Intent(context, AppointmentsDetailActivity.class);
                intent.putExtra("appointment_detail", listData);
                context.startActivity(intent);
            }else
                Toast.makeText(context,context.getResources().getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return appointmentList.size();
    }
}
