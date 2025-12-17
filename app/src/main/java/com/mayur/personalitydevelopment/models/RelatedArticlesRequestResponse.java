package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RelatedArticlesRequestResponse implements Serializable {

    @SerializedName("status_code")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable {

        @SerializedName("articles")
        @Expose
        private List<Article> articles = null;

        public List<Article> getArticles() {
            return articles;
        }

        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }

    }

    public class Article implements Serializable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("topic")
        @Expose
        private String topic;
        @SerializedName("photo")
        @Expose
        private String photo;
        @SerializedName("total_likes")
        @Expose
        private Integer totalLikes;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public Integer getTotalLikes() {
            return totalLikes;
        }

        public void setTotalLikes(Integer totalLikes) {
            this.totalLikes = totalLikes;
        }

    }

}