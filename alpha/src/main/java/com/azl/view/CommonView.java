package com.azl.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.example.zhlib.R;
import com.azl.view.helper.itf.ItfStatusActionSwitch;
import com.azl.view.helper.itf.ItfStatusView;

/**
 * Created by zhong on 2017/5/19.
 */

public class CommonView extends FrameLayout implements ItfStatusActionSwitch {

    private static final String TAG_ERROR = "error";
    private static final String TAG_NOT_NET = "noNet";
    private static final String TAG_NO_DATA = "noData";
    private static final String TAG_LOADING = "loading";
    private static final String TAG_CONTENT = "content";
    private ItfStatusView mStatusView;
    private View mContentView;

    public CommonView(@NonNull Context context) {
        this(context, null);
    }

    public CommonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommonView);
        accessXmlStatusData(array);
        init();
        accessXmlDrawableData(array);
        isXmlSetAction();
        array.recycle();
    }

    private void accessXmlDrawableData(TypedArray array) {
        int errorLayoutId = array.getResourceId(R.styleable.CommonView_mc_error_layout, -1);
        int loadingLayoutId = array.getResourceId(R.styleable.CommonView_mc_loading_layout, -1);
        int noDataLayoutId = array.getResourceId(R.styleable.CommonView_mc_noData_layout, -1);
        int noNetLayoutId = array.getResourceId(R.styleable.CommonView_mc_noNet_layout, -1);
        if (errorLayoutId != -1) {
            View errorLayout = LayoutInflater.from(getContext()).inflate(errorLayoutId, null, false);
            replaceErrorView(errorLayout);
        }
        if (loadingLayoutId != -1) {
            View loadingLayout = LayoutInflater.from(getContext()).inflate(loadingLayoutId, null, false);
            replaceLoadingView(loadingLayout);
        }
        if (noDataLayoutId != -1) {
            View noDataLayout = LayoutInflater.from(getContext()).inflate(noDataLayoutId, null, false);
            replaceNoDataView(noDataLayout);
        }
        if (noNetLayoutId != -1) {
            View noNetLayout = LayoutInflater.from(getContext()).inflate(noNetLayoutId, null, false);
            replaceNoNetView(noNetLayout);
        }
    }

    private void isXmlSetAction() {
        if (!TextUtils.isEmpty(mIsHandle)) {
            mStatusView.setVisibility(View.VISIBLE);
        }
    }

    private void accessXmlStatusData(TypedArray array) {
        int status = array.getInt(R.styleable.CommonView_mc_status, -1);
        if (status != -1) {
            switch (status) {
                case 1:
                    showError();
                    break;
                case 2:
                    showLoading();
                    break;
                case 3:
                    showNoData();
                    break;
                case 4:
                    showNoNet();
                    break;
                case 5:
                    showContent();
                    break;
            }
        }
    }

    public void init() {
        StatusView statusView = getStatusView();
        statusView.setVisibility(View.GONE);
        statusView.setAlpha(0);
        setStatusView(statusView);
    }

    protected StatusView getStatusView() {
        return new StatusView(getContext());
    }

    public void setStatusView(ItfStatusView view) {
        boolean isShow = false;
        if (mStatusView != null) {
            removeView(mStatusView);
            isShow = mStatusView.getVisibility() == View.VISIBLE;
        }
        mStatusView = view;
        mStatusView.setAlpha(isShow ? 1 : 0);
        mStatusView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        addView(mStatusView);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int childCount = getChildCount();
        if (childCount > 2) {
            throw new RuntimeException("Child cannot exceed a");
        }
        mContentView = null;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view != mStatusView) {
                mContentView = view;
            }
        }
        mIsLayout = true;
        if (!TextUtils.isEmpty(mIsHandle)) {
            mContentView.setVisibility(View.GONE);
            if (mIsHandle.equals(TAG_ERROR)) {
                showError(false);
            } else if (mIsHandle.equals(TAG_NOT_NET)) {
                showNoNet(false);
            } else if (mIsHandle.equals(TAG_NO_DATA)) {
                showNoData(false);
            } else if (mIsHandle.equals(TAG_LOADING)) {
                showLoading(false);
            } else if (mIsHandle.equals(TAG_CONTENT)) {
                showContent(false);
            }
            mIsHandle = null;

        }
    }


    private String mIsHandle;
    private boolean mIsLayout;

    public void showError(boolean isAnimation) {
        handleText(TAG_ERROR);
        if (mStatusView == null) return;
        mStatusView.showError();
        showStatus(isAnimation);

    }

    @Override
    public void showError() {
        showError(false);
    }


    @Override
    public void showNoNet() {
        showNoNet(true);
    }

    public void showNoNet(boolean isAnimation) {
        handleText(TAG_NOT_NET);
        if (mStatusView == null) return;
        mStatusView.showNoNet();
        showStatus(isAnimation);
    }

    @Override
    public void showNoData() {
        showNoData(true);
    }

    public void showNoData(boolean isAnimation) {
        handleText(TAG_NO_DATA);
        if (mStatusView == null) return;
        mStatusView.showNoData();
        showStatus(isAnimation);
    }

    @Override
    public void showLoading() {
        showLoading(true);
    }

    @Override
    public void replaceErrorView(View view) {
        mStatusView.replaceErrorView(view);
    }

    @Override
    public void replaceLoadingView(View view) {
        mStatusView.replaceLoadingView(view);
    }

    @Override
    public void replaceNoDataView(View view) {
        mStatusView.replaceNoDataView(view);
    }

    @Override
    public void replaceNoNetView(View view) {
        mStatusView.replaceNoNetView(view);
    }

    public void showLoading(boolean isAnimation) {
        handleText(TAG_LOADING);
        if (mStatusView == null) return;
        mStatusView.showLoading();
        showStatus(isAnimation);
    }

    private void showStatus(boolean isAnimator) {
        if (!mIsLayout) {
            return;
        }
        if (mStatusView.getVisibility() != View.VISIBLE) {
            mStatusView.setVisibility(View.VISIBLE);
        }
        if (isAnimator) {
            Animator showAnimator = getShowAnimator(mStatusView);
            Animator hideAnimator = null;
            if (mContentView != null) {
                hideAnimator = getHideAnimator(mContentView);
            }
            if (hideAnimator != null) {
                hideAnimator.addListener(new AnimatorEndAdapter(showAnimator, mContentView));
                hideAnimator.start();
            } else {
                showAnimator.start();
            }
        } else {
            mStatusView.setAlpha(1);
            mContentView.setVisibility(View.GONE);
            mContentView.setAlpha(0);
        }
    }

    private void handleText(String st) {
        if (!mIsLayout) {
            mIsHandle = st;
        }
    }

    public void showContent() {
        showContent(true);
    }

    public void showContent(boolean isAnimation) {
        handleText(TAG_CONTENT);
        if (!mIsLayout || mContentView == null || mContentView.getVisibility() == View.VISIBLE) {
            return;
        }
        if (isAnimation) {
            Animator hideAnimator = getHideAnimator(mStatusView);
            Animator showAnimator = null;
            if (mContentView != null) {
                mContentView.setVisibility(View.VISIBLE);
                showAnimator = getShowAnimator(mContentView);
            }
            hideAnimator.addListener(new AnimatorEndAdapter(showAnimator, mStatusView));
            hideAnimator.start();
        } else {
            mStatusView.setVisibility(View.GONE);
            mStatusView.setAlpha(0);
            mContentView.setVisibility(View.VISIBLE);
            mContentView.setAlpha(1);
        }
    }


    private Animator getShowAnimator(View view) {

        Animator showAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.animator_apha_0_to_1);
        showAnimator.setTarget(view);
        return showAnimator;
    }

    private Animator getHideAnimator(View view) {
        Animator hideAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.animator_apha_1_to_0);
        hideAnimator.setTarget(view);
        return hideAnimator;
    }

    class AnimatorEndAdapter extends AnimatorListenerAdapter {
        private Animator mAnimator;
        private View mView;

        public AnimatorEndAdapter(Animator animator, View view) {
            this.mAnimator = animator;
            this.mView = view;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mAnimator != null) {
                mAnimator.start();
            }
            if (mView != null) {
                mView.setVisibility(View.GONE);
            }
        }
    }
}
