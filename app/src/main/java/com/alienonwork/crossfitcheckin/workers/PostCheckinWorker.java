package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.alienonwork.crossfitcheckin.network.model.PostCheckin;
import com.alienonwork.crossfitcheckin.repository.CfCheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.ClassCrossfit;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

public class PostCheckinWorker extends Worker {
    static final String TAG = "POST_CHECKIN";
    static final String AUTO_CHECKIN_DISABLED = "AUTO_CHECKIN_DISABLED";
    static final String INVALID_USER_ID = "INVALID_USER_ID";
    static final String INVALID_TOKEN = "INVALID_TOKEN";
    static final String INVALID_AGENDA = "INVALID_TOKEN";

    public PostCheckinWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Boolean autoCheckinEnabled;
            Integer userId;
            String token;
            List<ClassCrossfit> classes;
            ClassCrossfit lastCheckin;
            String url;

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            autoCheckinEnabled = sharedPref.getBoolean(getApplicationContext().getString(R.string.pref_auto_checkin_enabled), false);
            userId = sharedPref.getInt("USER_ID", 0);
            token = sharedPref.getString("TOKEN", "");

            if (!autoCheckinEnabled) {
                Data data = new Data.Builder()
                        .putString(AUTO_CHECKIN_DISABLED, AUTO_CHECKIN_DISABLED)
                        .build();
                return Result.failure(data);
            }

            if (userId == 0) {
                Data data = new Data.Builder()
                        .putString(INVALID_USER_ID, INVALID_USER_ID)
                        .build();
                return Result.failure(data);
            }

            if (token.isEmpty()) {
                Data data = new Data.Builder()
                        .putString(INVALID_TOKEN, INVALID_TOKEN)
                        .build();
                return Result.failure(data);
            }

            lastCheckin = CfCheckinDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .classCrossfitDAO()
                    .getLastCheckin();

            if (lastCheckin == null) {

            }

            PostCheckin postCheckin = new PostCheckin();
            WodEngageApi api = new WodEngageApi();

            url = getApplicationContext().getString(R.string.wodengage_api_host) + getApplicationContext().getString(R.string.wodengage_post_checkin);

            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<PostCheckin> jsonAdapter = moshi.adapter(PostCheckin.class);
            String body = jsonAdapter.toJson(postCheckin);

            Response response = api.post(url, body, token);

            if (response.isSuccessful())
                return Result.success();
            else
                return Result.failure();

        } catch (Exception e) {
            return Result.failure();
        }
    }

    public class Build {
        public void Build() {
        List<Agenda> agenda;

        agenda = CfCheckinDatabaseAccessor
                .getInstance(getApplicationContext())
                .agendaDAO()
                .listAgenda();
        }
    }
}
