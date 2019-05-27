package com.alienonwork.crossfitcheckin.activities;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.alienonwork.crossfitcheckin.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidThreeTen.init(this);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);

        bottomNavigationView.setSelectedItemId(navController.getCurrentDestination().getId());

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch(menuItem.getItemId()) {
                        case R.id.settingsDest:
                            navController.navigate(R.id.settingsDest, null, new NavOptions.Builder().build());
                            break;
                        case R.id.scheduleDest:
                            navController.navigate(R.id.scheduleDest);
                            break;
                        case R.id.historyDest:
                            navController.navigate(R.id.historyDest);
                            break;
                    }
                    return true;
                }
            }
        );


    }

}
