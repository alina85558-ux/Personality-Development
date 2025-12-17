package com.mayur.personalitydevelopment.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPost(Post post);

    @Query("DELETE FROM POST")
    void deleteAll();

    @Query("SELECT * from POST ORDER BY post_id DESC")
    List<Post> getAllPost();

    @Query("DELETE from POST WHERE post_id =:post_id")
    void deletePost(int post_id);

    @Query("SELECT * from POST WHERE isSynch=:isSynch ORDER BY post_id DESC")
    List<Post> getAllPostSynch(boolean isSynch);

    @Query("UPDATE POST SET total_like=:likeTotal ,isLike =:isLike WHERE post_id = :postId")
    void setLikes(int likeTotal, boolean isLike, int postId);

    @Query("UPDATE POST SET isSynch =:isSynch WHERE post_id = :postId")
    void setSynch(boolean isSynch, int postId);
}
