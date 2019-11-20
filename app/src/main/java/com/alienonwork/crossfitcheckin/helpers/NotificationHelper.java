package com.alienonwork.crossfitcheckin.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alienonwork.crossfitcheckin.R;
import com.alienonwork.crossfitcheckin.activities.MainActivity;

public class NotificationHelper {

    private static final String NOTIFICATION_CHANNEL = "autocheckin";
    private static final int NOTIFICATION_ID = 1;

    private Context context;

    private String title;

    private String text;

    private int reqCode;

    public NotificationHelper(Context context, String title, String text, int reqCode) {
        this.context = context;
        this.title = title;
        this.text = text;
        this.reqCode = reqCode;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getReqCode() {
        return reqCode;
    }

    public void setReqCode(int reqCode) {
        this.reqCode = reqCode;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context != null) {
            CharSequence name = this.context.getString(R.string.channel_name);
            String description = this.context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = this.context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification() {
        createNotificationChannel();

        Intent startActivityIntent = new Intent(this.context, MainActivity.class);
        PendingIntent launchIntent = PendingIntent.getActivity(this.context, this.reqCode, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(this.title)
                .setContentText(this.text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(launchIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static class Builder() {}
}
