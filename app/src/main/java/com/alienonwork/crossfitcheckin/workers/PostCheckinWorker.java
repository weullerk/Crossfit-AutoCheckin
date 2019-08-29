package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.constants.CheckinStatus;
import com.alienonwork.crossfitcheckin.fragments.SettingsFragment;
import com.alienonwork.crossfitcheckin.helpers.CheckinHelper;
import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.alienonwork.crossfitcheckin.network.model.PostCheckin;
import com.alienonwork.crossfitcheckin.repository.CheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Checkin;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.services.CheckinService;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.threeten.bp.OffsetDateTime;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

public class PostCheckinWorker extends Worker {
    public static final String TAG = "post_checkin";

    public static final String ERROR_INVALID_SCHEDULE_ID = "error_invalid_schedule_id";
    public static final String ERROR_TIME_LIMIT_EXCEEDED = "error_time_limit_exceeded";
    public static final String ERROR_NO_CONNECTION = "error_no_connection";
    public static final String ERROR_POST_FAILURE = "error_post_failure";

    public static final String PARAM_CHECKIN_ID = "schedule_id";

    Context mContext;
    WorkerParameters mWorkerParameters;

    public PostCheckinWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mWorkerParameters = workerParams;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            if (isAbleToPostCheckin().first) {
                Data.Builder outputData = new Data.Builder();
                Integer checkinId = mWorkerParameters.getInputData().getInt(PostCheckinWorker.PARAM_CHECKIN_ID, 0);
                outputData.putInt(PostCheckinWorker.PARAM_CHECKIN_ID, checkinId);

                CheckinService checkinService = new CheckinService(mContext);
                Checkin checkin = checkinService.getCheckin(checkinId);

                Integer scheduleId = checkin.getScheduleId();

                if (!hasConnectionEnabled()) {
                    outputData.putBoolean(PostCheckinWorker.ERROR_NO_CONNECTION, true);
                    return Result.failure(outputData.build());
                }

                if (!hasTimeToCheckin(scheduleId)) {
                    outputData.putBoolean(PostCheckinWorker.ERROR_TIME_LIMIT_EXCEEDED, true);
                    return Result.failure(outputData.build());
                }

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                String token = sharedPref.getString(SettingsFragment.PREF_TOKEN, "");
                String url = mContext.getString(R.string.wodengage_api_host) + mContext.getString(R.string.wodengage_post_checkin);

                Integer userId = sharedPref.getInt(SettingsFragment.PREF_USER_ID, 0);

                Schedule schedule = CheckinDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .scheduleDAO()
                        .getSchedule(checkin.getScheduleId());

                Integer classId = schedule.getClassId();
                String classDate = schedule.getDatetimeUTC().toLocalDate().toString();
                OffsetDateTime utc = OffsetDateTime.now();

                PostCheckin postCheckin = new PostCheckin();
                postCheckin.setUserId(userId);
                postCheckin.setClassId(classId);
                postCheckin.setDateString(classDate);
                postCheckin.setUtc(utc.toString());

                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<PostCheckin> jsonAdapter = moshi.adapter(PostCheckin.class);
                String body = jsonAdapter.toJson(postCheckin);

                WodEngageApi api = new WodEngageApi(mContext);
                Response response = api.post(url, body, token);

                if (response.isSuccessful()) {
                    checkin.setStatus(CheckinStatus.DONE);
                    checkinService.saveCheckin(checkin);

                    return Result.success();
                } else {
                    outputData.putBoolean(PostCheckinWorker.ERROR_POST_FAILURE, true);
                    return Result.failure(outputData.build());
                }
            }
            return Result.failure();
        } catch (Exception e) {
            return Result.failure();
        }
    }

    private Pair<Boolean, HashMap<String, String>> isAbleToPostCheckin() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean autoCheckinEnabled = sharedPref.getBoolean(getApplicationContext().getString(R.string.pref_auto_checkin_enabled), false);
        Integer userId = sharedPref.getInt(SettingsFragment.PREF_USER_ID, 0);
        String token = sharedPref.getString(SettingsFragment.PREF_TOKEN, "");
        String url = mContext.getString(R.string.wodengage_api_host) + mContext.getString(R.string.wodengage_post_checkin);
        Integer scheduleId = mWorkerParameters.getInputData().getInt(PostCheckinWorker.PARAM_CHECKIN_ID, 0);

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

        if (scheduleId == 0) {
            errors.put(PostCheckinWorker.ERROR_INVALID_SCHEDULE_ID, "Código do checkin não informado.");
        }
        return new Pair(errors.size() == 0, errors);
    }

    private boolean hasConnectionEnabled() {
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        Long checkinTimeLimit = sharedPref.getLong(mContext.getApplicationContext().getString(R.string.pref_checkin_limit), Long.parseLong(getApplicationContext().getString(R.string.pref_checkin_limit_default)));

        OffsetDateTime dateTimeSchedule = schedule.getDatetimeUTC();
        dateTimeSchedule.minusMinutes(checkinTimeLimit);

        return dateTimeSchedule;
    }
}
