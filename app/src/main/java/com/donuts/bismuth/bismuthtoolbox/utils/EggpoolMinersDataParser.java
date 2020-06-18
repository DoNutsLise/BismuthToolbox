package com.donuts.bismuth.bismuthtoolbox.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolMinersData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_MINER_STATS_URL;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsize;

/**
*  This class parses all the data for Eggpool miners from EGGPOOL_MINER_STATS_URL and saves it in several tables of Room database:
 *  EggpoolMinersData (for MinersFragment), EggpoolPayoutsData (a list of 10 last payouts for each wallet in  payouts fragment)
 *  The changes in Room database are observed with LiveData and corresponding views are updated.
 */

public class EggpoolMinersDataParser {
    private Context mContext;

    public EggpoolMinersDataParser(Context context){
        mContext = context;
    }

    public void parseMinersData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                "called");

        Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext).getAll();
        DataDAO dataDAO = DataRoomDatabase.getInstance(mContext.getApplicationContext()).getDataDAO();
        dataDAO.clearMinersTableInDb();

        /*
         * 1. parse EGGPOOL_MINER_STATS_URL (mining stats).
         * Parsing eggpool data is tricky: at the beginning of each round values become "null", then they change to "0"
         *  until hashrate is estimated (miner sends first share in the round), and only then real hashrates appear.
         */
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                "parsing mining stats...");

        // loop through all the preferences looking for a miningWalletAddress
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("miningWalletAddress" + "\\d+")) {
                // if a miningWalletAddress found, get json response corresponding for this address from Room database
                String miningWalletRawData = dataDAO.getUrlDataByUrl(EGGPOOL_MINER_STATS_URL + entry.getValue()).getUrlJsonResponse();
                // then parse json for this wallet
                try {
                    JSONObject miningWalletRawDataJsonObj = new JSONObject(miningWalletRawData);
                    // 1. check: if there are no miners associated with this wallet ("count"<1); if so - send a toast and move to the next address
                        if (miningWalletRawDataJsonObj.getJSONObject("workers").optInt("count", 0)<1) {
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                                    "there are no miners associated with the wallet " + ellipsize(entry.getKey(), 10));
                            Toast.makeText(mContext, "There are no miners associated with the wallet " + ellipsize(entry.getKey(), 10) +
                                    ". Please check the wallet address. ", Toast.LENGTH_LONG).show();
                            continue;
                        }
                    // 2. get object "detail" and check if it's not null; if so - send a toast and move to the next address
                        if (miningWalletRawDataJsonObj.getJSONObject("workers").isNull("detail")){
                            // if "detail" object is null - go to the next miningWalletAddress
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                                    " details object is null for wallet " + ellipsize(entry.getKey(), 10));
                            continue;
                        }
                    // 3. if details are available - loop through "details" object and add information about every miner to the miners database
                        JSONObject workersDetailJsonObject = miningWalletRawDataJsonObj.getJSONObject("workers").getJSONObject("detail");

                        for(int j=0; j<workersDetailJsonObject.length(); j++) {
                            String key = workersDetailJsonObject.names().getString(j); // miner's name
                            if (workersDetailJsonObject.get(key) instanceof JSONArray) {
                                EggpoolMinersData eggpoolMinersData = new EggpoolMinersData();
                                eggpoolMinersData.setMinerName(key);

                                // for a given miner there is an array with the following stats:
                                // 0. current hashrate; it's better to get it from the 0th element of the minerStatsJsonArray than from the 12th element of historic hashrates array, because there it could be 0 at the start of the round
                                // 1: miner last seen (timestamp long).
                                // 2: hashrate for the last 12h (array of int) + current hashrate. The 13th value can be 0 at the beginning of the round. Array can be empty (or null) for a missing miner.
                                // 3: shares for the last 12h  (array of int) + current shares. The 13th value can be 0 at the beginning of the round. Array can be empty (or null) for a missing miner.

                                JSONArray minerStatsJsonArray = (JSONArray) workersDetailJsonObject.get(key);
                                // 0. current hashrate
                                    eggpoolMinersData.setHashrateCurrent((int) minerStatsJsonArray.optLong(0, 0));
                                // 1.
                                    eggpoolMinersData.setLastSeen(minerStatsJsonArray.optLong(1, 1));
                                // 2.
                                    JSONArray minerHashrate12hJsonArray = minerStatsJsonArray.optJSONArray(2);
                                    int hashrateAverage = 0;
                                    // Deal with the case of a non-array value: fill the list with 13 zeros
                                    if (minerHashrate12hJsonArray == null || minerHashrate12hJsonArray.length() ==0) {
                                        eggpoolMinersData.setHashrate12hList(Collections.nCopies(13, 0));
                                        eggpoolMinersData.setHashrateCurrent(0);
                                    }else {
                                        List<Integer> hashrate12hList = new ArrayList<>();
                                        for (int i = 0; i < minerHashrate12hJsonArray.length(); ++i) {
                                            hashrate12hList.add(minerHashrate12hJsonArray.optInt(i, 0));
                                            hashrateAverage += minerHashrate12hJsonArray.optInt(i, 0);
                                        }
                                        eggpoolMinersData.setHashrate12hList(hashrate12hList);
                                        eggpoolMinersData.setHashrateAverage(hashrateAverage/13);
                                    }

                                // 3.
                                    JSONArray minerShares12hJsonArray = minerStatsJsonArray.optJSONArray(3);
                                    int sharesAverage = 0;
                                    // Deal with the case of a non-array value: fill the list with 13 zeros
                                    if (minerShares12hJsonArray == null || minerShares12hJsonArray.length() == 0) {
                                        eggpoolMinersData.setShares12hList(Collections.nCopies(13, 0));
                                        eggpoolMinersData.setSharesCurrent(0);
                                    }else {
                                        List<Integer> shares12hList = new ArrayList<>();
                                        for (int i = 0; i < minerShares12hJsonArray.length(); ++i) {
                                            shares12hList.add(minerShares12hJsonArray.optInt(i, 0));
                                            sharesAverage += minerShares12hJsonArray.optInt(i, 0);
                                        }
                                        eggpoolMinersData.setShares12hList(shares12hList);
                                        eggpoolMinersData.setSharesAverage(sharesAverage/13);
                                        eggpoolMinersData.setSharesCurrent(shares12hList.get(shares12hList.size() - 1));
                                    }

                                eggpoolMinersData.setIsActive((System.currentTimeMillis() - eggpoolMinersData.getLastSeen() * 1000) / 60000 <= 10); // if timeLastSeen is > 10 min from now, then miner is missing

                                dataDAO.insertMinersData(eggpoolMinersData);
                                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                                        "adding data for " + eggpoolMinersData.getMinerName() + " miner to Room database");
                            }else{
                                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                                        "Failed to parse JSON data from "+ EGGPOOL_MINER_STATS_URL + " for wallet " + entry.getValue() + ", miner " +
                                        key);
                                Toast.makeText(mContext, "Failed to get data from " + ellipsize(EGGPOOL_MINER_STATS_URL, 25) + " for wallet "
                                        + ellipsize(String.valueOf(entry.getValue()), 10)+ ", miner " + key, Toast.LENGTH_LONG).show();
                            }
                        }
                }catch(JSONException | ClassCastException e){
                    Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                            "Failed to parse JSON data from "+ EGGPOOL_MINER_STATS_URL + " for wallet " + entry.getValue());
                    Toast.makeText(mContext, "Failed to get data from " + ellipsize(EGGPOOL_MINER_STATS_URL, 25) + " for wallet "
                            + ellipsize(String.valueOf(entry.getValue()), 10), Toast.LENGTH_LONG).show();
                }
            }
        }

        // we cleared the table in the beginning, so if it's empty by now - populate it with default values.
        if (dataDAO.getNumOfMiners()<1){
            dataDAO.insertAllMiners(EggpoolMinersData.populateMiners());
        }
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                "Miners data parsed.");

    }
}