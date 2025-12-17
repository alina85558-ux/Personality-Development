package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfferResponse {

    @SerializedName("status_code")
    @Expose
    private int statusCode;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private OfferData data;

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

    public OfferData getData() {
        return data;
    }

    public void setData(OfferData data) {
        this.data = data;
    }

    public class OfferData {

        @SerializedName("is_offer_active")
        @Expose
        private boolean isOfferActive;

        @SerializedName("is_lifetime_active")
        @Expose
        private boolean isLifetimeActive;

        @SerializedName("label")
        @Expose
        private String label;

        @SerializedName("small_label")
        @Expose
        private String smallLabel;

        @SerializedName("url")
        @Expose
        private String url;

        public boolean isOfferActive() {
            return isOfferActive;
        }

        public void setOfferActive(boolean offerActive) {
            isOfferActive = offerActive;
        }

        public boolean isLifetimeActive() {
            return isLifetimeActive;
        }

        public void setLifetimeActive(boolean lifetimeActive) {
            isLifetimeActive = lifetimeActive;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSmallLabel() {
            return smallLabel;
        }

        public void setSmallLabel(String smallLabel) {
            this.smallLabel = smallLabel;
        }
    }


}
