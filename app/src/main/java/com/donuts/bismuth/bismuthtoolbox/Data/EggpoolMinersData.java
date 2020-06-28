package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Collections;
import java.util.List;

/**
 * Entity for storing miners data from Eggpool:  a list of miners with their individual stats
 */

@Entity(tableName = "eggpool_miners_data")
public class EggpoolMinersData {

    public EggpoolMinersData() {
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "miner_name")
    private String minerName; // miner name

    @ColumnInfo(name = "hashrate_current")
    private int hashrateCurrent; // current for a given miner

    @ColumnInfo(name = "hashrate_average")
    private int hashrateAverage; // 13h average for a given miner, MH/s

    @ColumnInfo(name = "shares_current")
    private int sharesCurrent; // current for a given miner

    @ColumnInfo(name = "shares_average")
    private int sharesAverage; //13h average for a given miner

    @ColumnInfo(name = "last_seen") // timestamp
    private long lastSeen;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @TypeConverters(DataTypeConverter.class)
    @ColumnInfo(name = "12h_hashrate_list")
    private List<Integer> hashrate12hList;

    @TypeConverters(DataTypeConverter.class)
    @ColumnInfo(name = "12h_shares_list")
    private List<Integer> shares12hList;

    /*
     * getters / setters
     */

    @NonNull
    public String getMinerName() {
        return minerName;
    }

    public void setMinerName(@NonNull String minerName) {
        this.minerName = minerName;
    }

    public int getHashrateCurrent() {
        return hashrateCurrent;
    }

    public void setHashrateCurrent(int hashrateCurrent) {
        this.hashrateCurrent = hashrateCurrent;
    }

    public int getHashrateAverage() {
        return hashrateAverage;
    }

    public void setHashrateAverage(int hashrateAverage) {
        this.hashrateAverage = hashrateAverage;
    }

    public int getSharesCurrent() {
        return sharesCurrent;
    }

    public void setSharesCurrent(int sharesCurrent) {
        this.sharesCurrent = sharesCurrent;
    }

    public int getSharesAverage() {
        return sharesAverage;
    }

    public void setSharesAverage(int sharesAverage) {
        this.sharesAverage = sharesAverage;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<Integer> getHashrate12hList() {
        return hashrate12hList;
    }

    public void setHashrate12hList(List<Integer> hashrate12hList) {
        this.hashrate12hList = hashrate12hList;
    }

    public List<Integer> getShares12hList() {
        return shares12hList;
    }

    public void setShares12hList(List<Integer> shares12hList) {
        this.shares12hList = shares12hList;
    }



    // to initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
    // this method is called in DataRoomDatabase in override method in database builder
    public static EggpoolMinersData populateMiners() {
        EggpoolMinersData eggpoolMinersData = new EggpoolMinersData();
        eggpoolMinersData.setMinerName("N/A");
        eggpoolMinersData.setHashrateCurrent(0);
        eggpoolMinersData.setHashrateAverage(0);
        eggpoolMinersData.setSharesCurrent(0);
        eggpoolMinersData.setSharesAverage(0);
        eggpoolMinersData.setLastSeen(0);
        eggpoolMinersData.setIsActive(false);
        eggpoolMinersData.setHashrate12hList(Collections.nCopies(13, 0));
        eggpoolMinersData.setShares12hList(Collections.nCopies(13, 0));

        return eggpoolMinersData;
    }

}
