package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;

import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.alienonwork.crossfitcheckin.network.model.PostCheckin;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

public class PostCheckinWorker extends Worker {
    public PostCheckinWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PostCheckin postCheckin = new PostCheckin();
        WodEngageApi api = new WodEngageApi();
        try {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<PostCheckin> jsonAdapter = moshi.adapter(PostCheckin.class);
            String body = jsonAdapter.toJson(postCheckin);

            Response response = api.post(body, "");

        } catch (Exception e) {

        }

        return null;
    }
}
