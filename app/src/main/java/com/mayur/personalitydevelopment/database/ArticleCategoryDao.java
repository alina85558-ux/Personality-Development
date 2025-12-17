package com.mayur.personalitydevelopment.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArticleCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticleCategory(ArticleCategory category);

    @Query("DELETE FROM ARTICLE_CATEGORY")
    void deleteAll();

    @Query("SELECT * from ARTICLE_CATEGORY")
    List<ArticleCategory> getArticleCategory();

}
