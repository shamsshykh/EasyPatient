package com.app.easy_patient.ktx

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.easy_patient.R
import com.app.easy_patient.adapter.BaseRecyclerViewAdapter
import com.app.easy_patient.model.kotlin.*
import com.app.easy_patient.wrappers.Resource
import com.app.easy_patient.wrappers.successOr
import com.google.android.material.button.MaterialButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("visible_flag")
fun View.visibleFlag(flag: Boolean) {
    if (flag) {
        this.show()
    } else {
        this.gone()
    }
}

@BindingAdapter("appointment_status")
fun TextView.setAppointmentStatus(appointment: Appointment) {
    text = appointment.schedule_status_name
    when (appointment.schedule_status_id) {
        1 -> {
            text = context.getString(R.string.appointment_pending_str)
            setTextColor(context.getColor(R.color.colorPrimary))
        }
        2 -> {
            text = context.getString(R.string.appointment_confirm_str)
            setTextColor(context.getColor(R.color.greenColor))
        }
        3 -> {
            text = context.getString(R.string.appointment_completed_str)
            setTextColor(context.getColor(R.color.lightBlueColor))
        }
    }
}

@BindingAdapter("appointment_status_title")
fun TextView.setAppointmentStatusTitle(scheduleStatusId: Int?) {
    scheduleStatusId?.let {
        text = when (it) {
            1 -> context.getString(R.string.waiting_title_str)

            2 -> context.getString(R.string.confirmed_title_str)

            3 -> context.getString(R.string.accomplished_title_str)

            else -> ""
        }
        setTextColor(context.getColor(R.color.black))
    }
}

@BindingAdapter("appointment_status_desc")
fun TextView.setAppointmentStatusDesc(scheduleStatusId: Int?) {
    scheduleStatusId?.let {
        text = when (it) {
            1 -> context.getString(R.string.waiting_desc_str)

            2 -> context.getString(R.string.confirmed_desc_str)

            else -> context.getString(R.string.accomplished_desc_str)
        }
        setTextColor(context.getColor(R.color.colorText))
    }
}

@BindingAdapter("adapterData")
fun <T> RecyclerView.bindDataSet(data: List<T>?) {
    adapter?.let {
        if (it is ListAdapter<*, in RecyclerView.ViewHolder>) {
            val tempAdapter = it as ListAdapter<T, in RecyclerView.ViewHolder>
            tempAdapter.submitList(data)
        }
        else if (it is RecyclerView.Adapter<in RecyclerView.ViewHolder>) {
            if (it is BaseRecyclerViewAdapter<*, *>) {
                val tempAdapter = it as BaseRecyclerViewAdapter<in RecyclerView.ViewHolder, T>
                tempAdapter.submitList(data ?: emptyList())
            }
        }
    }
}

@BindingAdapter("adapterDataSet", "refreshList", requireAll = false)
fun <T> RecyclerView.bindDataSet(result: Resource<List<T>>?, refreshList: Boolean) {
    adapter?.let {
        if (it is ListAdapter<*, in RecyclerView.ViewHolder>) {
            val tempAdapter = it as ListAdapter<T, in RecyclerView.ViewHolder>
            tempAdapter.submitList(result?.successOr(mutableListOf()))
        }
        else if (it is RecyclerView.Adapter<in RecyclerView.ViewHolder>) {
            if (it is BaseRecyclerViewAdapter<*, *>) {
                val tempAdapter = it as BaseRecyclerViewAdapter<in RecyclerView.ViewHolder, T>
                if (refreshList) {
                    tempAdapter.replaceList(result?.successOr(mutableListOf()) ?: emptyList())
                } else {
                    tempAdapter.submitList(result?.successOr(mutableListOf()) ?: emptyList())
                }
            }
        }
    }
}

@BindingAdapter(value = ["setAdapter"])
fun RecyclerView.bindRecyclerViewAdapter(adapter: RecyclerView.Adapter<*>?) {
    this.run {
        this.adapter = adapter
    }
}

@BindingAdapter("menuIcon")
fun ImageView.setMenuIcon(menu: Menu) {
    when (menu.id) {
        1 -> setImageResource(R.drawable.ic_meal_menu)
        2 -> setImageResource(R.drawable.ic_menu_medicine)
        3 -> setImageResource(R.drawable.ic_menu_orientation)
        4 -> setImageResource(R.drawable.ic_menu_recommendations)
        else -> setImageResource(R.drawable.ic_meal_menu)
    }
}

@BindingAdapter("setNextDose")
fun TextView.nextDose(medicine: Medicine) {
    val frequencyType = medicine.days_of_the_week!!.split(",").toTypedArray()[0].toInt()
    val cal = Calendar.getInstance()
    cal.time = medicine.start_time!!.stringToDate()

    while (cal.timeInMillis <= Calendar.getInstance().timeInMillis) {
        when (frequencyType) {
            0 -> cal.add(Calendar.HOUR_OF_DAY, medicine.frequency!!)
            1 -> cal.add(Calendar.DAY_OF_YEAR, medicine.frequency!!)
            2 -> cal.add(Calendar.WEEK_OF_YEAR, medicine.frequency!!)
        }
    }

    val hour = cal[Calendar.HOUR_OF_DAY].checkDigit()
    val min = cal[Calendar.MINUTE].checkDigit()

    val formatterDate = SimpleDateFormat("dd MMM yyyy", Locale("pt", "BR"))
    val formattedDate = formatterDate.format(cal.time)
    val formattedTime = hour + "h" + min
    text = context.getString(R.string.next_dose_date_time_str, formattedDate, formattedTime)
}

@BindingAdapter("setDaysOfWeek")
fun TextView.daysOfWeek(medicine: Medicine) {
    val frequencyValue = medicine.frequency!!
    val frequencyType: Int = medicine.days_of_the_week?.let {
        it.split(",")[0].toInt()
    } ?: 0

    when (frequencyType) {
        0 -> {
            text = if (frequencyValue > 1) {
                context.getString(R.string.frequency_value_type_str, frequencyValue, context.getString(R.string.hours_str))
            } else {
                context.getString(R.string.frequency_value_type_str, frequencyValue, context.getString(R.string.hour_str))
            }
        }
        1 -> {
            text = if (frequencyValue > 1) {
                context.getString(R.string.frequency_value_type_str, frequencyValue, context.getString(R.string.days_str))
            } else {
                context.getString(R.string.frequency_value_type_str, frequencyValue, context.getString(R.string.day_str))
            }
        }
        2 -> {
            text = if (frequencyValue > 1) {
                context.getString(R.string.frequency_value_type_str, frequencyValue, context.getString(R.string.weeks_str))
            } else {
                context.getString(R.string.frequency_value_type_str, frequencyValue, context.getString(R.string.week_str))
            }
        }
    }
}

@BindingAdapter("setMedicineImage")
fun ImageView.medicineImage(medicine: Medicine) {
    if (!medicine.picture_link.isNullOrEmpty()) {
        loadCircularImage(model = medicine.picture_link,
            borderSize = 1f,
            borderColor = context.getColor(R.color.colorPrimary)
        )
    } else {
        if (!medicine.default_icon.isNullOrEmpty())
            setImageResource(medicine.default_icon!!.medicineIcon())
        else
            setImageResource(R.drawable.ic_med_1)
    }
}

@BindingAdapter("setMedicineReminderDetails")
fun TextView.changeLeftDrawable(medicineReminder: MedicineReminder) {
    when (medicineReminder.status) {
        MedicineStatus.Missed -> setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_med_not_taken, 0, 0, 0)
        MedicineStatus.UpComing -> setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_med_snooz, 0, 0, 0)
        MedicineStatus.Taken -> setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_med_taken, 0, 0, 0)
        MedicineStatus.Future -> setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_med_reminder, 0, 0, 0)
    }
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = medicineReminder.reminderTime()

    var hour = calendar.get(Calendar.HOUR_OF_DAY)
    var hourStr = "$hour"
    if (hour < 10)
        hourStr = "0${hour}"

    var min = calendar.get(Calendar.MINUTE)
    var minStr = "$min"
    if (min < 10)
        minStr = "0${min}"

    text = "${hourStr}:${minStr} - ${medicineReminder.name}"
}

@BindingAdapter("medicineTitleText")
fun TextView.medicineTitleText(medicineReminder: MedicineReminder) {
    if (medicineReminder.status == MedicineStatus.Missed) {
        text = context.getString(R.string.medicine_reminder_title_str)
    } else if (medicineReminder.status == MedicineStatus.UpComing) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = medicineReminder.reminderTime()

        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var hourStr = "$hour"
        if (hour < 10)
            hourStr = "0${hour}"

        var min = calendar.get(Calendar.MINUTE)
        var minStr = "$min"
        if (min < 10)
            minStr = "0${min}"

        text = "${hourStr}:${minStr} - ${medicineReminder.name}"
    }
}

@BindingAdapter("medicineDescriptionText")
fun TextView.medicineDescriptionText(medicineReminder: MedicineReminder) {
    if (medicineReminder.status == MedicineStatus.Missed) {
        text = medicineReminder.name
    } else if (medicineReminder.status == MedicineStatus.UpComing) {
        text = context.getString(R.string.medicine_reminder_desc_str)
    }
}

@BindingAdapter("medicineDosageText")
fun TextView.medicineDosageText(medicineReminder: MedicineReminder) {
    if (medicineReminder.status == MedicineStatus.Missed) {
        text = context.getString(R.string.dosage_value_str, medicineReminder.dosage)
    } else if (medicineReminder.status == MedicineStatus.UpComing) {
        visibility = View.GONE
    }
}

@BindingAdapter("cancelButtonText")
fun Button.cancelButtonText(medicineReminder: MedicineReminder) {
    text = context.getString(R.string.adiar_str)
    /*if (medicineReminder.status == MedicineStatus.Missed) {
        text = context.getString(R.string.editar_str)
    } else if (medicineReminder.status == MedicineStatus.UpComing) {
        text = context.getString(R.string.adiar_str)
    }*/
}



@BindingAdapter("enable")
fun SwipeRefreshLayout.enable(enable : Boolean)  {
    isEnabled = enable
}


@BindingAdapter("archiveSrc")
fun MaterialButton.setViewResource(archive : Boolean) {
    icon = if (archive) AppCompatResources.getDrawable(context, R.drawable.ic_archive)
    else AppCompatResources.getDrawable(context, R.drawable.ic_unarchive)
}


@BindingAdapter("setText")
fun TextView.setText(archive : Boolean) {
    text = if (archive) resources.getText(R.string.archive_btn_str)
    else resources.getText(R.string.dearchive_btn_st)
}

@BindingAdapter("snoozeTitleText")
fun TextView.snoozeTitleText(medicineReminder: MedicineReminder) {
    val currentTime = Calendar.getInstance()

    val medicineTime = Calendar.getInstance()
    medicineTime.timeInMillis = medicineReminder.reminderTime()

    var diff = medicineTime.timeInMillis - currentTime.timeInMillis
    val diffDay = diff / (24 * 60 * 60 * 1000)
    diff -= diffDay * 24 * 60 * 60 * 1000 //will give you remaining milli seconds relating to hours,minutes and seconds

    var diffHours = diff / (60 * 60 * 1000)
    diff -= diffHours * 60 * 60 * 1000
    var diffMinutes = diff / (60 * 1000)


    var time: String?
    if (diffHours > 0)
        time = "${diffHours}hora ${diffMinutes}min"
    else
        time = "${diffMinutes}min"

    text = context.getString(R.string.snooze_text_str, medicineReminder.dosage, time)
}

/*
// This is used to load images from URL inside ImageView throughd data binding.
@BindingAdapter("load_url", "placeholder", "image_size", requireAll = false)
fun ImageView.load(productImage: ProductImage?, placeholder: Int?, imageSize: ImageSize?) {

    productImage?.let {
        if (placeholder != null) {
            this.loadProductImage(productImage, placeholder, imageSize ?: ImageSize.MEDIUM)
        } else {
            this.loadProductImage(productImage)
        }
    }
}

/**
 * Load ProductImage model into ImageView as an image using Glide considering the size of the image.
 *
 * @param productImage - ProductImage is model which contain information about image urls
 * @param placeholderId - placeholder image for products
 * @param imageType - define the type of image to load like Medium, Large size
 */
fun ImageView.loadProductImage(
    productImage: ProductImage?,
    placeholderId: Int = R.drawable.ic_redesign_placeholder,
    imageType: ImageSize = ImageSize.MEDIUM
) {
   var imageUrl =  if(imageType == ImageSize.MEDIUM) productImage?.medium else productImage?.large

    imageUrl = if(imageUrl.isNullOrEmpty()) productImage?.src else imageUrl

    Glide.with(context).load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(RequestOptions.bitmapTransform(RoundedCorners(16)))
        .placeholder(placeholderId)
        .error(
            Glide.with(context)
                .load(productImage?.src)
        )
        .into(this)
}

*/