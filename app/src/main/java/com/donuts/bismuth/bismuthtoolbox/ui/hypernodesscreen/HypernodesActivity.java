package com.donuts.bismuth.bismuthtoolbox.ui.hypernodesscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.donuts.bismuth.bismuthtoolbox.Data.RawUrlData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.AsyncFetchData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.EggpoolBisStatsDataParser;
import com.donuts.bismuth.bismuthtoolbox.utils.HypernodesRewardAddressDataParser;
import com.donuts.bismuth.bismuthtoolbox.utils.HypernodesVerboseDataParser;
import com.donuts.bismuth.bismuthtoolbox.utils.InterfaceOnDataFetched;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_API_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_HN_VERBOSE_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_BIS_STATS_URL;

/**
 * BIS coins distribution schedule: https://hypernodes.bismuth.live/?p=989
 * HN rewards calculations: https://github.com/bismuthfoundation/Bismuth/blob/6d05c5f69e808f17e4788c4f1437b47f54aa01e1/dbhandler.py#L250
 * HN rewards always come from: 3e08b5538a4509d9daa99e01ca5912cda3e98a7f79ca01248c2bde16
 * Bismuth webpage with hypernodes stats: https://hypernodes.bismuth.live/?page_id=163
 */

public class HypernodesActivity extends BaseActivity implements InterfaceOnDataFetched {

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "onCreate: called");

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_hypernodes, contentFrameLayout);

        /*
         * set bottom navigation        
         */
        BottomNavigationView navView = findViewById(R.id.hypernodes_bottom_nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_hypernodes_my_hypernodes, R.id.navigation_hypernodes_pos_network)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.hypernodes_bottom_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // create swipe-to-refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_hypernodes);
        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // call asynctask to fetch fresh data; refresh timer set to 0.
                getFreshData(0);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "onResume: " +
                "called");

        // check if the data is up-to-date (<5 min old), and if not - request asynctask to pull new data
        getFreshData(5);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "onPause: " +
                "called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "onDestroy: " +
                "called");
    }

    private void getFreshData(int refreshTimerMinutes){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "getFreshData: " +
                "called");
        /*
         * This method checks if the data in Room database is up-to-date (<5 min old), and if not it will request AsyncFetchData to fetch fresh data.
         * 1. Identify all urls that are needed to be updated for Hypernodes Screen (all fragments) and add them to the list.
         * 2. Check every url in Room database - when was it last updated.
         * 3. If the url doesn't need to be updated - delete it from the list and pass the remaining list to asynctask for update.
         * 4. Request AsyncFetchFreshData to update all urls from the list in Room database.
         */

        /*
         * Minimum data for the Hypernodes Screen (all fragments) is this:
         * 1. BIS_HN_VERBOSE_URL
         * 2. hn reward addresses specific balances: BIS_API_URL+ "node" + "/" + "balancegetjson:939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127".
         * 3. EGGPOOL_BIS_STATS_URL - I only need pow block height from here
         *  For this we have to:
         *  a) get all hypernodes IPs from shared preferences.
         *  b) search room db (AllHypernodesData) for corresponding reward addresses.
         *  c) create urls to get recent transactions for corresponding reward addresses from BIS_API_URL
         */

        List<String> urls = new ArrayList<>(Arrays.asList(BIS_HN_VERBOSE_URL, EGGPOOL_BIS_STATS_URL)); // some hardcoded urls come from the constants; and wallet specific urls will be added to this list later.

        Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(this).getAll();

        // loop through all the shared preferences and, if a HN IP found, - search for its reward address in AllHypernodesData table of Room db.
        // Then create an url to pull the wallet transactions and add it to the list of urls for update.
        // another way of doing it is to query AllHypernodesData db: list of all reward addresses where hypernode_mine = true
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("hypernodeIP"+"\\d+")) {
                // if a hypernode IP found in shared preferences, get corresponding reward address
                String hnRewardAddress = dataDAO.getRewardAddressByHypernodeIp(String.valueOf(entry.getValue()));
                // reward address may be null if the ip was entered incorrectly or something else, so check it
                if (hnRewardAddress != null) {
                    // url to get 100 last transaction for a bis address
                    urls.add(BIS_API_URL + "node" + "/" + "addlistlimjson:" + hnRewardAddress +":100");
                }
            }
        }

         /* At this point List<urls> contains all the urls for Hypernodes Screen update.
         * Now we loop through the List<urls> and for each of them get timeLastUpdated from Room database.
         * If the timeLastUpdate < refreshTimerMinutes min - delete url from the list and pass the remaining List to AsyncFetchData.
         */
        for (Iterator<String> iterator = urls.listIterator(); iterator.hasNext(); ){
            RawUrlData rawUrlDataFromDb = dataDAO.getUrlDataByUrl(iterator.next());
            if (rawUrlDataFromDb != null && (System.currentTimeMillis() - rawUrlDataFromDb.getUrlLastUpdatedTime()) < refreshTimerMinutes*60*1000) {
                iterator.remove();
            }
        }

        // if the List<urls> is not empty and something needs to be updated - fire up the asynctask
        if (!urls.isEmpty()) {
            AsyncFetchData asyncFetchFreshData = new AsyncFetchData(this); // passing context to asynctask here
            asyncFetchFreshData.setOnDataFetchedListener(this); // setting listener to get results from asynctask
            asyncFetchFreshData.execute(urls); // executing asynctask

            linearLayoutProgress.setVisibility(View.VISIBLE);
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "getFreshData: " +
                    "requested to fetch fresh data");
        }
    }

    public void onDataFetched() {
        // this method is called through a InterfaceOnFreshData listener when AsyncFetchFreshData is finished
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "onDataFetched: " +
                "received fresh data from asynctask");

        //disable progressbar
        linearLayoutProgress.setVisibility(View.GONE);

        //disable swipe-to-refresh progress bar
        swipeRefreshLayout.setRefreshing(false);

        // request to parse the data
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesActivity", "onDataFetched: " +
                "calling data parser");

        new EggpoolBisStatsDataParser(this).parseBisStatsData();
        new HypernodesVerboseDataParser(this).parseHypernodesVerboseData();
        new HypernodesRewardAddressDataParser(this).parseHypernodesRewardAddressesData();
    }

}