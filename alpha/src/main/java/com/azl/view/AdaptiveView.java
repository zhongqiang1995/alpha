package com.azl.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhlib.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhong on 2017/5/25.
 */

public class AdaptiveView extends ViewGroup {

    private int mHorizontalInterval;
    private int mVerticalInterval;

    public AdaptiveView(Context context) {
        super(context);
    }

    public AdaptiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AdaptiveView);
        mHorizontalInterval = (int) array.getDimension(R.styleable.AdaptiveView_ma_horizontal_interval, 0);
        mVerticalInterval = (int) array.getDimension(R.styleable.AdaptiveView_ma_vertical_interval, 0);


    }

    Map<View, ViewIndex> mViewLayoutMap = new HashMap<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewLayoutMap.clear();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int x = 0;
        int y = 0;
        int maxHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            int childMarginLeft = mHorizontalInterval;
            int childMarginRight = mHorizontalInterval;
            int childMarginTop = mVerticalInterval;
            int childMarginBottom = mVerticalInterval;

            if (view.getVisibility() != View.VISIBLE) continue;
            measureChild(view, MeasureSpec.makeMeasureSpec(widthSize - childMarginLeft - childMarginRight, MeasureSpec.AT_MOST), MeasureSpec.UNSPECIFIED);
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();


            if(y==0){
                childMarginTop=0;
            }
            if (childMarginLeft + childWidth + childMarginRight + x >= widthSize) {
                if (x == 0) {
                    measureChild(view, MeasureSpec.makeMeasureSpec(widthSize , MeasureSpec.AT_MOST), MeasureSpec.UNSPECIFIED);
                    ViewIndex index = new ViewIndex(x , y + childMarginTop, x + childWidth +childMarginLeft+ childMarginRight, y + childHeight + childMarginTop);
                    y += childHeight + childMarginBottom + childMarginTop;
                    mViewLayoutMap.put(view, index);
                } else {
                    y += maxHeight;
                    maxHeight = 0;
                    x = 0;
                    i--;
                }
            } else {
                if (x == 0) {
                    childMarginLeft = 0;
                }
                ViewIndex index = new ViewIndex(x + childMarginLeft, y + childMarginTop, x + childWidth + (childMarginLeft==0&&mHorizontalInterval!=0?childMarginLeft:childMarginRight), y + childHeight + childMarginTop);
                maxHeight = maxHeight > childHeight + childMarginTop + childMarginBottom ? maxHeight : childHeight + childMarginTop + childMarginBottom;
                x += childWidth + (childMarginLeft == 0 ? mHorizontalInterval : childMarginLeft) + (childMarginLeft==0&&mHorizontalInterval!=0?childMarginLeft:childMarginRight);
                mViewLayoutMap.put(view, index);
            }
        }
        y += maxHeight;
        setMeasuredDimension(widthSize, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            ViewIndex index = mViewLayoutMap.get(view);
            view.layout(index.left, index.top, index.right, index.bottom);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    class ViewIndex {
        public ViewIndex(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        int left;
        int top;
        int right;
        int bottom;
    }
}
