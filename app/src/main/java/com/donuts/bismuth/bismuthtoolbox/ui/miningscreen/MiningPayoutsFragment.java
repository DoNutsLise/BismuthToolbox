package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

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
import com.donuts.bismuth.bismuthtoolbox.Data.EggpoolPayoutsData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;

import java.util.List;

public class MiningPayoutsFragment extends Fragment {

    private TextView textView_immatureBalance;
    private TextView textView_unpaidBalance;
    private TextView textView_totalPaid;
    private TextView textView_profitBisDay;
    private TextView textView_profitBtcDay;
    private TextView textView_profitUsdDay;
    private TextView textView_profitBisMonth;
    private TextView textView_profitBtcMonth;
    private TextView textView_profitUsdMonth;
    private MiningPayoutsRecyclerViewAdapter payoutsRecyclerViewAdapter;
    private List<EggpoolPayoutsData> payoutsDataModel;

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
         * register listener for Room db update of the ParsedMiningScreenData entity
         */
        ((MiningActivity) requireActivity()).dataDAO.getPayoutsForRecyclerViewLiveData().observe(requireActivity(), this::updatePayoutsFragmentViews);
    }

    private void getMiningPayoutsFragmentViews() {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "getMiningPayoutsFragmentViews: "+
                "called");
        textView_immatureBalance = getView().findViewById(R.id.payouts_textView_immatureBalance);
        textView_unpaidBalance = getView().findViewById(R.id.payouts_textView_unpaidBalance);
        textView_totalPaid = getView().findViewById(R.id.payouts_textView_totalPaid);
        textView_profitBisDay = getView().findViewById(R.id.payouts_textView_profitBisDay);
        textView_profitBtcDay = getView().findViewById(R.id.payouts_textView_profitBtcDay);
        textView_profitUsdDay =getView().findViewById(R.id.payouts_textView_profitUsdDay);
        textView_profitBisMonth = getView().findViewById(R.id.payouts_textView_profitBisMonth);
        textView_profitBtcMonth = getView().findViewById(R.id.payouts_textView_profitBtcMonth);
        textView_profitUsdMonth =getView().findViewById(R.id.payouts_textView_profitUsdMonth);

        /*
         * set recyclerview for miners
         */

        // create minersDataModel object to set empty minersRecyclerViewAdapter
        payoutsDataModel =  ((MiningActivity) requireActivity()).dataDAO.getPayoutsDataList();

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
        payoutsRecyclerViewAdapter = new MiningPayoutsRecyclerViewAdapter(payoutsDataModel);
        payoutsStatsRecyclerView.setAdapter(payoutsRecyclerViewAdapter);

    }

    private void updatePayoutsFragmentViews(List<EggpoolPayoutsData> eggpoolPayoutsData) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "updatePayoutsFragmentViews: "+
                "called");

    }
}
