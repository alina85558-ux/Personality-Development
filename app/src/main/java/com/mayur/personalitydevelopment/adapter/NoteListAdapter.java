package com.mayur.personalitydevelopment.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.activity.CreateTodoNoteActivity;
import com.mayur.personalitydevelopment.listener.TodoItemClickListener;
import com.mayur.personalitydevelopment.models.TodoListResponse;
import com.mayur.personalitydevelopment.viewholder.NotesHolder;

import java.util.ArrayList;

public class NoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private NotesHolder notesHolder = new NotesHolder();
    private ArrayList<TodoListResponse.Notes> noteList = new ArrayList<>();
    private TodoListResponse.Cards selectedItemListObject;
    private TodoItemClickListener todoItemClickListener;
    private Context mContext;
    private int headerPosition;

    public NoteListAdapter(Context mContext, TodoItemClickListener todoItemClickListener, int headerPosition, TodoListResponse.Cards selectedItemListObject, ArrayList<TodoListResponse.Notes> noteList) {
        this.noteList = noteList;
        this.mContext = mContext;
        this.headerPosition = headerPosition;
        this.todoItemClickListener = todoItemClickListener;
        this.selectedItemListObject = selectedItemListObject;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesHolder.setItemBinding(mContext, parent);
        return notesHolder.getHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final NotesHolder.MyHolder myHolder = notesHolder.castHolder(holder);
        myHolder.noteTitletextView.setText(noteList.get(position).getTitle());
        myHolder.isTaskComplcheckBox.setChecked(noteList.get(position).getIs_checked());

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreateTodoNoteActivity.class);
                intent.putExtra("selectedItem", selectedItemListObject);
                mContext.startActivity(intent);
            }
        });

        myHolder.isTaskComplcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                todoItemClickListener.onItemCheckBoxClickListn(headerPosition, position, selectedItemListObject.getId() + "",
                        selectedItemListObject.getNotes().get(position).getId() + "",
                        isChecked);
            }
        });

    }

    @Override
    public int getItemCount() {
        return noteList != null ? noteList.size() : 0;
    }

}