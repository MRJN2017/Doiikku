package com.example.doiikku.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.doiikku.database.dao.DatabaseDao;
import com.example.doiikku.model.ModelDatabase;

@Database(entities = {ModelDatabase.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatabaseDao databaseDao();
}


