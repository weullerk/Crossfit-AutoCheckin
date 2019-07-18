package com.alienonwork.crossfitcheckin.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.repository.dao.AgendaDAO;
import com.alienonwork.crossfitcheckin.repository.dao.ScheduleDAO;

@Database(entities = {Schedule.class, Agenda.class}, version = 1)
public abstract class CheckinDatabase extends RoomDatabase {
    public abstract ScheduleDAO scheduleDAO();

    public abstract AgendaDAO agendaDAO();
}
