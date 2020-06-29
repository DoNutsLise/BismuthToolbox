package com.donuts.bismuth.bismuthtoolbox.Data;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
* this class is used to convert List<Integer> data to String and back for storage in Room Database.
 * E.g. individual miners have Lists of historic hashrates and shares to store in db together with
 * primitive data (e.g. lastSeen, isActive, etc) and Room db doesn't allow to store complex data; thus it must be converted to strings.
 */

public class DataTypeConverter {
    @TypeConverter
    public static List<Integer> fromStringToList(String dataString) {
        List<Integer> dataList = new ArrayList<>();
        String[] array = dataString.split(",");
        for (String s : array) {
            if (!s.isEmpty()) {
                dataList.add(Integer.parseInt(s));
            }
        }
        return dataList;
    }

    @TypeConverter
    public static String fromListToString(List<Integer> dataList) {
        String dataString="";
        for (int i : dataList) {
            dataString += "," + i;
        }
        return dataString;
    }
}