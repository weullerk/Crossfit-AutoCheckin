package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.alienonwork.crossfitcheckin.network.model.GetCheckin;
import com.alienonwork.crossfitcheckin.repository.CfCheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.ClassCrossfit;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

class GetCheckinWorker extends Worker {
    public static final String ERROR_INVALID_DATE_BEGIN = "ERROR_DATE_BEGIN";
    public static final String ERROR_INVALID_DATE_END = "ERROR_DATE_END";

    public static final String PARAM_DATE_BEGIN = "PARAM_DATE_BEGIN";
    public static final String PARAM_DATE_END = "PARAM_DATE_END";

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
                List<ClassCrossfit> classCrossfitList = new ArrayList<>();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                String dateBegin = mWorkerParameters.getInputData().getString(GetCheckinWorker.PARAM_DATE_BEGIN);
                String dateEnd = mWorkerParameters.getInputData().getString(GetCheckinWorker.PARAM_DATE_END);

                Integer userId = sharedPref.getInt("USER_ID", 0);
                String token = sharedPref.getString("TOKEN", "");

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

                        ClassCrossfit classCrossfit = new ClassCrossfit(
                            item.getId(),
                            timestampUTC,
                            datetimeUTC,
                            item.getDayOfYear(),
                            item.getHour(),
                            item.getPlans(),
                            item.getClassName(),
                            item.isCheckinMade(),
                            item.isWeekLimit(),
                            item.getVacancy()
                        );

                        classCrossfitList.add(classCrossfit);
                    }

                    List<ClassCrossfit> dbClassCrossfitList = CfCheckinDatabaseAccessor
                            .getInstance(getApplicationContext())
                            .classCrossfitDAO()
                            .listClasses(1, 2);

                    if (dbClassCrossfitList.size() > 0) {
                        Integer apiClassLengthChanged = classCrossfitList.size();

                        for (ClassCrossfit classCrossfit : classCrossfitList) {
                            for (ClassCrossfit dbClassCrossfit : dbClassCrossfitList) {
                                if (classCrossfit.getClassId() == dbClassCrossfit.getClassId())
                                    apiClassLengthChanged--;
                            }
                        }

                        if (apiClassLengthChanged > 0) {
                            SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                            sharedPrefEditor.putBoolean(getApplicationContext().getString(R.string.pref_class_modified), true);
                            sharedPrefEditor.commit();
                        }
                    }

                    CfCheckinDatabaseAccessor
                            .getInstance(getApplicationContext())
                            .classCrossfitDAO()
                            .insertClasses(classCrossfitList);

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
        Boolean autoCheckinEnabled = sharedPref.getBoolean(getApplicationContext().getString(R.string.pref_auto_checkin_enabled), false);
        Integer userId = sharedPref.getInt("USER_ID", 0);
        String token = sharedPref.getString("TOKEN", "");
        String url = mContext.getString(R.string.wodengage_api_host) + mContext.getString(R.string.wodengage_get_checkin);
        String dateBegin = mWorkerParameters.getInputData().getString(GetCheckinWorker.PARAM_DATE_BEGIN);
        String dateEnd = mWorkerParameters.getInputData().getString(GetCheckinWorker.PARAM_DATE_END);

        HashMap<String, String> errors = new HashMap<>();

        if (!autoCheckinEnabled) {
            errors.put(PostCheckinWorker.ERROR_AUTO_CHECKIN_DISABLED, "Auto checkin desabilitado.");
        }

        if (userId == 0) {
            errors.put(PostCheckinWorker.ERROR_INVALID_USER_ID, "Usuário não informado.");
        }

        if (token.isEmpty()) {
            errors.put(PostCheckinWorker.ERROR_INVALID_TOKEN, "Token não informado.");
        }

        if (url.isEmpty()) {
            errors.put(PostCheckinWorker.ERROR_INVALID_URL, "URL inválida.");
        }

        if (dateBegin.isEmpty()) {
            errors.put(GetCheckinWorker.ERROR_INVALID_DATE_BEGIN, "Data de início inválida.");
        }

        if (dateEnd.isEmpty()) {
            errors.put(GetCheckinWorker.ERROR_INVALID_DATE_BEGIN, "Data de fim inválida.");
        }

        return new Pair(errors.size() == 0, errors);
    }
}