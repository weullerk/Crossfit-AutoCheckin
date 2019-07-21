package com.alienonwork.crossfitcheckin.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.fragments.SettingsFragment;
import com.alienonwork.crossfitcheckin.helpers.Date;
import com.alienonwork.crossfitcheckin.repository.CheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;

import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.lifecycle.LifecycleService;

public class ScheduleService extends LifecycleService {

    public static final String TAG = "ScheduleService";

    public final IBinder binder = new ScheduleServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        Log.i(TAG, "ScheduleService binded");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ScheduleService destroyed");
    }

    public void handleSchedule() {
        if (canSchedule()) {
            //has checkin scheduled? is not valid?
            // cancel actions

            Integer todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
            OffsetTime todayTime = OffsetTime.now();

            List<Agenda> agendas = CheckinDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .agendaDAO()
                    .listAgenda();

            if (agendas.size() == 0) {
                throw new IllegalArgumentException("No schedule selected");
            }

            List<Schedule> nextSchedules = CheckinDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .scheduleDAO()
                    .nextSchedules(OffsetDateTime.now());

            if (nextSchedules.size() > 0) {
                //get next agenda
                Agenda validAgenda = null;
                Schedule nextSchedule = null;

                for (Agenda agenda : agendas) {
                    if (validAgenda == null
                        && ((agenda.getDayOfWeek() == todayDayOfWeek && agenda.getTime().isAfter(todayTime))
                            || agenda.getDayOfWeek() > todayDayOfWeek)) {
                        validAgenda = agenda;
                    }
                }

                if (validAgenda == null) validAgenda = agendas.get(0);

                for (Schedule schedule : nextSchedules) {
                    if (nextSchedule == null && schedule.getClassName() == validAgenda.getName() && schedule.getHour().isEqual(validAgenda.getTime())) {
                        nextSchedule = schedule;
                    }
                }

                if (nextSchedule != null) {
                    
                }

            } else {
                // no classes listed
                // call worker to get next classes (GetCheckinWorker)
            }
        }
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



    public class ScheduleServiceBinder extends Binder {

        public ScheduleService getService() {
            return ScheduleService.this;
        }
    }
}
