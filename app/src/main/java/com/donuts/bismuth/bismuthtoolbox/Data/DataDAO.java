package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

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
     *  Eggpool Miners Data
     */

    @Query("SELECT COUNT(miner_name) from EggpoolMinersData")
    long getNumOfMiners();

    @Insert // for initializing the database with default values
    void insertAllMiners(EggpoolMinersData... miners);

    @Query("SELECT * from EggpoolMinersData") // live data for the observer (all Miners data)
    List<EggpoolMinersData> getMinersDataList();

    @Query("SELECT * FROM EggpoolMinersData") // live data for the observer (all Miners data)
    LiveData<List<EggpoolMinersData>> getMinersForRecyclerViewLiveData();

    @Query("SELECT SUM (hashrate_average) FROM EggpoolMinersData") // it's a sum of individual miners average hashrates
    int getAllMinersAverageHashrate();

    @Query("SELECT SUM (shares_average) FROM EggpoolMinersData") // it's a sum of individual miners average shares
    int getAllMinersAverageShares();

    @Query("SELECT SUM (hashrate_current) FROM EggpoolMinersData")
    int getAllMinersCurrentHashrate();

    @Query("SELECT SUM (shares_current) FROM EggpoolMinersData")
    int getAllMinersCurrentShares();

    @Query("SELECT COUNT(miner_name) FROM EggpoolMinersData WHERE is_active=1")
    int getNumOfActiveMiners();

    @Query("SELECT COUNT(miner_name) FROM EggpoolMinersData WHERE is_active=0")
    int getNumOfInactiveMiners();

    @Query("DELETE FROM EggpoolMinersData")
    void clearMinersTableInDb();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertMinersData(EggpoolMinersData eggpoolMinersData);

    /*
     *  Eggpool Payouts Data
     */

    @Query("SELECT * from EggpoolPayoutsData") // live data for the observer
    List<EggpoolPayoutsData> getPayoutsDataList();

    @Query("SELECT * FROM EggpoolPayoutsData") // live data for the observer (all Miners data)
    LiveData<List<EggpoolPayoutsData>> getPayoutsForRecyclerViewLiveData();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertPayoutsData(EggpoolPayoutsData eggpoolPayoutsData);

    @Insert // for initializing the database with default values
    void insertAllPayouts(EggpoolPayoutsData... payouts);
}
