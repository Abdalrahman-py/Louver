package com.example.louver.data.seed;

import android.util.Log;

import com.example.louver.data.auth.PasswordHasher;
import com.example.louver.data.converter.UserRole;
import com.example.louver.data.dao.UserDao;
import com.example.louver.data.db.AppDatabase;
import com.example.louver.data.entity.UserEntity;

public class AdminSeeder {

    public static void seedAdmin(AppDatabase db) {
        Log.d("ADMIN_SEED", "seedAdmin() CALLED");
        UserDao userDao = db.userDao();
        Log.d("ADMIN_SEED", "Admin count = " + userDao.countAdmins());
        if (userDao.countAdmins() > 0) return;

        UserEntity admin = new UserEntity();
        admin.fullName = "Admin";
        admin.email = "admin@louver.com";
        admin.passwordHash = PasswordHasher.hashPassword("admin123".toCharArray());
        admin.role = UserRole.ADMIN;
        admin.createdAt = System.currentTimeMillis();

        userDao.insert(admin);
    }
}
