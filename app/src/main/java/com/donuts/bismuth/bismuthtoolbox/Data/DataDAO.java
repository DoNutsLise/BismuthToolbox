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

    @Query("SELECT COUNT(miner_name) from eggpool_miners_data")
    long getNumOfMiners();

    @Insert // for initializing the database with default values
    void insertAllMiners(EggpoolMinersData... miners);

    @Query("SELECT * from eggpool_miners_data") // live data for the observer (all Miners data)
    List<EggpoolMinersData> getMinersDataList();

    @Query("SELECT * FROM eggpool_miners_data") // live data for the observer (all Miners data)
    LiveData<List<EggpoolMinersData>> getMinersForRecyclerViewLiveData();

    @Query("SELECT SUM (hashrate_average) FROM eggpool_miners_data") // it's a sum of individual miners average hashrates
    int getAllMinersAverageHashrate();

    @Query("SELECT SUM (shares_average) FROM eggpool_miners_data") // it's a sum of individual miners average shares
    int getAllMinersAverageShares();

    @Query("SELECT SUM (hashrate_current) FROM eggpool_miners_data")
    int getAllMinersCurrentHashrate();

    @Query("SELECT SUM (shares_current) FROM eggpool_miners_data")
    int getAllMinersCurrentShares();

    @Query("SELECT COUNT(miner_name) FROM eggpool_miners_data WHERE is_active=1")
    int getNumOfActiveMiners();

    @Query("SELECT COUNT(miner_name) FROM eggpool_miners_data WHERE is_active=0")
    int getNumOfInactiveMiners();

    @Query("DELETE FROM eggpool_miners_data")
    void clearMinersTableInDb();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertMinersData(EggpoolMinersData eggpoolMinersData);

    /*
     *  Eggpool Payouts Data
     */

    @Query("SELECT * from eggpool_payouts_data") // live data for the observer
    List<EggpoolPayoutsData> getPayoutsDataList();

    @Query("SELECT * FROM eggpool_payouts_data") // live data for the observer (all Miners data)
    LiveData<List<EggpoolPayoutsData>> getPayoutsForRecyclerViewLiveData();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertPayoutsData(EggpoolPayoutsData eggpoolPayoutsData);

    @Insert // for initializing the database with default values
    void insertAllPayouts(EggpoolPayoutsData... payouts);

    @Query("DELETE FROM eggpool_payouts_data")
    void clearEggpoolPayoutsTableInDb();

    @Query("SELECT COUNT(time_payout) from eggpool_payouts_data")
    long getNumOfPayouts();

    @Query("SELECT * FROM eggpool_payouts_data ORDER BY time_payout ASC")
    long sortEggpoolPayoutsByDate();

    /*
     *  EggpoolBalanceData
     */

    @Query("SELECT * from eggpool_balance_data") // live data for the observer
    List<EggpoolBalanceData> getEggpoolBalanceDataList();

    @Insert // for initializing the database with default values
    void insertAllEggpoolBalanceData(EggpoolBalanceData... eggpoolBalanceData);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertEggpoolBalanceData(EggpoolBalanceData eggpoolBalanceData);

    @Query("SELECT COUNT(eggpool_wallet) from eggpool_balance_data")
    long getNumOfEggpoolBalances();

    @Query("DELETE FROM eggpool_balance_data")
    void clearEggpoolBalanceTableInDb();

    @Query("SELECT * FROM eggpool_balance_data") // live data for the observer
    LiveData<List<EggpoolBalanceData>> getEggpoolBalanceLiveDataList();

    @Query("SELECT SUM (immature_eggpool_balance) FROM eggpool_balance_data") // it's a sum of individual wallets immature balances
    double getAllEggpoolWalletsImmatureBalance();

    @Query("SELECT SUM (unpaid_eggpool_balance) FROM eggpool_balance_data") // it's a sum of individual wallets unpaid balances
    double getAllEggpoolWalletsUnpaidBalance();

    @Query("SELECT SUM (total_paid_eggpool_balance) FROM eggpool_balance_data") // it's a sum of individual wallets immature balances
    double getAllEggpoolWalletsTotalPaidBalance();

    /*
     *  EggpoolBisStatsData
     */

    @Query("SELECT * from eggpool_bis_stats_data") // live data for the observer
    List<EggpoolBisStatsData> getEggpoolBisStatsDataList();

    @Insert // for initializing the database with default values
    void insertAllEggpoolBisStatsData(EggpoolBisStatsData... eggpoolBisStatsData);

    @Update
    void updateEggpoolBisStatsData(EggpoolBisStatsData eggpoolBisStatsData);

    @Query("SELECT * FROM eggpool_bis_stats_data") // live data for the observer
    LiveData<EggpoolBisStatsData> getEggpoolBisStatsLiveData();


    /*
     *  CoingeckoBisPriceData
     */


    @Query("SELECT * from coingecko_bis_price_data") // live data for the observer
    List<CoingeckoBisPriceData> getCoingeckoBisPriceDataList();

    @Query("SELECT * from coingecko_bis_price_data") // live data for the observer
    CoingeckoBisPriceData getCoingeckoBisPriceData();

    @Insert // for initializing the database with default values
    void insertAllCoingeckoBisPriceData(CoingeckoBisPriceData... coingeckoBisPriceData);

    @Update
    void updateCoingeckoBisPriceData(CoingeckoBisPriceData coingeckoBisPriceData);

    @Query("SELECT * FROM coingecko_bis_price_data") // live data for the observer
    LiveData<List<CoingeckoBisPriceData>> getCoingeckoBisPriceLiveDataList();



}
