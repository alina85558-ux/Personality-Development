package com.mayur.personalitydevelopment.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    @Query("DELETE FROM CATEGORY")
    void deleteAll();

    @Query("SELECT * from CATEGORY")
    List<Category> getAllCategory();

}
