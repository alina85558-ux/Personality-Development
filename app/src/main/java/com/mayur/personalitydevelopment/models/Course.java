package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Course {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("course_name")
    @Expose
    private String courseName;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("access_message")
    @Expose
    private String accessmessage;

    @SerializedName("access_flag")
    @Expose
    private int accessFlag;

    @SerializedName("remain_task_msg")
    @Expose
    private String remainTaskMsg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAccessmessage() {
        return accessmessage;
    }

    public void setAccessmessage(String accessmessage) {
        this.accessmessage = accessmessage;
    }

    public int getAccessFlag() {
        return accessFlag;
    }

    public void setAccessFlag(int accessFlag) {
        this.accessFlag = accessFlag;
    }

    public String getRemainTaskMsg() {
        return remainTaskMsg;
    }

    public void setRemainTaskMsg(String remainTaskMsg) {
        this.remainTaskMsg = remainTaskMsg;
    }
}