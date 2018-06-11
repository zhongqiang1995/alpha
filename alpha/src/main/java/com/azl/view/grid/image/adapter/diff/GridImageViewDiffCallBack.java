package com.azl.view.grid.image.adapter.diff;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by zhong on 2018/3/5.
 */

public class GridImageViewDiffCallBack extends DiffUtil.Callback {
    private List<Object> mOldData;
    private List<Object> mNewData;

    public GridImageViewDiffCallBack(List<Object> olddatas, List<Object> newDatas) {
        this.mOldData = olddatas;
        this.mNewData = newDatas;
    }

    @Override
    public int getOldListSize() {
        return mOldData.size();
    }

    @Override
    public int getNewListSize() {
        return mNewData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return jump(oldItemPosition,newItemPosition);
    }


    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return jump(oldItemPosition,newItemPosition);
    }
    private boolean jump(int oldItemPosition, int newItemPosition) {
        Object oldObj = mOldData.get(oldItemPosition);
        Object newObj = mNewData.get(newItemPosition);

        if (newObj instanceof String && oldObj instanceof String) {
            return newObj.equals(oldObj);
        } else if (newObj instanceof Integer && oldObj instanceof Integer) {
            return newObj == oldObj;
        }
        return false;
    }

}
