package com.alienonwork.crossfitcheckin.network;

import java.io.IOException;

import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WodEngageApi {

    public static final String AUTH_ENDPOINT = "auth.wodengage.com/api/v1/auth";
    public static final String API_ENDPOINT = "1161.wodengage.com/api/v1/";

    public static final String REQUEST_USER_AGENT = "Mozilla/5.0 (Linux; Android 7.0; Moto C Build/NRD90M.062; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.99 Mobile Safari/537.36";
    public static final String REQUEST_X_REQUESTED_WITH = "com.ionicframework.appengage223601";
    public static final String REQUEST_ORIGIN = "file://";
    public static final String REQUEST_MEDIA_TYPE = "application/json; charset=UTF-8";

    public Response auth(String body) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get(REQUEST_MEDIA_TYPE);

        RequestBody requestBody = RequestBody.create(mediaType, body);

        Request request = new Request.Builder()
                .url(AUTH_ENDPOINT)
                .header("User-Agent", REQUEST_USER_AGENT)
                .header("X-Requested-With", REQUEST_X_REQUESTED_WITH)
                .header("Origin", REQUEST_ORIGIN)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    public Response get(String url, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_ENDPOINT + url)
                .header("User-Agent", REQUEST_USER_AGENT)
                .header("X-Requested-With", REQUEST_X_REQUESTED_WITH)
                .header("Origin", REQUEST_ORIGIN)
                .header("authorization", "Basic " + token)
                .build();

        return client.newCall(request).execute();
    }

    public Response post(String url, String body, @Nullable String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get("application/json; charset=UTF-8");

        RequestBody requestBody = RequestBody.create(mediaType, body);

        Request request = new Request.Builder()
                .url(AUTH_ENDPOINT)
                .header("User-Agent", REQUEST_USER_AGENT)
                .header("X-Requested-With", REQUEST_X_REQUESTED_WITH)
                .header("Origin", REQUEST_ORIGIN)
                .header("authorization", "Basic " + token)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }
}
