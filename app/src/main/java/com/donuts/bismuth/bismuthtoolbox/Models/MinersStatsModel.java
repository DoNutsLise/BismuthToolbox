package com.donuts.bismuth.bismuthtoolbox.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MinersStatsModel {
    public String minerName;
    public String minerHashrate;
    public String minerLastSeen; // timestamp
    public String minerStatus; // online-offline
    public List<Double> minerHashrate12hList = new ArrayList<>(Arrays.asList()); // hashrate for the last 12 h for plotting
    public List<Double> minerShares12hList = new ArrayList<>(Arrays.asList()); // shares for the last 12 h for plotting
}
