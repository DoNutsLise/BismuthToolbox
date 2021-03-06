package com.donuts.bismuth.bismuthtoolbox.ui.settingsscreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.FirebasePush.SendDataToFirebase;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAlertDialogMessage;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.PREFERENCES_CATEGORIES_KEYS;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsizeMiddle;

/**
 * All the preferences are stored in DefaultSharedPreferences. For settings like radiobuttons and switches it is simple to do
 * and all the views are defined in preferences.xml file.
 * It's more complicated for user's data like hypernodeIP addresses and wallets addresses.
 * For this preferences only the Categories defined in preferences.xml and the EditTextPreferences are inflated
 * programmatically depending on how many addresses are stored (were entered by the user).
 * At the moment there are three Categories ("hypernodeIP", "miningWalletAddress", "bisWalletAddress") and they are stored
 * in the Constant PREFERENCES_CATEGORIES_KEYS. In preference.xml file these names correspond to android:key value.
 * The details on how the views are inflated are below.
 * A special case of preferences - hypernodes rewards address. These are not readily available and have to be extracted
 * from hypernodes api (e.g. https://hypernodes.bismuth.live/status_ex.json) after a hypernode ip was entered in settings by a user. To do it: every time the settings
 * changed by a user we will ask asynctask to fetch fresh hypernodes data and then parse it to extract HNs reward addresses and store them in Room database. So when a user
 * navigates to hypernodes screen, we will first search room database for rewards addresses corresponding to HN IP addresses in shared preferences.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private boolean isSettingsChanged = false;
    private DataDAO dataDAO;
    private PreferenceScreen preferenceScreen;
    private Map<String, ?> allPreferencesKeys;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " SettingsFragment", "onCreatePreferences: "+
                "called");

        /*
         * By default preference Categories are empty and we inflate the EditTextPreferences programmatically:
         * 1. We first check how many preferences from a particular Category are stored in the SharedPreferences
         * 2. We then inflated an EditTextPreference for each record and one more blank EditTextPreference for the
         * user to add a new record.
         * 3. Then, depending on what user does (adds a new record, modifies the existing one or deletes and existing one),
         * we update the views correspondingly.
         */

        //inflate the views from preferences.xml
        setPreferencesFromResource(R.xml.preferences, rootKey);
        preferenceScreen = getPreferenceManager().getPreferenceScreen();

        // get a map of all the preferences stored in SharedPreferences
        allPreferencesKeys = PreferenceManager.getDefaultSharedPreferences(getActivity()).getAll();

        // get room database
        dataDAO = DataRoomDatabase.getInstance(getActivity().getApplicationContext()).getDataDAO();

        /*
        * Everything below is done in a loop for each of three Categories
         */
        for (String category : PREFERENCES_CATEGORIES_KEYS){
            // categoryRecordsIds stores a list of "IDs" of all the records in SharedPreferences corresponding to a particular Category,
            // e.g. hypernodeIP1, hypernodeIP3 (in case hypernodeIP2 was deleted), so "IDs" will be 1 and 3 correspondingly.
            // We need them in order not to create a new record with existing ID (they are permanent and even if one gets deleted, we
            // need to create a new one that is bigger then the largest one.
            List<Integer> categoryRecordsIds = new ArrayList<>();
            PreferenceGroup preferenceGroup= findPreference(category);

            /*
             * loop through all the records in SharedPreferences and find the ones that correspond to
             * the current Category: they will have keys the same as Category with a number at the end,
             * e.g. for a Category "hypernodeIP" records will be "hypernodeIP1", "hypernodeIP5", etc
             */

            for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
                if (entry.getKey().matches(category+"\\d+")) {
                    //if the record is found, add "ID" of it to the list and...
                    categoryRecordsIds.add(Integer.valueOf(entry.getKey().replaceAll("\\D+","")));
                    // ...create a new EditTextPreference
                    EditTextPreference editTextPreference = new EditTextPreference(preferenceScreen.getContext());
                    editTextPreference.setKey(entry.getKey());
                    editTextPreference.setSummary(ellipsizeMiddle(String.valueOf(entry.getValue()),16));
                    editTextPreference.setText((String) entry.getValue());
                    preferenceGroup.addPreference(editTextPreference);
                }
            }

            /*
            * after we inflated all EditTextPreference for already stored in SharedPreferences values,
            * we need to inflate one more blank EditTextPreference for the user to add new values.
             */

            // First, sort the list with "ID"s
            Collections.sort(categoryRecordsIds);

            // Then add a blank EditTextPreference with "ID" larger than the last (biggest) element in the list
            EditTextPreference editTextPreference = new EditTextPreference(preferenceScreen.getContext());
            if (categoryRecordsIds.size() >0) {
                editTextPreference.setKey(category + (categoryRecordsIds.get(categoryRecordsIds.size() - 1) + 1));
            }else{
                editTextPreference.setKey(category + 1);
            }
            editTextPreference.setSummary("Click to add a new record");
            editTextPreference.setText("");
            editTextPreference.setOrder(0);
            preferenceGroup.addPreference(editTextPreference);
        }

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "SettingsFragment(onCreatePreferences): inflated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "SettingsFragment(onResume): called");
        //register the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        // register PushNotifications switch listener: if enabled - ask for permission to send data to Firebase
        Preference pushNotificationsSwitchPref = findPreference("isPushNotifications");
        pushNotificationsSwitchPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // check if the consent to send data was already given or not
                if (!PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("isPushNotificationsConsent", false) &&
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("isPushNotifications", false)){
                    // if consent was not given and user wants to enable push notifications - ask for consent; otherwise do nothing

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("Please read!");
                    alertDialogBuilder.setMessage(R.string.data_share_consent);
                    alertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("isPushNotificationsConsent", true).apply();
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if user didn't accept data sharing: set push notifications back to false and consent to false
                            Preference pushNotificationsSwitchPref = (Preference) findPreference("isPushNotifications");
                            ((SwitchPreference) pushNotificationsSwitchPref).setChecked(false);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String preferenceKey) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "SettingsFragment(onSharedPreferenceChanged): called");

        // find which preference changed
        Preference preference = findPreference(preferenceKey);

        /* if EditTextPreference was changed (these are the only ones we care about),
         * then the options are:
         * 1. A random record was deleted (value was cleared) => we need to delete it from SharedPreferences and delete EditTextPreference
         * 2. A random record was modified => we only need to re-set the summary of EditTextPreference
         * 3. A new record was made (using the blank EditTextPreference) => we need to add a new blank EditTextPreference
         */

        if (preference instanceof EditTextPreference) {
            // First, identify which Category and PreferenceGroup the modified preference belongs to
            String category = preferenceKey.replaceAll("\\d+", "");
            PreferenceGroup preferenceGroup = findPreference(category);
            String preferenceValue = sharedPreferences.getString(preferenceKey, "");
            preference.setSummary(preferenceValue);

            // Get a list of all the preferences stored in SharedPreferences for this Category and sort it
            allPreferencesKeys = PreferenceManager.getDefaultSharedPreferences(getActivity()).getAll();
            List<Integer> categoryRecordsIds = new ArrayList<>();
            for (Map.Entry<String, ?> entry : allPreferencesKeys.entrySet()) {
                if (entry.getKey().matches(category+"\\d+")) {
                    categoryRecordsIds.add(Integer.valueOf(entry.getKey().replaceAll("\\D+","")));
                }
            }
            Collections.sort(categoryRecordsIds);
            // Get "ID" of the modified preference ; e.g. hypernodeIP3 => id=3
            int modifiedPreferenceId = Integer.parseInt(preferenceKey.replaceAll("\\D+", ""));

            /*
            * Now act depending on what action was done by the user (see three cases above)
             */

            /*
            * if the ID of the modified preference is the last one in the list, it means that a new record was added by the user, since
            * blank EditTextPReferences are always created with the largest ID.
            * Problem here is allPreferencesKeys returns only records stored in the sharedPreferences, leaving the blank EditTextPreference unaccounted.
            * So if I have three records stored in SharedPreferences and modify the 3rd one -it will appear as I modified the last one. Also if I enter a new record in the
            * blank EditTextReference - it will appear that I modified the 4th one - the last one. How do I know which one I modified?
            * Answer: numOfEditTextPreferencesInGroup == numOfSharedPreferenceInGroup only when a blank EditTextPreference used to enter a new preference, - see below.
             */
            int numOfEditTextPreferencesInGroup = preferenceGroup.getPreferenceCount(); // number of EditTextPreferences in the corresponding preference category
            int numOfSharedPreferenceInGroup = categoryRecordsIds.size(); // number of preferences stored in sharedPreferences
            if (numOfEditTextPreferencesInGroup == numOfSharedPreferenceInGroup) { //(no need to check for null, since the preference has been modified, hence it exists)
                // We  just need to create a new blank EditTextPreference
                EditTextPreference editTextPreference = new EditTextPreference(preferenceScreen.getContext());
                editTextPreference.setKey(category + (categoryRecordsIds.get(categoryRecordsIds.size() - 1) + 1));
                editTextPreference.setSummary("Click to add a new record");
                editTextPreference.setText("");
                editTextPreference.setOrder(0);
                preferenceGroup.addPreference(editTextPreference);
            }
            if (preferenceValue.equals("")){
                // no matter what preference was modified, if its value == "", then delete it
                preferenceGroup.removePreference(preference);
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove(preferenceKey).apply();
            }

            /*
            * for user's sake check if it's a duplicate entry (notify and ask to delete manually if so)
             */
            int numOfDuplicates = 0;
            for (Map.Entry<String, ?> entry : allPreferencesKeys.entrySet()) {
                if (String.valueOf(entry.getValue()).equals(preferenceValue)) {
                    numOfDuplicates += 1;
                }
            }
            if (numOfDuplicates > 1) {  // there should be more then one to consider it a duplicate, because it will always find itself - that's one.
                MyAlertDialogMessage myAlertDialogMessage = new MyAlertDialogMessage(getActivity());
                myAlertDialogMessage.warningMessage("Warning!", "You have already entered this address before. Please check and delete if necessary.");
            }

        }
        // regardless of which preference changed we need to set the flag to true - it's being used for triggering data update
        isSettingsChanged = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "SettingsFragment(onPause): called");
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

        if (isSettingsChanged) {
            /*
            * whenever settings are changed, the following things need to be done:
            * 1. activities should pull fresh data: that can be forced by setting time of last refresh to >5 min from now.
            * 2. Firebase database has to be updated with new wallet addresses and hypernodes IPs (only if data sharing consent was given): that can be done by
            * obtaining new firebase registration token and sending data to firebase.
            * 3. request asynctask to pull hypernodes data, so that we can extract hypernodes rewards address and store them in the database
             */

            // 1. settings were changed, so we set url_last_update_time in RawUrlData Room database entity to a
            // small number for all the urls. In this case switching to any other activity will trigger data update.
            dataDAO.updateAllUrlLastUpdateTime(1000000);

            // 2. getting new firebase registration token is a bit tricky (sometimes it's actually the same token)
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                    String newRegistrationToken = instanceIdResult.getToken();
                    // after we have obtained the token we can send all the data to firebase
                    new SendDataToFirebase(getActivity()).sendData(newRegistrationToken);
                    Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " SettingsFragment", "onSuccess: " +
                            "new token is " + newRegistrationToken);
                });

            // 3. call "getFreshData" from the Settings activity to fetch fresh hypernodes data so that we can extract hypernodes rewards
            // addresses and store them in Room database.
            ((SettingsActivity)getActivity()).getFreshData();

            isSettingsChanged = false;
        }
    }
}