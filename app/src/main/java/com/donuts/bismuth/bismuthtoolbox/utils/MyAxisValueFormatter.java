package com.donuts.bismuth.bismuthtoolbox.utils;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Class for formatting axis labels for MPandroidcharts: timestamp long -> string
 */

public class MyAxisValueFormatter extends ValueFormatter {
    private final BarLineChartBase<?> chart;
    public MyAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }
    @Override
    public String getFormattedValue(float value) {
        return new SimpleDateFormat("MMM-dd", Locale.US).format(value);
    }
}