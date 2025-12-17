package com.mayur.personalitydevelopment.models;

import java.util.ArrayList;

/**
 * Created by Admin on 1/21/2018.
 */

public class Articles {

    private int id;
    private String topic;
    private String description;
    private int language_type;
    private String search_txt;
    private String photo;
    private long created_at;
    private int updated_at;
    private boolean is_like;
    private boolean is_favourite;
    private int total_likes = 0;
    private boolean article_is_locked;
    private boolean user_article_is_locked;
    private ArrayList<Integer> category_ids;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isIs_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
    }

    public boolean isIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(boolean is_favourite) {
        this.is_favourite = is_favourite;
    }

    public int getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(int total_likes) {
        this.total_likes = total_likes;
    }

    public boolean isArticle_is_locked() {
        return article_is_locked;
    }

    public void setArticle_is_locked(boolean article_is_locked) {
        this.article_is_locked = article_is_locked;
    }

    public boolean isUser_article_is_locked() {
        return user_article_is_locked;
    }

    public void setUser_article_is_locked(boolean user_article_is_locked) {
        this.user_article_is_locked = user_article_is_locked;
    }

    public ArrayList<Integer> getCategory_ids() {
        return category_ids;
    }

    public void setCategory_ids(ArrayList<Integer> category_ids) {
        this.category_ids = category_ids;
    }
}
