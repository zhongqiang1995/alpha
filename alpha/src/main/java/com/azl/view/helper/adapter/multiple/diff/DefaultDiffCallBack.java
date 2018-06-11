package com.azl.view.helper.adapter.multiple.diff;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by zhong on 2018/1/16.
 */

public class DefaultDiffCallBack extends DiffUtil.Callback{

    private List<Object> mNewList;
    private List<Object> mOldList;

    public DefaultDiffCallBack(List<Object> mNewList, List<Object> mOldList) {
        this.mNewList = mNewList;
        this.mOldList = mOldList;
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
        return mOldList.get(oldItemPosition)==mNewList.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
