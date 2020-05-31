package com.donuts.bismuth.bismuthtoolbox.ui.settingsscreen;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;

/**
 * Settings activity cannot extend PreferenceActivity because it already extends BaseActivity
 * in order to keep the navigation drawer. Therefore we need to create a fragment within this activity and
 * that fragment will extend PreferenceFragmentCompat.
 * Details of how the preferences are stored and view inflated in the SettingsFragment
 */

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " SettingsActivity", "onCreate: "+
                "called");

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_settings, contentFrameLayout);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "SettingsActivity(onCreate): inflated");

        // load settings fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment(), "fragmentTag")
                .commit();

    }
}