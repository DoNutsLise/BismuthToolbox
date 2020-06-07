package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.AsyncFetchData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.InterfaceOnDataFetched;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_PRICE_URL;

public class MiningActivity extends BaseActivity implements InterfaceOnDataFetched {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "onCreate: "+
                "called");

        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_mining, contentFrameLayout);

        /*
         * set bottom navigation
         */
        BottomNavigationView navView = findViewById(R.id.mining_bottom_nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mining_miners, R.id.navigation_mining_payouts)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.mining_bottom_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "onResume: "+
                "called");

        // check if the data is up-to-date (<5 min old), and if not - request asynctask to pull new data
        getFreshData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "onPause: "+
                "called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "onDestroy: "+
                "called");
    }

    private void getFreshData(){

        List<String> urls = new ArrayList<>(Arrays.asList(BIS_PRICE_URL));
        AsyncFetchData asyncFetchFreshData = new AsyncFetchData(this); // passing context to asynctask here
        asyncFetchFreshData.setOnDataFetchedListener(this); // setting listener to get results from asynctask
            asyncFetchFreshData.execute(urls); // executing asynctask

            linearLayoutProgress.setVisibility(View.VISIBLE);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "getFreshData: "+
                "requested fresh data");
    }

    public void onDataFetched() {
        // this method is called through a InterfaceOnFreshData listener when AsyncFetchFreshData is finished
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "onDataFetched: "+
                "received fresh data");

        //disable progressbar
        linearLayoutProgress.setVisibility(View.GONE);
    }


}