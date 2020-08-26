package com.donuts.bismuth.bismuthtoolbox.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolBalanceData;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolMinersData;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolPayoutsData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_MINER_STATS_URL;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsizeMiddle;

/**
*  This class parses all the data for Eggpool miners from EGGPOOL_MINER_STATS_URL and saves it in several tables of Room database:
 *  EggpoolMinersData (for MinersFragment), EggpoolPayoutsData (a list of 10 last payouts for each wallet in  payouts fragment) and EggpoolBalanceData (for the remaining textviews of both fragments)
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

        // we clear EggpoolMinersData and EggpoolPayoutsData tables completely and populate them with new data (or default data at the end if there is no new data)
        dataDAO.clearMinersTableInDb();
        dataDAO.clearEggpoolPayoutsTableInDb();
        dataDAO.clearEggpoolBalanceTableInDb();


        /*
         * Parse EGGPOOL_MINER_STATS_URL (mining stats).
         * Parsing eggpool data is tricky: at the beginning of each round values become "null", then they change to "0"
         *  until hashrate is estimated (miner sends first share in the round), and only then real hashrates appear.
         */
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                "parsing eggpool mining stats...");

        // loop through all the preferences looking for a miningWalletAddress
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("miningWalletAddress" + "\\d+")) {
                // if a miningWalletAddress found, get json response corresponding for this address from Room database
                String miningWalletRawData = dataDAO.getUrlDataByUrl(EGGPOOL_MINER_STATS_URL + entry.getValue()).getUrlJsonResponse();
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " EggpoolMinersDataParser", "parseMinersData: "+
                        "parsing eggpool data for wallet " + entry.getValue());
                // then parse json for this wallet
                try {
                    JSONObject miningWalletRawDataJsonObj = new JSONObject(miningWalletRawData);

                    // 1. check: if there are no miners associated with this wallet ("count"<1); if so - send a toast and move to the next address
                        if (miningWalletRawDataJsonObj.getJSONObject("workers").optInt("count", 0)<1) {
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                                    "there are no miners associated with the wallet " + ellipsizeMiddle(entry.getKey(), 10));
                            Toast.makeText(mContext, "There are no miners associated with the wallet " + ellipsizeMiddle(entry.getKey(), 10) +
                                    ". Please check the wallet address. ", Toast.LENGTH_LONG).show();
                            continue;
                        }

                    // 2. get object "detail" and check if it's not null; if so - send a toast and move to the next address
                        if (miningWalletRawDataJsonObj.getJSONObject("workers").isNull("detail")){
                            // if "detail" object is null - go to the next miningWalletAddress
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                                    " details object is null for wallet " + ellipsizeMiddle(entry.getKey(), 10));
                            Toast.makeText(mContext, "details object is null for wallet " + ellipsizeMiddle(entry.getKey(), 10), Toast.LENGTH_LONG).show();
                            continue;
                        }

                    // 3. if details are available - loop through "details" object and add information about every miner to the EggpoolMinersData table of the db
                        JSONObject workersDetailJsonObject = miningWalletRawDataJsonObj.getJSONObject("workers").getJSONObject("detail");

                        for(int j=0; j<workersDetailJsonObject.length(); j++) {
                            String key = workersDetailJsonObject.names().getString(j); // miner's name
                            if (workersDetailJsonObject.get(key) instanceof JSONArray) {
                                EggpoolMinersData eggpoolMinersData = new EggpoolMinersData();
                                eggpoolMinersData.setMinerName(key);

                                // for a given miner there is an array with the following stats:
                                // a. current hashrate; it's better to get it from the 0th element of the minerStatsJsonArray than from the 12th element of historic hashrates array, because there it could be 0 at the start of the round.
                                // b: miner last seen (timestamp long).
                                // c: hashrate for the last 12h (array of int) + current hashrate. The 13th value can be 0 at the beginning of the round. Array can be empty (or null) for a missing miner.
                                // d: shares for the last 12h  (array of int) + current shares. The 13th value can be 0 at the beginning of the round. Array can be empty (or null) for a missing miner.

                                JSONArray minerStatsJsonArray = (JSONArray) workersDetailJsonObject.get(key);
                                // a. current hashrate
                                    eggpoolMinersData.setHashrateCurrent((int) minerStatsJsonArray.optLong(0, 0));
                                // b. miner last seen
                                    eggpoolMinersData.setLastSeen(minerStatsJsonArray.optLong(1, 1));
                                // c. 12h hashrate
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

                                // d. 12h shares
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
                                Toast.makeText(mContext, "Failed to get data from " + ellipsizeMiddle(EGGPOOL_MINER_STATS_URL, 25) + " for wallet "
                                        + ellipsizeMiddle(String.valueOf(entry.getValue()), 10)+ ", miner " + key, Toast.LENGTH_LONG).show();
                            }
                        }

                    // 4. parse payouts stats: payouts object is an array of 10 arrays for a good wallet (for a non-existent wallet it can be an object with 3 entries, but never null). This data is saved in EggpoolPayoutsData

                        if (miningWalletRawDataJsonObj.isNull("payouts") || !(miningWalletRawDataJsonObj.get("payouts") instanceof JSONArray)){
                            // if payouts array is null or not an array - move to the next wallet.
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " EggpoolMinersDataParser", "parseMinersData: "+
                                    "unexpected format of payouts data");
                            Toast.makeText(mContext, "Unexpected format of payouts data from" + ellipsizeMiddle(EGGPOOL_MINER_STATS_URL, 25), Toast.LENGTH_LONG).show();
                            continue;
                        }

                        JSONArray payoutsJsonArray = miningWalletRawDataJsonObj.getJSONArray("payouts");
                        EggpoolPayoutsData eggpoolPayoutsData = new EggpoolPayoutsData();
                        for(int j=0; j<payoutsJsonArray.length(); j++) {
                            eggpoolPayoutsData.setPayoutTime(payoutsJsonArray.getJSONArray(j).optString(0, "N/A"));
                            eggpoolPayoutsData.setPayoutAmount(payoutsJsonArray.getJSONArray(j).optDouble(1, 0));
                            if (Objects.equals(payoutsJsonArray.getJSONArray(j).optString(2, " ")," ")){
                                eggpoolPayoutsData.setPayoutTx("no data available");
                            }else{
                                eggpoolPayoutsData.setPayoutTx("<html> <a href=\"http://bismuth.online/details?mydetail="+payoutsJsonArray.getJSONArray(j).optString(2, " ")+"\">"+payoutsJsonArray.getJSONArray(j).optString(2, " ")+"</a> </html>");
                            }
                            dataDAO.insertPayoutsData(eggpoolPayoutsData);
                        }

                    // 5. finally get immature, unpaid and total paid balances for this wallet (this data is saved in EggpoolMiscData table of the database)

                        if (miningWalletRawDataJsonObj.isNull("BIS") || !(miningWalletRawDataJsonObj.get("BIS") instanceof JSONObject)){
                            // if BIS object is null or not an object - move to the next wallet.
                            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " EggpoolMinersDataParser", "parseMinersData: "+
                                    "unexpected format of BIS data");
                            continue;
                        }

                        EggpoolBalanceData eggpoolBalanceData = new EggpoolBalanceData();
                        eggpoolBalanceData.setEggpoolWallet(String.valueOf(entry.getValue()));
                        eggpoolBalanceData.setImmatureEggpoolBalance(miningWalletRawDataJsonObj.getJSONObject("BIS").optDouble("immature", 0));
                        eggpoolBalanceData.setUnpaidEggpoolBalance(miningWalletRawDataJsonObj.getJSONObject("BIS").optDouble("balance", 0));
                        eggpoolBalanceData.setTotalPaidEggpoolBalance(miningWalletRawDataJsonObj.getJSONObject("BIS").optDouble("total_paid", 0));

                        dataDAO.insertEggpoolBalanceData(eggpoolBalanceData);


                }catch(JSONException | ClassCastException e){
                    Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                            "Failed to parse JSON data from "+ EGGPOOL_MINER_STATS_URL + " for wallet " + entry.getValue());
                    Toast.makeText(mContext, "Failed to get data from " + ellipsizeMiddle(EGGPOOL_MINER_STATS_URL, 25) + " for wallet "
                            + ellipsizeMiddle(String.valueOf(entry.getValue()), 10), Toast.LENGTH_LONG).show();
                }
            }
        }

        // we cleared the tables at the beginning, so if they are empty by now - populate them with default values.
        if (dataDAO.getNumOfMiners()<1){
            dataDAO.insertAllMiners(EggpoolMinersData.populateMiners());
        }
        if (dataDAO.getNumOfPayouts()<1){
            dataDAO.insertAllPayouts(EggpoolPayoutsData.populatePayouts());
        }
        if (dataDAO.getNumOfEggpoolBalances()<1){
            dataDAO.insertAllEggpoolBalanceData(EggpoolBalanceData.populateEggpoolBalanceData());
        }


        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MinersDataParser", "parseMinersData: "+
                "Eggpool miners data parsed.");

    }
}