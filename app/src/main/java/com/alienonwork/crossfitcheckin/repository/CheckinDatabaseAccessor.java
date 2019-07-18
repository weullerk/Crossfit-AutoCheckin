package com.alienonwork.crossfitcheckin.repository;

import androidx.room.Room;
import android.content.Context;

public class CheckinDatabaseAccessor {
    private static CheckinDatabase checkinDatabaseInstance;
    private static final String AUTO_CHECKIN_DB_NAME = "auto_checkin_db";

    private CheckinDatabaseAccessor() {}

    public static CheckinDatabase getInstance(Context context) {
        if (checkinDatabaseInstance == null) {
            checkinDatabaseInstance = Room.databaseBuilder(context, CheckinDatabase.class, AUTO_CHECKIN_DB_NAME).build();
        }

        return checkinDatabaseInstance;
    }
}
