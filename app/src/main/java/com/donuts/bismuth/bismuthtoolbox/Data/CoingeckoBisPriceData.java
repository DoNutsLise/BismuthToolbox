package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity for storing data from COINGECKO_BIS_PRICE_URL = "https://api.coingecko.com/api/v3/simple/price?ids=bismuth&vs_currencies=usd,btc"
 *  Single row table with column being key-value pairs of stats, e.g. bis-btc price or bis-usd price
 */

@Entity(tableName = "coingecko_bis_price_data")
public class CoingeckoBisPriceData {

    public CoingeckoBisPriceData() {
    }

    @PrimaryKey(autoGenerate = false)
    private int id;

    @ColumnInfo(name = "coingecko_bis_price_btc")
    private double coingeckoBisPriceBtc; // bis-btc price

    @ColumnInfo(name = "coingecko_bis_price_usd")
    private double coingeckoBisPriceUsd; // bis-usd price

    /*
     * getters / setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCoingeckoBisPriceBtc() {
        return coingeckoBisPriceBtc;
    }

    public void setCoingeckoBisPriceBtc(double coingeckoBisPriceBtc) {
        this.coingeckoBisPriceBtc = coingeckoBisPriceBtc;
    }

    public double getCoingeckoBisPriceUsd() {
        return coingeckoBisPriceUsd;
    }

    public void setCoingeckoBisPriceUsd(double coingeckoBisPriceUsd) {
        this.coingeckoBisPriceUsd = coingeckoBisPriceUsd;
    }


    // to initialise the entity with initial values (in order to avoid nulls in observer of LiveData)
    // this method is called in DataRoomDatabase in override method in database builder
    public static CoingeckoBisPriceData populateCoingeckoBisPriceData() {
        CoingeckoBisPriceData coingeckoBisPriceData = new CoingeckoBisPriceData();
        coingeckoBisPriceData.setId(1);
        coingeckoBisPriceData.setCoingeckoBisPriceBtc(1);
        coingeckoBisPriceData.setCoingeckoBisPriceUsd(1);

        return coingeckoBisPriceData;
    }

}
