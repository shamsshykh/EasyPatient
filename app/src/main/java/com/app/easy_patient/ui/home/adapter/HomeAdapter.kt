package com.app.easy_patient.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.adapter.BaseRecyclerViewAdapter
import com.app.easy_patient.databinding.*
import com.app.easy_patient.model.kotlin.*

class HomeAdapter<T>(
    val onItemClick: ((item: T) -> Unit)? = null,
    val type: HomeAdapterType
): BaseRecyclerViewAdapter<HomeAdapter<T>.ViewHolder, T>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (type) {
            NotificationType -> NotificationItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            AppointmentType -> AppointmentItemNewLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            MedicineType -> MedicineItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            MenuType -> MenuItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            NotificationMedicineType -> NotificationMedicineSuggestionLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            AppointmentTabType -> AppointmentItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        }
        return ViewHolder(binding, type)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(
        private val itemBinding: ViewDataBinding,
        private val bindingType: HomeAdapterType): RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: T) {

            when (bindingType) {
                AppointmentType -> (itemBinding as AppointmentItemNewLayoutBinding).appointment = item as Appointment
                MedicineType -> (itemBinding as MedicineItemLayoutBinding).medicineReminder = item as MedicineReminder
                MenuType -> (itemBinding as MenuItemLayoutBinding).menu = item as Menu
                NotificationType -> (itemBinding as NotificationItemLayoutBinding).notification = item as Notification
                NotificationMedicineType -> (itemBinding as NotificationMedicineSuggestionLayoutBinding).suggestedMedicine = item as SuggestedMedicine
                AppointmentTabType -> (itemBinding as AppointmentItemLayoutBinding).appointment = item as Appointment
            }
            itemBinding.root.setOnClickListener {
                onItemClick?.apply { invoke(item) }
            }
            itemBinding.executePendingBindings()
        }
    }
}