package com.mayur.personalitydevelopment.adapter;

import android.content.Context;
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
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.models.Articles;
import com.mayur.personalitydevelopment.viewholder.GetToKnowHolder;

import java.util.ArrayList;

public class ReadingListAdapter extends RecyclerView.Adapter<GetToKnowHolder.MyHolder> {

    public SharedPreferences.Editor editor;
    private ArrayList<Articles> items;
    private Context context;
    private ReadingListAdapter.AdapterListener listener;
    private GetToKnowHolder holderInstance = new GetToKnowHolder();
    private SharedPreferences sp;
    private boolean isLightTheme = false;
    public ReadingListAdapter(Context context, ArrayList<Articles> items, ReadingListAdapter.AdapterListener listener) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        isLightTheme = sp.getBoolean("light", false);
        this.items = items;
        this.listener = listener;
    }

    @Override
    public GetToKnowHolder.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        holderInstance.setItemBinding(context, parent);
        return holderInstance.getHolder();
    }

    @Override
    public void onBindViewHolder(GetToKnowHolder.MyHolder holder, final int position) {

        final GetToKnowHolder.MyHolder myHolder = holderInstance.castHolder(holder);
        final Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/MRegular.ttf");
        final Articles articlesBean = items.get(position);

        myHolder.tv2.setText(articlesBean.getTopic());
        myHolder.tv2.setTypeface(font);
        myHolder.tvTime.setReferenceTime(articlesBean.getCreated_at());
        myHolder.tvLikes.setText(Utils.convertNumberToCount(/*1099+*/articlesBean.getTotal_likes()) + " Likes");

        if (articlesBean.isArticle_is_locked()) {
            if (articlesBean.isUser_article_is_locked()) {
                myHolder.img_lock_article.setBackground(context.getResources().getDrawable(R.drawable.lock));
            } else {
                myHolder.img_lock_article.setBackground(context.getResources().getDrawable(R.drawable.unlock));
            }
        } else {
            myHolder.img_lock_article.setBackground(null);
        }

        RequestOptions options = new RequestOptions();

        final RequestOptions placeholder_error = options.error(R.drawable.temo).placeholder(R.drawable.temo)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(articlesBean.getPhoto())
                .apply(placeholder_error).into(myHolder.img2);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                listener.onClickEvent(articlesBean);
            }
        });

        if (isLightTheme) {
            myHolder.cardView.setCardBackgroundColor(Color.parseColor("#464646"));
            myHolder.tv2.setTextColor(Color.parseColor("#ffffff"));
            myHolder.tvTime.setTextColor(Color.parseColor("#ffffff"));
            myHolder.tvLikes.setTextColor(Color.parseColor("#ffffff"));
        } else {
            myHolder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            myHolder.tv2.setTextColor(Color.parseColor("#000000"));
            myHolder.tvTime.setTextColor(Color.parseColor("#000000"));
            myHolder.tvLikes.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public interface AdapterListener {
        void onClickEvent(Articles courseCategory);
    }
}