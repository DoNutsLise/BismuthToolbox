package com.donuts.bismuth.bismuthtoolbox.ui.hypernodesscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAlertDialogMessage;

public class HypernodesPosNetworkFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " HypernodesPosNetworkFragment", "onCreateView: " +
                "called");
        return inflater.inflate(R.layout.fragment_hypernodes_pos_network, null);


    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // WIP message

        new MyAlertDialogMessage(getContext()).warningMessage("Coming soon...", "This section of the app is under development. Please check back later!");
    }
}
