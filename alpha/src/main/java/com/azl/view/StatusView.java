package com.azl.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhlib.R;
import com.azl.view.helper.itf.ItfStatusView;

/**
 * Created by zhong on 2017/5/11.
 */

public class StatusView extends ItfStatusView {
    private View mNoDataView, mErrorView, mNoNetView, mLoadingView;
    private ViewGroup mStatusGroupView;

    private TextView mTvError;
    private TextView mTvNoData;
    private TextView mTvNoNet;

    public StatusView(@NonNull Context context) {
        this(context, null);
    }

    public StatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    private void initView() {
        mStatusGroupView = (ViewGroup) LayoutInflater.from(getContext()).inflate(getStatusGroupViewId(), null);
        mNoDataView = mStatusGroupView.findViewById(getNoDataViewId());
        mErrorView = mStatusGroupView.findViewById(getErrorViewId());
        mNoNetView = mStatusGroupView.findViewById(getNoNetViewId());
        mLoadingView = mStatusGroupView.findViewById(getLoadingViewId());

        addView(mStatusGroupView);
    }

    protected int getStatusGroupViewId() {
        return R.layout.item_status_layout_ib;
    }

    protected int getNoDataViewId() {
        return R.id.viewNoData;
    }

    protected int getErrorViewId() {
        return R.id.viewNoError;
    }

    protected int getNoNetViewId() {
        return R.id.viewNoNet;
    }

    protected int getLoadingViewId() {
        return R.id.viewLoading;
    }

    public View getNoDataView() {
        return mNoDataView;
    }

    public View getErrorView() {
        return mErrorView;
    }

    public View getNoNetView() {
        return mNoNetView;
    }

    /**
     * 显示异常
     */
    public void showError() {

        mNoNetView.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 显示没有网络
     */
    public void showNoNet() {
        mNoNetView.setVisibility(View.VISIBLE);
        mNoDataView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 显示没有数据
     */
    public void showNoData() {
        mNoNetView.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    /**
     * 显示loading
     */
    public void showLoading() {
        mNoNetView.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void replaceErrorView(View view) {
        int isVisibility = mErrorView.getVisibility();
        mStatusGroupView.removeView(mErrorView);
        mErrorView = view;
        mErrorView.setVisibility(isVisibility);
        mStatusGroupView.addView(mErrorView);
    }

    @Override
    public void replaceLoadingView(View view) {
        int isVisibility = mLoadingView.getVisibility();
        mStatusGroupView.removeView(mLoadingView);
        mLoadingView = view;
        mLoadingView.setVisibility(isVisibility);
        mStatusGroupView.addView(mLoadingView);
    }

    @Override
    public void replaceNoDataView(View view) {
        int isVisibility = mNoDataView.getVisibility();
        mStatusGroupView.removeView(mNoDataView);
        mNoDataView = view;
        mNoDataView.setVisibility(isVisibility);
        mStatusGroupView.addView(mNoDataView);
    }

    @Override
    public void replaceNoNetView(View view) {
        int isVisibility = mNoNetView.getVisibility();
        mStatusGroupView.removeView(mNoNetView);
        mNoNetView = view;
        mNoNetView.setVisibility(isVisibility);
        mStatusGroupView.addView(mNoNetView);
    }

    public void setLoadingView(View view) {
        if (view == null) return;
        view.setVisibility(mLoadingView.getVisibility());
        removeView(mLoadingView);

        mLoadingView = view;
        addView(mLoadingView);
    }

    public void setNoDataView(View view) {
        if (view == null) return;
        view.setVisibility(mNoDataView.getVisibility());
        removeView(mNoDataView);
        mNoDataView = view;
        addView(mNoDataView);
    }

    public void setNoNetView(View view) {
        if (view == null) return;
        view.setVisibility(mNoNetView.getVisibility());
        removeView(mNoNetView);
        mNoNetView = view;
        addView(mNoNetView);
    }

    public void setErrorView(View view) {
        if (view == null) return;
        view.setVisibility(mErrorView.getVisibility());
        removeView(mErrorView);
        mErrorView = view;
        addView(mErrorView);
    }


}
