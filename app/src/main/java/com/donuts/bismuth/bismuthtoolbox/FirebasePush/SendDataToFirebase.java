package com.donuts.bismuth.bismuthtoolbox.FirebasePush;

import android.content.Context;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This is a helper class for FirebaseMessaging service.
 * Whenever a new token generated this class is called to send wallets addresses and hypernodes ips to the firebase database.
 * It's also called from settings fragment whenever any changes done to settings.
 */

public class SendDataToFirebase {
        private Context mContext;

    public SendDataToFirebase(Context context){
            mContext = context;
        }

        public void sendData(String token){
            /*
             * Registration token is sent to Firebase Realtime Database
             * IP address of the HN and wallet addresses are sent together with the token. That's all needed to send a notification.
             * Both IP and token are extracted by a script running on my server.
             * Firebase database is read protected and I'll connect to its API with a personal authentication token.
             */

            // if no consent was given for data sharing - abort here
            if (!PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("isPushNotificationsConsent", false)) {
                return;
            }

            // get a unique device id - it remains the same for the lifetime of the device
            // it's needed to satisfy the read/write rules of the database
            String deviceId = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            // get lists of hypernodeIPs and wallets addresses

            Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext).getAll();
            List<String> hypernodeIPsList = new ArrayList<>(Arrays.asList());
            List<String> miningWalletsList = new ArrayList<>(Arrays.asList());
            List<String> bisWalletsList = new ArrayList<>(Arrays.asList());

            // loop through all the preferences and find ips and wallets
            for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
                if (entry.getKey().matches("bisWalletAddress"+"\\d+")) {
                    // if a wallet address for balances found
                    bisWalletsList.add((String) entry.getValue());
                }else if(entry.getKey().matches("miningWalletAddress"+"\\d+")){
                    // if a wallet address for mining stats found
                    miningWalletsList.add((String) entry.getValue());
                }else if(entry.getKey().matches("hypernodeIP"+"\\d+")){
                    // if a hypernode IP found
                    hypernodeIPsList.add((String) entry.getValue());
                }
            }

            // get "push notifications" preference value
            boolean isPushNotifications = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean("isPushNotifications", false);

            // Write a message to the database - the example below works
            FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("users");

            databaseReference.child(deviceId).child("registrationToken").setValue(token);
            databaseReference.child(deviceId).child("isPushNotifications").setValue(isPushNotifications);
            databaseReference.child(deviceId).child("timestamp").setValue(System.currentTimeMillis());
            databaseReference.child(deviceId).child("hypernodeIP").setValue(hypernodeIPsList);
            databaseReference.child(deviceId).child("bisWalletAddress").setValue(bisWalletsList);
            databaseReference.child(deviceId).child("miningWalletAddress").setValue(miningWalletsList);
        }
}
