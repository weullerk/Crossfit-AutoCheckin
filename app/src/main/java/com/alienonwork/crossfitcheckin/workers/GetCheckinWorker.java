package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.constants.PreferencesConstants;
import com.alienonwork.crossfitcheckin.fragments.SettingsFragment;
import com.alienonwork.crossfitcheckin.helpers.CheckinHelper;
import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.alienonwork.crossfitcheckin.network.model.GetCheckin;
import com.alienonwork.crossfitcheckin.repository.CheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

public class GetCheckinWorker extends Worker {
    public static final String TAG = "get_checkin";

    public static final String ERROR_INVALID_DATE_BEGIN = "error_invalid_date_begin";
    public static final String ERROR_INVALID_DATE_END = "error_invalid_date_end";

    public static final String PARAM_DATE_BEGIN = "date_begin";
    public static final String PARAM_DATE_END = "date_end";

    private Context mContext;
    private WorkerParameters mWorkerParameters;

    public GetCheckinWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mWorkerParameters = workerParams;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if (isAbleToGetCheckin().first) {
                WodEngageApi api = new WodEngageApi(mContext);
                List<Schedule> scheduleList = new ArrayList<>();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Data data = mWorkerParameters.getInputData();
                String dateBegin = data.getString(GetCheckinWorker.PARAM_DATE_BEGIN);
                String dateEnd = data.getString(GetCheckinWorker.PARAM_DATE_END);

                Integer userId = sharedPref.getInt(PreferencesConstants.PREF_USER_ID, 0);
                String token = sharedPref.getString(PreferencesConstants.PREF_TOKEN, "");

                String urlGetCheckin = String.format(mContext.getString(R.string.wodengage_get_checkin), dateBegin, dateEnd, userId);
                String url = mContext.getString(R.string.wodengage_api_host) + urlGetCheckin;

                Response response = api.get(url, token);
                Moshi moshi = new Moshi.Builder().build();

                JsonAdapter<GetCheckin> jsonAdapter = moshi.adapter(GetCheckin.class);

                GetCheckin checkin = jsonAdapter.fromJson(response.body().string());

                if (checkin.getList().length > 0) {

                    for (GetCheckin.List item : checkin.getList()) {

                        Instant timestampUTC = Instant.ofEpochMilli(item.getTimestampUTC());
                        OffsetDateTime datetimeUTC = OffsetDateTime.parse(item.getDatetimeUTC());
                        Integer dayOfWeek = datetimeUTC.getDayOfWeek().getValue();
                        OffsetTime time = OffsetTime.parse(item.getHour());

                        Schedule schedule = new Schedule(
                            item.getId(),
                            timestampUTC,
                            datetimeUTC,
                            item.getDayOfYear(),
                            dayOfWeek,
                            time,
                            item.getClassName(),
                            item.isBlocked()
                        );

                        scheduleList.add(schedule);
                    }

                    List<Schedule> dbScheduleList = CheckinDatabaseAccessor
                            .getInstance(getApplicationContext())
                            .scheduleDAO()
                            .listSchedules();

                    if (dbScheduleList.size() > 0) {
                        Integer apiClassLengthChanged = scheduleList.size();

                        for (Schedule schedule : scheduleList) {
                            for (Schedule dbSchedule : dbScheduleList) {
                                if (schedule.getClassName().equals(dbSchedule.getClassName())
                                        && schedule.getTimestampUTC().equals(dbSchedule.getTimestampUTC()))
                                    apiClassLengthChanged--;
                            }
                        }

                        if (apiClassLengthChanged > 0) {
                            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                            sharedPrefEditor.putBoolean(PreferencesConstants.PREF_MODIFIED_CLASS, true);
                            sharedPrefEditor.commit();
                        }
                    }

                    CheckinDatabaseAccessor
                            .getInstance(getApplicationContext())
                            .scheduleDAO()
                            .createSchedules(scheduleList);

                    return Result.success();
                }

                return Result.failure();
            } else {
                return Result.failure();
            }
        } catch (IOException e) {
            return Result.failure();
        }
    }

    private Pair<Boolean, HashMap<String, String>> isAbleToGetCheckin() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean autoCheckinEnabled = sharedPref.getBoolean(mContext.getString(R.string.pref_auto_checkin_enabled), false);
        Integer userId = sharedPref.getInt(PreferencesConstants.PREF_USER_ID, 0);
        String token = sharedPref.getString(PreferencesConstants.PREF_TOKEN, "");
        String url = mContext.getString(R.string.wodengage_api_host) + mContext.getString(R.string.wodengage_get_checkin);
        String dateBegin = mWorkerParameters.getInputData().getString(GetCheckinWorker.PARAM_DATE_BEGIN);
        String dateEnd = mWorkerParameters.getInputData().getString(GetCheckinWorker.PARAM_DATE_END);

        HashMap<String, String> errors = new HashMap<>();

        if (!autoCheckinEnabled) {
            errors.put(CheckinHelper.ERROR_AUTO_CHECKIN_DISABLED, "Auto checkin desabilitado.");
        }

        if (userId == 0) {
            errors.put(CheckinHelper.ERROR_INVALID_USER_ID, "Usuário não informado.");
        }

        if (token.isEmpty()) {
            errors.put(CheckinHelper.ERROR_INVALID_TOKEN, "Token não informado.");
        }

        if (url.isEmpty()) {
            errors.put(CheckinHelper.ERROR_INVALID_URL, "URL inválida.");
        }

        if (dateBegin.isEmpty()) {
            errors.put(GetCheckinWorker.ERROR_INVALID_DATE_BEGIN, "Data de início inválida.");
        }

        if (dateEnd.isEmpty()) {
            errors.put(GetCheckinWorker.ERROR_INVALID_DATE_END, "Data de fim inválida.");
        }

        return new Pair(errors.size() == 0, errors);
    }

    public static void create(Pair<LocalDate, LocalDate> localDatePair) {

    }
}