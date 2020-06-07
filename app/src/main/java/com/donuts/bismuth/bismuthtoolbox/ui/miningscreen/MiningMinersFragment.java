package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.donuts.bismuth.bismuthtoolbox.Data.ParsedMiningScreenData;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;

import java.util.ArrayList;
import java.util.Objects;

public class MiningMinersFragment extends Fragment {

    public TextView textView_wallet;

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
        // set views including recycler view here
        textView_wallet =  getView().findViewById(R.id.text_dashboard1);
        textView_wallet.setText("1");

        /*
         * register listener for Room db update of the ParsedHomeScreenData entity
         */
        ((MiningActivity) requireActivity()).dataDAO.getParsedMiningScreenLiveData().observe(requireActivity(), this::updateMinersFragmentViews);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "onResume: "+
                "called");
         // used to call data update here
    }

    private void updateMinersFragmentViews(ParsedMiningScreenData parsedMiningScreenData) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningMinersFragment", "updateMinersFragmentViews: ");
        // used to call data update here
        textView_wallet.setText(String.valueOf(parsedMiningScreenData.getTest()));
    }
}
