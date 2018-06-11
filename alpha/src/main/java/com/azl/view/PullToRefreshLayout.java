package com.azl.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhlib.R;


/**
 * Created by zhongq on 2016/11/11.
 */

public class PullToRefreshLayout extends ActionExecutionView {
    private static final String TAG = "PullToRefreshLayout";
    private TextView topTv;
    private TextView bottomTv;
    private ImageView img_dropDown, img_bottom_loading, img_top_loading;
    private ObjectAnimator mTopRotateAnimation, mBottomRotateAnimation, startDownAnimator, dropDownHalfwayAnimator;

    public PullToRefreshLayout(Context context) {
        super(context);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View prepareBottomView() {
        View view = View.inflate(getContext(), R.layout.item_pulltorefreshlayout_down, null);
        bottomTv = (TextView) view.findViewById(R.id.tv_pullTo);
        img_bottom_loading = (ImageView) view.findViewById(R.id.img_loading);
        mBottomRotateAnimation = getRotationAnimator0to360(img_bottom_loading);
        bottomTv.setText("上拉加载");
        img_bottom_loading.setVisibility(View.GONE);
        return view;
    }

    @Override
    protected View prepareTopView() {
        View view = View.inflate(getContext(), R.layout.item_pulltorefreshlayout_top, null);
        img_top_loading = (ImageView) view.findViewById(R.id.img_drop_loading);
        mTopRotateAnimation = getRotationAnimator0to360(img_top_loading);
        topTv = (TextView) view.findViewById(R.id.tv_dropDown);
        img_dropDown = (ImageView) view.findViewById(R.id.img_drop);
        topTv.setText("下拉刷新");
        img_top_loading.setVisibility(View.GONE);
        return view;
    }


    private ObjectAnimator getRotationAnimator0to360(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 359);
        objectAnimator.setDuration(1000);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setInterpolator(new LinearInterpolator());
        return objectAnimator;
    }

    @Override
    protected void startDropDown(View topView) {
        if (dropDownHalfwayAnimator != null) {
            dropDownHalfwayAnimator.cancel();
            dropDownHalfwayAnimator = null;
        }
        if (startDownAnimator == null) {

            startDownAnimator = ObjectAnimator.ofFloat(img_dropDown, "rotation", img_dropDown.getRotation(), 0);
            startDownAnimator.setDuration(200);
            startDownAnimator.start();
            topTv.setText("下拉刷新");
        }
    }

    @Override
    protected void dropDownHalfway(View topView) {
        if (startDownAnimator != null) {
            startDownAnimator.cancel();
            startDownAnimator = null;
        }
        if (dropDownHalfwayAnimator == null) {
            dropDownHalfwayAnimator = ObjectAnimator.ofFloat(img_dropDown, "rotation", img_dropDown.getRotation(), 180);
            dropDownHalfwayAnimator.setDuration(200);
            dropDownHalfwayAnimator.start();
            topTv.setText("放开刷新");
        }
    }

    @Override
    protected void completeDropDown(View topView) {
        img_dropDown.setVisibility(View.GONE);
        img_top_loading.setVisibility(View.VISIBLE);
        mTopRotateAnimation.start();
        topTv.setText("正在刷新");
    }

    @Override
    protected void startPullUp(View topView) {
        if (!bottomTv.getText().equals("上拉加载")) {
            bottomTv.setText("上拉加载");
        }
    }


    @Override
    protected void pullUpHalfway(View topView) {
        if (!bottomTv.getText().equals("放开加载")) {
            bottomTv.setText("放开加载");
        }
    }


    @Override
    protected void completePullUp(View bottomView) {
        bottomTv.setText("正在加载");
        img_bottom_loading.setVisibility(View.VISIBLE);
        mBottomRotateAnimation.start();
    }

    @Override
    protected void resetView(View topView, View bottomView) {
        Log.e(TAG, "resetView");
        if (mTopRotateAnimation.isRunning()) {
            mTopRotateAnimation.cancel();
        }
        if (mBottomRotateAnimation.isRunning()) {
            mBottomRotateAnimation.cancel();
        }
        topTv.setText("下拉刷新");
        bottomTv.setText("上拉加载");
        img_top_loading.setVisibility(View.GONE);
        img_dropDown.setVisibility(View.VISIBLE);
        img_bottom_loading.setVisibility(View.GONE);
        img_dropDown.setRotation(0);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(TAG, "onDetachedFromWindow");
        if (mTopRotateAnimation != null) {
            mTopRotateAnimation.cancel();
            mTopRotateAnimation = null;
        }
        if (mBottomRotateAnimation != null) {
            mBottomRotateAnimation.cancel();
            mBottomRotateAnimation = null;
        }
        if (startDownAnimator != null) {
            startDownAnimator.cancel();
            startDownAnimator = null;
        }
        if (dropDownHalfwayAnimator != null) {
            dropDownHalfwayAnimator.cancel();
            dropDownHalfwayAnimator = null;
        }
    }
}
