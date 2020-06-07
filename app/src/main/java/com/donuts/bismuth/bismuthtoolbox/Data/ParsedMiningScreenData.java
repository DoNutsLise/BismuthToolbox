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

    @ColumnInfo(name = "test")
    private int test;



    /*
     * getters / setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }


    // to initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
    // this method is called in DataRoomDatabase in override method in database builder
    public static ParsedMiningScreenData populateParsedMiningScreenData() {
        ParsedMiningScreenData parsedMiningScreenData = new ParsedMiningScreenData();
        parsedMiningScreenData.setId(1);
        parsedMiningScreenData.setTest(5);
        return parsedMiningScreenData;
    }
}
