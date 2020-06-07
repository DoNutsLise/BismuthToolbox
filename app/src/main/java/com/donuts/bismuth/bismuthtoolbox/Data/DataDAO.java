package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DataDAO {

    //RawUrlData:

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertUrlData(RawUrlData urlData);

    @Delete
    public void deleteUrlData(RawUrlData... urlData);

    @Update
    public void updateUrlData(RawUrlData... urlData);

    @Query("SELECT * from raw_url_data WHERE url= :url")
    RawUrlData getUrlDataByUrl(String url);

    @Query("UPDATE raw_url_data SET url_last_update_time= :url_last_update_time")
    void updateAllUrlLastUpdateTime(long url_last_update_time);

    @Query("SELECT * from raw_url_data")
    RawUrlData getAllUrlData();

    @Query("SELECT COUNT(url_last_update_time) from raw_url_data")
    long getNumOfRawUrlDataRecords();

    /*
     *  ParsedHomeScreenData
     */

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertParsedHomeScreenData(ParsedHomeScreenData parsedUrlData);

    @Update
    void updateParsedHomeScreenData(ParsedHomeScreenData parsedUrlData);

    @Query("SELECT * from parsed_home_screen_data")
    ParsedHomeScreenData getParsedHomeScreenData();

    @Query("SELECT * from parsed_home_screen_data") // live data for the observer
    LiveData<ParsedHomeScreenData> getParsedHomeScreenLiveData();

    @Insert // for initializing the database with default values
    void insertAllParsedHomeScreenData(ParsedHomeScreenData... parsedHomeScreenData);

    /*
     *  ParsedMiningScreenData
     */

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertParsedMiningScreenData(ParsedMiningScreenData parsedUrlData);

    @Update
    void updateParsedMiningScreenData(ParsedMiningScreenData parsedUrlData);

    @Query("SELECT * from parsed_mining_screen_data")
    ParsedMiningScreenData getParsedMiningScreenData();

    @Query("SELECT * from parsed_mining_screen_data") // live data for the observer
    LiveData<ParsedMiningScreenData> getParsedMiningScreenLiveData();

    @Insert // for initializing the database with default values
    void insertAllParsedMiningScreenData(ParsedMiningScreenData... parsedMiningScreenData);

}
