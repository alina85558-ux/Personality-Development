package com.mayur.personalitydevelopment.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.TextviewDetail2Binding;


public class TextView2Holder {
    private TextviewDetail2Binding itemBinding;

    public TextView2Holder() {

    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.textview_detail2, parent, false);
    }

    public TextView2Holder.MyHolder castHolder(RecyclerView.ViewHolder holder) {
        return (TextView2Holder.MyHolder) holder;
    }

    public TextView2Holder.MyHolder getHolder() {
        return new TextView2Holder.MyHolder(itemBinding.getRoot());
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public MyHolder(View view) {
            super(view);
            textView = itemBinding.text2;
        }

    }
}
