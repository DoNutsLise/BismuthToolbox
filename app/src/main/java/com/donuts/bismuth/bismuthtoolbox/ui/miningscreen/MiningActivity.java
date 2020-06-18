package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.donuts.bismuth.bismuthtoolbox.Data.RawUrlData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.AsyncFetchData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.InterfaceOnDataFetched;
import com.donuts.bismuth.bismuthtoolbox.utils.EggpoolMinersDataParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_PRICE_COINGECKO_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_BIS_STATS_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_MINER_STATS_URL;

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
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "getFreshData: "+
                "called");
        /*
         * This method checks if the data in Room database is up-to-date (<5 min old), and if not it will request AsyncFetchData to fetch fresh data.
         * 1. Identify all urls that are needed to be updated for Mining Screen (all fragments) and add them to the list.
         * 2. Check every url in Room database - when was it last updated.
         * 3. If the url doesn't need to be updated - delete it from the list and pass the remaining list to asynctask for update.
         * 4. Request AsyncFetchFreshData to update all urls from the list in Room database.
         */

        /*
         * Minimum data for the Mining Screen (all fragments) is this:
         * 1. EGGPOOL_BIS_STATS_URL ("https://eggpool.net/api/currencies")
         * 2. BIS_PRICE_COINGECKO_URL => BIS price from coingecko vs BTC and USD. ("https://api.coingecko.com/api/v3/simple/price?ids=bismuth&vs_currencies=usd,btc")
         * 3. miningWalletAddress eggpool specific: EGGPOOL_MINER_STATS_URL+"939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127f". ("https://eggpool.net/index.php?action=api&type=detail&miner="+walletAddress)
         * The later two have to be for each registered in settings address.
         */

        Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(this).getAll();
        List<String> urls = new ArrayList<>(Arrays.asList(BIS_PRICE_COINGECKO_URL, EGGPOOL_BIS_STATS_URL));// some hardcoded urls come from the constant; and wallet specific urls will be added to this list later.

        // loop through all the preferences and if a mining wallet address found - add it to the list of urls (properly modified)
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("miningWalletAddress"+"\\d+")) {
                // if a mining wallet address found
                urls.add(EGGPOOL_MINER_STATS_URL +  entry.getValue());
            }
        }

        /* At this point List<urls> contains all the urls for Mining Screen update.
         * Now we loop through the List<urls> and for each of them get timeLastUpdated from Room database.
         * If the timeLatUpdate <5 min - delete url from the list and pass the remaining List to AsyncFetchData.
         */
        for (Iterator<String> iterator = urls.listIterator(); iterator.hasNext(); ){
            RawUrlData rawUrlDataFromDb = dataDAO.getUrlDataByUrl(iterator.next());
            if (rawUrlDataFromDb != null && (System.currentTimeMillis() - rawUrlDataFromDb.getUrlLastUpdatedTime() )<300000) {
                iterator.remove();
            }
        }

        // if the List<urls> is not empty and something needs to be updated - fire up the asynctask
        if (!urls.isEmpty()) {
            AsyncFetchData asyncFetchFreshData = new AsyncFetchData(this); // passing context to asynctask here
            asyncFetchFreshData.setOnDataFetchedListener(this); // setting listener to get results from asynctask
            asyncFetchFreshData.execute(urls); // executing asynctask

            linearLayoutProgress.setVisibility(View.VISIBLE);
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "getFreshData: " +
                    "requested fresh data");
        }
    }

    public void onDataFetched() {
        // this method is called through a InterfaceOnFreshData listener when AsyncFetchFreshData is finished
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "onDataFetched: "+
                "received fresh data");

        //disable progressbar
        linearLayoutProgress.setVisibility(View.GONE);

        // request to parse the data
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningActivity", "onDataFetched: "+
                "calling data parser");
        new EggpoolMinersDataParser(this).parseMinersData();
    }


}