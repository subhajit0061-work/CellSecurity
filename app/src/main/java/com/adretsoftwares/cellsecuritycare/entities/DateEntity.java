package com.adretsoftwares.cellsecuritycare.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.adretsoftwares.cellsecuritycare.converters.DateConverter;

import java.sql.Time;
import java.util.Date;

@Entity(tableName = "date_table")
public class DateEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date")
    @TypeConverters({DateConverter.class})
    private Date yourDate;

    @ColumnInfo(name = "time")
    @TypeConverters({DateConverter.class})
    private Time yourTime;

    public Date getYourDate() {
        return yourDate;
    }

    public void setYourDate(Date yourDate) {
        this.yourDate = yourDate;
    }

    public Time getYourTime() {
        return yourTime;
    }

    public void setYourTime(Time yourTime) {
        this.yourTime = yourTime;
    }

    public int getId() {
        return id;
    }

    // Setter method for id
    public void setId(int id) {
        this.id = id;
    }
}
