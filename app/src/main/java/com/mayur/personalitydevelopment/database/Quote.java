package com.mayur.personalitydevelopment.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quote")
public class Quote {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "quotes_id")
    private int id;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
