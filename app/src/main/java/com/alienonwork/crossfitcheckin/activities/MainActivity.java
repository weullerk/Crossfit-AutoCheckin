package com.alienonwork.crossfitcheckin.activities;

import androidx.annotation.NonNull;

import com.alienonwork.crossfitcheckin.fragments.SettingsFragment;
import com.alienonwork.crossfitcheckin.helpers.Date;
import com.alienonwork.crossfitcheckin.repository.CheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.alienonwork.crossfitcheckin.R;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.OffsetDateTime;

import java.util.List;
import java.util.stream.Collectors;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {

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

    private boolean setupScheduling() {
        if (canSchedule()) {
            //has checkin scheduled?
            // cancel actions

            List<Schedule> nextClasses = CheckinDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .scheduleDAO().nextSchedules(OffsetDateTime.now());

            if (nextClasses.size() == 0) {
                // no classes listed
                // call worker to get next classes (GetCheckinWorker)
            }

            List<Agenda> agenda = CheckinDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .agendaDAO()
                    .listAgenda();

            if (agenda.size() == 0) {
                // no agenda selected
                // -> has class listed?
                // call this when change agenda
                return false;
            }

            Integer todayDayOfWeek = Date.getTodayDayOfWeek();

            List<Agenda> nextAgendas = agenda.stream()
                    .filter(a -> a.getDayOfWeek() >= todayDayOfWeek)
                    .collect(Collectors.toList());

            return true;
        }
        return false;
    }

    private boolean canSchedule() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean autoCheckinEnabled = sharedPref.getBoolean(getApplicationContext().getString(R.string.pref_auto_checkin_enabled), false);
        Integer userId = sharedPref.getInt(SettingsFragment.PREF_USER_ID, 0);
        String token = sharedPref.getString(SettingsFragment.PREF_TOKEN, "");

        if (!autoCheckinEnabled)
            return false;

        if (userId == 0)
            return false;

        if (token.isEmpty())
            return false;

        return true;
    }

}
