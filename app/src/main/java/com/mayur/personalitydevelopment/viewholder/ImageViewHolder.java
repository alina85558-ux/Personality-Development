package com.mayur.personalitydevelopment.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.ImageviewDetailBinding;

public class ImageViewHolder {

    private ImageviewDetailBinding itemBinding;

    public ImageViewHolder() {
    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.imageview_detail, parent, false);
    }

    public MyHolder castHolder(RecyclerView.ViewHolder holder) {
        return (MyHolder) holder;
    }

    public MyHolder getHolder() {
        return new MyHolder(itemBinding.getRoot());
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public MyHolder(View view) {
            super(view);
            image = itemBinding.image;
        }

    }
}
