package com.alienonwork.crossfitcheckin.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.alienonwork.crossfitcheckin.helpers.Date;
import com.alienonwork.crossfitcheckin.repository.CheckinDatabaseAccessor;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.workers.GetCheckinWorker;
import com.alienonwork.crossfitcheckin.workers.PostCheckinWorker;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {

    private LiveData<List<Schedule>> schedulesLiveData;
    private LiveData<List<WorkInfo>> postCheckinLiveData;

    public ScheduleViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Schedule>> getSchedules() {
        if (schedulesLiveData == null) {
            schedulesLiveData = CheckinDatabaseAccessor
                    .getInstance(getApplication())
                    .scheduleDAO()
                    .listSchedulesLiveData();
        }
        return schedulesLiveData;
    }

    public void loadSchedules() {
        GetCheckinWorker.create(Date.getFirstAndLastDayOfCurrentWeek(null));
    }
}
