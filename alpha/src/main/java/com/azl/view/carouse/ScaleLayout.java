package com.azl.view.carouse;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.azl.util.ScreenUtil;

/**
 * Created by zhong on 2017/8/17.
 */

public class ScaleLayout extends FrameLayout {
    int padding = ScreenUtil.dip2px(getContext(), 10);

    public ScaleLayout(@NonNull Context context) {
        this(context, null);
    }

    public ScaleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.TRANSPARENT);
    }


    public void scaleRight(float f) {

        if (f > 1) {
            f = 1;
        }
        if (f == 1) {
            resetScale();
            return;
        }
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int paddingRight = width - ((int) (getWidth() * f));
        int vp = (height) - (int) (getHeight() * f);
        int paddingTopBottom = vp / 2;
        setPadding(0, paddingTopBottom, paddingRight, paddingTopBottom);


    }


    public void scaleLeft(float f) {
        if (f > 1) {
            f = 1;
        }
        if (f == 1) {
            resetScale();
            return;
        }
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int paddingRight = width - ((int) (getWidth() * f));
        int vp = (height) - (int) (getHeight() * f);
        int paddingTopBottom = vp / 2;
        setPadding(paddingRight, paddingTopBottom, 0, paddingTopBottom);
    }

    public void resetScale() {
        Log.e("resetScale", "resetScale");
        setPadding(0, 0, 0, 0);
        Log.e("resetScale:","width:"+getWidth());
        Log.e("resetScale:","width:"+getHeight());
    }

    public void setPaddingLeft(int paddingLeft) {
        setPadding(paddingLeft, 0, 0, 0);
    }

    public void setPaddingRight(int paddingRight) {
        setPadding(0, 0, paddingRight, 0);
    }
}
