package com.alienonwork.crossfitcheckin.repository.entities;

import org.threeten.bp.OffsetTime;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Agenda {

    @PrimaryKey(autoGenerate = true)
    Integer id;
    Integer dayOfWeek;
    String name;
    OffsetTime time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetTime getTime() {
        return time;
    }

    public void setTime(OffsetTime time) {
        this.time = time;
    }
}
