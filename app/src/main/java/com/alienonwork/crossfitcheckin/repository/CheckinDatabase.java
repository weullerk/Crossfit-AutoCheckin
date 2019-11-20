package com.alienonwork.crossfitcheckin.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.alienonwork.crossfitcheckin.converters.DateTypeConverters;
import com.alienonwork.crossfitcheckin.repository.dao.CheckinDAO;
import com.alienonwork.crossfitcheckin.repository.entities.Agenda;
import com.alienonwork.crossfitcheckin.repository.entities.Checkin;
import com.alienonwork.crossfitcheckin.repository.entities.Schedule;
import com.alienonwork.crossfitcheckin.repository.dao.AgendaDAO;
import com.alienonwork.crossfitcheckin.repository.dao.ScheduleDAO;

@Database(entities = {Schedule.class, Agenda.class, Checkin.class},  version = 1)
@TypeConverters({DateTypeConverters.class})
public abstract class CheckinDatabase extends RoomDatabase {
    public abstract ScheduleDAO scheduleDAO();
    public abstract AgendaDAO agendaDAO();
    public abstract CheckinDAO checkinDAO();
}

