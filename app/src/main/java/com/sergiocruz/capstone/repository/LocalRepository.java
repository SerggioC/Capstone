package com.sergiocruz.capstone.repository;

import android.content.Context;

import com.sergiocruz.capstone.database.DatabaseDAO;
import com.sergiocruz.capstone.database.LocalDatabase;

public class LocalRepository {
    private static LocalRepository sInstance;
    private static DatabaseDAO databaseDAO;

    public static LocalRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocalRepository();
            databaseDAO = LocalDatabase.getInstance(context).DatabaseDAO();
        }
        return sInstance;
    }
}
