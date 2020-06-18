package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Collections;
import java.util.List;

/**
 * Entity for storing details about payouts from Eggpool. One entry in db holds info for one wallet.
 */

@Entity(tableName = "eggpoolPayoutsData")
public class EggpoolPayoutsData {

    public EggpoolPayoutsData() {
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "time_payout")
    private String payoutTime; // miner name

    @ColumnInfo(name = "amount_payout")
    private double payoutAmount;

    @ColumnInfo(name = "tx_payout")
    private String payoutTx;

    /*
     * getters / setters
     */

    @NonNull
    public String getPayoutTime() {
        return payoutTime;
    }

    public void setPayoutTime(@NonNull String payoutTime) {
        this.payoutTime = payoutTime;
    }

    public double getPayoutAmount() {
        return payoutAmount;
    }

    public void setPayoutAmount(double payoutAmount) {
        this.payoutAmount = payoutAmount;
    }

    public String getPayoutTx() {
        return payoutTx;
    }

    public void setPayoutTx(String payoutTx) {
        this.payoutTx = payoutTx;
    }


    // to initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
    // this method is called in DataRoomDatabase in override method in database builder
    public static EggpoolPayoutsData populatePayouts() {
        EggpoolPayoutsData eggpoolPayoutsData = new EggpoolPayoutsData();
        eggpoolPayoutsData.setPayoutTime("N/A");
        eggpoolPayoutsData.setPayoutAmount(0);
        eggpoolPayoutsData.setPayoutTx("N/A");

        return eggpoolPayoutsData;
    }

}
