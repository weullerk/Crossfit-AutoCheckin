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
import com.alienonwork.crossfitcheckin.workers.GetCheckinWorker;

import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.Nullable;
import android.util.Pair;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class ScheduleService extends LifecycleService {

    public static final String TAG = "ScheduleService";
    private static final String SETUP_SCHEDULE = "SetupSchedule";

    public final IBinder binder = new ScheduleServiceBinder();

    private Observer<WorkInfo> workerObserver = new Observer<WorkInfo>() {
        @Override
        public void onChanged(WorkInfo workInfo) {
            if (workInfo.getState().isFinished()) {
                if (workInfo.getTags().contains(GetCheckinWorker.TAG)) {
                    Data data = workInfo.getOutputData();
                    if (data != null && data.getBoolean(ScheduleService.SETUP_SCHEDULE, false)) {
                        handleSchedule();
                    }
                }
            }
        }
    };

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

    public void handleSchedule() throws IllegalArgumentException {
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
                    if (nextSchedule == null
                            && schedule.getClassName() == validAgenda.getName()
                            && schedule.getHour().isEqual(validAgenda.getTime())
                            && schedule.getDayOfWeek() == validAgenda.getDayOfWeek()) {
                        nextSchedule = schedule;
                    }
                }

                if (nextSchedule != null) {
                    
                } else {
                    throw new IllegalStateException("Schedule not found for agenda");
                }

            } else {
                getCheckinList(Date.getFirstAndLastDayOfNextWeek(), true);
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

    private void getCheckinList(Pair<LocalDate, LocalDate> localDatePair, @Nullable boolean setupSchedule) {
        Data.Builder builder = new Data.Builder();

        builder.putString(GetCheckinWorker.PARAM_DATE_BEGIN, localDatePair.first.toString());
        builder.putString(GetCheckinWorker.PARAM_DATE_END, localDatePair.second.toString());

        if (setupSchedule)
            builder.putBoolean(ScheduleService.SETUP_SCHEDULE, true);

        Data data = builder.build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest getCheckin = new OneTimeWorkRequest.Builder(GetCheckinWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag(GetCheckinWorker.TAG)
                .build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        workManager.enqueue(getCheckin);
        workManager.getWorkInfoByIdLiveData(getCheckin.getId()).observe(this, workerObserver);
    }

    public class ScheduleServiceBinder extends Binder {
        public ScheduleService getService() {
            return ScheduleService.this;
        }
    }
}
