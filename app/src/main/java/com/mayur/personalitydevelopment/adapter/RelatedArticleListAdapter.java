package com.mayur.personalitydevelopment.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.activity.ArticleDetailActivity;
import com.mayur.personalitydevelopment.models.RelatedArticlesRequestResponse;
import com.mayur.personalitydevelopment.viewholder.RelatedArticlesHolder;

import java.util.ArrayList;
import java.util.List;

public class RelatedArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RelatedArticlesHolder holderInstance = new RelatedArticlesHolder();
    private List<RelatedArticlesRequestResponse.Article> articlesList = new ArrayList<>();
    private Activity context;
    private SharedPreferences sp,prefs;
    public SharedPreferences.Editor editor;
    private RelatedArticlesRequestResponse.Article currentSelectedArticle;

    public RelatedArticleListAdapter(List<RelatedArticlesRequestResponse.Article> articlesList, Activity context) {
        this.articlesList = articlesList;
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        holderInstance.setItemBinding(context, parent);
        return holderInstance.getHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final RelatedArticlesHolder.MyHolder myHolder = holderInstance.castHolder(holder);
        final Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/MRegular.ttf");
        final RelatedArticlesRequestResponse.Article articlesBean = articlesList.get(position);

        myHolder.tv2.setText(articlesBean.getTopic());
        myHolder.tv2.setTypeface(font);
        RequestOptions options = new RequestOptions();

        final RequestOptions placeholder_error = options.error(R.drawable.temo).placeholder(R.drawable.temo)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(articlesBean.getPhoto())
                .apply(placeholder_error).into(myHolder.img2);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Constants.IS_RELATED_ARTICLE_CLICK = true;
                Constants.RELATED_ARTICLE_ACTIVITY_INSTANCE_COUNT++;
                currentSelectedArticle = articlesBean;
                ((ArticleDetailActivity)context).onRelatedArticleClick(position,articlesBean);

            }
        });

        if (sp.getBoolean("light", false)) {
            myHolder.cardView.setCardBackgroundColor(Color.parseColor("#464646"));
            myHolder.tv2.setTextColor(Color.parseColor("#ffffff"));
        } else {
            myHolder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            myHolder.tv2.setTextColor(Color.parseColor("#000000"));
        }

    }

    @Override
    public int getItemCount() {
        return articlesList != null ? articlesList.size() : 0;
    }


}