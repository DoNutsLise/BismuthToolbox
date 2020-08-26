package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity for storing data for my hypernodes reward addresses:  parsed data (100 recent transactions)
 * coming from "BIS_API_URL + "node" + "/" + "addlistlimjson:" + hnRewardAddress +":100"", e.g.
 * https://bismuth.online/api/node/addlistlimjson:7def3db17a766d48c7966fde06f60b7f2b910f2fddf2c3910c95c35e:100
 * Table will contain n x 100 records (or <100), where n = number of hypernode reward addresses.
 */

@Entity(tableName = "hypernodes_reward_addresses_data")
public class HypernodesRewardAddressesData {

    public HypernodesRewardAddressesData() {
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "transaction_signature")
    private String hypernodeRewardTransactionSignature; // signature of the transaction

    @ColumnInfo(name = "transaction_block_height")
    private long transactionBlockHeight; // block height of the transaction

    @ColumnInfo(name = "transaction_timestamp")
    private double transactionTimestamp; // time stamp of the transaction (in seconds)

    @ColumnInfo(name = "sender_address")
    private String senderAddress; // address from which funds were sent

    @ColumnInfo(name = "recipient_address")
    private String recipientAddress; // recipient address

    @ColumnInfo(name = "transaction_amount")
    private double transactionAmount; // amount BIS received


    /*
     * getters / setters
     */

    @NonNull
    public String getHypernodeRewardTransactionSignature() {
        return hypernodeRewardTransactionSignature;
    }

    public void setHypernodeRewardTransactionSignature(@NonNull String hypernodeRewardTransactionSignature) {
        this.hypernodeRewardTransactionSignature = hypernodeRewardTransactionSignature;
    }

    public long getTransactionBlockHeight() {
        return transactionBlockHeight;
    }

    public void setTransactionBlockHeight(long transactionBlockHeight) {
        this.transactionBlockHeight = transactionBlockHeight;
    }

    public double getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(double transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /*
     * To initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
     * this method is called in DataRoomDatabase in override method in database builder
     */
    public static HypernodesRewardAddressesData populateHypernodesRewardAddressesData() {
        HypernodesRewardAddressesData hypernodesRewardAddressesData = new HypernodesRewardAddressesData();
        hypernodesRewardAddressesData.setHypernodeRewardTransactionSignature("N/A");
        hypernodesRewardAddressesData.setRecipientAddress("N/A");
        hypernodesRewardAddressesData.setSenderAddress("N/A");
        hypernodesRewardAddressesData.setTransactionAmount(0);
        hypernodesRewardAddressesData.setTransactionBlockHeight(1);
        hypernodesRewardAddressesData.setTransactionTimestamp(1551549064.09);

        return hypernodesRewardAddressesData;
    }

}
