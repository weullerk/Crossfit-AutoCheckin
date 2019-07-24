package com.alienonwork.crossfitcheckin.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.alienonwork.crossfitcheckin.repository.entities.Schedule;


import org.threeten.bp.OffsetDateTime;

import java.util.List;

@Dao
public interface ScheduleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertSchedules(List<Schedule> schedules);

    @Query("SELECT * FROM Schedule")
    public List<Schedule> listSchedules();

    @Query("SELECT * FROM Schedule")
    public LiveData<List<Schedule>> listSchedulesLiveData();

    @Query("SELECT * FROM Schedule WHERE datetimeUTC > :now")
    public List<Schedule> nextSchedules(OffsetDateTime now);

    @Query("SELECT * FROM Schedule WHERE id = :id")
    public Schedule getSchedule(int id);

}
