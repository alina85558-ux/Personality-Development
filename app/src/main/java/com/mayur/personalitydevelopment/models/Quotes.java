package com.mayur.personalitydevelopment.models;

import java.util.List;

/**
 * Created by Admin on 10/8/2017.
 */

public class Quotes {
    private int total_pages;
    private List<QuotesBean> quotes;

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<QuotesBean> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<QuotesBean> quotes) {
        this.quotes = quotes;
    }

    public static class QuotesBean {
        private int id;
        private String topic;
        private String image_url;
        private int created_at;
        private int updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public int getCreated_at() {
            return created_at;
        }

        public void setCreated_at(int created_at) {
            this.created_at = created_at;
        }

        public int getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(int updated_at) {
            this.updated_at = updated_at;
        }
    }
}
