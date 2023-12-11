package com.adretsoftwares.cellsecuritycare.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.adretsoftwares.cellsecuritycare.entities.DateEntity;

import java.util.List;
@Dao
public interface DateDao {
    @Insert
    void insert(DateEntity dateEntity);

    @Query("SELECT * FROM date_table")
    List<DateEntity> getAllDates();
}
