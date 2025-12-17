package com.mayur.personalitydevelopment.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.QuotBinding;

public class QuotesHolder {

    private QuotBinding itemBinding;

    public QuotesHolder() {

    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.quot, parent, false);
    }

    public MyHolder castHolder(RecyclerView.ViewHolder holder) {
        return (MyHolder) holder;
    }

    public MyHolder getHolder() {
        return new MyHolder(itemBinding.getRoot());
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView img2, d_btn;

        public MyHolder(View view) {
            super(view);
            img2 = itemBinding.img2;
            d_btn = itemBinding.dBtn;
        }

    }
}
