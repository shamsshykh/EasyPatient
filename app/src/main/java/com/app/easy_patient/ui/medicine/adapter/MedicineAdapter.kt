package com.app.easy_patient.ui.medicine.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.easy_patient.adapter.BaseRecyclerViewAdapter
import com.app.easy_patient.databinding.MedicineAdapterLayoutNewBinding
import com.app.easy_patient.model.kotlin.Medicine

class MedicineAdapter(val onMedicineSelected: (medicine: Medicine, position: Int) -> Unit) :
    BaseRecyclerViewAdapter<MedicineAdapter.MedicineViewHolder, Medicine>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val binding = MedicineAdapterLayoutNewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, position)
        }
    }

    inner class MedicineViewHolder(private val itemBinding: MedicineAdapterLayoutNewBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(medicine: Medicine, position: Int) {
            itemBinding.medicine = medicine
            itemBinding.apply {
                rootItemLayout.setOnClickListener {
                    onMedicineSelected(medicine, position)
                }
            }
            itemBinding.executePendingBindings()
        }
    }
}