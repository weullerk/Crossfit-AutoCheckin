package com.alienonwork.crossfitcheckin.repository.dao;

import com.alienonwork.crossfitcheckin.repository.entities.Agenda;

import org.threeten.bp.OffsetTime;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface AgendaDAO {

    @Query("SELECT * FROM Agenda ORDER BY dayOfWeek ASC")
    public List<Agenda> listAgenda();

    @Query("SELECT * FROM Agenda WHERE (dayOfWeek == :week AND time > :time) OR dayOfWeek > :week  ORDER BY dayOfWeek ASC")
    public List<Agenda> listAgenda(Integer week, OffsetTime time);
}
