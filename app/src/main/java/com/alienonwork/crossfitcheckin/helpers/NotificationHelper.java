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
    private int priority;
    private PendingIntent pendingIntent;

    public NotificationHelper(Context context, String title, String text, int priority, PendingIntent pendingIntent) {
        this.context = context;
        this.title = title;
        this.text = text;
        this.priority = priority;
        this.pendingIntent = pendingIntent;
    }

    private NotificationHelper() {}

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
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

    public void create() {
        createNotificationChannel();

//        Intent startActivityIntent = new Intent(this.context, MainActivity.class);
//        PendingIntent launchIntent = PendingIntent.getActivity(this.context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(this.title)
                .setContentText(this.text)
                .setAutoCancel(true);

        if (this.priority >= -2 && priority <= 2) {
            notificationBuilder.setPriority(this.priority);
        } else {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        if (this.pendingIntent != null) {
            notificationBuilder.setContentIntent(this.pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static class Builder {

        private Context context;
        private String title;
        private String text;
        private int priority;
        private PendingIntent pendingIntent;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder withTitle(String title) {
            this.title = title;

            return this;
        }

        public Builder withText(String text) {
            this.text = text;

            return this;
        }

        public Builder withPriority(int priority) {
            this.priority = priority;

            return this;
        }

        public Builder withPendingIntent(PendingIntent pendingIntent) {
            this.pendingIntent = pendingIntent;

            return this;
        }

        public NotificationHelper build() {
            if (this.title.isEmpty()) {
                throw new IllegalArgumentException("Title of notification cannot be null");
            }

            if (this.text.isEmpty()) {
                throw new IllegalArgumentException("Text of notification cannot be null");
            }

            NotificationHelper notificationHelper = new NotificationHelper();
            notificationHelper.setContext(this.context);
            notificationHelper.setTitle(this.title);
            notificationHelper.setText(this.text);
            notificationHelper.setPriority(this.priority);

            if (this.pendingIntent != null) {
                notificationHelper.setPendingIntent(pendingIntent);
            }

            return notificationHelper;
        }
    }
}
