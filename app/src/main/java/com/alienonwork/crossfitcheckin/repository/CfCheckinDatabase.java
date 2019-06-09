package com.alienonwork.crossfitcheckin.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.ClassCrossfit;
import com.alienonwork.crossfitcheckin.repository.dao.AgendaDAO;
import com.alienonwork.crossfitcheckin.repository.dao.ClassCrossfitDAO;

@Database(entities = {ClassCrossfit.class, Agenda.class}, version = 1)
public abstract class CfCheckinDatabase extends RoomDatabase {
    public abstract ClassCrossfitDAO classCrossfitDAO();

    public abstract AgendaDAO agendaDAO();
}
