package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolMinersData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MiningMinersFragment extends Fragment {

    private TextView textView_currentHashRate;
    private TextView textView_averageHashRate;
    private TextView textView_currentShares;
    private TextView textView_averageShares;
    private TextView textView_workersOnline;
    private TextView textView_workersOffline;
    private MiningMinersRecyclerViewAdapter minersRecyclerViewAdapter;
    private CombinedChart combinedChart;
    private List<EggpoolMinersData> eggpoolMinersDataModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "onCreateView: "+
                "called");
        return inflater.inflate(R.layout.fragment_mining_miners, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "onActivityCreated: "+
                "called");

        // set views including recyclerview here
        getMiningMinersFragmentViews();

        /*
         * register listener for Room db update of the ParsedMiningScreenData entity
         */
        ((MiningActivity) requireActivity()).dataDAO.getMinersForRecyclerViewLiveData().observe(requireActivity(), this::updateMinersFragmentViews);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "onResume: "+
                "called");
         // used to call data update here
    }

    private void getMiningMinersFragmentViews(){
        // get all screen view when activity is created
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "getMiningMinersFragmentViews: "+
                "called");
        textView_currentHashRate = getView().findViewById(R.id.dashboard_textView_currentHashRate);
        textView_averageHashRate = getView().findViewById(R.id.dashboard_textView_averageHashRate);
        textView_currentShares = getView().findViewById(R.id.dashboard_textView_currentShares);
        textView_averageShares = getView().findViewById(R.id.dashboard_textView_averageShares);
        textView_workersOnline = getView().findViewById(R.id.dashboard_textView_workersOnline);
        textView_workersOffline =getView().findViewById(R.id.dashboard_textView_workersOffline);
        combinedChart = getView().findViewById(R.id.combinedChartMinersStats);

        /*
        * set recyclerview for miners
         */

        // create minersDataModel object to set empty minersRecyclerViewAdapter
        eggpoolMinersDataModel =  ((MiningActivity) requireActivity()).dataDAO.getMinersDataList();

        // Setting the minersRecyclerViewAdapter
        RecyclerView minersStatsRecyclerView = getView().findViewById(R.id.recyclerView_miners);

        // set true if your RecyclerView is finite and has fixed size
        minersStatsRecyclerView.setHasFixedSize(false);

        // Set the required LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        minersStatsRecyclerView.setLayoutManager(llm);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(minersStatsRecyclerView.getContext(),
                        llm.getOrientation());
        minersStatsRecyclerView.addItemDecoration(dividerItemDecoration);

        // Initialize and set the RecyclerView Adapter
        minersRecyclerViewAdapter = new MiningMinersRecyclerViewAdapter(eggpoolMinersDataModel);
        minersStatsRecyclerView.setAdapter(minersRecyclerViewAdapter);

    }

    private void updateMinersFragmentViews(List<EggpoolMinersData> eggpoolMinersData) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "updateMiningMinersFragmentViews: "+
                "called");
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "updateMinersFragmentViews: "+
                "updating recyclerview, all textviews and chart...");

        if (!isAdded()){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "updateMinersFragmentViews: "+
                    "fragment detached from the activity - views will not be updated");
            return;
        }

        if (eggpoolMinersData == null) {
            return;
        }

        // update textviews with stats at the top of screen:
        Resources resources = getResources();
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

        textView_currentHashRate.setText(String.format(resources.getString(R.string.hashrate_current_tv),
                decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getAllMinersCurrentHashrate())));
        textView_averageHashRate.setText(String.format(resources.getString(R.string.hashrate_average_tv),
                decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getAllMinersAverageHashrate())));
        textView_currentShares.setText(String.format(resources.getString(R.string.shares_current_tv),
                decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getAllMinersCurrentShares())));
        textView_averageShares.setText(String.format(resources.getString(R.string.shares_average_tv),
                decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getAllMinersAverageShares())));
        textView_workersOnline.setText(String.format(resources.getString(R.string.miners_active_tv),
                decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getNumOfActiveMiners())));
        textView_workersOffline.setText(String.format(resources.getString(R.string.miners_inactive_tv),
                decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getNumOfInactiveMiners())));

        // update recyclerview: To update minersRecyclerViewAdapter: 1. clear previous data; 2. Set new data
        eggpoolMinersDataModel.clear();
        eggpoolMinersDataModel.addAll(eggpoolMinersData);
        minersRecyclerViewAdapter.notifyDataSetChanged();

        /*
        * update the chart
         */
        // 1. build an array, which is a sum of hashrates lists of individual miners, i.e. from shares12hList of each miner to shares12hList sum of all
        int[] hashrate12hSumArray = new int[13];
        for (EggpoolMinersData nextEggpoolMinersData : eggpoolMinersData) {
            for (int i = 0; i < nextEggpoolMinersData.getHashrate12hList().size(); i++){
                hashrate12hSumArray[i] += nextEggpoolMinersData.getHashrate12hList().get(i);
            }
        }
        // 2. build an array, which is a sum of shares lists of individual miners, i.e. from shares12hList of each miner to shares12hList sum of all
        int[] shares12hSumArray = new int[13];
        for (EggpoolMinersData nextEggpoolMinersData : eggpoolMinersData) {
            for (int i = 0; i < nextEggpoolMinersData.getShares12hList().size(); i++){
                shares12hSumArray[i] += nextEggpoolMinersData.getShares12hList().get(i);
            }
        }

        // 3. build <Entry> lists for plots, which are list of (x,y) values for plotting
        List<Entry> lineEntries = new ArrayList<>();
        for(int i=0; i<hashrate12hSumArray.length; i++){
            lineEntries.add(new Entry(i, hashrate12hSumArray [i]));
        }

        List<BarEntry> barEntries = new ArrayList<>();
        for(int i=0; i<shares12hSumArray.length; i++){
            barEntries.add(new BarEntry(i, shares12hSumArray[i]));
        }

        List<String> xLabels = new ArrayList<>();
        for(int i=0; i<shares12hSumArray.length; i++){
            //xLabels.add(timeStamp[i]);
            xLabels.add(String.valueOf(i));
        }

        // setting up the Line chart

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Hashrate, MH/s"); // add entries to dataset
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.rgb(156, 39, 176));
        lineDataSet.setCircleColor(Color.rgb(156, 39, 176));
        lineDataSet.setCircleHoleColor(Color.rgb(156, 39, 176));
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawValues(false);
        LineData lineData = new LineData(lineDataSet);

        // setting up the Bar chart
        BarDataSet barDataSet = new BarDataSet(barEntries, "Shares"); // add entries to dataset
        barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        barDataSet.setColor(Color.rgb(38, 166, 154));
        barDataSet.setDrawValues(false);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        // setting up the Combined chart
        //CombinedChart combinedChart = getView().findViewById(R.id.combinedChartMinersStats);
        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(barData);
        combinedChart.setData(combinedData);
        combinedChart.invalidate(); // refresh

        // decorating combined chart

        // legend
        Legend legend = combinedChart.getLegend();
        legend.setTextColor(Color.DKGRAY);

        // description
        combinedChart.getDescription().setText("Stats for the last 12h");
        combinedChart.getDescription().setTextColor(Color.DKGRAY);
        //combinedChart.getDescription().setPosition(3f,3f);

        //  X axis
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        //xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);

        // left Y axis
        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.rgb(156, 39, 176));
        //leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(30f);

        // right Y axis
        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setAxisMinimum(0);
        rightAxis.setTextSize(10f);
        rightAxis.setTextColor(Color.rgb(38, 166, 154));
        //rightAxis.setAxisLineColor(Color.WHITE);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawGridLines(false);
    }
}
