package com.donuts.bismuth.bismuthtoolbox.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinersStatsModel {
    public String minerName;
    public double minerHashrate;
    public long minerLastSeen; // timestamp
    public boolean isMinerOnline; // online-offline
    public List<Integer> minerHashrate12hList = new ArrayList<>(Arrays.asList()); // hashrate for the last 12 h for plotting
    public List<Integer> minerShares12hList = new ArrayList<>(Arrays.asList()); // shares for the last 12 h for plotting
}
