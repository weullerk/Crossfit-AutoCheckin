package com.alienonwork.crossfitcheckin.repository.dao;

import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.Checkin;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;

import org.threeten.bp.OffsetTime;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CheckinDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long createCheckin(Checkin checkin);

    @Update
    public void saveCheckin(Checkin checkin);

    @Query("SELECT * FROM checkin ORDER BY id DESC LIMIT 1")
    public Checkin getLastCheckinMade();

    @Query("SELECT * FROM checkin WHERE id = :id")
    public Checkin getCheckin(Long id);
}
