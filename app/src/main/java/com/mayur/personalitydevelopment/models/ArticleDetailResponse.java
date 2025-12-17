
package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArticleDetailResponse {

    @SerializedName("status_code")
    @Expose
    private int statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArticleDetailData data;

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

    public ArticleDetailData getData() {
        return data;
    }

    public void setData(ArticleDetailData data) {
        this.data = data;
    }

    public class ArticleDetailData {

        @SerializedName("total_pages")
        @Expose
        private String totalPages;
        @SerializedName("articles")
        @Expose
        private Articles articles;

        public String getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(String totalPages) {
            this.totalPages = totalPages;
        }

        public Articles getArticles() {
            return articles;
        }

        public void setArticles(Articles articles) {
            this.articles = articles;
        }

    }

}
