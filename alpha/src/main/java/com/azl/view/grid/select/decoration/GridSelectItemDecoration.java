package com.azl.view.grid.select.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zhong on 2018/3/5.
 */

public class GridSelectItemDecoration extends RecyclerView.ItemDecoration {
    private int mColumnCount;
    private int mSpacing;

    public GridSelectItemDecoration(int space, int columnCount) {
        this.mColumnCount = columnCount;
        this.mSpacing = space;
    }

    public void setSize(int space, int columnCount) {
        this.mSpacing = space;
        this.mColumnCount = columnCount;
    }

    public void setSpacing(int mSpacing) {
        this.mSpacing = mSpacing;
    }

    public int getSpacing() {
        return mSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);

        int h = position / mColumnCount;
        outRect.left = 0;
        outRect.right = mSpacing;
        outRect.bottom = mSpacing;
        if (h == 0) {
            outRect.top = mSpacing;
        } else {
            outRect.top = 0;
        }

    }
}
