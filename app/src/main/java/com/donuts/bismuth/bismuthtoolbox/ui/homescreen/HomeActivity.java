package com.donuts.bismuth.bismuthtoolbox.ui.homescreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.donuts.bismuth.bismuthtoolbox.Data.ParsedHomeScreenData;
import com.donuts.bismuth.bismuthtoolbox.Data.RawUrlData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.AsyncFetchData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.InterfaceOnDataFetched;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_API_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_HN_BASIC_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_PRICE_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_MINER_STATS_URL;

/**
 * This is the main screen with overview/summary of all BIS activities (mining, hypernodes, wallets, network stats)
 * This activity, just like all other activities, extends BaseActivity that implements most of the UI stuff (navigation drawer, etc)
 */

//TODO: replace asynctask with multithreaded httpurl requests: https://www.youtube.com/watch?v=jH-3spGUa7c

// TODO: replace getInMemoryDatabase(mContext) in asynctask and settingsFragment to persistent db for production

public class HomeActivity extends BaseActivity implements InterfaceOnDataFetched {

    private FrameLayout contentFrameLayout;
    private AsyncFetchData asyncFetchFreshData;

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

        // TODO: below are shared preferences added for testing purpose:
//        sharedPreferences.edit().putString("hypernodeIP1", "163.172.222.163").apply(); // active
//        sharedPreferences.edit().putString("hypernodeIP2", "142.93.93.4").apply(); // inactive
//        sharedPreferences.edit().putString("hypernodeIP3", "142.93.1.1").apply(); // doesn't exist
//        sharedPreferences.edit().putString("bisWalletAddress1", "4f92743a7f5549fe19205842b117aa9c8a611fa8533b1934b43a9ce1").apply(); // random
//        sharedPreferences.edit().putString("bisWalletAddress2", "7d5c2999f9a2e44c23e7b2b73b4c0edae308e9d39482bf44da481edc").apply(); // casino
//        sharedPreferences.edit().putString("bisWalletAddress3", "7d5c2999f9a2e44c23e7b2b73b4c0edae308e9d39482bf44d").apply(); // non-existent
//        // http://bismuth.online/api/node/balancegetjson:939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127f
//        sharedPreferences.edit().putString("miningWalletAddress1", "15158a334b969fa7486a2a1468d04a583f3b51e6e0a7d330723701c3").apply(); // 3 workers
//        sharedPreferences.edit().putString("miningWalletAddress2", "1dfdc05f34681ef2360c2a0fa0dbe190e20981cd1cfcc425aace6a00").apply(); // 2 workers
//        sharedPreferences.edit().putString("miningWalletAddress3", "1dfdc05f34681ef2360c2a0fa0dbe190e20981cd1cfcc425aace").apply(); // non-existent
//        // https://eggpool.net/index.php?action=api&type=detail&miner=15158a334b969fa7486a2a1468d04a583f3b51e6e0a7d330723701c3


        contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "MainActivity(onCreate): view inflated");

        getMainScreenViews();

        // register listener for Room db update of the ParsedHomeScreenData entity
        dataDAO.getParsedHomeScreenLiveData().observe(this, new Observer<ParsedHomeScreenData>() {
            @Override
            public void onChanged(@Nullable ParsedHomeScreenData data){
                assert data != null;
                updateHomeScreenViews(data);
            }
        });

        Toast.makeText(this, "Main Activity created", Toast.LENGTH_LONG).show();
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

        // finish asynctask if it was running
//        if (asyncFetchFreshData != null)
//            asyncFetchFreshData.cancel(true);
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

    private void updateHomeScreenViews(ParsedHomeScreenData data){
        // this is called whenever the LiveData changes are detected by the observer (registered in onCreate)
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "updateHomeScreenViews: "+
                "called");
        tv_hypernodes_active.setText(String.valueOf(data.getHypernodesActive()));
        tv_hypernodes_inactive.setText(String.valueOf(data.getHypernodesInactive()));
        tv_hypernodes_lagging.setText(String.valueOf(data.getHypernodesLagging()));
        tv_wallets_number.setText(String.valueOf(data.getRegisteredWallets()));
        tv_bis_balance.setText(String.valueOf(data.getBalanceBis()));
        tv_usd_balance.setText(String.valueOf(data.getBalanceUsd()));
        tv_mining_active.setText(String.valueOf(data.getMinersActive()));
        tv_mining_inactive.setText(String.valueOf(data.getMinersInactive()));
        tv_mining_hashrate.setText(String.valueOf(data.getMinersHashrate()));
        tv_block_height.setText(String.valueOf(data.getBlockHeight()));
        tv_bis_to_btc.setText(String.valueOf(data.getBisToBtc()));
        tv_bis_to_usd.setText(String.valueOf(data.getBisToUsd()));
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
        * 2. BIS_PRICE_URL // BIS price from coingecko vs BTC and USD.
        * 3. bisWalletAddress specific balance: BIS_API_URL+ "node" + "/" + "balancegetjson:939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127f".
        * 4. miningWalletAddress eggpool specific: EGGPOOL_MINER_STATS_URL+"939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127f".
        * The later two have to be for each registered in settings address.
         */

        Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(this).getAll();
        List<String> urls = new ArrayList<>(Arrays.asList(BIS_PRICE_URL, BIS_HN_BASIC_URL));// some hardcoded urls come from the constant; and wallet specific urls will be added to this list later.

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
            asyncFetchFreshData = new AsyncFetchData(this); // passing context to asynctask here
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