package com.mayur.personalitydevelopment.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.listener.TodoItemDeleteListener;
import com.mayur.personalitydevelopment.models.AddNoteListModel;
import com.mayur.personalitydevelopment.viewholder.AddNoteHolder;

import java.util.ArrayList;

public class AddNoteListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<AddNoteListModel.AddNoteList> addNoteList = new ArrayList<>();
    private TodoItemDeleteListener todoItemDeleteListener;
    private AddNoteHolder addNoteHolder = new AddNoteHolder();
    private Context mContext;

    public AddNoteListAdapter(Context mContext, ArrayList<AddNoteListModel.AddNoteList> addNoteList, TodoItemDeleteListener todoItemDeleteListener) {
        this.mContext = mContext;
        this.addNoteList = addNoteList;
        this.todoItemDeleteListener = todoItemDeleteListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        addNoteHolder.setItemBinding(mContext, parent);
        return addNoteHolder.getHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AddNoteHolder.MyHolder myHolder = addNoteHolder.castHolder(holder);

        myHolder.noteCompletedCheckBox.setChecked(addNoteList.get(position).isNoteCompleted());
        myHolder.noteTextView.setText(addNoteList.get(position).getNoteTitle());

        myHolder.noteCompletedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (addNoteList.size() > position) {
                        addNoteList.get(position).setNoteCompleted(isChecked);
                    }
                }
        );

        myHolder.deleteNoteImageView.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                        addNoteList.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: //No button clicked
                        dialog.dismiss();
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Are you sure you want to delete this note?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
        });
    }

    @Override
    public int getItemCount() {
        return addNoteList != null ? addNoteList.size() : 0;
    }

}