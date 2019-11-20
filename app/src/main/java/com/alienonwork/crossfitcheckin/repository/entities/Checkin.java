package com.alienonwork.crossfitcheckin.repository.entities;

import org.threeten.bp.OffsetDateTime;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Checkin {

    @PrimaryKey(autoGenerate = true)
    long id;
    Integer scheduleId;
    Integer status;
    OffsetDateTime createdAt;

    public Checkin() {
    }

    @Ignore
    public Checkin(Integer scheduleId, Integer status, OffsetDateTime createdAt) {
        this.scheduleId = scheduleId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
