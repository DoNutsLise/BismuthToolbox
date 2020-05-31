package com.donuts.bismuth.bismuthtoolbox.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.donuts.bismuth.bismuthtoolbox.Models.Constants;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.homescreen.HomeActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotificationManager {
    private Context mContext;
    private static MyNotificationManager INSTANCE;

    private MyNotificationManager(Context context) {
        mContext = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MyNotificationManager(context);
        }
        return INSTANCE;
    }

    public void displayNotification(String title, String body) {

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        /*
         *  Clicking on the notification will take us to this intent that will open the main activity
         * */
        Intent resultIntent = new Intent(mContext, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
        * for devices > oreo (SDK>=26) we need to configure a notification channel
         */
        if (Build.VERSION.SDK_INT >= 26) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
            notificationChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            notificationChannel.enableLights(true); // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        /*
        * Create a notification and set the notification channel.
         */
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, Constants.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentIntent(pendingIntent)
                        .setContentText(body);

        /*
         * fire up a notification
         */
        if (notificationManager != null) {
            notificationManager.notify(1, notificationBuilder.build());
        }
    }
}
