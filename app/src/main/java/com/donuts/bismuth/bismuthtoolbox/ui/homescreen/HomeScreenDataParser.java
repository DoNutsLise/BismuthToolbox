package com.donuts.bismuth.bismuthtoolbox.ui.homescreen;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.Data.ParsedHomeScreenData;
import com.donuts.bismuth.bismuthtoolbox.Data.RawUrlData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_API_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_HN_BASIC_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_PRICE_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_MINER_STATS_URL;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsize;

class HomeScreenDataParser {
    private Context mContext;

    HomeScreenDataParser(Context context){
        mContext = context;
    }

    void parseHomeScreenData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "
        +"called");

        Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext).getAll();
        DataDAO dataDAO = DataRoomDatabase.getInstance(mContext.getApplicationContext()).getDataDAO();

        int numOfActiveHypernodes = 0;
        int numOfInactiveHypernodes = 0;
        int numOfLaggingHypernodes = 0;
        int numOfRegisteredWallets = 0;
        double balanceBis = 0.0;
        double balanceUsd = 0.0;
        int numOfAllMiners = 0;
        int numOfActiveMiners = 0;
        int numOfInactiveMiners = 0;
        int minersHashrate = 0;
        int blockHeight = 0;
        double bisToUsd = 0;
        double bisToBtc = 0;

        /*
         * 1. parse BIS_HN_BASIC_URL (get hypernodes stats)
         */

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                "parsing BIS_HN_BASIC_URL");

        // get json response string from the db
        String basicHypernodeRawData = "";
        RawUrlData rawUrlData = dataDAO.getUrlDataByUrl(BIS_HN_BASIC_URL);
        if (rawUrlData != null) {
            basicHypernodeRawData = rawUrlData.getUrlJsonResponse();
        }else{
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                    "no data to parse. Probably no entries in Room database");
            Toast.makeText(mContext, "HomeScreenDataParser failed to parse data. No data available", Toast.LENGTH_LONG).show();
            return;
        }

        // get all preferences records from SharedPreferences
        //allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(this).getAll();

        try {
            JSONObject basicHypernodeRawDataJsonObj = new JSONObject(basicHypernodeRawData);
            // get block height as the max height of all hypernodes:
            // get all heights in List and get maximum of the List
            List<Integer> blockHeightList = new ArrayList<>(Arrays.asList(0));
            for (Iterator<String> iterator = basicHypernodeRawDataJsonObj.keys(); iterator.hasNext(); ) {
                blockHeightList.add((Integer) basicHypernodeRawDataJsonObj.get(iterator.next()));
            }
            blockHeight = Collections.max(blockHeightList);

            // loop through all the preferences and find records with hypernodes IPs
            for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
                if (entry.getKey().matches("hypernodeIP" + "\\d+")) {
                    // if a hypernode IP found, check its status.
                    try {
                        Object hypernodeStatusObj = basicHypernodeRawDataJsonObj.get((String) entry.getValue());
                        //json returns -1 for inactive hypernodes and the block height to active ones
                        if (hypernodeStatusObj instanceof Number && (Integer)hypernodeStatusObj > -1) {
                            if (Math.abs((blockHeight - (Integer)hypernodeStatusObj))>10){ // if the hypernodes is more than 10 blocks behinds - it's lagging
                                numOfLaggingHypernodes++;
                            }else {
                                numOfActiveHypernodes++; // otherwise it's active
                            }
                        }else if(hypernodeStatusObj instanceof Number && (Integer)hypernodeStatusObj < 0){
                            numOfInactiveHypernodes++;
                        }else{
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                                    "Can't get the status of hypernode IP " + entry.getValue());

                        }
                        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                                "hypernode IP " + entry.getValue() + " status is " + hypernodeStatusObj);
                    }catch (JSONException e){ // if IP is not found in the JSON response - a null pointer exception will be thrown and we will display a snackbar
                        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                                "failed to parse JSON for hypernode IP " + entry.getValue());
                        Toast.makeText(mContext, "Failed to get data for hypernode IP " + entry.getValue()
                                + ". Please double check the IP", Toast.LENGTH_LONG).show();

                    }
                }
            }
        }catch (JSONException e) {
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                    "Failed to parse JSON data from"+BIS_HN_BASIC_URL);
            Toast.makeText(mContext, "Failed to get data from"+BIS_HN_BASIC_URL.substring(0, Math.min(BIS_HN_BASIC_URL.length(), 25)), Toast.LENGTH_LONG).show();

        }

        /*
         * 2. parse BIS_PRICE_URL (get bis to usd and bis to btc prices)
         */

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                "parsing BIS_PRICE_URL");
        // get json response string from the db
        String bisPriceRawData = dataDAO.getUrlDataByUrl(BIS_PRICE_URL).getUrlJsonResponse();
        try{
            JSONObject bisPriceRawDataJsonObj = new JSONObject(bisPriceRawData);
            Object bisToUsdObj = bisPriceRawDataJsonObj.getJSONObject("bismuth").get("usd");
            Object bisToBtcObj = bisPriceRawDataJsonObj.getJSONObject("bismuth").get("btc");

            if (bisToUsdObj instanceof Number){
                bisToUsd = (Double) bisToUsdObj;
            }else{
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                        "Can't get BIS price in USD");
            }

            if (bisToBtcObj instanceof Number){
                bisToBtc = ((Double) bisToBtcObj)*1000000;
            }else{
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                        "Can't get BIS price in BTC");

            }
        }catch(JSONException e){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                    "Failed to parse JSON data from "+ BIS_PRICE_URL);
            Toast.makeText(mContext, "Failed to get data from" + BIS_PRICE_URL.substring(0, Math.min(BIS_PRICE_URL.length(), 25)), Toast.LENGTH_LONG).show();
        }

        /*
         * 3. parse BIS_API_URL (wallets balances)
         */

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeActivity", "parseHomeScreenRawData: "+
                "parsing wallets balances");
        List<Double> blockHeightList = new ArrayList<>(Arrays.asList(0.0));

        // loop through all the preferences looking for a biswWalletAddress
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("bisWalletAddress" + "\\d+")) {
                // if a bisWalletAddress found, get json response corresponding to this address from Room database
                String bisWalletRawData = dataDAO.getUrlDataByUrl(BIS_API_URL + "node" + "/" + "balancegetjson:" + entry.getValue()).getUrlJsonResponse();
                // then get the balance of the wallet and add it to the List
                try {
                    JSONObject bisWalletRawDataJsonObj = new JSONObject(bisWalletRawData);
                    Object walletBalanceObj = bisWalletRawDataJsonObj.get("balance");
                    if (walletBalanceObj instanceof String) {
                        // bis api returns values as strings. Even for non-existent wallet it returns values.
                        blockHeightList.add(Double.parseDouble((String) walletBalanceObj));
                        numOfRegisteredWallets++;
                        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                                "Wallet " + entry.getValue() +
                                " balance is " + walletBalanceObj);
                    }
                }catch(JSONException | ClassCastException e){
                    Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                            "Failed to parse JSON data from "+ BIS_API_URL + " for wallet " + entry.getValue());
                    Toast.makeText(mContext, "Failed to get data from" + BIS_API_URL.substring(0, Math.min(BIS_API_URL.length(), 25))+ " for wallet "
                            + ellipsize(String.valueOf(entry.getValue()), 8), Toast.LENGTH_LONG).show();
                }
            }
        }

        // get a sum of all wallets balances in Bis
        balanceBis = (blockHeightList.stream().mapToDouble(Double::doubleValue).sum());
        // get balances in USD
        balanceUsd = balanceBis*bisToUsd;

        /*
         * 4. parse EGGPOOL_MINER_STATS_URL (mining stats).
         * Parsing eggpool data is tricky: at the beginning of each round values become "null", then they change to "0"
         *  until hashrate is estimated (miner sends first share in the round), and only then real hashrates appear.
         */
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                "parsing mining stats");

        List<Integer> minersHashrateList = new ArrayList<>(Arrays.asList(0));
        List<Integer> minersInactiveList = new ArrayList<>(Arrays.asList(0));
        List<Integer> minersTotalList = new ArrayList<>(Arrays.asList(0));

        // loop through all the preferences looking for a miningWalletAddress
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("miningWalletAddress" + "\\d+")) {
                // if a miningWalletAddress found, get json response corresponding for this address from Room database
                String miningWalletRawData = dataDAO.getUrlDataByUrl(EGGPOOL_MINER_STATS_URL + entry.getValue()).getUrlJsonResponse();
                // then get its status and hashrate and add it to the List
                try {
                    JSONObject miningWalletRawDataJsonObj = new JSONObject(miningWalletRawData);
                    // get object "round" and check it for null
                    JSONObject minersRoundJsonObj;
                    if (miningWalletRawDataJsonObj.isNull("round")){
                        // if the Round object is null - try to get hashrate from previous round
                        minersRoundJsonObj = miningWalletRawDataJsonObj.getJSONObject("lastround");
                    }else{
                        minersRoundJsonObj = miningWalletRawDataJsonObj.getJSONObject("round");
                    }
                    // if both Round and lastround are nulls - then fail to parse json and value remains 0.
                    Object minersHashrateObj = minersRoundJsonObj.get("hr"); // current hashrate in MH/s
                    if (minersHashrateObj instanceof Number) {
                        minersHashrateList.add((Integer) minersHashrateObj);
                        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                                "hashrate for wallet " + entry.getValue() +
                                "is " + minersHashrateObj);
                    }
                    // get total number of miners for this wallet
                    Object minersTotalObj = miningWalletRawDataJsonObj.getJSONObject("workers").get("count");
                    if (minersTotalObj instanceof Number) {
                        minersTotalList.add((Integer) minersTotalObj);
                        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                                "total miners for wallet " + entry.getValue()+
                                "is " + minersTotalObj);
                    }

                    // get total missing miners for this wallet
                    Object minersInactiveObj = miningWalletRawDataJsonObj.getJSONObject("workers").get("missing_count");
                    if (minersInactiveObj  instanceof Number) {
                        minersInactiveList.add((Integer) minersInactiveObj);
                        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                                "inactive miners for wallet " + entry.getValue() +
                                "is " + minersInactiveObj );
                    }
                }catch(JSONException | ClassCastException e){
                    Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HomeScreenDataParser", "parseHomeScreenData: "+
                            "Failed to parse JSON data from "+ EGGPOOL_MINER_STATS_URL + " for wallet " + entry.getValue());
                    Toast.makeText(mContext, "Failed to get data from " + EGGPOOL_MINER_STATS_URL.substring(0, Math.min(EGGPOOL_MINER_STATS_URL.length(), 25)) + " for wallet "
                            + ellipsize(String.valueOf(entry.getValue()), 8), Toast.LENGTH_LONG).show();
                }
            }
        }

        // get a sum of hashrates
        for (int i: minersHashrateList){
            minersHashrate += i;
        }

        for (int i: minersInactiveList){
            numOfInactiveMiners += i;
        }

        for (int i: minersTotalList){
            numOfAllMiners += i;
        }
        //numOfInactiveMiners = minersInactiveList.stream().mapToInt(Integer::intValue).sum();
        //minersHashrate = Double.parseDouble(decimalFormat.format(minersHashrateList.stream().mapToInt(Integer::intValue).sum()/1000d));
        //numOfAllMiners = minersTotalList.stream().mapToInt(Integer::intValue).sum();
        numOfActiveMiners = numOfAllMiners -  numOfInactiveMiners;

        /*
         * 5. update the database
         */

        ParsedHomeScreenData parsedHomeScreenData = new ParsedHomeScreenData();
        parsedHomeScreenData.setId(1);
        parsedHomeScreenData.setHypernodesActive(numOfActiveHypernodes);
        parsedHomeScreenData.setHypernodesInactive(numOfInactiveHypernodes);
        parsedHomeScreenData.setHypernodesLagging(numOfLaggingHypernodes);
        parsedHomeScreenData.setRegisteredWallets(numOfRegisteredWallets);
        parsedHomeScreenData.setBalanceBis(balanceBis);
        parsedHomeScreenData.setBalanceUsd(balanceUsd);
        parsedHomeScreenData.setMinersActive(numOfActiveMiners);
        parsedHomeScreenData.setMinersInactive(numOfInactiveMiners);
        parsedHomeScreenData.setMinersHashrate(minersHashrate);
        parsedHomeScreenData.setBlockHeight(blockHeight);
        parsedHomeScreenData.setBisToBtc(bisToBtc);
        parsedHomeScreenData.setBisToUsd(bisToUsd);

        dataDAO.updateParsedHomeScreenData(parsedHomeScreenData);
    }
}