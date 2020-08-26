package com.donuts.bismuth.bismuthtoolbox.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.AllHypernodesData;
import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_HN_VERBOSE_URL;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsizeMiddle;

/**
*  This class parses all the data for BIS_HN_VERBOSE_URL
 *  and saves it in  HypernodesVerboseData table of Room database:
 *  The changes in Room database are observed with LiveData and corresponding views are updated.
 */

public class HypernodesVerboseDataParser {
    private Context mContext;

    public HypernodesVerboseDataParser(Context context){
        mContext = context;
    }

    public void parseHypernodesVerboseData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesVerboseDataParser", "parseHypernodesVerboseData: "+
                "called");

        DataDAO dataDAO = DataRoomDatabase.getInstance(mContext.getApplicationContext()).getDataDAO();

        // we clear AllHypernodesData table completely and populate it with new data (or default data at the end if there is no new data)
        dataDAO.clearAllHypernodesDataTableInDb();

        // Get json response with hypernodes stats from BIS_HN_VERBOSE_URL from Room database.
        String hypernodesVerboseRawData = dataDAO.getUrlDataByUrl(BIS_HN_VERBOSE_URL).getUrlJsonResponse();

        // Get a list of my hypernodes IPs from shared preferences
        Map<String, ?> allPreferencesKeys = android.preference.PreferenceManager.getDefaultSharedPreferences(mContext).getAll();
        List<String> myHypernodesIps = new ArrayList<>(Arrays.asList());
        for (Map.Entry<String, ?> entry: allPreferencesKeys.entrySet()) {
            if (entry.getKey().matches("hypernodeIP"+"\\d+")) {
                // if a hypernode IP found in shared preferences, add it to the list of my ips
                myHypernodesIps.add(String.valueOf(entry.getValue()));
            }
        }

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesVerboseDataParser", "parseHypernodesVerboseData: " +
                "parsing hypernodes verbose data...");

        try {
            JSONObject hypernodesVerboseRawDataJsonObj = new JSONObject(hypernodesVerboseRawData);
            // at the moment we are only interested in "data" object which is an array and contains arrays of 9 for each hypernode

            // 1. get jsonArray "data" and check if it's not null; if so - send a toast and move to the next address
            if (hypernodesVerboseRawDataJsonObj.isNull("data") || !(hypernodesVerboseRawDataJsonObj.get("data") instanceof JSONArray)){
                // if "data" object is null - go to the next miningWalletAddress
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesVerboseDataParser", "parseHypernodesVerboseData: "+
                        " unexpected format of DATA object of hypernodes json");
                Toast.makeText(mContext, "Unexpected format of DATA array from" + ellipsizeMiddle(BIS_HN_VERBOSE_URL, 25), Toast.LENGTH_LONG).show();
                return;
            }

            // 2. loop through "data" array and get data for each hypernode
            JSONArray hypernodesDataJsonArray = hypernodesVerboseRawDataJsonObj.getJSONArray("data");

            for(int i=0; i<hypernodesDataJsonArray.length(); i++) {
                AllHypernodesData allHypernodesData = new AllHypernodesData();
                // each record in "data" array is an array of 9 with data for a particular hypernode

                // 2.1 check if the entry is a json array of 9; if not - move to the next entry.
                if (hypernodesDataJsonArray.get(i) instanceof JSONArray){
                    // write all the data for the current hypernode from the array to allHypernodesData
                    allHypernodesData.setHypernodeID(hypernodesDataJsonArray.getJSONArray(i).optString(0, "N/A"));
                    allHypernodesData.setHypernodeIP(hypernodesDataJsonArray.getJSONArray(i).optString(1, "N/A"));
                    allHypernodesData.setHypernodePort(hypernodesDataJsonArray.getJSONArray(i).optString(2, "N/A"));
                    allHypernodesData.setHypernodeTier(hypernodesDataJsonArray.getJSONArray(i).optInt(3, 1));
                    allHypernodesData.setHypernodeCollateralAddress(hypernodesDataJsonArray.getJSONArray(i).optString(4, "N/A"));
                    allHypernodesData.setHypernodeRewardAddress(hypernodesDataJsonArray.getJSONArray(i).optString(5, "N/A"));
                    allHypernodesData.setHypernodeBlockHeight(hypernodesDataJsonArray.getJSONArray(i).optLong(6, 0));
                    allHypernodesData.setHypernodeVersion(hypernodesDataJsonArray.getJSONArray(i).optString(7, "N/A"));
                    allHypernodesData.setHypernodeStatus(hypernodesDataJsonArray.getJSONArray(i).optString(8, "N/A"));

                    // check if the hypernode belongs to me (compare ip with those in shared preferences)
                    if(myHypernodesIps.contains(hypernodesDataJsonArray.getJSONArray(i).optString(1, "N/A"))) {
                        allHypernodesData.setHypernodeMine(true);
                    }else{
                        allHypernodesData.setHypernodeMine(false);
                    }

                    // insert the record into the db table
                    dataDAO.insertHypernodeData(allHypernodesData);
                }
            }

        }catch(JSONException | ClassCastException e){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesVerboseDataParser", "parseHypernodesVerboseData: "+
                    "Failed to parse JSON data from "+ BIS_HN_VERBOSE_URL);
            Toast.makeText(mContext, "Failed to get data from " + ellipsizeMiddle(BIS_HN_VERBOSE_URL, 25), Toast.LENGTH_LONG).show();
        }

        // we cleared the table at the beginning, so if it's empty by now - populate it with default values.
        if (dataDAO.getNumOfHypernodes()<1){
            dataDAO.insertAllHypernodesData(AllHypernodesData.populateAllHypernodesData());
        }

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesVerboseDataParser", "parseHypernodesVerboseData: " +
                "hypernodes data parsed for " + dataDAO.getNumOfHypernodes() + " hypernodes");
    }
}