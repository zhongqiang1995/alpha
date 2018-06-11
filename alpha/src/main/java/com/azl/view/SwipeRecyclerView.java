package com.azl.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.azl.view.manager.WrapContentLinearLayoutManager;
import com.example.zhlib.R;
import com.azl.view.helper.itf.ItfStatusView;


/**
 * Created by zhong on 2017/5/11.
 */

public class SwipeRecyclerView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private FooterView mFooterView;
    private FooterAdapter mPackageAdapter;
    private ViewGroup mContentLayout;
    private View mStatusView;
    private boolean mIsLoadMoreEnable;//是否要加载更多
    private boolean mIsLoadMore;//当前是否是加载更多状态
    private boolean mIsRefreshEnable = true;//是否要下拉刷新
    private boolean mTabRefreshEnable;//记录是否要下拉刷新的状态，在加载更多触发的时候回用到
    private AdapterObserver mDataObserver;

    public SwipeRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_pull_to_refresh, null);
        mContentLayout = (ViewGroup) view.findViewById(R.id.contentLayout);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRefreshLayout.setOnRefreshListener(this);
        mFooterView = new FooterView(getContext());
        mRecyclerView.addOnScrollListener(new OnRecyclerOnScrollListener());


        addView(view);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    /**
     * 改变加载显示的颜色
     *
     * @param color
     */
    public void setRefreshIconColor(int... color) {
        mRefreshLayout.setColorSchemeColors(color);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        setAdapter(adapter, new WrapContentLinearLayoutManager(getContext()));
    }

    public void setAdapter(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        mPackageAdapter = new FooterAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mPackageAdapter);
        mDataObserver = new AdapterObserver();
        adapter.registerAdapterDataObserver(mDataObserver);

    }

    public void setStatusView(ItfStatusView statusView) {
        if (this.mStatusView != null) {
            mContentLayout.removeView(mStatusView);
        }
        complete();
        this.mStatusView = (View) statusView;
        if (mDataObserver != null) {
            mDataObserver.onChanged();
        }
    }

    public void observerChanged() {
        if (mDataObserver != null) {
            mDataObserver.onChanged();
        }
    }

    private int mLastItemPosition;

    public void adapterNotifyDataSetChanged() {
        mPackageAdapter.notifyDataSetChanged();
    }

    public void onNoMore(String text) {
        setLoadMoreEnable(false);
        complete();
        mFooterView.showNoMore(text);
        if(mPackageAdapter!=null) {
            mPackageAdapter.notifyDataSetChanged();
        }
    }

    public void onProgress() {
        mFooterView.showProgress();
        mPackageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefreshing();
        }
        mFooterView.showProgress();
        mFooterView.hideSeparateProgress();
    }

    class OnRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!isLoadMoreEnable() || isRefreshing() || isLoading()) {
                return;
            }
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                mLastItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof GridLayoutManager) {
                mLastItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                mLastItemPosition = findMax(into);
            }
            int childCount = mPackageAdapter == null ? 0 : mPackageAdapter.getItemCount() - 1;
            if (mLastItemPosition == childCount) {
                if (!isLoading() && mOnLoadListener != null && mFooterView.isShowProgress() && !mIsShowEmptyView) {
                    Log.e("mOnLoadListener", "mOnLoadListener");
                    mIsLoadMore = true;
                    mOnLoadListener.onLoadMore();
                    mTabRefreshEnable = isRefreshEnable();
                    mRefreshLayout.setEnabled(false);
                }
            }

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public void complete() {
        completeRefresh();
        completeLoadMore();
    }

    public void completeRefresh() {
        if (!mRefreshLayout.isRefreshing()) {
            return;
        }
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mFooterView.showSeparateProgress();
    }

    public void completeLoadMore() {
        if (!mIsLoadMore) {
            return;
        }
        mIsLoadMore = false;
        mRefreshLayout.setEnabled(mTabRefreshEnable);
        if (mPackageAdapter != null) {
            mPackageAdapter.notifyItemRemoved(mPackageAdapter.getItemCount());
        }
    }

    public void setLoadMoreEnable(boolean isEnable) {
        this.mIsLoadMoreEnable = isEnable;
        mFooterView.showProgress();
    }


    public void setRefreshEnable(boolean isEnable) {
        this.mIsRefreshEnable = isEnable;
    }

    public boolean isLoading() {
        return mIsLoadMore;
    }

    public boolean isLoadMoreEnable() {
        return mIsLoadMoreEnable;
    }

    public boolean isRefreshEnable() {
        return mIsRefreshEnable;
    }

    public boolean isRefreshing() {
        return mRefreshLayout.isRefreshing();
    }

    public boolean isShowContent() {
        return mRecyclerView.getVisibility() == View.VISIBLE;
    }

    private OnLoadListener mOnLoadListener;
    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener mOnRefreshListener) {
        this.mOnRefreshListener = mOnRefreshListener;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.mOnLoadListener = onLoadListener;
    }


    private boolean mIsShowEmptyView;

    class AdapterObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter != null && mStatusView != null) {
                int count = 0;
                if (isLoadMoreEnable() && adapter.getItemCount() != 0 && !isRefreshing()) {
                    count++;
                }
                if (adapter.getItemCount() == count) {
                    mIsShowEmptyView = true;
                    if (mStatusView.getParent() == null) {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                        params.gravity = Gravity.CENTER;
                        mContentLayout.addView(mStatusView, 0, params);
                    }
                    mRecyclerView.setVisibility(View.GONE);
                    mStatusView.setVisibility(View.VISIBLE);
                } else {
                    mIsShowEmptyView = false;
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (mStatusView != null) {
                        mStatusView.setVisibility(View.GONE);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }


        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {

            super.onItemRangeChanged(positionStart, itemCount);
            mPackageAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {

            super.onItemRangeChanged(positionStart, itemCount, payload);
            mPackageAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {

            super.onItemRangeInserted(positionStart, itemCount);
            mPackageAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {

            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            mPackageAdapter.notifyItemRangeRemoved(fromPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {

            super.onItemRangeRemoved(positionStart, itemCount);
            mPackageAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }


    }

    public interface OnLoadListener {
        void onLoadMore();
    }

    public interface OnRefreshListener {
        void onRefreshing();
    }

    class FooterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_TYPE = 120;
        RecyclerView.Adapter mOperationAdapter;

        @Override
        public int getItemViewType(int position) {
            if (isMore(position)) {
                return FOOTER_TYPE;
            }
            return mOperationAdapter.getItemViewType(position);
        }

        public FooterAdapter(RecyclerView.Adapter adapter) {
            this.mOperationAdapter = adapter;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == FOOTER_TYPE) {
                return new FooterVh(mFooterView);
            }
            return mOperationAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (isMore(position)) return;
            mOperationAdapter.onBindViewHolder(holder, position);
        }


        @Override
        public int getItemCount() {
            int count = mOperationAdapter == null ? 0 : mOperationAdapter.getItemCount();
            if (count == 0) {
                return 0;
            }

            count = isM() ? count + 1 : count;
            return count;
        }

        private boolean isM(){
            boolean is=((isLoadMoreEnable()&&mFooterView.isShowProgress())||!isLoadMoreEnable()&&mFooterView.isShowMore());


            return is;
        }
        public boolean isMore(int position) {
            boolean is = position == getItemCount() - 1 && isM();
            return is;
        }

        @Override
        public long getItemId(int position) {
            return mOperationAdapter.getItemId(position);
        }


        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && isMore(holder.getLayoutPosition())) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            mOperationAdapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            mOperationAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        boolean isLoadMore = isMore(position);
                        return isLoadMore ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            mOperationAdapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mOperationAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return mOperationAdapter.onFailedToRecycleView(holder);
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            mOperationAdapter.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            mOperationAdapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            mOperationAdapter.onViewRecycled(holder);
        }
    }

    class FooterVh extends RecyclerView.ViewHolder {

        public FooterVh(View itemView) {
            super(itemView);
        }
    }


}
