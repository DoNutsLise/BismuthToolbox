package com.donuts.bismuth.bismuthtoolbox.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.Data.HypernodesRewardAddressesData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.BIS_API_URL;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsizeMiddle;

/**
*  This class parses the data for hypernode reward addresses for hypernodes screen
 *  and saves it in  HypernodesRewardAddressesData table of Room database:
 *  The changes in Room database are observed with LiveData and corresponding views are updated.
 */

public class HypernodesRewardAddressDataParser {
    private Context mContext;

    public HypernodesRewardAddressDataParser(Context context){
        mContext = context;
    }

    public void parseHypernodesRewardAddressesData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesRewardAddressDataParser", "parseHypernodesRewardAddressesData: "+
                "called");

        DataDAO dataDAO = DataRoomDatabase.getInstance(mContext.getApplicationContext()).getDataDAO();

        // we clear HypernodesRewardAddressesData table completely and populate it with new data (or default data at the end if there is no new data)
        dataDAO.clearHypernodesRewardAddressesDataTableInDb();

        // get a list of all my hypernode reward addresses
        List<String> myHypernodesRewardAddressesList = dataDAO.getMyHypernodesRewardAddressesList();

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesRewardAddressDataParser", "parseHypernodesRewardAddressesData: "+
                "parsing hypernodes rewards addresses data...");

        // for each hypernode reward address add data from json to the database. There could be 1 reward
        // address for several hns, so it's just a list of transaction of all wallets regardless of which hn it belongs to.
        for(String myHypernodesRewardAddress : myHypernodesRewardAddressesList){
            try {
                // for the current hypernode reward  address get raw json data from the db

                String myHypernodesRewardAddressRawData;
                // first, check if there is any data for this URL in the db (it can be null)
                if (dataDAO.getUrlDataByUrl(BIS_API_URL + "node" + "/" + "addlistlimjson:" + myHypernodesRewardAddress +":100") != null) {
                    myHypernodesRewardAddressRawData = dataDAO.getUrlDataByUrl(BIS_API_URL + "node" + "/" + "addlistlimjson:" + myHypernodesRewardAddress + ":100").getUrlJsonResponse();
                }else{
                    // if there is no data (null), go to the next address
                    continue;
                }
                // e.g. https://bismuth.online/api/node/addlistlimjson:939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127f:100

                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesRewardAddressDataParser", "parseHypernodesRewardAddressesData: "+
                        "parsing data for the hypernodes rewards address:  " + myHypernodesRewardAddress);
                // json response from the api should return an array of transaction objects.
                JSONArray myHypernodesRewardAddressRawDataJsonArray = new JSONArray(myHypernodesRewardAddressRawData);

                // loop through all transactions objects and add them to the db. We requested 100 transactions, but there might be fewer.
                for(int i=0; i<myHypernodesRewardAddressRawDataJsonArray.length(); i++) {
                    HypernodesRewardAddressesData hypernodesRewardAddressesData = new HypernodesRewardAddressesData();

                    hypernodesRewardAddressesData.setTransactionTimestamp(myHypernodesRewardAddressRawDataJsonArray.getJSONObject(i).optDouble("timestamp", 1597505828.44));
                    hypernodesRewardAddressesData.setTransactionBlockHeight(myHypernodesRewardAddressRawDataJsonArray.getJSONObject(i).optLong("block_height", 1));
                    hypernodesRewardAddressesData.setTransactionAmount(myHypernodesRewardAddressRawDataJsonArray.getJSONObject(i).optDouble("amount", 0));
                    hypernodesRewardAddressesData.setSenderAddress(myHypernodesRewardAddressRawDataJsonArray.getJSONObject(i).optString("address", "N/A"));
                    hypernodesRewardAddressesData.setRecipientAddress(myHypernodesRewardAddressRawDataJsonArray.getJSONObject(i).optString("recipient", "N/A"));
                    hypernodesRewardAddressesData.setHypernodeRewardTransactionSignature(myHypernodesRewardAddressRawDataJsonArray.getJSONObject(i).optString("signature", "N/A"));

                    dataDAO.insertHypernodesRewardAddressesData(hypernodesRewardAddressesData);
                }

            }catch(JSONException | ClassCastException e){
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesRewardAddressDataParser", "parseHypernodesRewardAddressesData: "+
                        "Failed to parse JSON data from reward address: "+ myHypernodesRewardAddress);
                Toast.makeText(mContext, "Failed to get data for hypernode reward address" + ellipsizeMiddle(myHypernodesRewardAddress, 25), Toast.LENGTH_LONG).show();
            }
        }

        // we cleared the table at the beginning, so if it's empty by now - populate it with default values.
        if (dataDAO.getNumOfHypernodesRewardsTransactions()<1){
            dataDAO.insertHypernodesRewardAddressesData(HypernodesRewardAddressesData.populateHypernodesRewardAddressesData());
        }

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesRewardAddressDataParser", "parseHypernodesRewardAddressesData: "+
                "hypernodes rewards addresses data parsed for " + dataDAO.getNumOfHypernodesRewardsTransactions() + " transactions");
    }
}