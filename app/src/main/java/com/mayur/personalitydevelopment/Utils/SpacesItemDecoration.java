package com.mayur.personalitydevelopment.Utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private boolean isGrid = false;

    public SpacesItemDecoration(int space, boolean isGrid) {
        this.space = space;
        this.isGrid = isGrid;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        if (isGrid) {

            if (position % 2 == 0) {
                outRect.left = space;
                outRect.right = space / 2;
            } else {
                outRect.left = space / 2;
                outRect.right = space;
            }
            if (position == 0 || position == 1)
                outRect.top = space;
            else
                outRect.top = 0;
            outRect.bottom = space;
        } else {
            outRect.left = space;
            outRect.right = space;
            if (position == 0)
                outRect.top = space;
            else
                outRect.top = 0;
            outRect.bottom = space;
        }
    /*// Add top margin only for the first item to avoid double space between items
    if (parent.getChildLayoutPosition(view) == 0) {
        outRect.top = space;
    } else {
        outRect.top = 0;
    }*/
    }
}