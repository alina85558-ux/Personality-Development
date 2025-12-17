package com.mayur.personalitydevelopment.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.activity.CreateTodoNoteActivity;
import com.mayur.personalitydevelopment.listener.TodoItemClickListener;
import com.mayur.personalitydevelopment.models.TodoListResponse;
import com.mayur.personalitydevelopment.viewholder.TodoHolder;

import java.util.ArrayList;

public class TodoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private TodoHolder todoHolder = new TodoHolder();
    private ArrayList<TodoListResponse.Cards> tooList;
    private TodoItemClickListener todoItemClickListener;
    private Context mContext;

    public TodoListAdapter(Context mContext, TodoItemClickListener todoItemClickListener, ArrayList<TodoListResponse.Cards> tooList) {
        this.mContext = mContext;
        this.todoItemClickListener = todoItemClickListener;
        this.tooList = tooList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        todoHolder.setItemBinding(mContext, parent);
        return todoHolder.getHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TodoHolder.MyHolder myHolder = todoHolder.castHolder(holder);

        myHolder.todoTitleTextView.setText(tooList.get(position).getName());

        NoteListAdapter noteListAdapter = new NoteListAdapter(mContext, todoItemClickListener, position, tooList.get(position), tooList.get(position).getNotes());

        myHolder.todoRecyclerView.setHasFixedSize(true);
        myHolder.todoRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        myHolder.todoRecyclerView.setAdapter(noteListAdapter);
        myHolder.todoRecyclerView.setNestedScrollingEnabled(false);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreateTodoNoteActivity.class);
                intent.putExtra("selectedItem", tooList.get(position));
                intent.putExtra("selectedItemPosition", position);
                mContext.startActivity(intent);
            }
        });

        myHolder.deleteTodoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                                todoItemClickListener.onItemDeleteClickListn(position, tooList.get(position).getId() + "");
                                dialog.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete this note?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tooList != null ? tooList.size() : 0;
    }

}