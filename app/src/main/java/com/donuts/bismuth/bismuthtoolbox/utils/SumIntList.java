package com.donuts.bismuth.bismuthtoolbox.utils;

import java.util.List;

/**
 * sum of values of List<Integer>
 */

public class SumIntList {
    public static int sumIntList(List<Integer> list) {
        int sum = 0;
        for (int i: list) {
            sum += i;
        }
        return sum;
    }
}
