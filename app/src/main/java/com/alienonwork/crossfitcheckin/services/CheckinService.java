package com.alienonwork.crossfitcheckin.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.content.Context;

import com.alienonwork.crossfitcheckin.constants.CheckinStatus;
import com.alienonwork.crossfitcheckin.repository.CheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Checkin;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.services.boundServices.ScheduleService;
import com.alienonwork.crossfitcheckin.workers.PostCheckinWorker;

import org.threeten.bp.OffsetDateTime;

public class CheckinService {

    Context mContext;

    public CheckinService(Context context) {
        this.mContext = context;
    }

    public Checkin createCheckinForSchedule(Schedule schedule) {
        OffsetDateTime createdAt = OffsetDateTime.now();
        Checkin checkin = new Checkin(schedule.getId(), CheckinStatus.CREATED, createdAt);

        return CheckinDatabaseAccessor
                .getInstance(mContext)
                .checkinDAO()
                .createCheckin(checkin);
    }

    public void saveCheckin(Checkin checkin) {
        CheckinDatabaseAccessor
                .getInstance(mContext)
                .checkinDAO()
                .saveCheckin(checkin);
    }

    public Checkin getCheckin(Integer id) {
        return CheckinDatabaseAccessor
                .getInstance(mContext)
                .checkinDAO()
                .getCheckin(id);
    }

    public Schedule findLastScheduleWithCheckinMade() {
        Checkin lastCheckin = CheckinDatabaseAccessor
                .getInstance(mContext)
                .checkinDAO()
                .getLastCheckinMade();

        if (lastCheckin != null) {
            return CheckinDatabaseAccessor
                    .getInstance(mContext)
                    .scheduleDAO()
                    .getSchedule(lastCheckin.getScheduleId());
        }

        return null;
    }

    public PendingIntent createPendingIntentAlarmCheckin(Integer checkinId, String extra) {
        Intent intent = new Intent(mContext, ScheduleService.class);
        intent.putExtra(extra, true);
        intent.putExtra(PostCheckinWorker.PARAM_CHECKIN_ID, checkinId);

        return PendingIntent.getService(mContext, 0, intent, 0);
    }

    public void createCheckinAlarm(Long seconds, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + seconds * 1000, pendingIntent);
    }
}