package com.alienonwork.crossfitcheckin.repository.dao;

import com.alienonwork.crossfitcheckin.repository.entities.Agenda;

import org.threeten.bp.OffsetTime;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface CheckinDAO {

    @Query("UPDATE Checkin SET status = :status WHERE id = :id")
    public List<Agenda> updateCheckin(int status, int id);
}
