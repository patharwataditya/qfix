package com.qfix.data.local;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient instance;
    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        // Create the database
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "qfix_database")
                // This app currently performs repository calls synchronously from activities/viewmodels.
                // Allowing main-thread access avoids immediate crashes in the existing local-demo flows.
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration() // Recreate database on version mismatch
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
