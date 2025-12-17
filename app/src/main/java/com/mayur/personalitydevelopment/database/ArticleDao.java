package com.mayur.personalitydevelopment.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(Article article);

    @Query("DELETE FROM ARTICLE")
    void deleteAll();

    @Query("SELECT * from ARTICLE ORDER BY article_id DESC")
    List<Article> getAllArticles();

    @Query("SELECT * from ARTICLE WHERE isArticleSynch=:isSynch ORDER BY article_id DESC")
    List<Article> getAllArticlesSynch(boolean isSynch);

    @Query("SELECT * FROM ARTICLE WHERE isBookMark =:isBookMark ORDER BY bookMarkTimeStamp DESC")
    List<Article> getBookMarkArticle(boolean isBookMark);

    @Query("SELECT * FROM ARTICLE WHERE article_id IN (SELECT category_article_id FROM ARTICLE_CATEGORY WHERE article_category_id =:categoryId) ORDER BY article_id DESC")
    List<Article> getArticleByCategory(int categoryId);

    @Query("SELECT * FROM ARTICLE WHERE isLike =:isLikes ORDER BY likeTimeStamp DESC")
    List<Article> getLikesArticle(boolean isLikes);

    @Query("SELECT * FROM ARTICLE WHERE article_topic LIKE '%' || :searchKey || '%'")
    List<Article> getArticleSearchKey(String searchKey);

    @Query("SELECT * FROM ARTICLE WHERE search_txt LIKE '%' || :searchKey || '%'")
    List<Article> getArticleSearchKeyOne(String searchKey);

    @Query("UPDATE ARTICLE SET isBookMark =:bookMark ,bookMarkTimeStamp =:timeStamp WHERE article_id = :articleId")
    void setBookMark(boolean bookMark, int articleId, long timeStamp);

    @Query("UPDATE ARTICLE SET isArticleSynch =:isSynch WHERE article_id = :articleId")
    void setSynch(boolean isSynch, int articleId);

    @Query("UPDATE ARTICLE SET noOfLikes=:likeTotal ,isLike =:isLike ,likeTimeStamp =:timeStamp WHERE article_id = :articleId")
    void setLikes(int likeTotal, boolean isLike, int articleId, long timeStamp);
}
