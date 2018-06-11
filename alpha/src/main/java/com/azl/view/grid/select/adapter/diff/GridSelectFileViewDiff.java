package com.azl.view.grid.select.adapter.diff;

import android.support.v7.util.DiffUtil;

import com.azl.view.grid.select.entity.GridAddEntity;
import com.azl.view.grid.select.entity.GridSelectEntity;

import java.util.List;

/**
 * Created by zhong on 2018/3/8.
 */

public class GridSelectFileViewDiff extends DiffUtil.Callback {

    private List<Object> mOldList;
    private List<Object> mNewList;

    public GridSelectFileViewDiff(List<Object> oldList, List<Object> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition) == mNewList.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Object oldObj = mOldList.get(oldItemPosition);
        Object newObj = mNewList.get(newItemPosition);
        if (newObj instanceof GridSelectEntity && oldObj instanceof GridSelectEntity) {
            GridSelectEntity newO = (GridSelectEntity) oldObj;
            GridSelectEntity newN = (GridSelectEntity) newObj;
            if (newO.getPath() != null && newN.getPath() != null && newO.getPath().equals(newN.getPath())) {
                return false;
            }
        } else if (newObj instanceof GridAddEntity && oldObj instanceof GridAddEntity) {
            return false;
        }

        return true;
    }
}
