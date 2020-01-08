package com.alienonwork.crossfitcheckin.activities;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import com.alienonwork.crossfitcheckin.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import static com.alienonwork.crossfitcheckin.util.Network.hasConnectionEnabled;

public class MainActivity extends AppCompatActivity {

    public final String CHECKIN_FAILED_DUE_NETWORK_EXTRA = "CHECKIN_FAILED_DUE_NETWORK_EXTRA";

    NavController mNavController;

    BottomNavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId()) {
                case R.id.settingsDest:
                    mNavController.navigate(R.id.settingsDest, null, new NavOptions.Builder().build());
                    break;
                case R.id.scheduleDest:
                    mNavController.navigate(R.id.scheduleDest);
                    break;
                case R.id.historyDest:
                    mNavController.navigate(R.id.historyDest);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidThreeTen.init(this);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);

        bottomNavigationView.setSelectedItemId(mNavController.getCurrentDestination().getId());
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationItemSelectedListener);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(CHECKIN_FAILED_DUE_NETWORK_EXTRA)) {
            if (!hasConnectionEnabled(getApplicationContext())) {
                Intent intentNewWorkSettings = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intentNewWorkSettings);
            }
        }
    }

}
