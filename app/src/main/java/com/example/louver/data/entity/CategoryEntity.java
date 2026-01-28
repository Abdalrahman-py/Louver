package com.example.louver.data.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "categories",
        indices = {
                @Index(value = {"name"}, unique = true)
        }
)
public class CategoryEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @Nullable
    @ColumnInfo(name = "iconUrl")
    public String iconUrl;

    public CategoryEntity() {}

    public CategoryEntity(String name, @Nullable String iconUrl) {
        this.name = name;
        this.iconUrl = iconUrl;
    }
}
