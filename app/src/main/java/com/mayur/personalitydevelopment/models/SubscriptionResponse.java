package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionResponse {

    @SerializedName("status_code")
    @Expose
    private int statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private SubscriptionData data;

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

    public SubscriptionData getData() {
        return data;
    }

    public void setData(SubscriptionData data) {
        this.data = data;
    }

    public class SubscriptionData {

        @SerializedName("is_subscription_active")
        @Expose
        private boolean isSubscriptionActive;
        @SerializedName("subscription_type")
        @Expose
        private String subscriptionType;
        @SerializedName("is_lifetime_active")
        @Expose
        private boolean isLifetimeActive = false;
        @SerializedName("lifetime_subscription_details")
        @Expose
        private String lifetimeSubscriptionDetails;

        public boolean isIsSubscriptionActive() {
            return isSubscriptionActive;
        }

        public void setIsSubscriptionActive(boolean isSubscriptionActive) {
            this.isSubscriptionActive = isSubscriptionActive;
        }

        public String getSubscriptionType() {
            return subscriptionType;
        }

        public void setSubscriptionType(String subscriptionType) {
            this.subscriptionType = subscriptionType;
        }

        public String getLifetimeSubscriptionDetails() {
            return lifetimeSubscriptionDetails;
        }

        public void setLifetimeSubscriptionDetails(String lifetimeSubscriptionDetails) {
            this.lifetimeSubscriptionDetails = lifetimeSubscriptionDetails;
        }

        public boolean isLifetimeActive() {
            return isLifetimeActive;
        }

        public void setLifetimeActive(boolean lifetimeActive) {
            isLifetimeActive = lifetimeActive;
        }
    }
}
