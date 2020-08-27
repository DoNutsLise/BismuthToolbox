package com.donuts.bismuth.bismuthtoolbox.Data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

/*
 * All data is stored in Android Room database.
 * There will be more entities with parsed data for each iof the activities or screens or fragments
 *
 *
 * Refs:
 * 1. https://developer.android.com/training/data-storage/room/
 * 2. https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
 * 2. http://thetechnocafe.com/how-to-use-room-in-android-all-you-need-to-know-to-get-started/
 * 3. https://www.techotopia.com/index.php/An_Android_Room_Database_and_Repository_Tutorial
 */


@Database(entities = {RawUrlData.class, ParsedHomeScreenData.class, EggpoolMinersData.class, EggpoolPayoutsData.class,
        EggpoolBalanceData.class, EggpoolBisStatsData.class, CoingeckoBisPriceData.class, AllHypernodesData.class,
        HypernodesRewardAddressesData.class}, version = 3, exportSchema = false)
@TypeConverters({DataTypeConverter.class})
public abstract class DataRoomDatabase extends RoomDatabase {
    public abstract DataDAO getDataDAO();

    private static DataRoomDatabase INSTANCE;

    public synchronized static DataRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE all_hypernodes_data (hypernode_ip TEXT NOT NULL, hypernode_port TEXT, hypernode_id TEXT, hypernode_tier INTEGER NOT NULL, hypernode_collateral_address TEXT, " +
                            "hypernode_reward_address TEXT, hypernode_block_height INTEGER NOT NULL, hypernode_version TEXT, hypernode_status TEXT, hypernode_mine INTEGER, PRIMARY KEY(hypernode_ip))");
            database.execSQL(
                    "CREATE TABLE hypernodes_reward_addresses_data (transaction_signature TEXT NOT NULL, transaction_block_height INTEGER NOT NULL, transaction_timestamp REAL NOT NULL, " +
                            "sender_address TEXT, recipient_address TEXT, transaction_amount REAL NOT NULL, PRIMARY KEY(transaction_signature))");
        }
    };

    private static DataRoomDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                DataRoomDatabase.class, "bis-database")
                // the below callback is needed to populate the entities with initial data (mostly zeros)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            getInstance(context).getDataDAO().insertAllParsedHomeScreenData(ParsedHomeScreenData.populateParsedHomeScreenData());
                            getInstance(context).getDataDAO().insertAllMiners(EggpoolMinersData.populateMiners());
                            getInstance(context).getDataDAO().insertAllPayouts(EggpoolPayoutsData.populatePayouts());
                            getInstance(context).getDataDAO().insertAllEggpoolBalanceData(EggpoolBalanceData.populateEggpoolBalanceData());
                            getInstance(context).getDataDAO().insertAllEggpoolBisStatsData(EggpoolBisStatsData.populateEggpoolBisStatsData());
                            getInstance(context).getDataDAO().insertAllCoingeckoBisPriceData(CoingeckoBisPriceData.populateCoingeckoBisPriceData());
                            getInstance(context).getDataDAO().insertAllHypernodesData(AllHypernodesData.populateAllHypernodesData());
                            getInstance(context).getDataDAO().insertHypernodesRewardAddressesData(HypernodesRewardAddressesData.populateHypernodesRewardAddressesData());
                        });
                    }
                })
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build();
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}