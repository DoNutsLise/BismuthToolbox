package com.donuts.bismuth.bismuthtoolbox.utils;

/**
 * class to ellipsize strings to a certain number of characters
 */
public class StringEllipsizer {
    public static String ellipsizeMiddle(String longString, int limit){
        // ellipsize string in the middle: e.g. "abcdefjhijklm" -> "abc...klm"
        int numOfChars = Math.floorDiv(limit, 2)-2;
        return (longString.length() > limit && longString.length() > 6) ? longString.substring(0, numOfChars ) + "..." + longString.substring(longString.length()-numOfChars): longString;
    }

}