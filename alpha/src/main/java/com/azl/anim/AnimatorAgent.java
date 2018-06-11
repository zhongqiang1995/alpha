package com.azl.anim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by zhong on 2017/2/10.
 */

public class AnimatorAgent {
    private AnimatorRun mRun;

    public static AnimatorAgent factor(AnimatorRun run) {
        return new AnimatorAgent(run);
    }

    private AnimatorAgent(AnimatorRun run) {
        this.mRun = run;
    }


    public AnimatorAction obj(View targetView, ValueAnimator obj) {
        return mRun.obj(targetView, obj);
    }


    public AnimatorAction moveToAbsX(View targetView, float start, float end, long duration) {
        return mRun.moveToAbsX(targetView, start, end, duration);
    }

    public AnimatorAction moveToAbsY(View targetView, float start, float end, long duration) {
        return mRun.moveToAbsY(targetView, start, end, duration);
    }

    public AnimatorAction moveToX(View targetView, float start, float end, long duration) {
        return mRun.moveToX(targetView, start, end, duration);
    }

    public AnimatorAction moveToY(View targetView, float start, float end, long duration) {
        return mRun.moveToY(targetView, start, end, duration);
    }

    public AnimatorAction scaleX(View targetView, float startF, float endF, long duration) {
        return mRun.scaleX(targetView, startF, endF, duration);
    }

    public AnimatorAction scaleY(View targetView, float startF, float endF, long duration) {
        return mRun.scaleY(targetView, startF, endF, duration);
    }

    public AnimatorAction alpha(View targetView, float startAlpha, float endAlpha, long duration) {
        return mRun.alpha(targetView, startAlpha, endAlpha, duration);
    }

    public AnimatorAction cubicT(View targetView, int fx1, int fy1, int fx2, int fy2, int fx3, int fy3, int fx4, int fy4, long duration) {
        return mRun.cubicT(targetView, fx1, fy1, fx2, fy2, fx3, fy3, fx4, fy4, duration);
    }

    public AnimatorAction delay(long delayTime) {
        return mRun.delay(delayTime);
    }
}
