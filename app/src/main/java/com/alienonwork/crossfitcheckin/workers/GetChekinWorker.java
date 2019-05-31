package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;

import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.alienonwork.crossfitcheckin.network.model.Checkin;
import com.alienonwork.crossfitcheckin.repository.CfCheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entity.ClassCrossfit;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

class GetCheckinWorker extends Worker {

    public GetCheckinWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        WodEngageApi api = new WodEngageApi();
        List<ClassCrossfit> classCrossfitList = new ArrayList<>();

        try {
            Response response = api.post("", "");
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<Checkin> jsonAdapter = moshi.adapter(Checkin.class);

            Checkin checkin = jsonAdapter.fromJson(response.body().string());

            if (checkin.getList().length > 0) {

                for (Checkin.List item : checkin.getList()) {

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

                    for  (ClassCrossfit classCrossfit : classCrossfitList) {
                        for (ClassCrossfit dbClassCrossfit : dbClassCrossfitList) {
                            if (classCrossfit.getId() == dbClassCrossfit.getId())
                                apiClassLengthChanged--;
                        }
                    }

                    if (apiClassLengthChanged > 0) {
                        // TODO: 30/05/2019 update notification of modified class flag
                    }
                }

                CfCheckinDatabaseAccessor
                        .getInstance(getApplicationContext())
                        .classCrossfitDAO()
                        .insertClasses(classCrossfitList);

            } else {
                return Result.failure();
            }

        } catch (IOException e) {
            return Result.failure();
        }

        return Result.success();
    }
}