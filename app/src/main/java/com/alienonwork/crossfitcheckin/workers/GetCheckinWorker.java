package com.alienonwork.crossfitcheckin.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.Response;

class GetCheckinWorker extends Worker {
    // TODO: 04/06/2019 get date and token params
    public GetCheckinWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        WodEngageApi api = new WodEngageApi();
        List<ClassCrossfit> classCrossfitList = new ArrayList<>();

        try {
            // TODO: 04/06/2019 parse the url from the date param
            Response response = api.get("", "");
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

                    for  (ClassCrossfit classCrossfit : classCrossfitList) {
                        for (ClassCrossfit dbClassCrossfit : dbClassCrossfitList) {
                            if (classCrossfit.getClassId() == dbClassCrossfit.getClassId())
                                apiClassLengthChanged--;
                        }
                    }

                    if (apiClassLengthChanged > 0) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
                        sharedPrefEditor.putBoolean(getApplicationContext().getString(R.string.pref_class_modified), true);
                        sharedPrefEditor.commit();
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