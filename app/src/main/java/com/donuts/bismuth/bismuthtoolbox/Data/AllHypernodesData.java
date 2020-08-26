package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Collections;
import java.util.List;

/**
 * Entity for storing data for all hypernodes:  parsed verbose data coming from "https://hypernodes.bismuth.live/hypernodes.php"
 *
 */

@Entity(tableName = "all_hypernodes_data")
public class AllHypernodesData {

    public AllHypernodesData() {
    }

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "hypernode_ip")
    private String hypernodeIP; // hypernode ip, e.g. 192.168.1.1

    @ColumnInfo(name = "hypernode_port")
    private String hypernodePort; // hypernode ip port 6262

    @ColumnInfo(name = "hypernode_id")
    private String hypernodeID; // hypernode id, e.g. BEwkhJRaC33zch53QdFnT8JnWm9HoY3Qiv

    @ColumnInfo(name = "hypernode_tier")
    private int hypernodeTier; // hypernode tier, int 1,2,3 corresponds to 10, 20, 30K hypernode

    @ColumnInfo(name = "hypernode_collateral_address")
    private String hypernodeCollateralAddress; // hypernode collateral address, e.g.  1ce6db528fca8349a3319b016a8587d8d38721f22e2fba81964ef3e2

    @ColumnInfo(name = "hypernode_reward_address")
    private String hypernodeRewardAddress; // hypernode reward address, e.g.  437d06abe09945a57ae8aa70c3eba791f8414071a2dc7228064aac33

    @ColumnInfo(name = "hypernode_block_height")
    private long hypernodeBlockHeight; // hypernode block height, long, e.g. 220394

    @ColumnInfo(name = "hypernode_version")
    private String hypernodeVersion; // node version, e.g.  0.0.99h6; for an inactive HN it can be "?"

    @ColumnInfo(name = "hypernode_status")
    private String hypernodeStatus; // status of hypernode, can be "Active", "Inactive", possible something else

    @ColumnInfo(name = "hypernode_mine")
    private Boolean hypernodeMine; // true -  if hypernode belongs to me; false - if not


    /*
     * getters / setters
     */

    @NonNull
    public String getHypernodeIP() {
        return hypernodeIP;
    }

    public void setHypernodeIP(@NonNull String hypernodeIP) {
        this.hypernodeIP = hypernodeIP;
    }

    public String getHypernodePort() {
        return hypernodePort;
    }

    public void setHypernodePort(String hypernodePort) {
        this.hypernodePort = hypernodePort;
    }

    public String getHypernodeID() {
        return hypernodeID;
    }

    public void setHypernodeID(String hypernodeID) {
        this.hypernodeID = hypernodeID;
    }

    public int getHypernodeTier() {
        return hypernodeTier;
    }

    public void setHypernodeTier(int hypernodeTier) {
        this.hypernodeTier = hypernodeTier;
    }

    public String getHypernodeCollateralAddress() {
        return hypernodeCollateralAddress;
    }

    public void setHypernodeCollateralAddress(String hypernodeCollateralAddress) {
        this.hypernodeCollateralAddress = hypernodeCollateralAddress;
    }

    public String getHypernodeRewardAddress() {
        return hypernodeRewardAddress;
    }

    public void setHypernodeRewardAddress(String hypernodeRewardAddress) {
        this.hypernodeRewardAddress = hypernodeRewardAddress;
    }

    public long getHypernodeBlockHeight() {
        return hypernodeBlockHeight;
    }

    public void setHypernodeBlockHeight(long hypernodeBlockHeight) {
        this.hypernodeBlockHeight = hypernodeBlockHeight;
    }

    public String getHypernodeVersion() {
        return hypernodeVersion;
    }

    public void setHypernodeVersion(String hypernodeVersion) {
        this.hypernodeVersion = hypernodeVersion;
    }

    public String getHypernodeStatus() {
        return hypernodeStatus;
    }

    public void setHypernodeStatus(String hypernodeStatus) {
        this.hypernodeStatus = hypernodeStatus;
    }

    public Boolean getHypernodeMine() {
        return hypernodeMine;
    }

    public void setHypernodeMine(Boolean hypernodeMine) {
        this.hypernodeMine = hypernodeMine;
    }



    /*
     * To initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
     * this method is called in DataRoomDatabase in override method in database builder
     */
    public static AllHypernodesData populateAllHypernodesData() {
        AllHypernodesData allHypernodesData = new AllHypernodesData();
        allHypernodesData.setHypernodeBlockHeight(0);
        allHypernodesData.setHypernodeCollateralAddress("N/A");
        allHypernodesData.setHypernodeID("N/A");
        allHypernodesData.setHypernodeIP("N/A");
        allHypernodesData.setHypernodePort("N/A");
        allHypernodesData.setHypernodeRewardAddress("N/A");
        allHypernodesData.setHypernodeStatus("N/A");
        allHypernodesData.setHypernodeTier(1);
        allHypernodesData.setHypernodeVersion("N/A");
        allHypernodesData.setHypernodeMine(false);

        return allHypernodesData;
    }

}
