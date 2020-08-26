package com.donuts.bismuth.bismuthtoolbox.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.Data.CoingeckoBisPriceData;
import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import static com.donuts.bismuth.bismuthtoolbox.Models.Constants.COINGECKO_BIS_PRICE_URL;
import static com.donuts.bismuth.bismuthtoolbox.utils.StringEllipsizer.ellipsizeMiddle;

/**
*  This class parses Bis price from COINGECKO_BIS_PRICE_URL = "https://api.coingecko.com/api/v3/simple/price?ids=bismuth&vs_currencies=usd,btc"
 *  and saves it in  CoingeckoBisPriceData table of Room database:
 *  The changes in Room database are observed with LiveData and corresponding views are updated.
 */

public class CoingeckoBisPriceDataParser {
    private Context mContext;

    public CoingeckoBisPriceDataParser(Context context){
        mContext = context;
    }

    public void parseCoingeckoBisPriceData(){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " CoingeckoBisPriceDataParser", "parseBisStatsData: " +
                "called");

        DataDAO dataDAO = DataRoomDatabase.getInstance(mContext.getApplicationContext()).getDataDAO();

        // Get json response with Bis stats from EGGPOOL_BIS_STATS_URL from Room database.
        String coingeckoBisPriceRawData = dataDAO.getUrlDataByUrl(COINGECKO_BIS_PRICE_URL).getUrlJsonResponse();

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " CoingeckoBisPriceDataParser", "parseBisStatsData: " +
                "parsing Bis price data from COINGECKO_BIS_PRICE_URL...");

        try {
            JSONObject coingeckoBisPriceRawDataJsonObj = new JSONObject(coingeckoBisPriceRawData);

            CoingeckoBisPriceData coingeckoBisPriceData = new CoingeckoBisPriceData();
            coingeckoBisPriceData.setId(1);
            coingeckoBisPriceData.setCoingeckoBisPriceUsd(coingeckoBisPriceRawDataJsonObj.getJSONObject("bismuth").optDouble("usd", 1));
            coingeckoBisPriceData.setCoingeckoBisPriceBtc(coingeckoBisPriceRawDataJsonObj.getJSONObject("bismuth").optDouble("btc", 1));

            dataDAO.updateCoingeckoBisPriceData(coingeckoBisPriceData);

        }catch(JSONException | ClassCastException e){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " CoingeckoBisPriceDataParser", "parseBisStatsData: " +
                    "Failed to parse JSON data from "+ COINGECKO_BIS_PRICE_URL);
            Toast.makeText(mContext, "Failed to get data from " + ellipsizeMiddle(COINGECKO_BIS_PRICE_URL, 25), Toast.LENGTH_LONG).show();
        }

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " CoingeckoBisPriceDataParser", "parseBisStatsData: " +
                "Bis price data from COINGECKO_BIS_PRICE_URL parsed");
    }
}