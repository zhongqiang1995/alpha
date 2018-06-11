package com.azl.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zhlib.R;

/**
 * Created by zhong on 2017/5/11.
 */

public class FooterView extends FrameLayout {

    private View mContentView, itemNoMore;
    private ProgressBar mProgressView;
    private TextView mTvNoMore;

    public FooterView(@NonNull Context context) {
        this(context, null);
    }

    public FooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mContentView = LayoutInflater.from(context).inflate(R.layout.item_swipe_recycler_layout, this);
        mProgressView = (ProgressBar) mContentView.findViewById(R.id.progress);
        itemNoMore = mContentView.findViewById(R.id.item_no_more);
        mTvNoMore = (TextView) mContentView.findViewById(R.id.tvNoMore);
    }


    private int status = STATUS_PROGRESS;
    private static final int STATUS_PROGRESS = 1;

    private static final int STATUS_NO_MORE = 2;

    public void showProgress() {
        mProgressView.setVisibility(View.VISIBLE);
        itemNoMore.setVisibility(View.GONE);
        status = STATUS_PROGRESS;
    }

    public void showNoMore(String text) {
        mProgressView.setVisibility(View.GONE);
        itemNoMore.setVisibility(View.VISIBLE);
        mTvNoMore.setText(text == null ? "" : text);
        status = STATUS_NO_MORE;
    }

    public void hideSeparateProgress() {
        mProgressView.setVisibility(View.GONE);
    }

    public void showSeparateProgress() {
        if (status == STATUS_PROGRESS) {
            mProgressView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isShowProgress() {
        return status == STATUS_PROGRESS;
    }

    public boolean isShowMore() {
        return status == STATUS_NO_MORE;
    }


    public boolean isShow(){
        return View.VISIBLE==getVisibility();
    }
}
