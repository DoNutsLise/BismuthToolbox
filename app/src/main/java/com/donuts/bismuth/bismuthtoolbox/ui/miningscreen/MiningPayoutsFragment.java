package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolBalanceData;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolBisStatsData;
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolPayoutsData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAlertDialogMessage;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;

public class MiningPayoutsFragment extends Fragment {

    private TextView textView_eggpoolImmatureBalance;
    private TextView textView_eggpoolUnpaidBalance;
    private TextView textView_eggpoolTotalPaidBalance;
    private TextView textView_profitBisDay;
    private TextView textView_profitBtcDay;
    private TextView textView_profitUsdDay;
    private TextView textView_profitBisMonth;
    private TextView textView_profitBtcMonth;
    private TextView textView_profitUsdMonth;
    private LineChart lineChart;
    private ImageButton button_help_profit;
    private MiningPayoutsRecyclerViewAdapter payoutsRecyclerViewAdapter;
    private List<EggpoolPayoutsData> eggpoolPayoutsDataModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "onCreateView: "+
                "called");
        return inflater.inflate(R.layout.fragment_mining_payouts, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "onActivityCreated: "+
                "called");

        // set views including recyclerview here
        getMiningPayoutsFragmentViews();

        /*
         * register listeners for Room db updates of the relevant for this screen tables
         */
        ((MiningActivity) requireActivity()).dataDAO.getPayoutsForRecyclerViewLiveData().observe(requireActivity(), this::updatePayoutsRecyclerView);
        ((MiningActivity) requireActivity()).dataDAO.getEggpoolBalanceLiveDataList().observe(requireActivity(), this::updatePayoutsBalanceView);
        ((MiningActivity) requireActivity()).dataDAO.getEggpoolBisStatsLiveData().observe(requireActivity(), this::updatePayoutsProfitTableView);

    }

    private void getMiningPayoutsFragmentViews() {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "getMiningPayoutsFragmentViews: "+
                "called");
        textView_eggpoolImmatureBalance = getView().findViewById(R.id.payouts_textView_immatureBalance);
        textView_eggpoolUnpaidBalance = getView().findViewById(R.id.payouts_textView_unpaidBalance);
        textView_eggpoolTotalPaidBalance = getView().findViewById(R.id.payouts_textView_totalPaid);
        textView_profitBisDay = getView().findViewById(R.id.payouts_textView_profitBisDay);
        textView_profitBtcDay = getView().findViewById(R.id.payouts_textView_profitBtcDay);
        textView_profitUsdDay =getView().findViewById(R.id.payouts_textView_profitUsdDay);
        textView_profitBisMonth = getView().findViewById(R.id.payouts_textView_profitBisMonth);
        textView_profitBtcMonth = getView().findViewById(R.id.payouts_textView_profitBtcMonth);
        textView_profitUsdMonth = getView().findViewById(R.id.payouts_textView_profitUsdMonth);
        button_help_profit = getView().findViewById(R.id.button_help_profitability_calculator);
        lineChart = getView().findViewById(R.id.lineChartPayouts);

        button_help_profit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAlertDialogMessage(getContext()).warningMessage("Mining Profitability Calculator", "Calculations are based on: 1. Current $BIS price at Coingecko; 2. The average hashrate of all your miners during the last 12h; 3. 10% pool fee is included into calculations.");
            }
        });


        /*
         * set recyclerview for miners
         */

        // create minersDataModel object to set empty minersRecyclerViewAdapter
        eggpoolPayoutsDataModel =  ((MiningActivity) requireActivity()).dataDAO.getPayoutsDataList();

        // Setting the minersRecyclerViewAdapter
        RecyclerView payoutsStatsRecyclerView = getView().findViewById(R.id.recyclerView_payouts);

        // set true if your RecyclerView is finite and has fixed size
        payoutsStatsRecyclerView.setHasFixedSize(false);

        // Set the required LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        payoutsStatsRecyclerView.setLayoutManager(llm);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(payoutsStatsRecyclerView.getContext(),
                        llm.getOrientation());
        payoutsStatsRecyclerView.addItemDecoration(dividerItemDecoration);

        // Initialize and set the RecyclerView Adapter
        payoutsRecyclerViewAdapter = new MiningPayoutsRecyclerViewAdapter(eggpoolPayoutsDataModel);
        payoutsStatsRecyclerView.setAdapter(payoutsRecyclerViewAdapter);

    }

    private void updatePayoutsRecyclerView(List<EggpoolPayoutsData> eggpoolPayoutsDataList) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsRecyclerView: "+
                "called");

        // check if the fragment is attached to the activity (common bug)
        if (!isAdded()){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsRecyclerView: "+
                    "fragment detached from the activity - views will not be updated");
            return;
        }
        /*
        * 1. update recyclerview: To update minersRecyclerViewAdapter: 1. clear previous data; 2. Set new data
         */

        eggpoolPayoutsDataModel.clear();
        eggpoolPayoutsDataModel.addAll(eggpoolPayoutsDataList);
        payoutsRecyclerViewAdapter.notifyDataSetChanged();

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsRecyclerView: "+
                "recyclerview updated");

        /*
        * 2. update chart with payouts
         */

            //
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        List<Entry> lineEntries = new ArrayList<>();
        List<String> xLabels = new ArrayList<>();
        long payoutTimeStampLong;
//        for(EggpoolPayoutsData eggpoolPayoutsData : eggpoolPayoutsDataList){
//            //convert timeStamps (strings e.g. "2019-06-03 08:17:26") to long
//            try {
//                String timeStampString = eggpoolPayoutsData.getPayoutTime();
//                Date timeStampDate = dateFormat.parse(timeStampString);
//                payoutTimeStampLong = timeStampDate.getTime();
//            } catch (ParseException e) {
//                continue;
//            }
//
//            // add X and Y values (time and payout amount) to the List of values for plotting
//            lineEntries.add(new Entry(payoutTimeStampLong, (float) eggpoolPayoutsData.getPayoutAmount()));
//            xLabels.add(eggpoolPayoutsData.getPayoutTime());
//        }

        for(EggpoolPayoutsData eggpoolPayoutsData : eggpoolPayoutsDataList){
            //convert timeStamps (strings e.g. "2019-06-03 08:17:26") to long
            try {
                String timeStampString = eggpoolPayoutsData.getPayoutTime();
                Date timeStampDate = dateFormat.parse(timeStampString);
                payoutTimeStampLong = timeStampDate.getTime();
            } catch (ParseException e) {
                continue;
            }
            // if the timestamp for the current transaction < 90 days old, we add it to the list of lineEntries (x,y values) and xLabels
            if((System.currentTimeMillis() - payoutTimeStampLong*1000)/1000/60/60/24 < 90){
                // convert timestamp from seconds (double) to days (int)
                int timestampDays = (int) TimeUnit.SECONDS.toDays((long) payoutTimeStampLong/1000); // epoch in days - now you can sort and sum by day
                double transactionAmount = eggpoolPayoutsData.getPayoutAmount();
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
                    xLabels.add(TimeLongToStringFormatter.formatTimeLongToString((long) payoutTimeStampLong*1000, "MMM-dd"));
                }else{
                    lineEntries.set(indexOfEntryToModify, new Entry(timestampDays, (float) valueOfEntryToModify));
                    xLabels.set(indexOfEntryToModify, TimeLongToStringFormatter.formatTimeLongToString((long) payoutTimeStampLong*1000, "MMM-dd"));
                }
            }
        }

        // convert timestamps from days back to seconds (that is needed for proper x-labels formatting with MyAxisValueFormatter). Note: xLabels list actually does nothing.
        for(Entry entry : lineEntries){
            entry.setX(TimeUnit.DAYS.toMillis((long) entry.getX()));
            int b=4;
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
        LineData lineData = new LineData(lineDataSet);

        // setting up the Combined chart
        //LineChart lineChart = getView().findViewById(R.id.lineChartPayouts);
        lineChart.setData(lineData);
        lineDataSet.setDrawFilled(true);
        lineChart.invalidate(); // refresh

        // decorate the chart

        // legend
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.DKGRAY);

        // description
        lineChart.getDescription().setText("Recent payouts from Eggpool");
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
    }

    private void updatePayoutsBalanceView(List<EggpoolBalanceData> eggpoolBalanceData) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsBalanceView: " +
                "called and isAdded = " + isAdded());

        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

        if (!isAdded()){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsBalanceView: "+
                    "fragment detached from the activity - views will not be updated");
            return;
        }

        textView_eggpoolImmatureBalance.setText(decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getAllEggpoolWalletsImmatureBalance()));
        textView_eggpoolUnpaidBalance.setText(decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getAllEggpoolWalletsUnpaidBalance()));
        textView_eggpoolTotalPaidBalance.setText(decimalFormat.format(((MiningActivity) requireActivity()).dataDAO.getAllEggpoolWalletsTotalPaidBalance()));

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsBalanceView: "+
                "eggpool balances views updated");
    }

    private void updatePayoutsProfitTableView(EggpoolBisStatsData eggpoolBisStatsData) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsProfitTableView: " +
                "called");

        if (!isAdded()){
            Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsProfitTableView: "+
                    "fragment detached from the activity - views will not be updated");
            return;
        }
        // the update of the table with profit calculations is only triggered by fresh data received (saved in Room database) from EGGPOOL_BIS_STATS_URL.
        // Bis price obtained from Coingecko is also required for this calculations, but the update of the view is not triggered by fresh data from Coingecko.

        // 1. get coingecko Bis price from Room database CoingeckoBisPriceData
            double bisToUsd = ((MiningActivity) requireActivity()).dataDAO.getCoingeckoBisPriceData().getCoingeckoBisPriceUsd();
            double bisToBtc = ((MiningActivity) requireActivity()).dataDAO.getCoingeckoBisPriceData().getCoingeckoBisPriceBtc();

        // 2. get all your miners average hashrate (MH/s) for the last 12h from Room database EggpoolMinersData
            int allEggpoolMinersAverageHashrate = ((MiningActivity) requireActivity()).dataDAO.getAllMinersAverageHashrate();

        // 3. calculate estimated Bis earning per day and per month (after 10% Eggpool fee); remember - hashrates returned from eggpoolBisStatsData are in H/s
            double estimatedBisPerDay = eggpoolBisStatsData.getEggpool24hPoolReward()*allEggpoolMinersAverageHashrate*1000000*0.9/eggpoolBisStatsData.getEggpool24hPoolHashrate();
            double estimatedBisPerMonth = estimatedBisPerDay*30;

        // 4. calculate estimated earning in usd and btc for day, and month
            double estimatedUsdPerDay = estimatedBisPerDay*bisToUsd;
            double estimatedBtcPerDay = estimatedBisPerDay*bisToBtc*1000000; // make it microBTC
            double estimatedUsdPerMonth = estimatedUsdPerDay*30;
            double estimatedBtcPerMonth = estimatedBtcPerDay*30;

        // 5. update table with calculated values
            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

            textView_profitBisDay.setText(decimalFormat.format(estimatedBisPerDay));
            textView_profitBisMonth.setText(decimalFormat.format(estimatedBisPerMonth));
            textView_profitBtcDay.setText(decimalFormat.format(estimatedBtcPerDay));
            textView_profitBtcMonth.setText(decimalFormat.format(estimatedBtcPerMonth));
            textView_profitUsdDay.setText(decimalFormat.format(estimatedUsdPerDay));
            textView_profitUsdMonth.setText(decimalFormat.format(estimatedUsdPerMonth));

        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsProfitTableView: " +
                "eggpool profit calculator table updated");
    }


}
