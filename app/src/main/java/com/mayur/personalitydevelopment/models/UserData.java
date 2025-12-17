package com.mayur.personalitydevelopment.models;

/**
 * Created by Admin on 1/9/2018.
 */

public class UserData {
    private String user_email;
    private String authentication_token;
    private int user_id;
    private String first_name;
    private String last_name;
    private String profile_photo_original;
    private String profile_photo_thumb;

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getAuthentication_token() {
        return authentication_token;
    }

    public void setAuthentication_token(String authentication_token) {
        this.authentication_token = authentication_token;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getProfilePic() {
        return profile_photo_original;
    }

    public void setProfilePic(String profilePic) {
        this.profile_photo_original = profilePic;
    }

    public String getProfileThumb() {
        return profile_photo_thumb;
    }

    public void setProfileThumb(String profileThumb) {
        this.profile_photo_thumb = profileThumb;
    }
}
