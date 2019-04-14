package com.alienonwork.crossfitcheckin.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alienonwork.crossfitcheckin.models.Checkin;
import com.alienonwork.crossfitcheckin.network.WodEngageApi;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;


public class CheckinViewModel extends AndroidViewModel {
    private static final String TAG = "CheckinRead";

    private MutableLiveData<Checkin> checkinLiveData;

    public CheckinViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Checkin> getCheckins() {
        if (checkinLiveData == null) {
            checkinLiveData = new MutableLiveData<Checkin>();
            loadCheckins();
        }
        return checkinLiveData;
    }

    public void loadCheckins() {
        try {
            WodEngageApi api = new WodEngageApi();
            Single.fromCallable(() -> api.post("", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map((Response response) ->  response.body().string())
                .subscribe((String json) -> {
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<Checkin> jsonAdapter = moshi.adapter(Checkin.class);

                    Checkin checkin = jsonAdapter.fromJson(json);
                    checkinLiveData.postValue(checkin);
                });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
