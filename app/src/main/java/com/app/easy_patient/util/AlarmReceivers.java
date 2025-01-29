package com.app.easy_patient.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.app.easy_patient.R;
import com.app.easy_patient.activity.dashboard.DashboardActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC;

public class AlarmReceivers extends BroadcastReceiver {
    String CHANNEL_ID = "CHANNEL_ID";
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context k1, Intent intent) {
        notificationManager = (NotificationManager) k1.getSystemService(NOTIFICATION_SERVICE);
        showNotification(k1);
    }
    private void showNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("This Channel is used to display notifications");
            channel.enableLights(true);
            channel.setVibrationPattern(new long[] {0, 1000, 500, 1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("setNotification>>>","setNotification");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Easy Patient")
                .setContentText( " Medicine Reminder")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);
        MediaPlayer ring = MediaPlayer.create(context, R.raw.a);
        ring.start();
        new Handler().postDelayed(ring::stop,2000);
        notificationManager.notify(1, builder.build());
    }
}
