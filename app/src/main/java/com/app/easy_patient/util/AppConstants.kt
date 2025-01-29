package com.app.easy_patient.util

object AppConstants {
    const val SERVER_URL = "https://api-patient.easy-health.app/"

    object INTENT_KEYS {
        const val APPOINTMENT_ID: String = "appointment_id"
        const val APPOINTMENT_NOTIFICATION_REQUEST_CODE: String = "appointment_notification_request_code"
        const val IS_FROM_NOTIFICATION: String = "notification"
        const val AUDIO_ID: String = "audio_id"
    }

    object PREF_KEYS {
        const val DB_NAME = "easy_patient_sp"
        const val LOGIN_STATUS = "loginstatus"
        const val NOTIFY_MEDICINE_STATUS = "notifyMedicineStatus"
        const val SET_ALARM_LIST = "setAlarmList"
        const val NOTIFY_SCHEDULE_STATUS = "notifyScheduleStatus"
        const val TOKEN = "token"
        const val REFRESH_TOKEN = "refreshToken"
        const val IMAGE_PATH = "imagePath"
        const val NAME = "name"
        const val EMAIL = "email"
        const val GENDER = "gender"
        const val FIRST_TIME_APP = "firstTimeApp"
        const val MEDICINE_PICTURE_PATH = "medicinePicturePath"
        const val DEVICE_CURRENT_VOLUME = "deviceCurrentVolume"
    }

    object REQUEST_CODE_KEYS {
        const val REQUEST_CODE_TAKE_PICTURE = 3000
        const val REQUEST_CODE_GALLERY = 3001
        const val START_TERMS_ACTIVITY = 101
        const val REQUEST_LOCATION_PERMISSION = 10101
    }
}