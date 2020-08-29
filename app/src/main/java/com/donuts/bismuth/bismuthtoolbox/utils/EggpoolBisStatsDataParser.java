package com.donuts.bismuth.bismuthtoolbox.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolBisStatsData;
import com.donuts.bismuth.bismuthtoolbox.Data.RawUrlData;

import org.json.JSONException;
import org.json.JSONObject;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_HN_VERBOSE_URL;
import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.EGGPOOL_BIS_STATS_URL;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsizeMiddle;

/**
*  This class parses all the data for Eggpool EGGPOOL_BIS_STATS_URL = "https://eggpool.net/api/currencies"
 *  and saves it in  EggpoolBisStatsData table of Room database:
 *  The changes in Room database are observed with LiveData and corresponding views are updated.
 */

public class EggpoolBisStatsDataParser {
    private Context mContext;

    public EggpoolBisStatsDataParser(Context context){
        mContext = context;
    }

    public void parseBisStatsData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " EggpoolBisStatsDataParser", "parseBisStatsData: "+
                "called");

        DataDAO dataDAO = DataRoomDatabase.getInstance(mContext.getApplicationContext()).getDataDAO();

        // Get json response with hypernodes stats from BIS_HN_VERBOSE_URL from Room database.
        String eggpoolBisStatsRawData = "";
        RawUrlData rawUrlData = dataDAO.getUrlDataByUrl(EGGPOOL_BIS_STATS_URL);
        if (rawUrlData != null) {
            eggpoolBisStatsRawData = rawUrlData.getUrlJsonResponse();
        }else{
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " EggpoolBisStatsDataParser", "parseBisStatsData: "+
                    "no data to parse. Probably no entries in Room database");
            Toast.makeText(mContext, "EggpoolBisStatsDataParser failed to parse data. No data available", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " EggpoolBisStatsDataParser", "parseBisStatsData: " +
                "parsing eggpool Bis stats data...");

        try {
            JSONObject eggpoolBisStatsRawDataJsonObj = new JSONObject(eggpoolBisStatsRawData);

            EggpoolBisStatsData eggpoolBisStatsData = new EggpoolBisStatsData();
            eggpoolBisStatsData.setId(1);
            eggpoolBisStatsData.setEggpoolBisNetworkHashrate(eggpoolBisStatsRawDataJsonObj.getJSONObject("BIS").optLong("network_hashrate", 1));
            eggpoolBisStatsData.setEggpool24hPoolHashrate(eggpoolBisStatsRawDataJsonObj.getJSONObject("BIS").optLong("24h_hashrate", 1));
            eggpoolBisStatsData.setEggpool24hPoolReward(eggpoolBisStatsRawDataJsonObj.getJSONObject("BIS").optDouble("24h_rewards", 0));
            eggpoolBisStatsData.setEggpoolBisNetworkBlockHeight(eggpoolBisStatsRawDataJsonObj.getJSONObject("BIS").optLong("height", 1));
            eggpoolBisStatsData.setEggpoolBisNetworkDifficulty(eggpoolBisStatsRawDataJsonObj.getJSONObject("BIS").optDouble("difficulty", 1));
            eggpoolBisStatsData.setEggpoolBisNetworkHashrate(eggpoolBisStatsRawDataJsonObj.getJSONObject("BIS").optLong("hashrate", 1));

            dataDAO.updateEggpoolBisStatsData(eggpoolBisStatsData);

        }catch(JSONException | ClassCastException e){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " EggpoolBisStatsDataParser", "parseBisStatsData: " +
                    "Failed to parse JSON data from "+ EGGPOOL_BIS_STATS_URL);
            Toast.makeText(mContext, "Failed to get data from " + ellipsizeMiddle(EGGPOOL_BIS_STATS_URL, 25), Toast.LENGTH_LONG).show();
        }

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " EggpoolBisStatsDataParser", "parseBisStatsData: " +
                "eggpool Bis stats data parsed");
    }
}