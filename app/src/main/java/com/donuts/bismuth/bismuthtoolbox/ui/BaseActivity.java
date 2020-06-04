package com.donuts.bismuth.bismuthtoolbox.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.donuts.bismuth.bismuthtoolbox.Data.DataDAO;
import com.donuts.bismuth.bismuthtoolbox.Data.DataRoomDatabase;
import com.donuts.bismuth.bismuthtoolbox.R;
import com.donuts.bismuth.bismuthtoolbox.ui.homescreen.HomeActivity;
import com.donuts.bismuth.bismuthtoolbox.ui.hypernodesscreen.HypernodesActivity;
import com.donuts.bismuth.bismuthtoolbox.ui.miningscreen.MiningActivity;
import com.donuts.bismuth.bismuthtoolbox.ui.networkscreen.NetworkActivity;
import com.donuts.bismuth.bismuthtoolbox.ui.settingsscreen.SettingsActivity;
import com.donuts.bismuth.bismuthtoolbox.ui.walletscreen.WalletActivity;
import com.donuts.bismuth.bismuthtoolbox.utils.AsyncFetchData;
import com.donuts.bismuth.bismuthtoolbox.utils.CurrentTime;
import com.donuts.bismuth.bismuthtoolbox.utils.MyAlertDialogMessage;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

/**
 * This is the base activity that implements Navigation Drawer for sliding side menu and other stuff shared between other activities..
 * This activity serves as a template for all other activities.
 * All other activities extend this activity instead of AppCompatActivity - that allows to have the same
 * Navigation Drawer in different activities and each item of the Navigation Drawer is an ACTIVITY (not a
 * fragment). Each item of the Navigation Drawer should be an activity, because I want to have a personalized
 * BottomNavigationView in each of of the activities, where items of BottomNavigationView  are fragments, and
 * in android you cannot have nested fragments (fragment in fragment).
 * This activity handles Navigation Drawer.
 * Also requests asynctask to refresh data every time any activity is resumed and handles the response through interface
 */

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    public DataDAO dataDAO;
    private AsyncFetchData asyncFetchFreshData;
    public SharedPreferences sharedPreferences;
    public LinearLayout linearLayoutProgress;
    public Map<String, ?> allPreferencesKeys; // preferences stored in SharedPreferences (we are looking for wallet addresses and hypernodes IPs)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // navigation drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.drawer_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Room database
        dataDAO = DataRoomDatabase.getInstance(getApplicationContext()).getDataDAO();

        // shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // make a layout with progress bar invisible
        linearLayoutProgress = findViewById(R.id.progress_layout);
        linearLayoutProgress.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(CurrentTime.getCurrentTime("HH:mm:ss") + " BaseActivity", "onResume: "+
                "called");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_base_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_licence) {
            MyAlertDialogMessage myAlertDialogMessage = new MyAlertDialogMessage(this);
            myAlertDialogMessage.warningMessage("License", this.getString(R.string.app_license));
            return true;
        }else if (id == R.id.action_about){
            MyAlertDialogMessage myAlertDialogMessage = new MyAlertDialogMessage(this);
            myAlertDialogMessage.warningMessage("W.I.P.", "Work in progress");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation drawer view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_hypernodes) {
            Intent intent = new Intent(getApplicationContext(), HypernodesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_mining) {
            Intent intent = new Intent(getApplicationContext(), MiningActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_wallet) {
            Intent anIntent = new Intent(getApplicationContext(), WalletActivity.class);
            startActivity(anIntent);
        } else if (id == R.id.nav_network) {
            Intent anIntent = new Intent(getApplicationContext(), NetworkActivity.class);
            startActivity(anIntent);
        } else if (id == R.id.nav_settings) {
            Intent anIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(anIntent);
        }

        drawerLayout.closeDrawers();
        return true;
    }
}
