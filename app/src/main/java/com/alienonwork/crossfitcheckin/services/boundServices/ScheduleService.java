package com.alienonwork.crossfitcheckin.services.boundServices;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.constants.PreferencesConstants;
import com.alienonwork.crossfitcheckin.fragments.SettingsFragment;
import com.alienonwork.crossfitcheckin.helpers.Date;
import com.alienonwork.crossfitcheckin.repository.CheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.Checkin;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.services.CheckinService;
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

import static com.alienonwork.crossfitcheckin.services.AppServices.isSettingsValid;

public class ScheduleService extends LifecycleService {

    public static final String TAG = "ScheduleService";
    private static final String TAG_SETUP_SCHEDULE = "SetupSchedule";
    private static final String TAG_SETUP_RESCHEDULE = "SetupSchedule";

    public static final String EXTRA_SCHEDULE_CHECKIN = "ScheduleCheckin";
    public static final String EXTRA_RESCHEDULE_CHECKIN = "RescheduleCheckin";

    public final IBinder binder = new ScheduleServiceBinder();

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
                   // Chama a função que agenda o próximo checkin
                   // Cancela o alarme de verificação do reagendamento se o worker possuir TAG_SETUP_RESCHEDULE
                }
                if (workInfo.getState().isFinished() && workInfo.getState() == WorkInfo.State.FAILED) {
                    Data data = workInfo.getOutputData();
                    Integer checkinId = data.getInt(PostCheckinWorker.PARAM_CHECKIN_ID, 0);

                    if(data.getBoolean(PostCheckinWorker.ERROR_NO_CONNECTION, false)) {
                        // cria um alarme de verificação para o tempo limite do checkin,
                        // notifica que o checkin não foi feito pois não teve conexão
                        // chama o worker novamente
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
        Log.i(TAG, "Alarm initiated " + intent.toString());

        if (intent.getBooleanExtra(ScheduleService.EXTRA_SCHEDULE_CHECKIN, false)) {
            postCheckin(intent.getIntExtra(PostCheckinWorker.PARAM_CHECKIN_ID, 0), TAG_SETUP_SCHEDULE);
        }

        // TODO: 29/08/2019 Registra comando para cancelar o worker que está aguardando a conexão ser habilitada.
        // TODO: e cria uma notificação informando que o checkin não foi feito pois não teve conexão e o tempo limite foi excedido
        // TODO: Agenda o próximo checkin

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

    public void handleSchedule() {
        if (isSettingsValid(getApplicationContext())) {
            CheckinService checkinService = new CheckinService(getApplicationContext());
            // has checkin scheduled? is not valid?
            // cancel actions

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Integer checkinTimeLimit = sharedPref.getInt(getApplication().getString(R.string.pref_checkin_limit), Integer.parseInt(getApplication().getString(R.string.pref_checkin_limit_default)));

            Schedule lastCheckin = checkinService.findLastScheduleWithCheckinMade();

            OffsetDateTime startDateTime = OffsetDateTime.now();

            OffsetTime todayTime;

            if (lastCheckin != null && startDateTime.isBefore(lastCheckin.getDatetimeUTC())) {
                startDateTime = lastCheckin.getDatetimeUTC();
                todayTime = startDateTime.toOffsetTime();
            } else {
                todayTime = startDateTime.toOffsetTime();
                todayTime.plusMinutes(checkinTimeLimit + 1);
            }

            Integer todayDayOfWeek = startDateTime.getDayOfWeek().getValue();

            List<Agenda> agendas = CheckinDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .agendaDAO()
                    .listAgenda();

            if (agendas.size() > 0) {

                List<Schedule> nextSchedules = CheckinDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .scheduleDAO()
                        .listNextSchedules(startDateTime);

                if (nextSchedules.size() > 0) {
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
                        Checkin checkin = checkinService.createCheckinForSchedule(nextSchedule);

                        Integer anticipated = sharedPref.getInt(getApplicationContext().getString(R.string.pref_checkin_realization), 0);
                        OffsetDateTime dateTimeNextSchedule = nextSchedule.getDatetimeUTC();

                        OffsetDateTime dateTimeNextScheduleAnticipate = dateTimeNextSchedule.minusHours(anticipated.longValue());

                        Long diffSeconds = dateTimeNextScheduleAnticipate.toEpochSecond() - OffsetDateTime.now().toEpochSecond();
                        if (diffSeconds > 0) {
                            PendingIntent pendingIntent = checkinService.createPendingIntentAlarmCheckin(checkin.getId(), EXTRA_SCHEDULE_CHECKIN);
                            checkinService.createCheckinAlarm(diffSeconds, pendingIntent);
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

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest.Builder getCheckinBuilder = new OneTimeWorkRequest.Builder(PostCheckinWorker.class)
                .setInputData(data)
                .addTag(tag);

        if (networkRequired)
            getCheckinBuilder.setConstraints(constraints);

        OneTimeWorkRequest getCheckin = getCheckinBuilder.build();

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
