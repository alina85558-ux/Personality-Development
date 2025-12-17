package com.mayur.personalitydevelopment.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {

    private ArticleRepository mRepository;

    private List<Article> mAllArticles;

    public ArticleViewModel(Application application) {
        super(application);
        mRepository = new ArticleRepository(application);
        mAllArticles = mRepository.getAllArticle();
    }

    public List<Article> getAllArticles() {
        return mAllArticles;
    }

    public void insertArticle(Article article) {
        mRepository.insert(article);
    }
}
