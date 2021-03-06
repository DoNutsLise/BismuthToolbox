package com.donuts.bismuth.bismuthtoolbox.ui.networkscreen;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.BaseActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAlertDialogMessage;

public class NetworkActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " NetworkActivity", "onCreate: "+
                "called");

        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within BaseActivity.xml
        getLayoutInflater().inflate(R.layout.activity_network, contentFrameLayout);

        // WIP message
        MyAlertDialogMessage myAlertDialogMessage = new MyAlertDialogMessage(this);
        myAlertDialogMessage.warningMessage("Coming soon...", "This section of the app is under development. Please check back later!");
    }
}