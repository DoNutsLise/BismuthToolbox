package com.donuts.bismuth.bismuthtoolbox.ui.hypernodesscreen;

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

import com.donuts.bismuth.bismuthtoolbox.Data.AllHypernodesData;
import com.donuts.bismuth.bismuthtoolbox.Data.HypernodesRewardAddressesData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAxisValueFormatter;
import com.donuts.bismuth.bismuthtoolbox.utils.TimeLongToStringFormatter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;


//TODO: expandable recycler view: https://medium.com/@nikola.jakshic/how-to-expand-collapse-items-in-recyclerview-49a648a403a6

public class HypernodesMyHypernodesFragment extends Fragment {

    private TextView textView_myHypernodesActive;
    private TextView textView_myHypernodesInactive;
    private TextView textView_myHypernodesLastWeekPay;
    private TextView textView_myHypernodesTotalCollateral;
    private TextView textView_weekProfit10kHypernode;
    private TextView textView_weekProfit20kHypernode;
    private TextView textView_weekProfit30kHypernode;
    private TextView textView_monthProfit10kHypernode;
    private TextView textView_monthProfit20kHypernode;
    private TextView textView_monthProfit30kHypernode;
    private TextView textView_yearProfit10kHypernode;
    private TextView textView_yearProfit20kHypernode;
    private TextView textView_yearProfit30kHypernode;
    private MyHypernodesRecyclerViewAdapter myHypernodesRecyclerViewAdapter;
    private List<AllHypernodesData> allHypernodesDataModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "onCreateView: " +
                "called");
        return inflater.inflate(R.layout.fragment_hypernodes_my_hypernodes, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "onActivityCreated: " +
                "called");

        // set views including recyclerview here
        getMyHypernodesFragmentViews();

        /*
         * register listeners for Room db updates of the relevant for this screen tables
         */
        ((HypernodesActivity) requireActivity()).dataDAO.getMyHypernodesDataForRecyclerViewLiveData().observe(requireActivity(), this::updateMyHypernodesRecyclerView);
        ((HypernodesActivity) requireActivity()).dataDAO.getMyHypernodesRewardsForRecyclerViewLiveData().observe(requireActivity(), this::updateMyHypernodesPayoutsChart);

    }

    private void getMyHypernodesFragmentViews() {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "getMyHypernodesFragmentViews: "+
                "called");
        textView_myHypernodesActive = getView().findViewById(R.id.textView_my_hypernodes_active);
        textView_myHypernodesInactive = getView().findViewById(R.id. textView_my_hypernodes_inactive);
        textView_myHypernodesLastWeekPay = getView().findViewById(R.id.textView_my_hypernodes_last_week_pay);
        textView_myHypernodesTotalCollateral = getView().findViewById(R.id.textView_my_hypernodes_total_collateral);
        textView_weekProfit10kHypernode = getView().findViewById(R.id.textView_week_profit_10K_hypernode);
        textView_weekProfit20kHypernode = getView().findViewById(R.id.textView_week_profit_20K_hypernode);
        textView_weekProfit30kHypernode = getView().findViewById(R.id.textView_week_profit_30K_hypernode);
        textView_monthProfit10kHypernode = getView().findViewById(R.id.textView_month_profit_10K_hypernode);
        textView_monthProfit20kHypernode = getView().findViewById(R.id.textView_month_profit_20K_hypernode);
        textView_monthProfit30kHypernode = getView().findViewById(R.id.textView_month_profit_30K_hypernode);
        textView_yearProfit10kHypernode = getView().findViewById(R.id.textView_year_profit_10K_hypernode);
        textView_yearProfit20kHypernode = getView().findViewById(R.id.textView_year_profit_20K_hypernode);
        textView_yearProfit30kHypernode = getView().findViewById(R.id.textView_year_profit_30K_hypernode);

        /*
         * set recyclerview for hypernodes
         */

        // create allHypernodesDataModel object to set empty MyHypernodesRecyclerViewAdapter
        allHypernodesDataModel =  ((HypernodesActivity) requireActivity()).dataDAO.getMyHypernodesDataList();

        // Setting the MyHypernodesRecyclerViewAdapter
        RecyclerView myHypernodesRecyclerView = getView().findViewById(R.id.recyclerView_my_hypernodes);

        // set true if your RecyclerView is finite and has fixed size
        myHypernodesRecyclerView.setHasFixedSize(false);

        // Set the required LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myHypernodesRecyclerView.setLayoutManager(llm);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(myHypernodesRecyclerView.getContext(),
                        llm.getOrientation());
        myHypernodesRecyclerView.addItemDecoration(dividerItemDecoration);

        // Initialize and set the RecyclerView Adapter
        myHypernodesRecyclerViewAdapter = new MyHypernodesRecyclerViewAdapter(allHypernodesDataModel);
        myHypernodesRecyclerView.setAdapter(myHypernodesRecyclerViewAdapter);

    }

    private void updateMyHypernodesRecyclerView(List<AllHypernodesData> allHypernodesDataList) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesRecyclerView: "+
                "called");
        /*
         * 1. update recyclerview: To update myHypernodesRecyclerViewAdapter: 1. clear previous data; 2. Set new data
         */

        allHypernodesDataModel.clear();
        allHypernodesDataModel.addAll(allHypernodesDataList);
        myHypernodesRecyclerViewAdapter.notifyDataSetChanged();

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesRecyclerView: "+
                " recyclerview updated");
        /*
         * 2. update hypernodes profit calculator:
         * Formula for POS reward:
         *          fork.POW_FORK = 1450000
         *          elif node.is_mainnet and node.last_block >= fork.POW_FORK:
         *             self.reward_sum = 24 - 10*(node.last_block + 5 - fork.POW_FORK)/3000000
         *         else:
         *             self.reward_sum = 24
         *
         *         if self.reward_sum < 0.5:
         *             self.reward_sum = 0.5
         */

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesRecyclerView: "+
                "updating calculator textviews");
        // get highest POW block
        long powBlockHeight = ((HypernodesActivity) requireActivity()).dataDAO.getPowBlockHeightFromEggpoolBisStatsData();

        // calculate HN reward per block (every 10 min):
        double hypernodesRewardPerBlock = 24 - (float) 10*(powBlockHeight + 5 - 1450000)/3000000;

        if (hypernodesRewardPerBlock  < 0.5){
            hypernodesRewardPerBlock  = 0.5;
        }

        // get number of active hypernodes total tier:
        int sumTierOfActiveHypernodes = ((HypernodesActivity) requireActivity()).dataDAO.getSumTierOfActiveHypernodesFromAllHypernodesData();

        // calculate HN reward per HN per week: assuming 6*24*7 blocks per week (1 block per 10 minutes) and get only active hypernodes.
        double rewardPer10KHypernodePerWeek = hypernodesRewardPerBlock/sumTierOfActiveHypernodes*6*24*7;

        // update calculator textviews
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

        textView_weekProfit10kHypernode.setText(decimalFormat.format(rewardPer10KHypernodePerWeek));
        textView_weekProfit20kHypernode.setText(decimalFormat.format(2*rewardPer10KHypernodePerWeek));
        textView_weekProfit30kHypernode.setText(decimalFormat.format(3*rewardPer10KHypernodePerWeek));
        textView_monthProfit10kHypernode.setText(decimalFormat.format(30/7*rewardPer10KHypernodePerWeek));
        textView_monthProfit20kHypernode.setText(decimalFormat.format(2*30/7*rewardPer10KHypernodePerWeek));
        textView_monthProfit30kHypernode.setText(decimalFormat.format(3*30/7*rewardPer10KHypernodePerWeek));
        textView_yearProfit10kHypernode.setText(decimalFormat.format(365/7*rewardPer10KHypernodePerWeek));
        textView_yearProfit20kHypernode.setText(decimalFormat.format(2*365/7*rewardPer10KHypernodePerWeek));
        textView_yearProfit30kHypernode.setText(decimalFormat.format(3*365/7*rewardPer10KHypernodePerWeek));

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesRecyclerView: "+
                "calculator textviews updated");
    }

    private void updateMyHypernodesPayoutsChart(List<HypernodesRewardAddressesData> hypernodesRewardAddressesDataList) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesPayoutsChart: "+
                "called");

        /*
         * 1. update textviews at the top
         */
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesPayoutsChart: "+
                "updating textviews at the top");

        Resources resources = getResources();
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

        textView_myHypernodesActive.setText(String.format(resources.getString(R.string.my_active_hns_tv),
                decimalFormat.format(((HypernodesActivity) requireActivity()).dataDAO.getNumOfMyActiveHypernodesFromAllHypernodesData())));
        textView_myHypernodesInactive.setText(String.format(resources.getString(R.string.my_inactive_hns_tv),
                decimalFormat.format(((HypernodesActivity) requireActivity()).dataDAO.getNumOfMyInactiveHypernodesFromAllHypernodesData())));
        textView_myHypernodesLastWeekPay.setText(String.format(resources.getString(R.string.last_week_hn_pay_tv),
                decimalFormat.format(((HypernodesActivity) requireActivity()).dataDAO.getMyHypernodesLastWeekRewardsFromHypernodesRewards(System.currentTimeMillis()))));
        textView_myHypernodesTotalCollateral.setText(String.format(resources.getString(R.string.my_hns_total_collateral_tv),
                decimalFormat.format(((HypernodesActivity) requireActivity()).dataDAO.getSumTierOfMyHypernodesFromAllHypernodesData()*10000)));

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesPayoutsChart: "+
                "textviews at the top updated");

        /*
         * 2. update hypernodes payouts chart
         */

        // x(time) and y(BIS) values for chart are extracted from HypernodesRewardAddressesData as doubles, where timestamp is in seconds.
        // First, we only want data for the last 60 days. Second, we want to sum up payouts to different addresses/hypernodes by date.
        // Summing up payouts by date is a bit tricky. The way I found is to convert timestamp (double in seconds) to days(integer), so that
        // I can combine them by date as done below.

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesPayoutsChart: "+
                "updating hypernodes payout chart");

        List<Entry> lineEntries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();

        for(HypernodesRewardAddressesData hypernodesRewardAddressesData : hypernodesRewardAddressesDataList){
            // if the timestamp for the current transaction < 90 days old, we add it to the list of lineEntries (x,y values) and xLabels
            if((System.currentTimeMillis() - hypernodesRewardAddressesData.getTransactionTimestamp()*1000)/1000/60/60/24 < 90){
                // convert timestamp from seconds (double) to days (int)
                int timestampDays = (int) TimeUnit.SECONDS.toDays((long) hypernodesRewardAddressesData.getTransactionTimestamp()); // epoch in days - now you can sort and sum by day
                double transactionAmount = hypernodesRewardAddressesData.getTransactionAmount();
                // now loop through lineEntries list and either add a new Entry or modify an existing one if that date already present
                Integer indexOfEntryToModify = null;
                float valueOfEntryToModify = (float) transactionAmount;
                for(Entry entry : lineEntries){
                    if (entry.getX() == timestampDays){
                        // entry with this date already exists and it's index is:
                        indexOfEntryToModify = lineEntries.indexOf(entry);
                        valueOfEntryToModify = valueOfEntryToModify + entry.getY();
                    }
                }

                // if a record with the same date already exists in lineEntries - then modify it with new value of
                // transaction (which is a sum of previous and current) with set method; otherwise add a new entry with add method
                if (isNull(indexOfEntryToModify) || lineEntries.size() == 0){
                    lineEntries.add(new Entry(timestampDays, (float) valueOfEntryToModify));
                    xLabels.add(TimeLongToStringFormatter.formatTimeLongToString((long) hypernodesRewardAddressesData.getTransactionTimestamp()*1000, "MMM-dd"));
                }else{
                    lineEntries.set(indexOfEntryToModify, new Entry(timestampDays, (float) valueOfEntryToModify));
                    xLabels.set(indexOfEntryToModify, TimeLongToStringFormatter.formatTimeLongToString((long) hypernodesRewardAddressesData.getTransactionTimestamp()*1000, "MMM-dd"));
                }
            }
        }

        // convert timestamps from days back to seconds (that is needed for proper x-labels formatting with MyAxisValueFormatter). Note: xLabels list actually does nothing.
        for(Entry entry : lineEntries){
            entry.setX(TimeUnit.DAYS.toMillis((long) entry.getX()));
        }

        // x values MUST be sorted - see doc
        Collections.sort(lineEntries, new EntryXComparator());

        // setting up the Line chart
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Payouts, BIS"); // add entries to dataset
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.rgb(156, 39, 176));
//        scatterDataSet.setCircleColor(Color.rgb(156, 39, 176));
//        scatterDataSet.setCircleHoleColor(Color.rgb(156, 39, 176));
//        scatterDataSet.setCircleRadius(3f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawFilled(true);
        LineData lineData = new LineData(lineDataSet);

        // setting up the Combined chart
        LineChart lineChart = getView().findViewById(R.id.lineChartMyHypernodesPayouts);
        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh

        // decorate the chart

        // legend
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.DKGRAY);

        // description
        lineChart.getDescription().setText("Last 60 days hypernodes payouts");
        lineChart.getDescription().setTextColor(Color.DKGRAY);
        //combinedChart.getDescription().setPosition(3f,3f);

        //  X axis
        ValueFormatter valueFormatter = new MyAxisValueFormatter(lineChart);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.DKGRAY);
        //xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelRotationAngle(270);
        xAxis.setValueFormatter(valueFormatter);

        // left Y axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextSize(10f);
        leftAxis.setTextColor(Color.rgb(156, 39, 176));
        //leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(true);

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesMyHypernodesFragment", "updateMyHypernodesPayoutsChart: "+
                " hypernodes payouts chart updated");

    }

}