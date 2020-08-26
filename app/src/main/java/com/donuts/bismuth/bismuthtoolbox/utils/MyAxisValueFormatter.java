package com.donuts.bismuth.bismuthtoolbox.utils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Class for formatting axis labels for MPandroidcharts: timestamp in milliseconds (long) -> string of "MMM-dd" format
 * Values for xLabels are taken from x value entries of lineEntries list(not from xLabels list!)
 */

public class MyAxisValueFormatter extends ValueFormatter {
    private final BarLineChartBase<?> chart;
    public MyAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }
    @Override
    public String getFormattedValue(float value) {
        return new SimpleDateFormat("MMM-dd", Locale.getDefault()).format(value);
    }
}