package com.mayur.personalitydevelopment.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.RowTodoListBinding;

public class TodoHolder {

    private RowTodoListBinding itemBinding;

    public TodoHolder() {

    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_todo_list, parent, false);
    }

    public MyHolder castHolder(RecyclerView.ViewHolder holder) {
        return (MyHolder) holder;
    }

    public MyHolder getHolder() {
        return new MyHolder(itemBinding.getRoot());
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public TextView todoTitleTextView;
        public ImageView deleteTodoImageView;
        public RecyclerView todoRecyclerView;

        public MyHolder(View view) {
            super(view);
            todoTitleTextView = itemBinding.noteTitleTextView;
            deleteTodoImageView = itemBinding.deleteNoteImageView;
            todoRecyclerView = itemBinding.noteRecyclerView;
        }

    }
}
