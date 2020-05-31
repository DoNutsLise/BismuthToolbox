package com.donuts.bismuth.bismuthtoolbox.ui.walletscreen;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAlertDialogMessage;

public class WalletActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_wallet, contentFrameLayout);

        // WIP message
        MyAlertDialogMessage myAlertDialogMessage = new MyAlertDialogMessage(this);
        myAlertDialogMessage.warningMessage("W.I.P.", "Work in progress");

        Toast.makeText(this, "Wallet Activity is W.I.P", Toast.LENGTH_LONG).show();
    }
}