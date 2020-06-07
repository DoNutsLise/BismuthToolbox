package com.donuts.bismuth.bismuthtoolbox.ui.miningscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;

public class MiningPayoutsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " MiningPayoutsFragment", "onCreateView: "+
                "called");
        return inflater.inflate(R.layout.fragment_mining_payouts, null);
    }
}
