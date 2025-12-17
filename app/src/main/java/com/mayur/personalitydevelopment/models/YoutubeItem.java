package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YoutubeItem implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("course_category")
    @Expose
    private String course_category;

    @SerializedName("course")
    @Expose
    private String course;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourse_category() {
        return course_category;
    }

    public void setCourse_category(String course_category) {
        this.course_category = course_category;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}