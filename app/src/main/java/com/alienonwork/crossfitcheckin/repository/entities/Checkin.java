package com.alienonwork.crossfitcheckin.repository.entities;

import org.threeten.bp.OffsetDateTime;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Checkin {

    @PrimaryKey(autoGenerate = true)
    Integer id;
    Integer scheduleId;
    Integer status;
    OffsetDateTime createdAt;
}
