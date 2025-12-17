
package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LikeUserListResponse {

    @SerializedName("status_code")
    @Expose
    private int statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private UserListData data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserListData getData() {
        return data;
    }

    public void setData(UserListData data) {
        this.data = data;
    }

    public class UserListData {

        @SerializedName("users")
        @Expose
        private List<UserDetail> users = null;

        public List<UserDetail> getUsers() {
            return users;
        }

        public void setUsers(List<UserDetail> users) {
            this.users = users;
        }
    }

    public class UserDetail {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("profile_img_url")
        @Expose
        private String profileImgUrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

        public String getProfileImgUrl() {
            return profileImgUrl;
        }

        public void setProfileImgUrl(String profileImgUrl) {
            this.profileImgUrl = profileImgUrl;
        }

    }

}
