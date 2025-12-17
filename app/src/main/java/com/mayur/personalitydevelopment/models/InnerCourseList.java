package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InnerCourseList {

    @SerializedName("courses")
    @Expose
    private ArrayList<Course> data;

    @SerializedName("video_url")
    @Expose
    private String youtubeVideoUrl;

    public ArrayList<Course> getData() {
        return data;
    }

    public void setData(ArrayList<Course> data) {
        this.data = data;
    }

    public String getYoutubeVideoUrl() {
        return youtubeVideoUrl;
    }

    public void setYoutubeVideoUrl(String youtubeVideoUrl) {
        this.youtubeVideoUrl = youtubeVideoUrl;
    }
}