package com.donuts.bismuth.bismuthtoolbox.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.Data.RawUrlData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * This asynctask class is called by every Activity when they need to update data. Activities decide when it's time to get fresh data and they
 * supply a List of urls to this asynctask for update.
 * This asynctask pulls data for each supplied in the list url and updated the Room database with raw data json responses.
 * It notifies the activity when the data is updated through InterfaceOnDataFetched. Activity is then responsible for parsing the data.
 *
 * NOTE: asyncTask is deprecated as of Dec-2019
 * Consider multiple threads in asynctask: https://stackoverflow.com/questions/41673318/asynctask-implementation-using-callback-interface-handle-multiple-call-respons
 * Consider threadpoolexecutor: https://developer.android.com/reference/java/util/concurrent/ThreadPoolExecutor
 */

public class AsyncFetchData extends AsyncTask<List<String>, Void, Void> {
    private InterfaceOnDataFetched listener;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private HttpURLConnection httpURLConnection;
    private List<String> urls;
    private ArrayList<String> apiResponseList;
    private WeakReference<Context> contextRef;
    private Context mContext;

    public void setOnDataFetchedListener(InterfaceOnDataFetched listener) {
        this.listener = listener;
    }

    public AsyncFetchData(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(List<String>... params) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): called");

        // a list of urls supplied by the activity for update.
        List<String> urls = params[0];

        // reference application context, not activity context!
        DataDAO dataDAO = DataRoomDatabase.getInstance(mContext.getApplicationContext()).getDataDAO();

        // keep this loop for testing purposes
//        for(int i=0; i<10; i++){
//            try {
//                Thread.sleep(1000);
//                Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): "+
//                        "sleeping" + i);
//            }catch( java.lang.InterruptedException e){
//                Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): "+
//                        "sleeping" + i);
//            }
//        }

        // 1. fetch data from servers and save it in "RawUrlData" entity of Room db
        for(String urlsItem : urls) {
            RawUrlData fetchedUrlData = fetchData(urlsItem);
            //update the record in db (or insert the record if it doesn't exist)
            RawUrlData rawUrlDataFromDb = dataDAO.getUrlDataByUrl(urlsItem);
            if (rawUrlDataFromDb == null) {
                dataDAO.insertUrlData(fetchedUrlData);
            } else {
                dataDAO.updateUrlData(fetchedUrlData);
            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(onCancelled): "+
                "Activity that called this asynctask was destroyed/paused and called to finish this asynctask");
    }

    @Override
    protected void onPostExecute(Void param) {
        super.onPostExecute(param);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(onPostExecute): "+
                "called");

        if(isCancelled()){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(onPostExecute): "+
                    "Activity that called this asynctask was destroyed/paused and called to finish this asynctask");
            return;
        }
        // notify calling activity about fresh data available in the Room database
        listener.onDataFetched();
    }

    private RawUrlData fetchData(String dataURL){
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " AsyncFetchData", "fetchData: "+
                "fetching data for "+dataURL);

        /*
        * standard HTTP request with HttpURLConnection
         */
        RawUrlData rawUrlData = new RawUrlData();
        String jsonResponse = "";
        String updateErrorString = "";
        boolean isUpdateSuccess = false;

        try {
            URL url = new URL(dataURL); // generate url from string
            /*
             * set http connection up
             */
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(false); // this is false for GET request and true for POST request. If you re-use connection for different requests you need to reset it with if switch: https://stackoverflow.com/questions/8587913/what-exactly-does-urlconnection-setdooutput-affect#8587995

            /*
             * check if connection is ok - then download data
             */
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): "+
                        "doInBackground: http connection OK for: "+dataURL);

                // Read data sent from server
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String nextLine;

                while ((nextLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(nextLine);
                }
                jsonResponse = stringBuilder.toString();
                isUpdateSuccess = true;

                Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): "+
                        "got json strings from api requests for: "+dataURL);
            } else {
                Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): "+
                        "Server refused connection for: "+dataURL);
                updateErrorString = "Error connecting to the server. HttpURLConnection response code = " + responseCode;
            }
        } catch (java.net.SocketTimeoutException e) {
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): "+
                    "HttpURLConnection timeout for: "+dataURL);
            updateErrorString = e.toString();
        } catch (IOException e) {
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): "+
                    "Failed to set up HttpURLConnection for: "+dataURL);
            updateErrorString = e.toString();
        } finally {
            httpURLConnection.disconnect();
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss"), "AsyncFetchFreshData(doInBackground): " +
                    "http disconnected for: " + dataURL);

            rawUrlData.setUrl(dataURL);
            rawUrlData.setUrlJsonResponse(jsonResponse);
            rawUrlData.setUrlLastUpdatedError(updateErrorString);
            rawUrlData.setUrlLastUpdatedSuccess(isUpdateSuccess);
            rawUrlData.setUrlLastUpdatedTime(System.currentTimeMillis());
        }
        return rawUrlData;
    }
}