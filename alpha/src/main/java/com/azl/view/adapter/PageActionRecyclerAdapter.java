package com.azl.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zhong on 2017/8/23.
 */

public class PageActionRecyclerAdapter<Item, VH extends RecyclerView.ViewHolder> extends LoadMoreRecyclerAdapter<VH> {

    private List<Item> mList;

    public PageActionRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getCount() {
        return 0;
    }

    @Override
    protected void onBindHolder(VH holder, int position) {

    }

    @Override
    protected VH onCreateHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public void refresh() {

    }
}
