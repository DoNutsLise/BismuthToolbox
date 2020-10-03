package com.donuts.bismuth.bismuthtoolbox.ui.walletscreen;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.donuts.bismuth.bismuthtoolbox.Data.RawUrlData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.AsyncFetchData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.HomeScreenDataParser;
import com.donuts.bismuth.bismuthtoolbox.utils.InterfaceOnDataFetched;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAlertDialogMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_API_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_HN_BASIC_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.COINGECKO_BIS_PRICE_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_MINER_STATS_URL;

public class WalletActivity extends BaseActivity implements InterfaceOnDataFetched {

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_wallet, contentFrameLayout);

        // create swipe-to-refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_wallet);
        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // call asynctask to fetch fresh data; refresh timer set to 0.
                getFreshData(0);
            }
        });

        // WIP message
        MyAlertDialogMessage myAlertDialogMessage = new MyAlertDialogMessage(this);
        myAlertDialogMessage.warningMessage("Coming soon...", "This section of the app is under development. Please check back later!");
    }

    private void getFreshData(int refreshTimerMinutes){

        // TODO: All code in this activity is for HomeScreen (getFreshData and OnDataFetched)
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

        //disable my progressbar
        linearLayoutProgress.setVisibility(View.GONE);

        //disable swipe-to-refresh progress bar
        swipeRefreshLayout.setRefreshing(false);


        // request to parse the data
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "onDataFetched: " +
                "calling data parser");
        new HomeScreenDataParser(this).parseHomeScreenData();
    }
}