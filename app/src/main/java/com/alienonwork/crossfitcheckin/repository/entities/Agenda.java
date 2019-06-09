package com.alienonwork.crossfitcheckin.repository.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Agenda {

    @PrimaryKey
    Integer dayOfWeek;
    String name;
    String time;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
