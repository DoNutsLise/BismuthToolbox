package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity for storing balances data for payouts fragment
 *  immature, not paid and total paid balances, etc.
 * Rows are wallets; columns are various key-value pairs.
 */

@Entity(tableName = "eggpool_balance_data")
public class EggpoolBalanceData {

    public EggpoolBalanceData() {
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "eggpool_wallet")
    private String eggpoolWallet; // wallet address

    @ColumnInfo(name = "immature_eggpool_balance")
    private double immatureEggpoolBalance; // your immature balance at eggpool (for each wallet)

    @ColumnInfo(name = "unpaid_eggpool_balance")
    private double unpaidEggpoolBalance; // your unpaid balance at eggpool (for each wallet)

    @ColumnInfo(name = "total_paid_eggpool_balance")
    private double totalPaidEggpoolBalance; // your total paid balance at eggpool (for each wallet)


    /*
     * getters / setters
     */

    @NonNull
    public String getEggpoolWallet() {
        return eggpoolWallet;
    }

    public void setEggpoolWallet(@NonNull String eggpoolWallet) {
        this.eggpoolWallet = eggpoolWallet;
    }

    public double getImmatureEggpoolBalance() {
        return immatureEggpoolBalance;
    }

    public void setImmatureEggpoolBalance(double immatureEggpoolBalance) {
        this.immatureEggpoolBalance = immatureEggpoolBalance;
    }

    public double getUnpaidEggpoolBalance() {
        return unpaidEggpoolBalance;
    }

    public void setUnpaidEggpoolBalance(double unpaidEggpoolBalance) {
        this.unpaidEggpoolBalance = unpaidEggpoolBalance;
    }

    public double getTotalPaidEggpoolBalance() {
        return totalPaidEggpoolBalance;
    }

    public void setTotalPaidEggpoolBalance(double totalPaidEggpoolBalance) {
        this.totalPaidEggpoolBalance = totalPaidEggpoolBalance;
    }


    // to initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
    // this method is called in DataRoomDatabase in override method in database builder
    public static EggpoolBalanceData populateEggpoolBalanceData() {
        EggpoolBalanceData eggpoolBalanceData = new EggpoolBalanceData();
        eggpoolBalanceData.setEggpoolWallet("N/A");
        eggpoolBalanceData.setImmatureEggpoolBalance(0);
        eggpoolBalanceData.setUnpaidEggpoolBalance(0);
        eggpoolBalanceData.setTotalPaidEggpoolBalance(0);


        return eggpoolBalanceData;
    }

}
