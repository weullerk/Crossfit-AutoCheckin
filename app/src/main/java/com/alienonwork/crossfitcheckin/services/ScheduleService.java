package com.alienonwork.crossfitcheckin.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.fragments.SettingsFragment;
import com.alienonwork.crossfitcheckin.helpers.Date;
import com.alienonwork.crossfitcheckin.network.model.PostCheckin;
import com.alienonwork.crossfitcheckin.repository.CheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.workers.GetCheckinWorker;
import com.alienonwork.crossfitcheckin.workers.PostCheckinWorker;

import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;

import java.util.List;

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
    private static final String TAG_SETUP_SCHEDULE = "SetupSchedule";

    private static final String EXTRA_SCHEDULE_CHECKIN = "ScheduleCheckin";
    private static final String EXTRA_RESCHEDULE_CHECKIN = "RescheduleCheckin";

    public final IBinder binder = new ScheduleServiceBinder();

    private Observer<WorkInfo> workerObserver = new Observer<WorkInfo>() {
        @Override
        public void onChanged(WorkInfo workInfo) {
            if (workInfo.getState().isFinished()) {
                if (workInfo.getTags().contains(GetCheckinWorker.TAG)) {
                    Data data = workInfo.getOutputData();
                    if (data != null && data.getBoolean(ScheduleService.TAG_SETUP_SCHEDULE, false)) {
                        handleSchedule();
                    }
                }
            }
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Debuggin the alarm command. " + intent.toString());
        return START_NOT_STICKY;
    }


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

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Integer checkinTimeLimit = sharedPref.getInt(getApplication().getString(R.string.pref_checkin_limit), Integer.parseInt(getApplication().getString(R.string.pref_checkin_limit_default)));

            Integer todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
            OffsetTime todayTime = OffsetTime.now();
            todayTime.plusMinutes(checkinTimeLimit);

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
                    Integer anticipated = sharedPref.getInt(getApplicationContext().getString(R.string.pref_checkin_realization), 0);

                    OffsetDateTime dateTimeNextSchedule = nextSchedule.getDatetimeUTC();
                    OffsetDateTime dateTimeNextScheduleAnticipate = dateTimeNextSchedule.minusHours(anticipated.longValue());

                    Long diffSeconds = dateTimeNextScheduleAnticipate.toEpochSecond() - OffsetDateTime.now().toEpochSecond();
                    if (diffSeconds > 0) {
                        Intent intent = new Intent(ScheduleService.this, ScheduleService.class);

                        setupAlarm(diffSeconds, intent);
                    } else {
                        postCheckin(nextSchedule.getId());
                    }

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
            builder.putBoolean(ScheduleService.TAG_SETUP_SCHEDULE, true);

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

    private void setupAlarm(Long seconds, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getService(ScheduleService.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + seconds * 1000, pendingIntent);
    }

    private void postCheckin(int id) {
        postCheckin(id, false);
    }

    private void postCheckin(int id, Boolean networkRequired) {
        Data.Builder builder = new Data.Builder();

        builder.putInt(PostCheckinWorker.PARAM_SCHEDULE_ID, id);

        Data data = builder.build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest.Builder getCheckinBuilder = new OneTimeWorkRequest.Builder(PostCheckinWorker.class)
                .setInputData(data)
                .addTag(PostCheckinWorker.TAG);

        if (networkRequired)
            getCheckinBuilder.setConstraints(constraints);

        OneTimeWorkRequest getCheckin = getCheckinBuilder.build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        workManager.enqueue(getCheckin);
        workManager.getWorkInfoByIdLiveData(getCheckin.getId()).observe(this, workerObserver);
    }

    private boolean hasConnectionEnabled() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return connectivityManager.getActiveNetwork() != null;
        } else {
            return connectivityManager.getActiveNetworkInfo() != null;
        }
    }

    private boolean hasTimeToCheckin(int id) {
        Schedule schedule = CheckinDatabaseAccessor
                .getInstance(getApplicationContext())
                .scheduleDAO()
                .getSchedule(id);

        OffsetDateTime dateTimeSchedule = calculateTimeLimitForCheckin(schedule);

        return dateTimeSchedule.isAfter(OffsetDateTime.now());
    }

    private OffsetDateTime calculateTimeLimitForCheckin(Schedule schedule) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Long checkinTimeLimit = sharedPref.getLong(getApplication().getString(R.string.pref_checkin_limit), Long.parseLong(getApplication().getString(R.string.pref_checkin_limit_default)));

        OffsetDateTime dateTimeSchedule = schedule.getDatetimeUTC();
        dateTimeSchedule.minusMinutes(checkinTimeLimit);

        return dateTimeSchedule;
    }

    public class ScheduleServiceBinder extends Binder {
        public ScheduleService getService() {
            return ScheduleService.this;
        }
    }
}
