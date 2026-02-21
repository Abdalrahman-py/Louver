package com.example.louver.data.seed;

import android.util.Log;

import com.example.louver.data.auth.PasswordHasher;
import com.example.louver.data.converter.FuelType;
import com.example.louver.data.converter.TransmissionType;
import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.AppSettingsEntity;
import com.example.louver.data.entity.CarEntity;
import com.example.louver.data.entity.CarImageEntity;
import com.example.louver.data.entity.CategoryEntity;
import com.example.louver.data.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public final class SeedData {

    private SeedData() {}

    public static void seed(AppDatabase db) {

        // 0) Users — seeded independently of other data, always checked
        if (db.userDao().countUsers() == 0) {
            long userNow = System.currentTimeMillis();

            UserEntity admin = new UserEntity();
            admin.fullName = "System Admin";
            admin.email = "admin@louver.com";
            admin.phone = "0000000000";
            admin.role = "ADMIN";
            admin.profileImageUri = null;
            Log.d("SEED_DEBUG", "Hashing password: [Admin123!]");
            admin.passwordHash = PasswordHasher.hashPassword("Admin123!".toCharArray());
            Log.d("SEED_DEBUG", "Admin hash: " + admin.passwordHash);
            admin.createdAt = userNow;
            db.userDao().insert(admin);

            UserEntity insertedAdmin = db.userDao().getByEmail("admin@louver.com");
            if (insertedAdmin != null) {
                insertedAdmin.role = "ADMIN";
                db.userDao().update(insertedAdmin);
            }

            UserEntity demo = new UserEntity();
            demo.fullName = "Demo User";
            demo.email = "demo@louver.com";
            demo.phone = "0599999999";
            demo.role = "CUSTOMER";
            demo.profileImageUri = null;
            demo.passwordHash = PasswordHasher.hashPassword("Demo123!".toCharArray());
            demo.createdAt = userNow;
            db.userDao().insert(demo);

            UserEntity insertedDemo = db.userDao().getByEmail("demo@louver.com");
            if (insertedDemo != null) {
                insertedDemo.role = "CUSTOMER";
                db.userDao().update(insertedDemo);
            }
        }

        // Avoid reseeding cars/categories/settings if already seeded
        AppSettingsEntity existing = db.settingsDao().getSettingsNow();
        if (existing != null) return;

        // 1) Categories (exactly 6)
        long familyId = db.categoryDao().insert(new CategoryEntity("Family", null));
        long suvId = db.categoryDao().insert(new CategoryEntity("SUV", null));
        long economyId = db.categoryDao().insert(new CategoryEntity("Economy", null));
        long luxuryId = db.categoryDao().insert(new CategoryEntity("Luxury", null));
        long sportId = db.categoryDao().insert(new CategoryEntity("Sport", null));
        long electricId = db.categoryDao().insert(new CategoryEntity("Electric", null));

        long now = System.currentTimeMillis();

        // 2) Cars (exactly 10) - at least 1 per category
        List<Long> carIds = new ArrayList<>();

        carIds.add(db.carDao().insert(new CarEntity(
                familyId,
                "Toyota Camry",
                "Camry SE",
                2022,
                55.0,
                true,
                TransmissionType.AUTOMATIC,
                FuelType.GAS,
                5,
                7.8,
                "Comfortable family sedan with great reliability.",
                "https://example.com/cars/camry_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                familyId,
                "Honda Accord",
                "Accord EX",
                2021,
                58.0,
                true,
                TransmissionType.AUTOMATIC,
                FuelType.GAS,
                5,
                7.5,
                "Spacious sedan ideal for city and highway trips.",
                "https://example.com/cars/accord_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                suvId,
                "Toyota RAV4",
                "RAV4 XLE",
                2022,
                70.0,
                true,
                TransmissionType.AUTOMATIC,
                FuelType.HYBRID,
                5,
                6.5,
                "Popular SUV with efficient hybrid option and roomy interior.",
                "https://example.com/cars/rav4_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                suvId,
                "Nissan X-Trail",
                "X-Trail SV",
                2020,
                65.0,
                true,
                TransmissionType.AUTOMATIC,
                FuelType.GAS,
                7,
                8.2,
                "7-seater SUV for family trips and extra luggage space.",
                "https://example.com/cars/xtrail_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                economyId,
                "Hyundai i10",
                "i10 Comfort",
                2021,
                32.0,
                true,
                TransmissionType.MANUAL,
                FuelType.GAS,
                4,
                5.4,
                "Affordable compact car, easy to park and very economical.",
                "https://example.com/cars/i10_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                economyId,
                "Kia Picanto",
                "Picanto LX",
                2020,
                30.0,
                true,
                TransmissionType.AUTOMATIC,
                FuelType.GAS,
                4,
                5.8,
                "Small city car with great fuel economy.",
                "https://example.com/cars/picanto_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                luxuryId,
                "Mercedes-Benz E-Class",
                "E 300",
                2022,
                140.0,
                true,
                TransmissionType.AUTOMATIC,
                FuelType.GAS,
                5,
                8.8,
                "Premium luxury sedan with advanced comfort features.",
                "https://example.com/cars/eclass_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                luxuryId,
                "BMW 5 Series",
                "520i",
                2021,
                135.0,
                false,
                TransmissionType.AUTOMATIC,
                FuelType.GAS,
                5,
                8.5,
                "Executive sedan with sporty feel and high-end interior.",
                "https://example.com/cars/bmw5_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                sportId,
                "Ford Mustang",
                "Mustang GT",
                2020,
                160.0,
                true,
                TransmissionType.AUTOMATIC,
                FuelType.GAS,
                4,
                12.5,
                "Iconic sports car with powerful performance.",
                "https://example.com/cars/mustang_main.jpg",
                now
        )));

        carIds.add(db.carDao().insert(new CarEntity(
                electricId,
                "Tesla Model 3",
                "Model 3 Long Range",
                2022,
                120.0,
                true,
                TransmissionType.AUTOMATIC,
                FuelType.ELECTRIC,
                5,
                null,
                "Fully electric sedan with long range and fast acceleration.",
                "https://example.com/cars/model3_main.jpg",
                now
        )));

        // 3) Car images: exactly 2 per car (20 total)
        List<CarImageEntity> images = new ArrayList<>();
        for (int i = 0; i < carIds.size(); i++) {
            long carId = carIds.get(i);
            int carNumber = i + 1;
            images.add(new CarImageEntity(carId, "https://example.com/cars/" + carNumber + "_1.jpg", 1));
            images.add(new CarImageEntity(carId, "https://example.com/cars/" + carNumber + "_2.jpg", 2));
        }
        db.carImageDao().insertAll(images);

        // 4) AppSettings default row
        db.settingsDao().upsert(new AppSettingsEntity(
                1,
                "en",
                false,
                true,
                null
        ));
    }
}
