package com.alienonwork.crossfitcheckin.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.alienonwork.crossfitcheckin.repository.entities.ClassCrossfit;

import java.util.List;

@Dao
public interface ClassCrossfitDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertClasses(List<ClassCrossfit> checkins);

    @Query("SELECT * FROM ClassCrossfit WHERE dayOfYear >= :firstDayOfYear AND dayOfYear <= :lastDayOfYear")
    public List<ClassCrossfit> listClasses(Integer firstDayOfYear, Integer lastDayOfYear);

    @Query("SELECT * FROM ClassCrossfit WHERE dayOfYear >= :firstDayOfYear AND dayOfYear <= :lastDayOfYear")
    public LiveData<List<ClassCrossfit>> listClassesLiveData(Integer firstDayOfYear, Integer lastDayOfYear);

    @Query("SELECT * FROM ClassCrossfit WHERE datetimeCheckin IS NOT NULL ORDER BY datetimeCheckin DESC LIMIT 1")
    public ClassCrossfit getLastCheckin();

}
