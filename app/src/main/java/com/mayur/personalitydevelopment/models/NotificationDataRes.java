package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationDataRes {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("article_id")
    @Expose
    private int articleId;

    @SerializedName("post_id")
    @Expose
    private int postId;

    @SerializedName("user_id")
    @Expose
    private int userId;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("detail")
    @Expose
    private String detail;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("redirect_to")
    @Expose
    private String redirectTo;

    @SerializedName("redirect_url")
    @Expose
    private String redirectUrl;

    @SerializedName("language_type")
    @Expose
    private String languageType;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}