package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity for storing raw data as obtained from various web resources (usually json format)
 */

@Entity(tableName = "raw_url_data")
public class RawUrlData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "url_json_response")
    private String urlJsonResponse;

    @ColumnInfo(name = "url_last_update_time")
    // time last update happened
    private long urlLastUpdatedTime;

    @ColumnInfo(name = "url_last_update_success")
    // success / or not
    private boolean urlLastUpdatedSuccess;

    @ColumnInfo(name = "url_last_update_error")
    // error message
    private String urlLastUpdatedError;

    @ColumnInfo(name = "url_update_error_counter")
    // error counter: if a particular url systematically fails to update  - do not request full data update next time
    private int urlUpdateErrorCounter;

    /*
     * getters / setters
     */

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getUrlLastUpdatedTime() {
        return urlLastUpdatedTime;
    }

    public void setUrlLastUpdatedTime(long urlLastUpdatedTime) {
        this.urlLastUpdatedTime = urlLastUpdatedTime;
    }

    public boolean getUrlLastUpdatedSuccess() {
        return urlLastUpdatedSuccess;
    }

    public void setUrlLastUpdatedSuccess(boolean urlLastUpdatedSuccess) {
        this.urlLastUpdatedSuccess = urlLastUpdatedSuccess;
    }

    public String getUrlJsonResponse() {
        return urlJsonResponse;
    }

    public void setUrlJsonResponse(String urlJsonResponse) {
       this.urlJsonResponse=urlJsonResponse;
    }

    public String getUrlLastUpdatedError() {
        return urlLastUpdatedError;
    }

    public void setUrlLastUpdatedError(String urlLastUpdatedError) {
        this.urlLastUpdatedError = urlLastUpdatedError;
    }

    public int getUrlUpdateErrorCounter() {
        return urlUpdateErrorCounter;
    }

    public void setUrlUpdateErrorCounter(int urlUpdateErrorCounter) {
        this.urlUpdateErrorCounter = urlUpdateErrorCounter;
    }

}
