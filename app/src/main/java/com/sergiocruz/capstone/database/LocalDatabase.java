package com.sergiocruz.capstone.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.sergiocruz.capstone.model.Travel;
import com.sergiocruz.capstone.model.User;

@Database(entities = {User.class, Travel.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class LocalDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();

    private static final String DATABASE_NAME = "capstone_travel_database.db";

    private static LocalDatabase INSTANCE;

    public static LocalDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Room
                        .databaseBuilder(context.getApplicationContext(), LocalDatabase.class, DATABASE_NAME)
                        //.allowMainThreadQueries() // sync
                        //.fallbackToDestructiveMigration() // Destroys the DB and recreates it with the new schema
                        .build();
            }
            return INSTANCE;
        }
    }

    public abstract DatabaseDAO DatabaseDAO();

}
