package com.azl.anim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by zhong on 2017/2/9.
 */

public interface AnimatorRun {

    AnimatorAction obj(View targetView, ValueAnimator objectAnimator);

    AnimatorAction moveToX(View targetView, float start, float end, long duration);

    AnimatorAction moveToY(View targetView, float start, float end, long duration);

    AnimatorAction moveToAbsX(View targetView, float start, float end, long duration);

    AnimatorAction moveToAbsY(View targetView, float start, float end, long duration);

    AnimatorAction scaleX(View targetView, float startF, float endF, long duration);

    AnimatorAction scaleY(View targetView, float startF, float endF, long duration);

    AnimatorAction alpha(View targetView, float startAlpha, float endAlpha, long duration);

    AnimatorAction cubicT(View targetView, int fx1, int fy1, int fx2, int fy2, int fx3, int fy3, int fx4, int fy4, long duration);

    AnimatorAction delay(long delayTime);
}
