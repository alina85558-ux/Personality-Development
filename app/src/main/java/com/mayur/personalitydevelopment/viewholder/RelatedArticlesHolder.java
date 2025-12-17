package com.mayur.personalitydevelopment.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.RowRelatedArticleBinding;

public class RelatedArticlesHolder {

    private RowRelatedArticleBinding itemBinding;

    public RelatedArticlesHolder() {

    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_related_article, parent, false);
    }

    public MyHolder castHolder(RecyclerView.ViewHolder holder) {
        return (MyHolder) holder;
    }

    public MyHolder getHolder() {
        return new MyHolder(itemBinding.getRoot());
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public TextView tv2;
        public ImageView img2;
        public CardView cardView;

        public MyHolder(View view) {
            super(view);
            tv2 = itemBinding.tv2;
            img2 = itemBinding.img2;
            cardView = itemBinding.card;
        }

    }
}
