package com.donuts.bismuth.bismuthtoolbox.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * convert long time (seconds) to strings e.g. "2019-06-03 08:17:26"
 *  dateFormat e.g. "yyyy-MM-dd hh:mm:ss"
 */

public class TimeLongToStringFormatter {
    public static String formatTimeLongToString(long timeLong, String dateFormat){
        Date date = new Date(timeLong);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}