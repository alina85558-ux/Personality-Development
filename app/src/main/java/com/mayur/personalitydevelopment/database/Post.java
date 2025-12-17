package com.mayur.personalitydevelopment.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post")
public class Post {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "post_id")
    private int id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "post_data")
    private String postData;

    @ColumnInfo(name = "created_date")
    private long createdDate;

    @ColumnInfo(name = "isLike")
    private boolean isLike;

    @ColumnInfo(name = "total_like")
    private int totalLike;

    @ColumnInfo(name = "total_comments")
    private int totalComments;

    @ColumnInfo(name = "show_options")
    private boolean showOptions;

    @ColumnInfo(name = "is_delete")
    private boolean isDelete;

    private boolean isSynch;

    @ColumnInfo(name = "profile_url")
    private String profileUrl;

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    public boolean isShowOptions() {
        return showOptions;
    }

    public void setShowOptions(boolean showOptions) {
        this.showOptions = showOptions;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public boolean isSynch() {
        return isSynch;
    }

    public void setSynch(boolean synch) {
        isSynch = synch;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }
}
