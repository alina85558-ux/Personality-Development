package com.mayur.personalitydevelopment.listener;

public interface TodoItemClickListener {

    void onItemCheckBoxClickListn(int position, int listPosition, String noteId, String noteItemId, boolean isCompleted);

    void onItemDeleteClickListn(int position, String id);

}


