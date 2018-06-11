package com.azl.view.grid.image.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhong on 2018/3/5.
 */

public class GridImageItemDecoration extends RecyclerView.ItemDecoration {
    private int mColumnCount;
    private int mSpacing;

    public GridImageItemDecoration(int space, int columnCount) {
        this.mColumnCount = columnCount;
        this.mSpacing = space;
    }

    public void setSize(int space, int columnCount) {
        this.mSpacing = space;
        this.mColumnCount = columnCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % mColumnCount; // item column
        outRect.left = column * mSpacing / mColumnCount; // column * ((1f / mColumnCount) * mSpacing)
        outRect.right = mSpacing - (column + 1) * mSpacing / mColumnCount; // mSpacing - (column + 1) * ((1f /    mColumnCount) * mSpacing)
        if (position >= mColumnCount) {
            outRect.top = mSpacing; // item top
        }
    }
}
