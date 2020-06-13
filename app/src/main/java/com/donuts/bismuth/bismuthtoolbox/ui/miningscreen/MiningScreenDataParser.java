package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.Data.ParsedMiningScreenData;
import com.donuts.bismuth.bismuthtoolbox.Models.MinersStatsModel;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_PRICE_COINGECKO_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_MINER_STATS_URL;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsize;

class MiningScreenDataParser {
    private Context mContext;

    MiningScreenDataParser(Context context){
        mContext = context;
    }

    void parseMiningScreenData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: "+
                "called");

        Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext).getAll();
        DataDAO dataDAO = DataRoomDatabase.getInstance(mContext.getApplicationContext()).getDataDAO();

        int numOfActiveMiners = 0;
        int numOfInactiveMiners = 0;
        int totalHashrateCurrent = 0; // int, MH/s?
        int totalHashrateAverage = 0; // round it, MH/s?
        int totalSharesCurrent = 0;
        int totalSharesAverage = 0; // round it

        double bisToUsd = 0;
        double bisToBtc = 0;

        /*
         * 1. parse BIS_PRICE_COINGECKO_URL (get bis to usd and bis to btc prices)
         */

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: "+
                "parsing BIS_PRICE_COINGECKO_URL");
        // get json response string from the db
        String bisPriceRawData = dataDAO.getUrlDataByUrl(BIS_PRICE_COINGECKO_URL).getUrlJsonResponse();
        try{
            JSONObject bisPriceRawDataJsonObj = new JSONObject(bisPriceRawData);
            Object bisToUsdObj = bisPriceRawDataJsonObj.getJSONObject("bismuth").get("usd");
            Object bisToBtcObj = bisPriceRawDataJsonObj.getJSONObject("bismuth").get("btc");

            if (bisToUsdObj instanceof Number){
                bisToUsd = (Double) bisToUsdObj;
            }else{
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: "+
                        "Failed to get BIS price in USD");
            }

            if (bisToBtcObj instanceof Number){
                bisToBtc = (Double) bisToBtcObj*1000000;  // make it in uBTC
            }else{
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: "+
                        "Failed to get BIS price in BTC");

            }
        }catch(JSONException e){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: "+
                    "Failed to parse JSON data from "+ BIS_PRICE_COINGECKO_URL);
            Toast.makeText(mContext, "Failed to get data from " + ellipsize(BIS_PRICE_COINGECKO_URL, 25), Toast.LENGTH_LONG).show();
        }

        /*
         * 2. parse EGGPOOL_MINER_STATS_URL (mining stats).
         * Parsing eggpool data is tricky: at the beginning of each round values become "null", then they change to "0"
         *  until hashrate is estimated (miner sends first share in the round), and only then real hashrates appear.
         */
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: "+
                "parsing mining stats...");

        List<MinersStatsModel> minersStatsModelList = new ArrayList<>();

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
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: " +
                                    "there are no miners associated with the wallet " + ellipsize(entry.getKey(), 10));
                            Toast.makeText(mContext, "There are no miners associated with the wallet " + ellipsize(entry.getKey(), 10) +
                                    ". Please check the wallet address. ", Toast.LENGTH_LONG).show();
                            continue;
                        }
                    // 2. get object "detail" and loop through it (if not null) and add information about every miner to the minersStatsModelList
                        if (miningWalletRawDataJsonObj.getJSONObject("workers").isNull("detail")){
                            // if "detail" object is null - go to the next miningWalletAddress
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: "+
                                    " details object is null for wallet " + ellipsize(entry.getKey(), 10));
                            continue;
                        }
                        JSONObject workersDetailJsonObject = miningWalletRawDataJsonObj.getJSONObject("workers").getJSONObject("detail");
                        Iterator<String> keys = workersDetailJsonObject.keys();

                        for(int j=0; j<workersDetailJsonObject.length(); j++) {
                            String key = workersDetailJsonObject.names().getString(j); // miner's name
                            if (workersDetailJsonObject.get(key) instanceof JSONArray) {
                                MinersStatsModel minersStatsModel = new MinersStatsModel();
                                minersStatsModel.minerName = key;

                                // for a given miner there is an array with the following stats:
                                // 0: current hashrate (double). Value can be 0 for a missing miner and int from the previous round at the beginning of the round.
                                // 1: miner last seen (timestamp long).
                                // 2: hashrate for the last 12h (array of int) + current hashrate. The 13th value can be 0 at the beginning of the round. Array can be empty (or null) for a missing miner.
                                // 3: shares for the last 12h  (array of int) + current shares. The 13th value can be 0 at the beginning of the round. Array can be empty (or null) for a missing miner.

                                JSONArray minerStatsJsonArray = (JSONArray) workersDetailJsonObject.get(key);
                                // 0.
                                    minersStatsModel.minerHashrate = minerStatsJsonArray.optDouble(0, 0);
                                // 1.
                                    minersStatsModel.minerLastSeen = minerStatsJsonArray.optLong(1, 1);
                                // 2.
                                    JSONArray minerHashrate12hJsonArray = minerStatsJsonArray.optJSONArray(2);
                                    // Deal with the case of a non-array value: fill the list with 13 zeros
                                    if (minerHashrate12hJsonArray == null) {
                                        minersStatsModel.minerHashrate12hList = Collections.nCopies(13, 0);
                                    }else {
                                        for (int i = 0; i < minerHashrate12hJsonArray.length(); ++i) {
                                            minersStatsModel.minerHashrate12hList.add(minerHashrate12hJsonArray.optInt(i, 0));
                                        }
                                    }
                                // 3.
                                    JSONArray minerShares12hJsonArray = minerStatsJsonArray.optJSONArray(3);
                                    // Deal with the case of a non-array value: fill the list with 13 zeros
                                    if (minerShares12hJsonArray == null) {
                                        minersStatsModel.minerShares12hList = Collections.nCopies(13, 0);
                                    }else {
                                        for (int i = 0; i < minerShares12hJsonArray.length(); ++i) {
                                            minersStatsModel.minerShares12hList.add(minerShares12hJsonArray.optInt(i, 0));
                                        }
                                    }

                                minersStatsModel.isMinerOnline = (System.currentTimeMillis() - minersStatsModel.minerLastSeen * 1000) / 60000 <= 10; // if timeLastSeen is > 10 min from now, then miner is missing

                                minersStatsModelList.add(minersStatsModel);
                            }else{
                                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: " +
                                        "Failed to parse JSON data from "+ EGGPOOL_MINER_STATS_URL + " for wallet " + entry.getValue() + ", miner " +
                                        key);
                                Toast.makeText(mContext, "Failed to get data from " + ellipsize(EGGPOOL_MINER_STATS_URL, 25) + " for wallet "
                                        + ellipsize(String.valueOf(entry.getValue()), 10)+ ", miner " + key, Toast.LENGTH_LONG).show();
                            }
                        }
                }catch(JSONException | ClassCastException e){
                    Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: " +
                            "Failed to parse JSON data from "+ EGGPOOL_MINER_STATS_URL + " for wallet " + entry.getValue());
                    Toast.makeText(mContext, "Failed to get data from " + ellipsize(EGGPOOL_MINER_STATS_URL, 25) + " for wallet "
                            + ellipsize(String.valueOf(entry.getValue()), 10), Toast.LENGTH_LONG).show();
                }
            }
        }

        // at this point we looped through all mining wallets and through all miners in each wallet and we got a list of all miners with their stats (minersStatsModelList).
        // now we get stats for views from this list:

        for (MinersStatsModel minersStatsModel : minersStatsModelList){
            if (minersStatsModel.isMinerOnline) {
                numOfActiveMiners += 1;
            }else{
                numOfInactiveMiners += 1;
            }

            totalHashrateCurrent += minersStatsModel.minerHashrate; // it's better to get is from here, because the 13th value in the array of historic hashrates can be zero at the beginning of the round.
            totalSharesCurrent += minersStatsModel.minerShares12hList.get(12);

            // to get averaged values - loop through lists:
            int sumHashrate = 0;
            for (int j=0; j<minersStatsModel.minerHashrate12hList.size()-1; j++){ // discard the 13th value as it can be zero at the beginning of the round
                sumHashrate += minersStatsModel.minerHashrate12hList.get(j);
            }
            totalHashrateAverage +=  sumHashrate /minersStatsModel.minerHashrate12hList.size();

            int sumShares = 0;
            for (int j=0; j<minersStatsModel.minerShares12hList.size()-1; j++){ // discard the 13th value as it can be zero at the beginning of the round
                sumShares += minersStatsModel.minerShares12hList.get(j);
            }
            totalSharesAverage +=  sumShares/minersStatsModel.minerShares12hList.size();
        }


        /*
         * 3. update the database
         */
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningScreenDataParser", "parseMiningScreenData: "+
                "updating Room database with parsed values...");
        ParsedMiningScreenData parsedMiningScreenData = new ParsedMiningScreenData();
        parsedMiningScreenData.setId(1);
        parsedMiningScreenData.setMinersActive(numOfActiveMiners);
        parsedMiningScreenData.setMinersInactive(numOfInactiveMiners);
        parsedMiningScreenData.setHashrateAverage(totalHashrateAverage);
        parsedMiningScreenData.setHashrateCurrent(totalHashrateCurrent);
        parsedMiningScreenData.setSharesAverage(totalSharesAverage);
        parsedMiningScreenData.setSharesCurrent(totalSharesCurrent);

        dataDAO.updateParsedMiningScreenData(parsedMiningScreenData);
    }
}