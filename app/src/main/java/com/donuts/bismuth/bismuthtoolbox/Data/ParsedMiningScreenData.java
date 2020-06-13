package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * All parsed data for MiningScreen
 */

@Entity(tableName = "parsed_mining_screen_data")
public class ParsedMiningScreenData {

    public ParsedMiningScreenData() {
    }

    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "miners_active")
    private int minersActive;

    @ColumnInfo(name = "miners_inactive")
    private int minersInactive;

    @ColumnInfo(name = "hashrate_current")
    private int hashrateCurrent;

    @ColumnInfo(name = "hashrate_average")
    private int hashrateAverage;

    @ColumnInfo(name = "shares_current")
    private int sharesCurrent;

    @ColumnInfo(name = "shares_average")
    private int sharesAverage;


    /*
     * getters / setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMinersActive() {
        return minersActive;
    }

    public void setMinersActive(int minersActive) {
        this.minersActive = minersActive;
    }

    public int getMinersInactive() {
        return minersInactive;
    }

    public void setMinersInactive(int minersInactive) {
        this.minersInactive = minersInactive;
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


    // to initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
    // this method is called in DataRoomDatabase in override method in database builder
    public static ParsedMiningScreenData populateParsedMiningScreenData() {
        ParsedMiningScreenData parsedMiningScreenData = new ParsedMiningScreenData();
        parsedMiningScreenData.setId(1);
        parsedMiningScreenData.setMinersActive(0);
        parsedMiningScreenData.setMinersInactive(0);
        parsedMiningScreenData.setHashrateAverage(0);
        parsedMiningScreenData.setHashrateCurrent(0);
        parsedMiningScreenData.setSharesAverage(0);
        parsedMiningScreenData.setSharesCurrent(0);
        return parsedMiningScreenData;
    }
}
