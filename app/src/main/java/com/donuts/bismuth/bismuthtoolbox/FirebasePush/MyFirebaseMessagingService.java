package com.donuts.bismuth.bismuthtoolbox.FirebasePush;

import android.util.Log;

import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.MyNotificationManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /*
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     * You have to reinstall the app to generate a new token
     */

    @Override
    public void onNewToken(String token) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "MyFirebaseMessagingService(onNewToken): " + token);

        // Send Instance ID token to my app server.
        new SendDataToFirebase(this).sendData(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "MyFirebaseMessagingService(onMessageReceived): "+
                remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "MyFirebaseMessagingService(onMessageReceived): "+
                    remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            /*
             * if the app is in foreground, then firebase notifications will not be displayed;
             * so I send my own notification
             */
            MyNotificationManager.getInstance(this).displayNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

    }

}