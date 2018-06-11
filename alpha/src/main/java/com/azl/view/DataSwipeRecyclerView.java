package com.azl.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.azl.obs.data.HttpDataGet;
import com.azl.obs.retrofit.itf.Observer;
import com.azl.util.ObjectValueUtil;
import com.azl.view.helper.adapter.CommonAdapter;
import com.azl.view.helper.itf.ItfStatusView;

/**
 * Created by zhong on 2017/5/18.
 */

public class DataSwipeRecyclerView extends SwipeRecyclerView implements SwipeRecyclerView.OnLoadListener, SwipeRecyclerView.OnRefreshListener, Observer<Object> {

    private HttpDataGet mDataGet;
    private String mPageName = "page";
    private String mPageCountName = "pageCount";
    private int mPage;
    private int mPageCount;
    private int mDefaultPage = 1;
    private CommonAdapter mAdapter;
    private ItfStatusView mStatusView;

    public DataSwipeRecyclerView(Context context) {
        this(context, null);
    }

    public DataSwipeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public void setAdapter(CommonAdapter adapter, HttpDataGet dataGet) {
        this.mAdapter = adapter;
        setDataGet(dataGet);
        super.setAdapter(adapter);
    }

    public void setAdapter(CommonAdapter adapter, HttpDataGet dataGet, String pageParamsName, int pageDefault, String pageCountParamsName, int pageCount) {
        this.mPageName = pageParamsName;
        this.mDefaultPage = pageDefault;
        this.mPageCountName = pageCountParamsName;
        this.mPageCount = pageCount;
        setAdapter(adapter, dataGet);
    }


    public void runRefresh() {
        if (!getRefreshLayout().isRefreshing()) {

            getRefreshLayout().setRefreshing(true);
            super.onRefresh();
            onRefreshing();
        }

    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
    }

    private void init() {
        mPage = mDefaultPage;
        mPageCount = 30;
        setOnRefreshListener(this);
        setOnLoadListener(this);
    }

    @Override
    public void setStatusView(ItfStatusView statusView) {
        super.setStatusView(statusView);
        this.mStatusView = statusView;
    }

    private void setDataGet(HttpDataGet mDataGet) {
        this.mDataGet = mDataGet;
        this.mDataGet.register(this);
        this.mDataGet.updateParams(mPageCountName, mPageCount + "");
        this.mDataGet.updateParams(mPageName, mPageCount + "");
    }

    public void setLayoutManage(RecyclerView.LayoutManager manage) {
        getRecyclerView().setLayoutManager(manage);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        updatePage();
        this.mDataGet.execute();
    }

    private void updatePage() {
        this.mDataGet.updateParams(mPageName, mPage + "");
        this.mDataGet.updateParams(mPageCountName, mPageCount + "");
    }


    private int mTempCount = -1;

    @Override
    public void onRefreshing() {
        setLoadMoreEnable(true);
        if (mAdapter.getItemCount() != 0&&mAdapter.getItemCount() != 1) {
            mTempCount = mPageCount;
            mPageCount = mPageCount * mPage;

        }
        mPage = mDefaultPage;
        updatePage();
        this.mDataGet.execute();
    }

    @Override
    public void onCache(Object t) {

    }

    @Override
    public void onNext(Object t) {
        if (mAdapter == null) return;
        judgeMoreData(t);
        if (mPage == mDefaultPage) {
            String path = mAdapter.getItemDataPath();
            int size = ObjectValueUtil.getInstance().getCollectionSize(t, path);
            if (size == 0) {
                mStatusView.showNoData();
            }
            if (mTempCount != -1 && mPageCount != 0) {
                mPage = mPageCount / mTempCount;
                mPageCount = mTempCount;

                mTempCount = -1;
            }
            mAdapter.refresh(t);
        } else {
            mAdapter.next(t);
        }
    }

    private void judgeMoreData(Object t) {
        String path = mAdapter.getItemDataPath();
        if (TextUtils.isEmpty(path)) return;
        int size = ObjectValueUtil.getInstance().getCollectionSize(t, path);
        Log.i("CountSize", "" + size);
        if (size < mPageCount) {
            setLoadMoreEnable(false);
            onNoMore(mTextMore == null ? "" : mTextMore);
        } else {
            setLoadMoreEnable(true);
        }
    }

    private String mTextMore = "没有更多了";

    public void setMoreText(String text) {
        this.mTextMore = text;
    }

    private boolean mViewLoading;

    public void setViewLoading(boolean mViewLoading) {
        this.mViewLoading = mViewLoading;
    }

    @Override
    public void onBegin() {
        if (mStatusView != null && mAdapter.getItemCount() == 0 && mViewLoading) {
            mStatusView.showLoading();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(int code, String msg) {
        mPage--;
        if (mOnDataLoadingChange != null) {
            mOnDataLoadingChange.loadDataError(code, msg);
        }
        if (mPage < mDefaultPage) {
            mPage = mDefaultPage;
        }
        if (mStatusView != null) {
            mStatusView.showError();
            if (mAdapter.getItemCount() == 0) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onComplete() {
        complete();
        if (mOnCompleteListener != null) {
            mOnCompleteListener.onComplete();
        }
    }

    private OnDataLoadingChange mOnDataLoadingChange;

    public void setOnDataLoadingChange(OnDataLoadingChange mOnDataLoadingChange) {
        this.mOnDataLoadingChange = mOnDataLoadingChange;
    }

    public interface OnDataLoadingChange {
        void loadDataError(int code, String msg);
    }

    private OnCompleteListener mOnCompleteListener;

    public void setOnCompleteListener(OnCompleteListener mOnCompleteListener) {
        this.mOnCompleteListener = mOnCompleteListener;
    }

    public interface OnCompleteListener {
        void onComplete();
    }

}
