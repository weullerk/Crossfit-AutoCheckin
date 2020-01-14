package com.alienonwork.crossfitcheckin.repository.entities;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Schedule {

    @PrimaryKey(autoGenerate = true)
    Integer id;
    Integer classId;
    Instant timestampUTC;
    OffsetDateTime datetimeUTC;
    Integer dayOfYear;
    Integer dayOfWeek;
    OffsetTime hour;
    String className;
    Boolean blocked;

    public Schedule(Integer classId, Instant timestampUTC, OffsetDateTime datetimeUTC, Integer dayOfYear, Integer dayOfWeek, OffsetTime hour, String className, Boolean blocked) {
        this.classId = classId;
        this.timestampUTC = timestampUTC;
        this.datetimeUTC = datetimeUTC;
        this.dayOfYear = dayOfYear;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.className = className;
        this.blocked = blocked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {this.id = id;}

    public Integer getClassId() { return classId; }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Instant getTimestampUTC() {
        return timestampUTC;
    }

    public void setTimestampUTC(Instant timestampUTC) {
        this.timestampUTC = timestampUTC;
    }

    public OffsetDateTime getDatetimeUTC() {
        return datetimeUTC;
    }

    public void setDatetimeUTC(OffsetDateTime datetimeUTC) {
        this.datetimeUTC = datetimeUTC;
    }

    public Integer getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(Integer dayOfYear) { this.dayOfYear = dayOfYear; }

    public Integer getDayOfWeek() { return dayOfWeek; }

    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public OffsetTime getHour() {
        return hour;
    }

    public void setHour(OffsetTime hour) {
        this.hour = hour;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}
