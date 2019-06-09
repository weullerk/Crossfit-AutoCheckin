package com.alienonwork.crossfitcheckin.repository.dao;

import com.alienonwork.crossfitcheckin.repository.entities.Agenda;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface AgendaDAO {

    @Query("SELECT * FROM Agenda")
    public List<Agenda> listAgenda();
}
