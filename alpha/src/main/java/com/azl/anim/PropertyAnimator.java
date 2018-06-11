package com.azl.anim;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;

/**
 * Created by zhong on 2017/2/10.
 */

public class PropertyAnimator implements AnimatorRun {

    private static PropertyAnimator mInstance;

    protected static PropertyAnimator newInstance() {
        if (mInstance == null) {
            synchronized (PropertyAnimator.class) {
                if (mInstance == null) {
                    mInstance = new PropertyAnimator();
                }
            }
        }
        return mInstance;
    }

    private PropertyAnimator() {
    }

    @Override
    public AnimatorAction obj(View targetView, ValueAnimator objectAnimator) {
        AnimatorControlEntity entity = new AnimatorControlEntity(objectAnimator);
        return entity;
    }

    @Override
    public AnimatorAction moveToX(View targetView, float start, float end, long duration) {
        ObjectAnimator obj = ObjectAnimator.ofFloat(targetView, View.TRANSLATION_X, start, end);
        obj.setDuration(duration);
        AnimatorControlEntity entity = new AnimatorControlEntity(obj);
        return entity;
    }

    @Override
    public AnimatorAction moveToY(View targetView, float start, float end, long duration) {
        ObjectAnimator obj = ObjectAnimator.ofFloat(targetView, View.TRANSLATION_Y, start, end);
        obj.setDuration(duration);
        AnimatorControlEntity entity = new AnimatorControlEntity(obj);
        return entity;
    }

    @Override
    public AnimatorAction moveToAbsX(View targetView, float start, float end, long duration) {
        ObjectAnimator obj = ObjectAnimator.ofFloat(targetView, View.X, start, end);
        obj.setDuration(duration);
        AnimatorControlEntity entity = new AnimatorControlEntity(obj);
        return entity;
    }

    @Override
    public AnimatorAction moveToAbsY(View targetView, float start, float end, long duration) {
        ObjectAnimator obj = ObjectAnimator.ofFloat(targetView, View.Y, start, end);
        obj.setDuration(duration);
        AnimatorControlEntity entity = new AnimatorControlEntity(obj);
        return entity;
    }

    @Override
    public AnimatorAction scaleX(View targetView, float startF, float endF, long duration) {
        ObjectAnimator obj = ObjectAnimator.ofFloat(targetView, View.SCALE_X, startF, endF);
        obj.setDuration(duration);
        AnimatorControlEntity entity = new AnimatorControlEntity(obj);
        return entity;
    }

    @Override
    public AnimatorAction scaleY(View targetView, float startF, float endF, long duration) {
        ObjectAnimator obj = ObjectAnimator.ofFloat(targetView, View.SCALE_Y, startF, endF);
        obj.setDuration(duration);
        AnimatorControlEntity entity = new AnimatorControlEntity(obj);
        return entity;
    }

    @Override
    public AnimatorAction alpha(View targetView, float startAlpha, float endAlpha, long duration) {
        ObjectAnimator obj = ObjectAnimator.ofFloat(targetView, View.ALPHA, startAlpha, endAlpha);
        obj.setDuration(duration);
        AnimatorControlEntity entity = new AnimatorControlEntity(obj);

        return entity;
    }

    @Override
    public AnimatorAction cubicT(View targetView, int fx1, int fy1, int fx2, int fy2, int fx3, int fy3, int fx4, int fy4, long duration) {
        Cubic cubic = new Cubic(duration, fx1, fy1, fx2, fy2, fx3, fy3, fx4, fy4);
        ObjectAnimator anim = ObjectAnimator.ofObject(new TargetAnimatorView(targetView), "path", new CubicTypeEvaluator(), cubic);
        anim.setDuration(duration);
        AnimatorControlEntity entity = new AnimatorControlEntity(anim);
        return entity;
    }

    class TargetAnimatorView {
        TargetAnimatorView(View targetView) {
            this.targetView = targetView;
        }

        private View targetView;

        public void setPath(Cubic cubic) {
            targetView.setTranslationX(cubic.newX);
            targetView.setTranslationY(cubic.newY);
        }

    }


    class CubicTypeEvaluator implements TypeEvaluator<Cubic> {

        @Override
        public Cubic evaluate(float currentTime, Cubic startValue, Cubic cubic) {
            float duration = (currentTime * cubic.duration) / cubic.duration;
            double x = cubic.fx1 * Math.pow((1 - duration), 3) + 3 * cubic.fx2 * duration * Math.pow((1 - duration), 2) + 3 * cubic.fx3 * Math.pow(duration, 2) * (1 - duration) + cubic.fx4 * Math.pow(duration, 3);
            double y = cubic.fy1 * Math.pow((1 - duration), 3) + 3 * cubic.fy2 * duration * Math.pow((1 - duration), 2) + 3 * cubic.fy3 * Math.pow(duration, 2) * (1 - duration) + cubic.fy4 * Math.pow(duration, 3);
            cubic.newX = (int) x;
            cubic.newY = (int) y;

            Log.e("newX", "newX" + cubic.newX);
            Log.e("newY", "newY" + cubic.newY);
            return cubic;
        }
    }

    @Override
    public AnimatorAction delay(long delayTime) {
        AnimatorControlEntity entity = new AnimatorControlEntity(AnimatorAction.ActionType.DELAY, delayTime);
        return entity;
    }

    class Cubic {
        private long duration;
        int fx1;
        int fy1;
        int fx2;
        int fy2;
        int fx3;
        int fy3;
        int fx4;
        int fy4;
        private int newX;
        private int newY;

        public Cubic(long duration, int fx1, int fy1, int fx2, int fy2, int fx3, int fy3, int fx4, int fy4) {
            this.duration = duration;
            this.fx1 = fx1;
            this.fy1 = fy1;
            this.fx2 = fx2;
            this.fy2 = fy2;
            this.fx3 = fx3;
            this.fy3 = fy3;
            this.fx4 = fx4;
            this.fy4 = fy4;
        }
    }
}
