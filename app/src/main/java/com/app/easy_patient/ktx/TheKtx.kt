package com.app.easy_patient.ktx

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.app.easy_patient.R
import com.app.easy_patient.model.kotlin.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.text.SimpleDateFormat
import java.util.*


fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun Appointment.appointmentTime(): String {
    val time = date?.let {
        val parser =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formatter = SimpleDateFormat("HH:mm")
        formatter.format(parser.parse(it))
    } ?: ""
    return time
}

fun Appointment.appointmentDate(showTime: Boolean = true): String {
    val date = date?.let {
        val parser =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //val formatter = SimpleDateFormat("EEE, dd de MMM ás HH:mm", Locale("pt", "BR"))
        val formatterDayName = SimpleDateFormat("EEE", Locale("pt", "BR"))
        val formatterMonth = SimpleDateFormat("MMM", Locale("pt", "BR"))
        val formatterTime = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
        val formatterDay = SimpleDateFormat("dd", Locale("pt", "BR"))
        val month = formatterMonth.format(parser.parse(it))
        val dayName = formatterDayName.format(parser.parse(it))
        val time = formatterTime.format(parser.parse(it))
        val day = formatterDay.format(parser.parse(it))

        if (showTime)
            "$dayName, $day de $month às $time"
        else
            "$dayName, $day de $month"
    } ?: ""
    return date
}

fun Appointment.showPhone(): Boolean {
    return !phone.isNullOrEmpty()
}

fun Appointment.showWhatsapp(): Boolean {
    return whatsapp!! && !whatsapp_value.isNullOrEmpty()
}


fun Appointment.showEmail(): Boolean {
    return !email.isNullOrEmpty()
}

fun Appointment.showContactTitle(): Boolean {
    return !phone.isNullOrEmpty() || (whatsapp!! && !whatsapp_value.isNullOrEmpty()) || !email.isNullOrEmpty()
}

fun String.toMonth(context: Context): String {
    this?.let {
        return try {
            val parser =  SimpleDateFormat("dd/MM/yyyy")
            val formatter = SimpleDateFormat("MMM", Locale("pt", "BR"))
            context.getString(R.string.received_on_str) + " " + formatter.format(parser.parse(it))
        } catch (ex: Exception) {
            context.getString(R.string.received_on_str) + " " + this
        }
    }
    return context.getString(R.string.received_on_str) + " "
}

fun String.toSimpleDate(): String {
    val date = this?.let {
        val parser =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formatter = SimpleDateFormat("dd/MM/yy", Locale("pt", "BR"))
        formatter.format(parser.parse(it))
    } ?: ""
    return date
}

fun String.stringToDate(): Date {
    val date = this.let {
        val parser =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        parser.parse(it)
    }
    return date
}

fun Address.sortClinicAddress(): String {
    return "${street ?: ""}, ${"${number?.let { "$it - " } ?: ""}  ${complement ?: ""} ${neighborhood ?: ""}, $city_name/$uf"}"
}

fun Audio.audioDate(): String {
    val date = date_attendance?.let {
        val parser =  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val formatter = SimpleDateFormat("EEE, dd MMM HH:mm", Locale("pt", "BR"))
        formatter.format(parser.parse(it))
    } ?: ""
    return date
}

fun AudioFile.toDuration(): String {
    val duration = this.duration?.let {
        val parser =  SimpleDateFormat("HH:mm:ss")
        val date = parser.parse(it)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val hour = "0${calendar.get(Calendar.HOUR)}".slice(0..1)
        val min = "0${calendar.get(Calendar.MINUTE)}".slice(0..1)
        val sec = "0${calendar.get(Calendar.SECOND)}".slice(0..1)
        if (calendar.get(Calendar.HOUR_OF_DAY) > 0) {
            "${hour}:${min}:${sec} min"
        } else {
            "${min}:${sec} min"
        }
    } ?: "0:00 min"
    return duration
}

fun AudioFile.toDurationSeconds(): Int {
    val duration = this.duration?.let {
        val splitValue = it.split(":")
        (splitValue[0].toInt() * 60 * 60) + (splitValue[1].toInt() * 60) + splitValue[2].toInt()
    } ?: 0
    return duration
}

fun Long.toSeconds(): Int {
    return (this/1000).toInt()
}

fun Medicine.notification(): Boolean {
    if (st_notification is Int)
        return st_notification == 1
    else if (st_notification is Double)
        return (st_notification as Double).toInt() == 1
    else if (st_notification is String && !(st_notification as String).isNullOrEmpty())
        return (st_notification as String).toInt() == 1

    return false
}

fun Medicine.critical(): Boolean {
    if (st_critical is Int)
        return st_critical == 1
    else if (st_critical is Double)
        return (st_critical as Double).toInt() == 1
    else if (st_critical is String && !(st_critical as String).isNullOrEmpty())
        return (st_critical as String).toInt() == 1

    return false
}

fun Int.checkDigit(): String {
    return if (this <= 9) "0$this" else this.toString()
}

fun String.mapToDayString(context: Context): String? {
    var returnDay: String? = null
    when (this.trim { it <= ' ' }) {
        "1" -> returnDay = context.getString(R.string.monday_str)
        "2" -> returnDay = context.getString(R.string.tuesday_str)
        "3" -> returnDay = context.getString(R.string.wednesday_str)
        "4" -> returnDay = context.getString(R.string.thursday_str)
        "5" -> returnDay = context.getString(R.string.friday_str)
        "6" -> returnDay = context.getString(R.string.saturday_str)
        "7" -> returnDay = context.getString(R.string.sunday_str)
    }
    return returnDay
}

fun String.medicineIcon(): Int  {
    var medImage: Int = R.drawable.ic_med_1
    when (this) {
        "1" -> medImage = R.drawable.ic_med_1
        "2" -> medImage = R.drawable.ic_med_2
        "3" -> medImage = R.drawable.ic_med_3
        "4" -> medImage = R.drawable.ic_med_4
        "5" -> medImage = R.drawable.ic_med_5
        "6" -> medImage = R.drawable.ic_med_6
        "7" -> medImage = R.drawable.ic_med_7
        "8" -> medImage = R.drawable.ic_med_8
        "9" -> medImage = R.drawable.ic_med_9
        "10" -> medImage = R.drawable.ic_med_10
        "11" -> medImage = R.drawable.ic_med_11
        "12" -> medImage = R.drawable.ic_med_12
    }
    return medImage
}

fun String.medicineNotificationIcon(): Int  {
    var medImage: Int = R.drawable.ic_med_notification_1
    when (this) {
        "1" -> medImage = R.drawable.ic_med_notification_1
        "2" -> medImage = R.drawable.ic_med_notification_2
        "3" -> medImage = R.drawable.ic_med_notification_3
        "4" -> medImage = R.drawable.ic_med_notification_4
        "5" -> medImage = R.drawable.ic_med_notification_5
        "6" -> medImage = R.drawable.ic_med_notification_6
        "7" -> medImage = R.drawable.ic_med_notification_7
        "8" -> medImage = R.drawable.ic_med_notification_8
        "9" -> medImage = R.drawable.ic_med_notification_9
        "10" -> medImage = R.drawable.ic_med_notification_10
        "11" -> medImage = R.drawable.ic_med_notification_11
        "12" -> medImage = R.drawable.ic_med_notification_12
    }
    return medImage
}

fun MedicineReminder.subStatus(): Boolean {
    return status == MedicineStatus.UpComing
}

fun Medicine.validateDay(calendar: Calendar): Boolean {
    if (days_of_the_week != null) {
        val daysOfWeek = days_of_the_week!!.split(",")

        if (daysOfWeek.size == 7) return true

        daysOfWeek.forEach {
            if (formatDayOfWeek(calendar) == it.toIntOrNull() ?: -1)
                return true
        }
    }
    return false
}

private fun formatDayOfWeek(calendar: Calendar): Int {
    val day = calendar.get(Calendar.DAY_OF_WEEK)
    return if (day == 1)
        day + 6
    else
        day - 1
}

fun MedicineReminder.isCurrentTime(): Boolean {
    val currentTime = Calendar.getInstance()

    val medicineTime = Calendar.getInstance()
    medicineTime.timeInMillis = reminderTime()

    return currentTime.get(Calendar.DAY_OF_MONTH) == medicineTime.get(Calendar.DAY_OF_MONTH) &&
            currentTime.get(Calendar.HOUR_OF_DAY) == medicineTime.get(Calendar.HOUR_OF_DAY) &&
            currentTime.get(Calendar.MINUTE) == medicineTime.get(Calendar.MINUTE)
}

fun <T> NotificationCompat.Builder.setLargeNotificationIcon(
    id: Int,
    notificationManager: NotificationManager,
    model: T,
    placeholderId: Int = R.drawable.ic_med_notification_1,
    context: Context
): NotificationCompat.Builder {
    Glide.with(context)
        .asBitmap()
        .load(model)
        .placeholder(placeholderId)
        .apply(RequestOptions.circleCropTransform())
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                setLargeIcon(resource)
                notificationManager.notify(id, build())
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                val bitmap = BitmapFactory.decodeResource(context.resources, placeholderId)
                setLargeIcon(bitmap)
                notificationManager.notify(id, build())
            }
        })
    return this
}

fun Medicine.toMedicineReminder(status: MedicineStatus, timeInMillis: Long): MedicineReminder {
    return MedicineReminder(
        id = medicineReminderId(timeInMillis),
        medicineId = id,
        time = timeInMillis,
        name = name!!,
        dosage = dosage!!,
        notification = notification(),
        status = status,
        pictureLink = picture_link,
        defaultIcon = default_icon?.toInt() ?: 1,
        duration = number_of_days!!,
        critical = critical()
    )
}

//$street, $number - $complement $neighborhood, $city_name/$uf
//fun Appointment.routeVisibilityConstraints(): Boolean {
//    return when {
//        clinic.isNullOrEmpty() -> { false }
//        address?.street.isNullOrEmpty() -> { false }
//        address?.number.isNullOrEmpty() -> { false }
//        address?.complement.isNullOrEmpty() -> { false }
//        address?.neighborhood.isNullOrEmpty() -> { false }
//        address?.city_name.isNullOrEmpty() -> { false }
//        address?.uf.isNullOrEmpty() -> { false }
//        latitude.isNullOrEmpty() -> { false }
//        longitude.isNullOrEmpty() -> { false }
//        else -> { true }
//    }
//}

fun Appointment.routeVisibility(): Boolean {
    return !clinic.isNullOrEmpty() ||
            !address?.street.isNullOrEmpty() ||
            !address?.number.isNullOrEmpty() ||
            !address?.complement.isNullOrEmpty() ||
            !address?.neighborhood.isNullOrEmpty() ||
            !address?.city_name.isNullOrEmpty() ||
            !address?.uf.isNullOrEmpty()
}


fun Appointment.routeTitleVisibility(): Boolean {
    return !clinic.isNullOrEmpty()
}

fun Appointment.routeAddressVisibility(): Boolean {
    return !address?.street.isNullOrEmpty() ||
            !address?.number.isNullOrEmpty() ||
            !address?.complement.isNullOrEmpty() ||
            !address?.neighborhood.isNullOrEmpty() ||
            !address?.city_name.isNullOrEmpty()
}


fun Appointment.routeBtnVisibility(): Boolean {
    return !latitude.isNullOrEmpty() ||
            !longitude.isNullOrEmpty()
}

fun MedicineReminder.toNotificationTime(): Long {
    val reminderTimeCalendar = Calendar.getInstance()
    reminderTimeCalendar.timeInMillis = reminderTime()

    val currentTimeCalendar = Calendar.getInstance()
    currentTimeCalendar.set(Calendar.SECOND, 0)
    currentTimeCalendar.set(Calendar.MILLISECOND, 0)

    val notificationDateCalendar = Calendar.getInstance()
    notificationDateCalendar.timeInMillis = SystemClock.elapsedRealtime()
    notificationDateCalendar.set(Calendar.SECOND, 0)
    notificationDateCalendar.set(Calendar.MILLISECOND, 0)
    notificationDateCalendar.timeInMillis = notificationDateCalendar.timeInMillis + reminderTimeCalendar.timeInMillis.minus(currentTimeCalendar.timeInMillis)

    return notificationDateCalendar.timeInMillis
}

fun Appointment.oneDayBeforeAppointmentNotificationTime(): Long {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val appointmentTimeCalendar = Calendar.getInstance()
        appointmentTimeCalendar.time = notify_at?.stringToDate()
        return appointmentTimeCalendar.timeInMillis
    } else {
        val appointmentTimeCalendar = Calendar.getInstance()
        appointmentTimeCalendar.time = notify_at?.stringToDate()

        val currentTimeCalendar = Calendar.getInstance()
        currentTimeCalendar.set(Calendar.SECOND, 0)
        currentTimeCalendar.set(Calendar.MILLISECOND, 0)

        val notificationDateCalendar = Calendar.getInstance()
        notificationDateCalendar.timeInMillis = SystemClock.elapsedRealtime()
        notificationDateCalendar.set(Calendar.SECOND, 0)
        notificationDateCalendar.set(Calendar.MILLISECOND, 0)
        notificationDateCalendar.timeInMillis = notificationDateCalendar.timeInMillis + appointmentTimeCalendar.timeInMillis.minus(currentTimeCalendar.timeInMillis)

        return notificationDateCalendar.timeInMillis
    }
}

fun Appointment.twoHrsBeforeAppointmentNotificationTime(): Long {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val appointmentTimeCalendar = Calendar.getInstance()
        appointmentTimeCalendar.time = date?.stringToDate()
        appointmentTimeCalendar.add(Calendar.HOUR_OF_DAY, -2)
        return appointmentTimeCalendar.timeInMillis
    } else {
        val appointmentTimeCalendar = Calendar.getInstance()
        appointmentTimeCalendar.time = date?.stringToDate()
        appointmentTimeCalendar.add(Calendar.HOUR_OF_DAY, -2)

        val currentTimeCalendar = Calendar.getInstance()
        currentTimeCalendar.set(Calendar.SECOND, 0)
        currentTimeCalendar.set(Calendar.MILLISECOND, 0)

        val notificationDateCalendar = Calendar.getInstance()
        notificationDateCalendar.timeInMillis = SystemClock.elapsedRealtime()
        notificationDateCalendar.set(Calendar.SECOND, 0)
        notificationDateCalendar.set(Calendar.MILLISECOND, 0)
        notificationDateCalendar.timeInMillis = notificationDateCalendar.timeInMillis + appointmentTimeCalendar.timeInMillis.minus(currentTimeCalendar.timeInMillis)

        return notificationDateCalendar.timeInMillis
    }
}

fun Context.showToast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}

fun Long.dateString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this

    val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH
    )

    return dateFormat.format(calendar.time)
}

fun Boolean.toStringFormat(): String {
    return if (this) "1" else "0"
}