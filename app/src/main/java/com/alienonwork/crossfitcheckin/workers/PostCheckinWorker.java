package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.alienonwork.crossfitcheckin.network.model.PostCheckin;
import com.alienonwork.crossfitcheckin.repository.CfCheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.ClassCrossfit;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

public class PostCheckinWorker extends Worker {
    static final String TAG = "POST_CHECKIN";

    public PostCheckinWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Integer userId;
            String token;
            List<Agenda> agenda;
            ClassCrossfit lastCheckin;

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            userId = sharedPref.getInt("USER_ID", 0);
            token = sharedPref.getString("TOKEN", "");

            agenda = CfCheckinDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .agendaDAO()
                    .listAgenda();

            lastCheckin = CfCheckinDatabaseAccessor
                    .getInstance(getApplicationContext())
                    .classCrossfitDAO()
                    .getLastCheckin();

            PostCheckin postCheckin = new PostCheckin();
            WodEngageApi api = new WodEngageApi();

            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<PostCheckin> jsonAdapter = moshi.adapter(PostCheckin.class);
            String body = jsonAdapter.toJson(postCheckin);

            Response response = api.post("", body, "");

            if (response.isSuccessful())
                return Result.success();
            else
                return Result.failure();

        } catch (Exception e) {
            return Result.failure();
        }
    }

    public static void Build() {

    }
}
