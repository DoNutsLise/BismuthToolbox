package com.donuts.bismuth.bismuthtoolbox.ui.homescreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.donuts.bismuth.bismuthtoolbox.Data.ParsedHomeScreenData;
import com.donuts.bismuth.bismuthtoolbox.Data.RawUrlData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.AsyncFetchData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.HomeScreenDataParser;
import com.donuts.bismuth.bismuthtoolbox.utils.InterfaceOnDataFetched;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_API_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_HN_BASIC_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.COINGECKO_BIS_PRICE_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_MINER_STATS_URL;

/**
 * This is the main screen with overview/summary of all BIS activities (mining, hypernodes, wallets, network stats)
 * This activity, just like all other activities, extends BaseActivity that implements most of the UI stuff (navigation drawer, etc)
 */

public class HomeActivity extends BaseActivity implements InterfaceOnDataFetched {

    private TextView tv_hypernodes_active;
    private TextView tv_hypernodes_inactive;
    private TextView tv_hypernodes_lagging;
    private TextView tv_wallets_number;
    private TextView tv_bis_balance;
    private TextView tv_usd_balance;
    private TextView tv_mining_active;
    private TextView tv_mining_inactive;
    private TextView tv_mining_hashrate;
    private TextView tv_block_height;
    private TextView tv_bis_to_btc;
    private TextView tv_bis_to_usd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "MainActivity(onCreate): view inflated");

        getMainScreenViews();

        /*
         * register listener for Room db update of the ParsedHomeScreenData entity
         */
        dataDAO.getParsedHomeScreenLiveData().observe(this, this::updateHomeScreenViews);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "MainActivity(onResume): ");

        //Create markers "active/not active" activity
        sharedPreferences.edit().putBoolean("isMainActivityForeground", true).apply();

        // check if the data is up-to-date (<5 min old), and if not - request asynctask to pull new data
         getFreshData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "MainActivity(onPause): " + "called");

        //Create markers "active/not active" activity
        sharedPreferences.edit().putBoolean("isMainActivityForeground", false).apply();

        // finish asynctask if it was running -
//        if (asyncFetchFreshData != null)
//            asyncFetchFreshData.cancel(true);
        // if you do that, asynctask will finish downloading all the data and
        // terminate after that only; so the data will not be returned to the activity and will not be parsed. That means
        // you will download the data but will not use it for displaying => waste of resource.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "MainActivity(onDestroy): " + "called");

        //Create markers "active/not active" activity
        sharedPreferences.edit().putBoolean("isMainActivityForeground", false).apply();

        // finish asynctask if it was running
//        if (asyncFetchFreshData != null)
//            asyncFetchFreshData.cancel(true);
    }


    private void getMainScreenViews(){
        // get all screen view when activity is created
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "getMainScreenViews: "+"called");
        tv_hypernodes_active = findViewById(R.id.tv_mainactivity_hypernodes_active);
        tv_hypernodes_inactive = findViewById(R.id.tv_mainactivity_hypernodes_inactive);
        tv_hypernodes_lagging = findViewById(R.id.tv_mainactivity_hypernodes_lagging);
        tv_wallets_number = findViewById(R.id.tv_mainactivity_wallets_number);
        tv_bis_balance = findViewById(R.id.tv_mainactivity_bis_balance);
        tv_usd_balance = findViewById(R.id.tv_mainactivity_usd_balance);
        tv_mining_active = findViewById(R.id.tv_mainactivity_mining_active);
        tv_mining_inactive= findViewById(R.id.tv_mainactivity_mining_inactive);
        tv_mining_hashrate = findViewById(R.id.tv_mainactivity_mining_hashrate);
        tv_block_height = findViewById(R.id.tv_mainactivity_block_height);
        tv_bis_to_btc = findViewById(R.id.tv_mainactivity_bis_to_btc);
        tv_bis_to_usd = findViewById(R.id.tv_mainactivity_bis_to_usd);
    }

    private void updateHomeScreenViews(ParsedHomeScreenData parsedHomeScreenData){
        // this is called whenever the LiveData changes are detected by the observer (registered in onCreate)
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "updateHomeScreenViews: " +
                "called");
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        DecimalFormat decimalFormat1 = new DecimalFormat("#,###.###");

        if (parsedHomeScreenData != null) {
            tv_hypernodes_active.setText(String.valueOf(parsedHomeScreenData.getHypernodesActive()));
            tv_hypernodes_inactive.setText(String.valueOf(parsedHomeScreenData.getHypernodesInactive()));
            tv_hypernodes_lagging.setText(String.valueOf(parsedHomeScreenData.getHypernodesLagging()));
            tv_wallets_number.setText(String.valueOf(parsedHomeScreenData.getRegisteredWallets()));
            tv_bis_balance.setText(decimalFormat.format(parsedHomeScreenData.getBalanceBis()));
            tv_usd_balance.setText(decimalFormat.format(parsedHomeScreenData.getBalanceUsd()));
            tv_mining_active.setText(String.valueOf(parsedHomeScreenData.getMinersActive()));
            tv_mining_inactive.setText(String.valueOf(parsedHomeScreenData.getMinersInactive()));
            tv_mining_hashrate.setText(decimalFormat .format(parsedHomeScreenData.getMinersHashrate()));
            tv_block_height.setText(decimalFormat.format(parsedHomeScreenData.getBlockHeight()));
            tv_bis_to_btc.setText(decimalFormat1.format(parsedHomeScreenData.getBisToBtc()));
            tv_bis_to_usd.setText(decimalFormat1.format(parsedHomeScreenData.getBisToUsd()));
        }
    }

    private void getFreshData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "getFreshData: "+
                "called");
        /*
        * This method checks if the data in Room database is up-to-date (<5 min old), and if not it will request AsyncFetchData to fetch fresh data.
        * 1. Identify all urls that are needed to be updated for Home Screen and add them to the list.
        * 2. Check every url in Room database - when was it last updated.
        * 3. If the url does'n needs to be updated - delete it from the list.
        * 4. Request AsyncFetchFreshData to update all urls from the list in Room database.
         */

        /*
        * Minimum data for the main screen is this:
        * 1. BIS_HN_BASIC_URL // all hypernodes stats.
        * 2. BIS_PRICE_COINGECKO_URL // BIS price from coingecko vs BTC and USD.
        * 3. bisWalletAddress specific balance: BIS_API_URL+ "node" + "/" + "balancegetjson:939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127".
        * 4. miningWalletAddress eggpool specific: EGGPOOL_MINER_STATS_URL+"939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127".
        * The later two have to be for each registered in settings address.
         */

        Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(this).getAll();
        List<String> urls = new ArrayList<>(Arrays.asList(COINGECKO_BIS_PRICE_URL, BIS_HN_BASIC_URL));// some hardcoded urls come from the constant; and wallet specific urls will be added to this list later.

        // loop through all the preferences and if a wallet address found - add it to the list of urls (properly modified)
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("bisWalletAddress"+"\\d+")) {
                // if a wallet address for balances found
                urls.add(BIS_API_URL+ "node" + "/" + "balancegetjson:" + entry.getValue());
            }else if(entry.getKey().matches("miningWalletAddress"+"\\d+")){
                // if a wallet address for mining stats found
                urls.add(EGGPOOL_MINER_STATS_URL + entry.getValue());
            }
        }

        /* At this point List<urls> contains all the urls for Home screen update.
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
            // fire up an asynctask to fetch new data (it will be returned to onFreshDataReceived)
            AsyncFetchData asyncFetchFreshData = new AsyncFetchData(this); // passing context to asynctask here
            asyncFetchFreshData.setOnDataFetchedListener(this); // setting listener to get results from asynctask
            asyncFetchFreshData.execute(urls); // executing asynctask

            linearLayoutProgress.setVisibility(View.VISIBLE);

            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "getFreshData: "+
                    "Some urls need to be updated; requested to execute asynctask");
        }
    }

    public void onDataFetched(){
        // this method is called through a InterfaceOnFreshData listener when AsyncFetchFreshData is finished
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "onDataFetched: " + "called");

        //disable progressbar
        linearLayoutProgress.setVisibility(View.GONE);

        // request to parse the data
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "onDataFetched: " +
                "calling data parser");
        new HomeScreenDataParser(this).parseHomeScreenData();
    }
}
