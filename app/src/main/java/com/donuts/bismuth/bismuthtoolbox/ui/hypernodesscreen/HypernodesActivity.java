package com.donuts.bismuth.bismuthtoolbox.ui.hypernodesscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.donuts.bismuth.bismuthtoolbox.Data.ParsedHomeScreenData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAlertDialogMessage;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * https://hypernodes.bismuth.live/status.json
 */

public class HypernodesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "onCreate: called");

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_hypernodes, contentFrameLayout);
        Toast.makeText(this, "you are in Hypernodes Activity", Toast.LENGTH_LONG).show();

        //DataDAO dataDAO = dataRoomDatabase.getDataDAO();
        //List<Data> retrieveData = dataDAO.getAllData();
        int ddd=2;
        
        /*
         * set bottom navigation        
         */
        BottomNavigationView navView = findViewById(R.id.hypernodes_bottom_nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_hypernodes_home, R.id.navigation__hypernodes_dashboard, R.id.navigation_hypernodes_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.hypernodes_bottom_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // WIP message
        MyAlertDialogMessage myAlertDialogMessage = new MyAlertDialogMessage(this);
        myAlertDialogMessage.warningMessage("W.I.P.", "Work in progress");
    }

    public void onDataFetched(ParsedHomeScreenData parsedUrlData){
        // this method is called through a InterfaceOnFreshData listener when AsyncFetchFreshData is finished
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "HypernodesActivity(onFreshDataReceived): "+
                "fresh data received from asynctask");

        // request to update currently active fragment
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "HypernodesActivity(onFreshDataReceived): "+
                "requested to update active fragment");
        linearLayoutProgress.setVisibility(View.GONE);
        //updateActiveFragment();
    }
}