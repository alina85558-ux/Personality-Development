package com.mayur.personalitydevelopment.models;

import java.util.List;

/**
 * Created by Admin on 1/21/2018.
 */

public class ArticlesData {

    private int total_pages;
    private List<Articles> articles;

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Articles> getArticles() {
        return articles;
    }

    public void setArticles(List<Articles> articles) {
        this.articles = articles;
    }

}
