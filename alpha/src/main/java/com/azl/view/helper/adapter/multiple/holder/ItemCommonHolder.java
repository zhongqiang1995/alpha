package com.azl.view.helper.adapter.multiple.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.azl.view.helper.holder.CommonHolder;

/**
 * Created by zhong on 2018/1/16.
 */

public class ItemCommonHolder extends CommonHolder {
    private ItemHolder mItemHolder;

    public ItemCommonHolder(RecyclerView.Adapter adapter, View itemView, ItemHolder itemHolder) {
        super(itemView);
        this.mItemHolder = itemHolder;
        this.mItemHolder.setAdapter(adapter);
        this.mItemHolder.initViewHolder(this);
    }

    public void setView(RecyclerView.Adapter adapter, Object bean, int position) {
        mItemHolder.setItemView(adapter, bean, this, position);
    }

}
