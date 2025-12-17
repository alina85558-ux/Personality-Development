package com.mayur.personalitydevelopment.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.mayur.personalitydevelopment.models.Reply;

import java.util.List;

public class ReplyDiffUtil extends DiffUtil.Callback {

    private final List<Reply> oldList;
    private final List<Reply> newList;

    public ReplyDiffUtil(List<Reply> oldList, List<Reply> newList) {
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
