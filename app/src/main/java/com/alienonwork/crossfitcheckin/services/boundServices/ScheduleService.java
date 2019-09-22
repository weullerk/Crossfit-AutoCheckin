package com.alienonwork.crossfitcheckin.services.boundServices;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.helpers.Date;
import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.Checkin;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.services.CheckinService;
import com.alienonwork.crossfitcheckin.workers.GetCheckinWorker;
import com.alienonwork.crossfitcheckin.workers.PostCheckinWorker;

import org.threeten.bp.Duration;
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

import static com.alienonwork.crossfitcheckin.services.AppService.isSettingsValid;

public class ScheduleService extends LifecycleService {

    public static final String TAG = "ScheduleService";
    private static final String TAG_SETUP_SCHEDULE = "SetupSchedule";
    private static final String TAG_SETUP_RESCHEDULE = "SetupSchedule";

    public static final String EXTRA_SCHEDULE_CHECKIN = "ScheduleCheckin";
    public static final String EXTRA_RESCHEDULE_CHECKIN = "ReScheduleCheckin";

    public final IBinder binder = new ScheduleServiceBinder();

    // TODO: 22/09/2019 Refactor actions on this observers
    private Observer<WorkInfo> workerObserver = new Observer<WorkInfo>() {
        @Override
        public void onChanged(WorkInfo workInfo) {
            if (workInfo.getTags().contains(GetCheckinWorker.TAG)) {
                if (workInfo.getState().isFinished() && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    if (workInfo.getTags().contains(TAG_SETUP_SCHEDULE)) {
                        handleSchedule();
                    }
                }
            }

            if (workInfo.getTags().contains(PostCheckinWorker.TAG)) {
                if (workInfo.getState().isFinished() && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    // Notifica o usuário que o checkin foi feito
                    // Cancela o alarme de verificação do reagendamento se o worker possuir TAG_SETUP_RESCHEDULE
                    // TODO: 22/09/2019 create function to cancel a active alarm
                    handleSchedule(); // Chama a função que agenda o próximo checkin
                }

                if (workInfo.getState().isFinished() && workInfo.getState() == WorkInfo.State.FAILED) {
                    Data data = workInfo.getOutputData();
                    Integer checkinId = data.getInt(PostCheckinWorker.PARAM_CHECKIN_ID, 0);

                    if(data.getBoolean(PostCheckinWorker.ERROR_NO_CONNECTION, false)) {
                        CheckinService checkinService = new CheckinService(getApplicationContext());
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        Schedule schedule = checkinService.getScheduleByCheckinId(checkinId);

                        PendingIntent pendingIntent = checkinService.createPendingIntentCheckin(checkinId, EXTRA_RESCHEDULE_CHECKIN);

                        Integer timeBeforeScheduleToRunCheckin = sharedPref.getInt(getApplicationContext().getString(R.string.pref_checkin_anticipate), 0);
                        Long secondsUntilCheckinRun = secondsUntilCheckin(schedule, timeBeforeScheduleToRunCheckin);

                        checkinService.createCheckinAlarm(secondsUntilCheckinRun, pendingIntent);

                        // TODO: notifica que o checkin não foi feito pois não teve conexão
                        postCheckin(checkinId, TAG_SETUP_RESCHEDULE, true);
                    }

                    if(data.getBoolean(PostCheckinWorker.ERROR_TIME_LIMIT_EXCEEDED, false)) {
                        // Notifica que o checkin não pode ser feito pois passou do horário
                        handleSchedule();
                    }

                    if(data.getBoolean(PostCheckinWorker.ERROR_POST_FAILURE, false)) {
                        Log.i(TAG, "Falha ao fazer checkin, a requisição do checkin falhou.");
                        // Agenda próximo checkin ou reagenda dependendo do erro
                    }
                }
            }
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent.getBooleanExtra(ScheduleService.EXTRA_SCHEDULE_CHECKIN, false)) {
            postCheckin(intent.getIntExtra(PostCheckinWorker.PARAM_CHECKIN_ID, 0), TAG_SETUP_SCHEDULE);
        }

        if (intent.getBooleanExtra(ScheduleService.EXTRA_SCHEDULE_CHECKIN, false)) {
            // TODO: 22/09/2019 cancelar o worker que está aguardando a conexão ser habilitada.
            // TODO: 22/09/2019 notifica o usuário informando que o checkin não foi feito pois não teve conexão antes do tempo limite foi excedido
            // TODO: 22/09/2019 agenda o próximo checkin
        }

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

    // TODO: 30/08/2019
    // has checkin scheduled?
    // is not valid? are in time, is worker or alarm up
    // cancel actions if are something invalid*
    // schedule if are valid and don't have nothing scheduled
    public void handleSchedule() {
        if (isSettingsValid(getApplicationContext())) {
            CheckinService checkinService = new CheckinService(getApplicationContext());
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            Checkin lastCheckin = checkinService.getLastCheckinMade();
            Schedule lastCheckinSchedule = checkinService.getSchedule(lastCheckin.getScheduleId());

            Integer checkinTimeLimit = sharedPref.getInt(getApplication().getString(R.string.pref_checkin_limit), Integer.parseInt(getApplication().getString(R.string.pref_checkin_limit_default)));
            OffsetDateTime startDateTime = selectStartDateTime(lastCheckinSchedule, checkinTimeLimit);

            List<Agenda> agenda = checkinService.listAgenda();

            if (agenda.size() > 0) {

                List<Schedule> nextSchedules = checkinService.listNextSchedules(startDateTime);

                if (nextSchedules.size() > 0) {
                    Schedule nextSchedule = selectNextScheduleFromAgenda(nextSchedules, agenda, startDateTime);

                    if (nextSchedule != null) {
                        Checkin checkin = checkinService.createCheckinForSchedule(nextSchedule);

                        Integer timeBeforeScheduleToRunCheckin = sharedPref.getInt(getApplicationContext().getString(R.string.pref_checkin_anticipate), 0);
                        Long secondsUntilCheckinRun = secondsUntilCheckin(nextSchedule, timeBeforeScheduleToRunCheckin);

                        if (secondsUntilCheckinRun > 0) {
                            PendingIntent pendingIntent = checkinService.createPendingIntentCheckin(checkin.getId(), EXTRA_SCHEDULE_CHECKIN);
                            checkinService.createCheckinAlarm(secondsUntilCheckinRun, pendingIntent);
                        } else {
                            postCheckin(checkin.getId(), TAG_SETUP_SCHEDULE);
                        }

                    } else {
                        getCheckinList(Date.getFirstAndLastDayOfNextWeek(), true);
                    }
                } else {
                    getCheckinList(Date.getFirstAndLastDayOfNextWeek(), true);
                }
            }
        }
    }

    private OffsetDateTime selectStartDateTime(Schedule schedule, Integer timeLimitUntilCheckin) {
        OffsetDateTime startDateTime = OffsetDateTime.now();

        if (schedule != null && schedule.getDatetimeUTC().isAfter(startDateTime)) {
            startDateTime = schedule.getDatetimeUTC();
        } else {
            startDateTime.plus(Duration.ofMinutes(timeLimitUntilCheckin));
        }

        return startDateTime;
    }

    private Schedule selectNextScheduleFromAgenda(List<Schedule> schedules, List<Agenda> agendas, OffsetDateTime startDateTime) {
        Agenda validAgenda = null;
        Schedule nextSchedule = null;
        OffsetTime startTime = startDateTime.toOffsetTime();
        Integer startDayOfWeek = startDateTime.getDayOfWeek().getValue();

        for (Agenda agenda : agendas) {
            if (validAgenda == null
                    && ((agenda.getDayOfWeek().equals(startDayOfWeek) && agenda.getTime().isAfter(startTime))
                    || agenda.getDayOfWeek() > startDayOfWeek)) {
                validAgenda = agenda;
            }
        }

        if (validAgenda == null) validAgenda = agendas.get(0);

        for (Schedule schedule : schedules) {
            if (nextSchedule == null
                    && schedule.getClassName().equals(validAgenda.getName())
                    && schedule.getHour().isEqual(validAgenda.getTime())
                    && schedule.getDayOfWeek().equals(validAgenda.getDayOfWeek())) {
                nextSchedule = schedule;
            }
        }

        return nextSchedule;
    }

    private void getCheckinList(Pair<LocalDate, LocalDate> localDatePair, @Nullable boolean setupSchedule) {
        Data.Builder builder = new Data.Builder();

        builder.putString(GetCheckinWorker.PARAM_DATE_BEGIN, localDatePair.first.toString());
        builder.putString(GetCheckinWorker.PARAM_DATE_END, localDatePair.second.toString());

        Data data = builder.build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest.Builder getCheckinBuilder = new OneTimeWorkRequest.Builder(GetCheckinWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .addTag(GetCheckinWorker.TAG);

        if (setupSchedule)
            getCheckinBuilder.addTag(ScheduleService.TAG_SETUP_SCHEDULE);

        OneTimeWorkRequest getCheckin = getCheckinBuilder.build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        workManager.enqueue(getCheckin);
        workManager.getWorkInfoByIdLiveData(getCheckin.getId()).observe(this, workerObserver);
    }

    private void postCheckin(int id, String tag) {
        postCheckin(id, tag,false);
    }

    private void postCheckin(int id, String tag, Boolean networkRequired) {
        Data.Builder builder = new Data.Builder();

        builder.putInt(PostCheckinWorker.PARAM_CHECKIN_ID, id);

        Data data = builder.build();

        OneTimeWorkRequest.Builder getCheckinBuilder = new OneTimeWorkRequest.Builder(PostCheckinWorker.class)
                .setInputData(data)
                .addTag(tag);

        if (networkRequired) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            getCheckinBuilder.setConstraints(constraints);
        }

        OneTimeWorkRequest getCheckin = getCheckinBuilder.build();

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());

        workManager.enqueue(getCheckin);
        workManager.getWorkInfoByIdLiveData(getCheckin.getId()).observe(this, workerObserver);
    }

    private Long secondsUntilCheckin(Schedule schedule, Integer timeToAnticipateCheckin) {
        OffsetDateTime dateTimeToRunCheckin = schedule.getDatetimeUTC().minusHours(timeToAnticipateCheckin);
        return  dateTimeToRunCheckin.toEpochSecond() - OffsetDateTime.now().toEpochSecond();
    }

    public class ScheduleServiceBinder extends Binder {
        public ScheduleService getService() {
            return ScheduleService.this;
        }
    }
}
