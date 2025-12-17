package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NotificationResponse {
    @SerializedName("notifications")
    private ArrayList<NotificationDataRes> notificationsList;

    public ArrayList<NotificationDataRes> getNotificationsList() {
        return notificationsList;
    }

    public void setNotificationsList(ArrayList<NotificationDataRes> notificationsList) {
        this.notificationsList = notificationsList;
    }
}
