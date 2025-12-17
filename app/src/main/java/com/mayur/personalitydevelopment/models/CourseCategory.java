package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CourseCategory {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("completed")
    @Expose
    private boolean isCourseCompleted;

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

    public boolean isCourseCompleted() {
        return isCourseCompleted;
    }

    public void setCourseCompleted(boolean courseCompleted) {
        isCourseCompleted = courseCompleted;
    }
}