package com.alienonwork.crossfitcheckin.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.alienonwork.crossfitcheckin.repository.entity.ClassCrossfit;

import java.util.List;

@Dao
public interface ClassCrossfitDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertClasses(List<ClassCrossfit> checkins);

    @Query("SELECT * FROM ClassCrossfit WHERE dayOfYear >= :firstDayOfYear AND dayOfYear <= :lastDayOfYear")
    public List<ClassCrossfit> listClasses(Integer firstDayOfYear, Integer lastDayOfYear);

}
