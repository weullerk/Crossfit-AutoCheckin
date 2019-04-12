package com.alienonwork.crossfitcheckin;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        new AsyncTask<Void, Void, Checkin>() {

            @Override
            protected Checkin doInBackground(Void... voids) {
                Checkin checkin = null;

                URL url;
                HttpURLConnection httpConnection = null;

                try {
                    String urlClasses = "1161.wodengage.com/api/v1/checkin/getWeek/2019-03-25/2019-03-31/13946";

                    url = new URL(urlClasses);
                    httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setRequestMethod("GET");
                    httpConnection.setRequestProperty("Accept", "application/json, text/plain, */*");
                    httpConnection.setRequestProperty("authorization", "Basic MTM5NDY6JDJ5JDEwJE9FcGhjMlEzTXpreWFHRnpaRFZCVXVaVHBSNkN3SHFCbjBUeEVWVjRvcXNpdVg4dXFaNlFx");
                    httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 7.0; Moto C Build/NRD90M.062; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.99 Mobile Safari/537.36");
                    httpConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    httpConnection.setRequestProperty("Accept-Language", "pt-BR,en-US;q=0.9");
                    httpConnection.setRequestProperty("X-Requested-With", "com.ionicframework.appengage223601");

                    httpConnection.connect();

                    int responseCode = httpConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream in = httpConnection.getInputStream();
                        InputStreamReader reader = new InputStreamReader(in);
                        Gson gson = new Gson();
                        checkin = gson.fromJson(reader, Checkin.class);
                        Log.e(TAG, checkin.status);
                    }


                } catch (MalformedURLException e) {
                    Log.e(TAG, "MalformatedURLEXception", e);
                } catch (IOException e) {
                    Log.e(TAG, "IOException", e);
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "JsonSyntaxException", e);
                } catch (JsonIOException e) {
                    Log.e(TAG, "JsonIOException", e);
                } finally {
                    if (httpConnection != null)
                        httpConnection.disconnect();
                }

                return checkin;
            }

            @Override
            protected void onPostExecute(Checkin data) {
                checkinLiveData.setValue(data);
            }
        }.execute();
    }
}
