package com.donuts.bismuth.bismuthtoolbox.ui.settingsscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.AsyncFetchData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.HypernodesVerboseDataParser;
import com.donuts.bismuth.bismuthtoolbox.utils.InterfaceOnDataFetched;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_HN_VERBOSE1_URL;

/**
 * Settings activity cannot extend PreferenceActivity because it already extends BaseActivity
 * in order to keep the navigation drawer. Therefore we need to create a fragment within this activity and
 * that fragment will extend PreferenceFragmentCompat.
 * Details of how the preferences are stored and view inflated in the SettingsFragment.
 */

public class SettingsActivity extends BaseActivity implements InterfaceOnDataFetched {

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

    public void getFreshData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " SettingsActivity", "getFreshData: "+
                "called");

        List<String> urls = new ArrayList<>(Arrays.asList(BIS_HN_VERBOSE1_URL));

        // if the List<urls> is not empty and something needs to be updated - fire up the asynctask
        if (!urls.isEmpty()) {
            // fire up an asynctask to fetch new data (it will be returned to onFreshDataReceived)
            AsyncFetchData asyncFetchFreshData = new AsyncFetchData(this); // passing context to asynctask here
            asyncFetchFreshData.setOnDataFetchedListener(this); // setting a listener to get results from asynctask
            asyncFetchFreshData.execute(urls); // executing asynctask

            linearLayoutProgress.setVisibility(View.VISIBLE);

            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " SettingsActivity", "getFreshData: "+
                    "Some urls need to be updated; requested to execute asynctask");
        }
    }

    public void onDataFetched(){
        // this method is called through a InterfaceOnFreshData listener when AsyncFetchFreshData is finished
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " SettingsActivity", "onDataFetched: "+
                "called");

        //disable progressbar
        linearLayoutProgress.setVisibility(View.GONE);

        // request to parse the data
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " SettingsActivity", "onDataFetched: "+
                "calling data parser");
        new HypernodesVerboseDataParser(this).parseHypernodesVerboseData();
    }
}