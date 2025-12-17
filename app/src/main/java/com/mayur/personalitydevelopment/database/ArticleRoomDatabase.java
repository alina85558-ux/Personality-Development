package com.mayur.personalitydevelopment.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Article.class, Quote.class, Category.class, Post.class, ArticleCategory.class}, version = 3, exportSchema = false)
public abstract class ArticleRoomDatabase extends RoomDatabase {

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            database.execSQL("ALTER TABLE post " + " ADD COLUMN total_comments INTEGER");
            database.execSQL("ALTER TABLE article " + " ADD COLUMN article_language INTEGER");
            database.execSQL("ALTER TABLE article " + " ADD COLUMN search_txt TEXT");

        }
    };

    private static volatile ArticleRoomDatabase INSTANCE;

    public static ArticleRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ArticleRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ArticleRoomDatabase.class, "bestify_database.db")
                            .allowMainThreadQueries()
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ArticleDao articleDao();

    public abstract CategoryDao categoryDao();

    public abstract QuotesDao quotesDao();

    public abstract PostDao postDao();

    public abstract ArticleCategoryDao articleCategoryDao();

}
