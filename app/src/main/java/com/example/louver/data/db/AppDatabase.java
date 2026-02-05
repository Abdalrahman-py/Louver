package com.example.louver.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.louver.data.converter.AppTypeConverters;
import com.example.louver.data.dao.BookingDao;
import com.example.louver.data.dao.CarDao;
import com.example.louver.data.dao.CarImageDao;
import com.example.louver.data.dao.CategoryDao;
import com.example.louver.data.dao.FavoriteDao;
import com.example.louver.data.dao.NotificationDao;
import com.example.louver.data.dao.ReviewDao;
import com.example.louver.data.dao.SettingsDao;
import com.example.louver.data.dao.UserDao;
import com.example.louver.data.entity.AppSettingsEntity;
import com.example.louver.data.entity.BookingEntity;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CarImageEntity;
import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.data.entity.FavoriteEntity;
import com.example.louver.data.entity.NotificationEntity;
import com.example.louver.data.entity.ReviewEntity;
import com.example.louver.data.entity.UserEntity;
import com.example.louver.data.seed.AdminSeeder;
import com.example.louver.data.seed.SeedData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserEntity.class, CategoryEntity.class, CarEntity.class, CarImageEntity.class, BookingEntity.class, FavoriteEntity.class, ReviewEntity.class, AppSettingsEntity.class, NotificationEntity.class}, version = 2, exportSchema = false)
@TypeConverters({AppTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract CategoryDao categoryDao();

    public abstract CarDao carDao();

    public abstract CarImageDao carImageDao();

    public abstract BookingDao bookingDao();

    public abstract FavoriteDao favoriteDao();

    public abstract ReviewDao reviewDao();

    public abstract SettingsDao settingsDao();

    public abstract NotificationDao notificationDao();

    // Single-thread executor for DB operations & seeding
    public static final ExecutorService DB_EXECUTOR = Executors.newSingleThreadExecutor();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "louver_db").addCallback(new Callback() {

                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            DB_EXECUTOR.execute(() -> {
                                SeedData.seed(INSTANCE);
                            });
                        }

                    })
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
