package com.app.easy_patient.util

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.media.MediaPlayer
import android.content.Intent
import android.media.AudioAttributes
import android.os.IBinder
import android.media.AudioManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.app.easy_patient.R
import com.app.easy_patient.model.kotlin.MedicineReminder
import dagger.android.AndroidInjection
import javax.inject.Inject

class SoundService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var medicine: MedicineReminder
    private lateinit var audioManager: AudioManager
    lateinit var notificationManager: NotificationManager
    var soundEnabledOnDevice: Boolean  = false

    @Inject
    lateinit var prefs: SharedPrefs

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel())
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.notification).apply {
            setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_ALARM).build())
            isLooping = false
        }
        mediaPlayer?.setOnCompletionListener { mp: MediaPlayer ->
            mp.stop()
            mp.release()
            mediaPlayer = null

            stopService()
        }

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        prefs.deviceVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        checkSoundEnabledOnDevice()
    }

    private fun createNotification() {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_SERVICE_ID)
            .setSmallIcon(R.drawable.exo_ic_audiotrack)
            .setContentText(applicationContext.getString(R.string.easy_patient_background_running_str))
            .setOngoing(false)
            .setWhen(0)

        val notification = builder.build()
        notificationManager!!.notify(101, notification)
        startForeground(101, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        medicine = intent.getParcelableExtra(Constants.INTENT_KEYS.MEDICINE_REMINDER)!!

        createNotification()
        playSound()

        return START_NOT_STICKY
    }

    private fun playSound() {
        val playSound = medicine.critical || soundEnabledOnDevice
        if (playSound) {
            increaseDeviceVolume()
            try {
                mediaPlayer?.start()
            } catch (ex: Exception) {
                ex.printStackTrace()
                stopService()
            }
        } else {
            stopService()
        }
    }

    private fun increaseDeviceVolume() {
        if (medicine.critical) {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)
        }
    }

    private fun checkSoundEnabledOnDevice() {
        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> soundEnabledOnDevice = false
            AudioManager.RINGER_MODE_VIBRATE -> soundEnabledOnDevice = false
            AudioManager.RINGER_MODE_NORMAL -> soundEnabledOnDevice = true
        }
    }

    private fun stopService() {
        stopSelf()
        stopForeground(true)
    }

    override fun onDestroy() {
        mediaPlayer?.apply {
            try {
                if (isPlaying)
                    stop()

                release()
            } catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        mediaPlayer = null

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, prefs.deviceVolume, 0)
        super.onDestroy()
    }
}