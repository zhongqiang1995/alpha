package com.azl.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhlib.R;

/**
 * Created by zhong on 2017/8/23.
 * 用于向
 */

public abstract class LoadMoreRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean mIsLoadMoreEnable = true;//记录是否要上拉加载

    private int mFootType = 10003;//加载更多的类型

    private Context mContext;//指定的上下文

    private OnLoadMoreListener mOnLoadMoreListener;//加载更多监听

    public LoadMoreRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        if (viewType == mFootType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_swipe_recycler_layout, parent, false);
            vh = new FootVH(view);
        } else {
            vh = onCreateHolder(parent, viewType);
        }
        return vh;
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == mFootType) {
            actionLoadMoreListener();
            return;
        }
        onBindHolder((VH) holder, position);
    }

    private void actionLoadMoreListener() {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoad();
        }
    }

    @Override
    public final int getItemCount() {
        int count;
        if (getCount() > 0 && mIsLoadMoreEnable) {
            count = getCount() + 1;
        } else {
            count = getCount();
        }
        return count;
    }

    @Override
    public final int getItemViewType(int position) {
        if (position >= getCount()) {
            return mFootType;
        }
        return getItemType(position);
    }

    protected int getItemType(int position) {
        return super.getItemViewType(position);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    class FootVH extends RecyclerView.ViewHolder {

        public FootVH(View itemView) {
            super(itemView);
        }
    }

    public interface OnLoadMoreListener {
        void onLoad();
    }


    protected abstract int getCount();

    protected abstract void onBindHolder(VH holder, int position);

    protected abstract VH onCreateHolder(ViewGroup parent, int viewType);

}
