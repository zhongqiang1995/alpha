package com.azl.view.grid.select;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.zhlib.R;

/**
 * Created by zhong on 2018/3/7.
 */

public class GridSelectActionView extends ViewGroup implements View.OnClickListener {

    private boolean mIsOffsetDeleteView;
    private ImageView mDeleteView;
    private OnClickDeleteListener mClickDeleteListener;

    public GridSelectActionView(@NonNull Context context) {
        this(context, null);
    }

    public GridSelectActionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray obs = context.obtainStyledAttributes(attrs, R.styleable.GridSelectFileView);
        mIsOffsetDeleteView = obs.getBoolean(R.styleable.GridSelectFileView_gsf_is_offset_delete, true);
        obs.recycle();
        initView();

    }

    private void initView() {
        setupDeleteView();
    }

    private void setupDeleteView() {
        mDeleteView = new ImageView(getContext());
        VectorDrawableCompat drawableCompat=VectorDrawableCompat.create(getResources(),R.drawable.alpha_img_delete_file,null);
        mDeleteView.setImageDrawable(drawableCompat);
        mDeleteView.setOnClickListener(this);
        if (mDeleteView.getParent() == null) {
            addView(mDeleteView, getChildCount());
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int deleteWidth;
        if (size == 0) {
            deleteWidth = size;
        } else {
            deleteWidth = size / 4;
        }

        int childWidth = size - (deleteWidth / 2);
        measureChildren(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY));
        measureChild(mDeleteView, MeasureSpec.makeMeasureSpec(deleteWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(deleteWidth, MeasureSpec.EXACTLY));

        setMeasuredDimension(size, size);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int size = getMeasuredWidth();
        int deleteWidth;
        if (size == 0) {
            deleteWidth = size;
        } else {
            deleteWidth = size / 4;
        }
        int margin = 0;
        if (deleteWidth != 0) {
            margin = deleteWidth / 2;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView != mDeleteView) {

                int childTop = 0;
                int childRight = 0;
                if (mIsOffsetDeleteView) {
                    childTop = margin;
                    childRight = getMeasuredWidth()-margin;
                } else {
                    childTop = 0;
                    childRight = getMeasuredWidth();
                }
                childView.layout(0, childTop, childRight, getMeasuredHeight());
            } else {
                childView.layout(getMeasuredWidth() - deleteWidth, 0, getMeasuredWidth(), deleteWidth);
            }
        }
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View view = getChildAt(i);
            if (view == mDeleteView) {
                if (i != getChildCount() - 1) {
                    removeViewAt(i);
                    addView(mDeleteView, getChildCount());
                }
            }
        }

    }

    public void showDelete() {
        mDeleteView.setVisibility(View.VISIBLE);
    }

    public void hideDelete() {
        mDeleteView.setVisibility(View.GONE);
    }

    public void setClickDeleteListener(OnClickDeleteListener mClickDeleteListener) {
        this.mClickDeleteListener = mClickDeleteListener;
    }

    @Override
    public void onClick(View v) {
        if (mClickDeleteListener != null) {
            mClickDeleteListener.onDelete();
        }
    }


    public interface OnClickDeleteListener {
        void onDelete();
    }
}
