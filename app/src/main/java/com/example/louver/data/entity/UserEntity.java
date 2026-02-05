package com.example.louver.data.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.louver.data.converter.UserRole;

@Entity(
        tableName = "users",
        indices = {
                @Index(value = {"email"}, unique = true)
        }
)
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "fullName")
    public String fullName;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "passwordHash")
    public String passwordHash;

    @Nullable
    @ColumnInfo(name = "phone")
    public String phone;

    @Nullable
    @ColumnInfo(name = "profileImageUri")
    public String profileImageUri;

    @ColumnInfo(name = "createdAt")
    public long createdAt;

    @ColumnInfo(name = "role")
    @NonNull
    public UserRole role;


    public UserEntity() {}

    public UserEntity(String fullName, String email, String passwordHash,
                      @Nullable String phone, @Nullable String profileImageUri,
                      long createdAt) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.profileImageUri = profileImageUri;
        this.createdAt = createdAt;
    }
}
