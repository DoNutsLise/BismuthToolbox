package com.donuts.bismuth.bismuthtoolbox.Data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

/**
 * All data is stored in Android Room database.
 * At the moment there are two entities:
 * 1. RawUrlData - with json responses from various web resources
 * 2. ParsedHomeScreenData - with parsed data for Home screen views
 * There will be more entities with parsed data for each iof the activities or screens or fragments
 *
 * Refs:
 * 1. https://developer.android.com/training/data-storage/room/
 * 2. https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
 * 2. http://thetechnocafe.com/how-to-use-room-in-android-all-you-need-to-know-to-get-started/
 * 3. https://www.techotopia.com/index.php/An_Android_Room_Database_and_Repository_Tutorial
 */


/**
 * Data with api responses from various servers is stored in Android Room database
 * Refs:
 * 1. https://developer.android.com/training/data-storage/room/
 * 2. https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
 * 2. http://thetechnocafe.com/how-to-use-room-in-android-all-you-need-to-know-to-get-started/
 * 3. https://www.techotopia.com/index.php/An_Android_Room_Database_and_Repository_Tutorial
 */

@Database(entities = {RawUrlData.class, ParsedHomeScreenData.class}, version = 1)
public abstract class DataRoomDatabase extends RoomDatabase {
    public abstract DataDAO getDataDAO();

    private static DataRoomDatabase INSTANCE;

    public synchronized static DataRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static DataRoomDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                DataRoomDatabase.class, "bis_database")
                // the below callback is needed to populate the entities with initial data (mostly zeros)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                getInstance(context).getDataDAO().insertAllParsedHomeScreenData(ParsedHomeScreenData.populateParsedHomeScreenData());
                            }
                        });
                    }
                })
                .allowMainThreadQueries() // TODO: remove this in production
                .build();
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}