package com.mayur.personalitydevelopment.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.RowAddNoteBinding;

public class AddNoteHolder {

    private RowAddNoteBinding itemBinding;

    public AddNoteHolder() {

    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_add_note, parent, false);
    }

    public MyHolder castHolder(RecyclerView.ViewHolder holder) {
        return (MyHolder) holder;
    }

    public MyHolder getHolder() {
        return new MyHolder(itemBinding.getRoot());
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public CheckBox noteCompletedCheckBox;
        public TextView noteTextView;
        public ImageView deleteNoteImageView;

        public MyHolder(View view) {
            super(view);
            noteCompletedCheckBox = itemBinding.isNoteCompletedCheckBox;
            noteTextView = itemBinding.noteTextView;
            deleteNoteImageView = itemBinding.deleteNoteImageView;
        }

    }
}
