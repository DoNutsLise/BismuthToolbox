package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.Data.ParsedHomeScreenData;
import com.donuts.bismuth.bismuthtoolbox.Data.ParsedMiningScreenData;
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
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_PRICE_COINGECKO_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_BIS_STATS_URL;
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
                bisToBtc = (Double) bisToBtcObj*1000000;  // make it in mBTC
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
                "parsing mining stats");

        List<Integer> minersHashrateList = new ArrayList<>(Arrays.asList(0));
        List<Integer> minersInactiveList = new ArrayList<>(Arrays.asList(0));
        List<Integer> minersTotalList = new ArrayList<>(Arrays.asList(0));

        // loop through all the preferences looking for a miningWalletAddress
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("miningWalletAddress" + "\\d+")) {
                // if a miningWalletAddress found, get json response corresponding for this address from Room database
                String miningWalletRawData = dataDAO.getUrlDataByUrl(EGGPOOL_MINER_STATS_URL + entry.getValue()).getUrlJsonResponse();
                // then parse json for this wallet
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
                    Toast.makeText(mContext, "Failed to get data from " + ellipsize(EGGPOOL_MINER_STATS_URL, 25) + " for wallet "
                            + ellipsize(String.valueOf(entry.getValue()), 10), Toast.LENGTH_LONG).show();
                }
            }
        }

        /*
         * 5. update the database
         */

        ParsedMiningScreenData parsedMiningScreenData = new ParsedMiningScreenData();
        parsedMiningScreenData.setId(1);
        parsedMiningScreenData.setTest(99);

        dataDAO.updateParsedMiningScreenData(parsedMiningScreenData);
    }
}