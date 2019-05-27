package com.alienonwork.crossfitcheckin.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.alienonwork.crossfitcheckin.repository.entity.ClassCrossfit;

@Database(entities = {ClassCrossfit.class}, version = 1)
public abstract class CfCheckinDatabase extends RoomDatabase {
    public abstract ClassCrossfitDAO classCrossfitDAO();
}
