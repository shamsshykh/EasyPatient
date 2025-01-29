package com.app.easy_patient.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.databinding.AppointmentItemLayoutBinding
import com.app.easy_patient.model.kotlin.Appointment

class AppointmentAdapter(val onAppointmentSelected: (appointment: Appointment, position: Int) -> Unit) :
    BaseRecyclerViewAdapter<AppointmentAdapter.AppointmentViewHolder, Appointment>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = AppointmentItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, position)
        }
    }

    inner class AppointmentViewHolder(private val itemBinding: AppointmentItemLayoutBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(appointment: Appointment, position: Int) {
            itemBinding.appointment = appointment
            itemBinding.apply {
                rootItemLayout.setOnClickListener {
                    onAppointmentSelected(appointment, position)
                }
            }
            itemBinding.executePendingBindings()
        }
    }
}