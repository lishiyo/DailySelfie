package com.cziyeli.dailyselfie.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.cziyeli.dailyselfie.MainActivity;
import com.cziyeli.dailyselfie.R;

/**
 * Sends a notification to open the DailySelfie app.
 */

public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification("Hey you! :P It's selfie time!");
    }

    private void sendNotification(String msg) {
        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Build a notification
        final NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("DailySelfie Alarm")
                .setSmallIcon(R.drawable.ic_camera)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setAutoCancel(true);

        // Create a PendingIntent that will start a new activity.
        // PendingIntents allow a foreign app to execute a predefined piece of code as if it were us (same permissions and identity).
        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        alarmNotificationBuilder.setContentIntent(contentIntent);

        alarmNotificationManager.notify(1, alarmNotificationBuilder.build());
    }
}
