package com.alienonwork.crossfitcheckin.network;

import android.content.Context;

import com.alienonwork.crossfitcheckin.R;

import java.io.IOException;

import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WodEngageApi {

    public static String AUTH_ENDPOINT;
    public static String REQUEST_USER_AGENT;
    public static String REQUEST_X_REQUESTED_WITH;
    public static String REQUEST_ORIGIN;
    public static String REQUEST_MEDIA_TYPE;

    public WodEngageApi(Context context) {
        AUTH_ENDPOINT = context.getString(R.string.wodengage_api_auth);
        REQUEST_USER_AGENT = context.getString(R.string.request_user_agent);
        REQUEST_X_REQUESTED_WITH = context.getString(R.string.request_required_with);
        REQUEST_ORIGIN = context.getString(R.string.request_origin);
        REQUEST_MEDIA_TYPE = context.getString(R.string.request_content_type_json_utf8);
    }

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
                .url(url)
                .header("User-Agent", REQUEST_USER_AGENT)
                .header("X-Requested-With", REQUEST_X_REQUESTED_WITH)
                .header("Origin", REQUEST_ORIGIN)
                .header("authorization", "Basic " + token)
                .build();

        return client.newCall(request).execute();
    }

    public Response post(String url, String body, @Nullable String token) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get(REQUEST_MEDIA_TYPE);

        RequestBody requestBody = RequestBody.create(mediaType, body);

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", REQUEST_USER_AGENT)
                .header("X-Requested-With", REQUEST_X_REQUESTED_WITH)
                .header("Origin", REQUEST_ORIGIN)
                .header("authorization", "Basic " + token)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }
}
