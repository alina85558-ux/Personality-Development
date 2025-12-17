package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Affirmation {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("detail_image_url")
    @Expose
    private String detail_image_url;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getDetail_image_url() {
        return detail_image_url;
    }

    public void setDetail_image_url(String detail_image_url) {
        this.detail_image_url = detail_image_url;
    }

}