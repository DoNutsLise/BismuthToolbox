package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity for storing data from EGGPOOL_BIS_STATS_URL = "https://eggpool.net/api/currencies"
 *  Single row table with column being key-value pairs of stats, e.g. total network difficulty or hashrate,etc
 */

@Entity(tableName = "eggpool_bis_stats_data")
public class EggpoolBisStatsData {

    public EggpoolBisStatsData() {
    }

    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "eggpool_bis_network_block_height")
    private long eggpoolBisNetworkBlockHeight; // POW network block height

    @ColumnInfo(name = "eggpool_bis_network_difficulty")
    private double eggpoolBisNetworkDifficulty; // network difficulty

    @ColumnInfo(name = "eggpool_bis_network_hashrate")
    private long eggpoolBisNetworkHashrate; // network hashrate in Hash/s units

    @ColumnInfo(name = "eggpool_bis_pool_hashrate")
    private long eggpoolBisPoolHashrate; // eggpool only current hashrate in Hash/s

    @ColumnInfo(name = "eggpool_24h_pool_hashrate")
    private long eggpool24hPoolHashrate; // eggpool only hashrate in Hash/s; average last 24h

    @ColumnInfo(name = "eggpool_24h_pool_reward")
    private double eggpool24hPoolReward; // eggpool only reward for the last 24h


    /*
     * getters / setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEggpoolBisNetworkBlockHeight() {
        return eggpoolBisNetworkBlockHeight;
    }

    public void setEggpoolBisNetworkBlockHeight(long eggpoolBisNetworkBlockHeight) {
        this.eggpoolBisNetworkBlockHeight = eggpoolBisNetworkBlockHeight;
    }

    public double getEggpoolBisNetworkDifficulty() {
        return eggpoolBisNetworkDifficulty;
    }

    public void setEggpoolBisNetworkDifficulty(double eggpoolBisNetworkDifficulty) {
        this.eggpoolBisNetworkDifficulty = eggpoolBisNetworkDifficulty;
    }

    public long getEggpoolBisNetworkHashrate() {
        return eggpoolBisNetworkHashrate;
    }

    public void setEggpoolBisNetworkHashrate(long eggpoolBisNetworkHashrate) {
        this.eggpoolBisNetworkHashrate = eggpoolBisNetworkHashrate;
    }

    public long getEggpoolBisPoolHashrate() {
        return eggpoolBisPoolHashrate;
    }

    public void setEggpoolBisPoolHashrate(long eggpoolBisPoolHashrate) {
        this.eggpoolBisPoolHashrate = eggpoolBisPoolHashrate;
    }

    public long getEggpool24hPoolHashrate() {
        return eggpool24hPoolHashrate;
    }

    public void setEggpool24hPoolHashrate(long eggpool24hPoolHashrate) {
        this.eggpool24hPoolHashrate = eggpool24hPoolHashrate;
    }

    public double getEggpool24hPoolReward() {
        return eggpool24hPoolReward;
    }

    public void setEggpool24hPoolReward(double eggpool24hPoolReward) {
        this.eggpool24hPoolReward = eggpool24hPoolReward;
    }


    // to initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
    // this method is called in DataRoomDatabase in override method in database builder
    public static EggpoolBisStatsData populateEggpoolBisStatsData() {
        EggpoolBisStatsData eggpoolBisStatsData = new EggpoolBisStatsData();
        eggpoolBisStatsData.setEggpool24hPoolHashrate(1);
        eggpoolBisStatsData.setEggpool24hPoolReward(0);
        eggpoolBisStatsData.setEggpoolBisNetworkBlockHeight(1);
        eggpoolBisStatsData.setEggpoolBisNetworkDifficulty(1);
        eggpoolBisStatsData.setEggpoolBisNetworkHashrate(1);
        eggpoolBisStatsData.setId(1);

        return eggpoolBisStatsData;
    }

}
