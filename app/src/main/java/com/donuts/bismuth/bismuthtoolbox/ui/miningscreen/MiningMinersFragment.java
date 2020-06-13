package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.content.res.Resources;
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

import com.donuts.bismuth.bismuthtoolbox.Data.ParsedMiningScreenData;
import com.donuts.bismuth.bismuthtoolbox.Models.MinersStatsModel;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;

import org.json.JSONArray;

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
    MiningMinersRecyclerViewAdapter minersRecyclerViewAdapter;
    RecyclerView minersStatsRecyclerView;
    List<MinersStatsModel> mMinersStatsModel;

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
        ((MiningActivity) requireActivity()).dataDAO.getParsedMiningScreenLiveData().observe(requireActivity(), this::updateMiningMinersFragmentViews);
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

        /*
        * set recyclerview for miners
         */

        // create mMinersStats object to set empty minersRecyclerViewAdapter
        mMinersStatsModel =new ArrayList<>();

        // Setting the minersRecyclerViewAdapter
        minersStatsRecyclerView = getView().findViewById(R.id.recyclerView_miners);

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
        minersRecyclerViewAdapter = new MiningMinersRecyclerViewAdapter(mMinersStatsModel);
        minersStatsRecyclerView.setAdapter(minersRecyclerViewAdapter);

    }

    private void updateMiningMinersFragmentViews(ParsedMiningScreenData parsedMiningScreenData) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "updateMinersFragmentViews: "+
                "called");

        if (parsedMiningScreenData != null) {
            Resources resources = getResources();
            DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

            textView_currentHashRate.setText(String.format(resources.getString(R.string.hashrate_current_tv), decimalFormat.format(parsedMiningScreenData.getHashrateCurrent())));
            textView_averageHashRate.setText(String.format(resources.getString(R.string.hashrate_average_tv), decimalFormat.format(parsedMiningScreenData.getHashrateAverage())));
            textView_currentShares.setText(String.format(resources.getString(R.string.shares_current_tv), decimalFormat.format(parsedMiningScreenData.getSharesCurrent())));
            textView_averageShares.setText(String.format(resources.getString(R.string.shares_average_tv), decimalFormat.format(parsedMiningScreenData.getSharesAverage())));
            textView_workersOnline.setText(String.format(resources.getString(R.string.miners_active_tv), decimalFormat.format(parsedMiningScreenData.getMinersActive())));
            textView_workersOffline.setText(String.format(resources.getString(R.string.miners_inactive_tv), decimalFormat.format(parsedMiningScreenData.getMinersInactive())));
        }

        // update recyclerview: To update minersRecyclerViewAdapter: 1. clear previous data; 2. Set new data
        List<MinersStatsModel> minersStatsModel = new ArrayList<>();
        MinersStatsModel minersStatsTMP = new MinersStatsModel();
        minersStatsTMP.minerName = "test";
        minersStatsTMP.minerHashrate = 9;
        minersStatsTMP.minerLastSeen = 10000000;
        minersStatsModel.add(minersStatsTMP);

        MinersStatsModel minersStatsTMP1 = new MinersStatsModel();
        minersStatsTMP1.minerName = "test1";
        minersStatsTMP1.minerHashrate = 99;
        minersStatsTMP1.minerLastSeen = 100000090;
        minersStatsModel.add(minersStatsTMP1);

        MinersStatsModel minersStatsTMP2 = new MinersStatsModel();
        minersStatsTMP2.minerName = "te2st";
        minersStatsTMP2.minerHashrate = 92;
        minersStatsTMP2.minerLastSeen = 100000020;
        minersStatsModel.add(minersStatsTMP2);

        MinersStatsModel minersStatsTMP4 = new MinersStatsModel();
        minersStatsTMP4.minerName = "te4st";
        minersStatsTMP4.minerHashrate = 49;
        minersStatsTMP4.minerLastSeen = 100004000;
        minersStatsModel.add(minersStatsTMP4);

        MinersStatsModel minersStatsTMP5 = new MinersStatsModel();
        minersStatsTMP5.minerName = "te4s5t";
        minersStatsTMP5.minerHashrate = 495;
        minersStatsTMP5.minerLastSeen = 1000054000;
        minersStatsModel.add(minersStatsTMP5);


        mMinersStatsModel.clear();
        mMinersStatsModel.addAll(minersStatsModel);
        minersRecyclerViewAdapter.notifyDataSetChanged();
    }
}
