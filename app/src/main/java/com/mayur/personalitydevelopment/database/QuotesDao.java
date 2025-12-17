package com.mayur.personalitydevelopment.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuotes(Quote quotes);

    @Query("DELETE FROM QUOTE")
    void deleteAll();

    @Query("SELECT * from QUOTE ORDER BY quotes_id DESC")
    List<Quote> getAllQuotes();

}
