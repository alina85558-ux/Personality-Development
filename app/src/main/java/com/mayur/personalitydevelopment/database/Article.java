package com.mayur.personalitydevelopment.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "article")
public class Article {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "article_id")
    private int id;
    @ColumnInfo(name = "article_topic")
    private String topic;

    @ColumnInfo(name = "article_description")
    private String descriptions;

    @ColumnInfo(name = "article_language")
    private int language_type;

    @ColumnInfo(name = "search_txt")
    private String search_txt;

    private boolean isLike;
    private boolean isBookMark;
    @ColumnInfo(name = "article_images")
    private String images;
    @ColumnInfo(name = "article_photo")
    private String article_photo;
    private boolean isLocked;
    private boolean isArticleLocked;
    private boolean isArticleSynch;
    private int noOfLikes;
    private long timeStamp;
    private long likeTimeStamp;
    private long bookMarkTimeStamp;
    @ColumnInfo(name = "created_at")
    private long createdAt;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public int getLanguage_type() {
        return language_type;
    }

    public void setLanguage_type(int language_type) {
        this.language_type = language_type;
    }

    public String getSearch_txt() {
        return search_txt;
    }

    public void setSearch_txt(String search_txt) {
        this.search_txt = search_txt;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isBookMark() {
        return isBookMark;
    }

    public void setBookMark(boolean bookMark) {
        isBookMark = bookMark;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isArticleLocked() {
        return isArticleLocked;
    }

    public void setArticleLocked(boolean articleLocked) {
        isArticleLocked = articleLocked;
    }

    public int getNoOfLikes() {
        return noOfLikes;
    }

    public void setNoOfLikes(int noOfLikes) {
        this.noOfLikes = noOfLikes;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getArticle_photo() {
        return article_photo;
    }

    public void setArticle_photo(String article_photo) {
        this.article_photo = article_photo;
    }

    public boolean isArticleSynch() {
        return isArticleSynch;
    }

    public void setArticleSynch(boolean articleSynch) {
        isArticleSynch = articleSynch;
    }

    public long getLikeTimeStamp() {
        return likeTimeStamp;
    }

    public void setLikeTimeStamp(long likeTimeStamp) {
        this.likeTimeStamp = likeTimeStamp;
    }

    public long getBookMarkTimeStamp() {
        return bookMarkTimeStamp;
    }

    public void setBookMarkTimeStamp(long bookMarkTimeStamp) {
        this.bookMarkTimeStamp = bookMarkTimeStamp;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
