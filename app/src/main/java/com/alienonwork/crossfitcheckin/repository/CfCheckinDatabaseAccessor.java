package com.alienonwork.crossfitcheckin.repository;

import androidx.room.Room;
import android.content.Context;

public class CfCheckinDatabaseAccessor {
    private static CfCheckinDatabase cfCheckinDatabaseInstance;
    private static final String CF_CHECKIN_DB_NAME = "cf_checkin_db";

    private CfCheckinDatabaseAccessor() {}

    public static CfCheckinDatabase getInstance(Context context) {
        if (cfCheckinDatabaseInstance == null) {
            cfCheckinDatabaseInstance = Room.databaseBuilder(context, CfCheckinDatabase.class, CF_CHECKIN_DB_NAME).build();
        }

        return cfCheckinDatabaseInstance;
    }
}
