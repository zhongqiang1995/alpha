package com.azl.view.helper.adapter.multiple.holder;

import android.support.v7.widget.RecyclerView;

import com.azl.util.GsonUtil;
import com.azl.view.helper.holder.CommonHolder;

/**
 * Created by zhong on 2018/1/16.
 */

public abstract class ItemHolder<Adapter, Bean> {

    private int mType;

    public ItemHolder(int type) {
        this.mType=type;
    }

    private RecyclerView.Adapter mAdapter;

    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }


    public void initViewHolder(CommonHolder ch) {
    }

    public abstract void setItemView(Adapter adapter, Bean bean, CommonHolder ch, int position);

    public abstract boolean isSelectItem(Adapter adapter, int position, Object item);

    public abstract int getItemLayoutId();

    public int getItemType() {
        return mType;
    }

    ;
}
