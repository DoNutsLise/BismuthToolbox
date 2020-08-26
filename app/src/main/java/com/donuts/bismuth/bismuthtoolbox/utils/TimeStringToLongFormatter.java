package com.donuts.bismuth.bismuthtoolbox.utils;

import com.github.mikephil.charting.data.Entry;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * convert timeStamps (strings e.g. "2019-06-03 08:17:26") to long (seconds)
 *  dateFormat e.g. "yyyy-MM-dd hh:mm:ss"
 */

public class TimeStringToLongFormatter {
    public static long formatTimeStringToLong(String timeString, String dateFormat){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
            Date timeDate = simpleDateFormat.parse(timeString);
            return timeDate.getTime();
        }catch (ParseException e) {
            return 0;
        }

    }
}
