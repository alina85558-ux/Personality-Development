package com.mayur.personalitydevelopment.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "article_category")
public class ArticleCategory {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "article_category_id")
    private int article_category_id;
    @NonNull
    @ColumnInfo(name = "category_article_id")
    private int category_article_id;

    @NonNull
    public int getArticle_category_id() {
        return article_category_id;
    }

    public void setArticle_category_id(@NonNull int article_category_id) {
        this.article_category_id = article_category_id;
    }

    @NonNull
    public int getCategory_article_id() {
        return category_article_id;
    }

    public void setCategory_article_id(@NonNull int category_article_id) {
        this.category_article_id = category_article_id;
    }
}
