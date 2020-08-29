package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * All parsed data for HomeScreen
 */

@Entity(tableName = "parsed_home_screen_data")
public class ParsedHomeScreenData {

    public ParsedHomeScreenData() {
    }

    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "hypernodes_active")
    private int hypernodesActive;

    @ColumnInfo(name = "hypernodes_inactive")
    private int hypernodesInactive;

    @ColumnInfo(name = "hypernodes_lagging")
    private int hypernodesLagging;

    @ColumnInfo(name = "registered_wallets")
    private int registeredWallets;

    @ColumnInfo(name = "balance_usd")
    private double balanceUsd;

    @ColumnInfo(name = "balance_bis")
    private double balanceBis;

    @ColumnInfo(name = "miners_active")
    private int minersActive;

    @ColumnInfo(name = "miners_inactive")
    private int minersInactive;

    @ColumnInfo(name = "miners_hashrate")
    private int minersHashrate;

    @ColumnInfo(name = "block_height")
    private long blockHeight; // POS

    @ColumnInfo(name = "bis_to_usd")
    private double bisToUsd;

    @ColumnInfo(name = "bis_to_btc")
    private double bisToBtc;

    /*
     * getters / setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHypernodesActive() {
        return hypernodesActive;
    }

    public void setHypernodesActive(int hypernodesActive) {
        this.hypernodesActive = hypernodesActive;
    }

    public int getHypernodesInactive() {
        return hypernodesInactive;
    }

    public void setHypernodesInactive(int hypernodesInactive) {
        this.hypernodesInactive = hypernodesInactive;
    }

    public int getHypernodesLagging() {
        return hypernodesLagging;
    }

    public void setHypernodesLagging(int hypernodesLagging) {
        this.hypernodesLagging = hypernodesLagging;
    }
    public int getRegisteredWallets() {
        return registeredWallets;
    }

    public void setRegisteredWallets(int registeredWallets) {
        this.registeredWallets = registeredWallets;
    }

    public double getBalanceUsd() {
        return balanceUsd;
    }

    public void setBalanceUsd(double balanceUsd) {
        this.balanceUsd = balanceUsd;
    }

    public double getBalanceBis() {
        return balanceBis;
    }

    public void setBalanceBis(double balanceBis) {
        this.balanceBis = balanceBis;
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

    public int getMinersHashrate() {
        return minersHashrate;
    }

    public void setMinersHashrate(int minersHashrate) {
        this.minersHashrate = minersHashrate;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public double getBisToUsd() {
        return bisToUsd;
    }

    public void setBisToUsd(double bisToUsd) {
        this.bisToUsd = bisToUsd;
    }

    public double getBisToBtc() {
        return bisToBtc;
    }

    public void setBisToBtc(double bisToBtc) {
        this.bisToBtc = bisToBtc;
    }

    // to initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
    // this method is called in DataRoomDatabase in override method in database builder
    public static ParsedHomeScreenData populateParsedHomeScreenData() {
        ParsedHomeScreenData parsedHomeScreenData = new ParsedHomeScreenData();
        parsedHomeScreenData.setId(1);
        parsedHomeScreenData.setHypernodesActive(0);
        parsedHomeScreenData.setHypernodesInactive(0);
        parsedHomeScreenData.setHypernodesLagging(0);
        parsedHomeScreenData.setRegisteredWallets(0);
        parsedHomeScreenData.setBalanceBis(0);
        parsedHomeScreenData.setBalanceUsd(0);
        parsedHomeScreenData.setMinersActive(0);
        parsedHomeScreenData.setMinersInactive(0);
        parsedHomeScreenData.setMinersHashrate(0);
        parsedHomeScreenData.setBisToBtc(0);
        parsedHomeScreenData.setBisToUsd(0);
        parsedHomeScreenData.setBlockHeight(0);
        return parsedHomeScreenData;
    }
}
