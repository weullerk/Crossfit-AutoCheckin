package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.fragments.SettingsFragment;
import com.alienonwork.crossfitcheckin.helpers.CheckinHelper;
import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.alienonwork.crossfitcheckin.network.model.PostCheckin;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.threeten.bp.OffsetDateTime;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

public class PostCheckinWorker extends Worker {
    public static final String TAG = "post_checkin";

    public static final String ERROR_INVALID_CLASS_ID = "error_invalid_class_id";
    public static final String ERROR_INVALID_CLASS_DATE = "error_invalid_class_date";

    public static final String PARAM_CLASS_ID = "class_id";
    public static final String PARAM_CLASS_DATE = "class_date";

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
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                String token = sharedPref.getString(SettingsFragment.PREF_TOKEN, "");
                String url = mContext.getString(R.string.wodengage_api_host) + mContext.getString(R.string.wodengage_post_checkin);

                Integer userId = sharedPref.getInt(SettingsFragment.PREF_USER_ID, 0);
                Integer classId = mWorkerParameters.getInputData().getInt(PostCheckinWorker.PARAM_CLASS_ID, 0);
                String classDate = mWorkerParameters.getInputData().getString(PostCheckinWorker.PARAM_CLASS_DATE);
                String utc = OffsetDateTime.now().toString();

                PostCheckin postCheckin = new PostCheckin();
                postCheckin.setUserId(userId);
                postCheckin.setClassId(classId);
                postCheckin.setDateString(classDate);
                postCheckin.setUtc(utc);

                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<PostCheckin> jsonAdapter = moshi.adapter(PostCheckin.class);
                String body = jsonAdapter.toJson(postCheckin);

                WodEngageApi api = new WodEngageApi(mContext);
                Response response = api.post(url, body, token);

                if (response.isSuccessful())
                    return Result.success();
                else
                    return Result.failure();
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
        Integer classId = mWorkerParameters.getInputData().getInt(PostCheckinWorker.PARAM_CLASS_ID, 0);
        String classDate = mWorkerParameters.getInputData().getString(PostCheckinWorker.PARAM_CLASS_DATE);

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

        if (classId == 0) {
            errors.put(PostCheckinWorker.ERROR_INVALID_CLASS_ID, "Código da turma não informado.");
        }

        if (classDate.isEmpty()) {
            errors.put(PostCheckinWorker.ERROR_INVALID_CLASS_DATE, "Data da turma não informada.");
        }

        return new Pair(errors.size() == 0, errors);
    }
}
