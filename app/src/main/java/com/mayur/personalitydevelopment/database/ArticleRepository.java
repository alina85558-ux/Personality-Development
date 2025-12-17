package com.mayur.personalitydevelopment.database;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class ArticleRepository {

    private ArticleDao mArticleDao;
    private List<Article> mAllArticle;

    ArticleRepository(Application application) {
        ArticleRoomDatabase articleRoomDatabase = ArticleRoomDatabase.getDatabase(application);
        mArticleDao = articleRoomDatabase.articleDao();
        mAllArticle = mArticleDao.getAllArticles();
    }

    List<Article> getAllArticle() {
        return mAllArticle;
    }

    public void insert(Article article) {
        new insertAsyncTask(mArticleDao).execute(article);
    }

    private static class insertAsyncTask extends AsyncTask<Article, Void, Void> {

        private ArticleDao mAsyncTaskDao;

        insertAsyncTask(ArticleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Article... params) {
            mAsyncTaskDao.insertArticle(params[0]);
            return null;
        }
    }

}
