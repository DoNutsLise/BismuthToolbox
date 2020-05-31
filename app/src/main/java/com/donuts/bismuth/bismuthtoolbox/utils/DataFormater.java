package com.donuts.bismuth.bismuthtoolbox.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataFormater {
    public static String getCurrentTime(String timeFormat){
        Format format = new SimpleDateFormat(timeFormat);
        return format.format(new Date());
    }
}