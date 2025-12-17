package com.mayur.personalitydevelopment.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.mayur.personalitydevelopment.models.Comment;

import java.util.List;

public class CommentDiffUtil extends DiffUtil.Callback {

    private final List<Comment> oldList;
    private final List<Comment> newList;

    public CommentDiffUtil(List<Comment> oldList, List<Comment> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).compareTo(newList.get(newItemPosition)) == 0;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
