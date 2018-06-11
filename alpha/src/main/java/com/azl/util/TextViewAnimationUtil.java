package com.azl.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.widget.TextView;

/**
 * Created by zhong on 2017/10/24.
 */

public class TextViewAnimationUtil {

    public static void increasingInInt(final TextView tv, int start, int end, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);

        valueAnimator.setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tv.setText(String.valueOf(value));
            }
        });
        valueAnimator.start();
    }

    public static void increasingInFloat(final TextView tv, float start, float end, int duration) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);

        valueAnimator.setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                tv.setText(String.format("%.2f", value));
            }
        });
        valueAnimator.start();
    }

}
