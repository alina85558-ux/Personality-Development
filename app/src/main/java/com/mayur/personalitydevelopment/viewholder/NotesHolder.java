package com.mayur.personalitydevelopment.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.RowTodoListItemBinding;

public class NotesHolder {

    private RowTodoListItemBinding itemBinding;

    public NotesHolder() {

    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_todo_list_item, parent, false);
    }

    public MyHolder castHolder(RecyclerView.ViewHolder holder) {
        return (MyHolder) holder;
    }

    public MyHolder getHolder() {
        return new MyHolder(itemBinding.getRoot());
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public TextView noteTitletextView;
        public CheckBox isTaskComplcheckBox;

        public MyHolder(View view) {
            super(view);
            noteTitletextView = itemBinding.noteTitleTextView;
            isTaskComplcheckBox = itemBinding.isTaskComplCheckBox;
        }

    }
}
