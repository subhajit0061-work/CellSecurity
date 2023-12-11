package com.adretsoftwares.cellsecuritycare.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.adretsoftwares.cellsecuritycare.converters.DateConverter;
import com.adretsoftwares.cellsecuritycare.daos.DateDao;
import com.adretsoftwares.cellsecuritycare.entities.DateEntity;

@Database(entities = {DateEntity.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class}) // Include TypeConverters if necessary
public abstract class CellSecurityDatabase extends RoomDatabase {
    public abstract DateDao dateDao(); // Include abstract methods for each DAO

    // Create a singleton instance of the database
    private static CellSecurityDatabase INSTANCE;

    public static synchronized CellSecurityDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    CellSecurityDatabase.class,
                    "cell_security_database"
            ).build();
        }
        return INSTANCE;
    }
}
