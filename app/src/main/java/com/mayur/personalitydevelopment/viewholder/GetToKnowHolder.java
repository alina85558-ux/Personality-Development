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

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.RowFBinding;

public class GetToKnowHolder {

    private RowFBinding itemBinding;

    public GetToKnowHolder() {

    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_f, parent, false);
    }

    public MyHolder castHolder(RecyclerView.ViewHolder holder) {
        return (MyHolder) holder;
    }

    public MyHolder getHolder() {
        return new MyHolder(itemBinding.getRoot());
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public RelativeTimeTextView tvTime;
        public TextView tv2, tvLikes;
        public ImageView img2, img_lock_article;
        public CardView cardView;

        public MyHolder(View view) {
            super(view);
            tvTime = itemBinding.tvTime;
            tv2 = itemBinding.tv2;
            tvLikes = itemBinding.tvLikes;
            img2 = itemBinding.img2;
            cardView = itemBinding.card;
            img_lock_article = itemBinding.imgLockArticle;
        }

    }
}
