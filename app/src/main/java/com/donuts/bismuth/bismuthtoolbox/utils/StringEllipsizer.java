package com.donuts.bismuth.bismuthtoolbox.utils;

public class StringEllipsizer {
    public static String ellipsize(String longString, int limit){
        int numOfChars = Math.floorDiv(limit, 2)-2;
        return (longString.length() > limit && longString.length() > 6) ? longString.substring(0, numOfChars ) + "..." + longString.substring(longString.length()-numOfChars): longString;
    }
}