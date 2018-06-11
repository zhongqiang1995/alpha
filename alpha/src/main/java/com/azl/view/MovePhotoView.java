package com.azl.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

import com.azl.anim.AnimatorAction;
import com.azl.anim.AnimatorAgent;
import com.azl.anim.AnimatorHelper;
import com.azl.view.bean.MovePhotoBean;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by zhong on 2017/11/28.
 */

public class MovePhotoView extends SimpleDraweeView {


    private static final int ANIMATION_DURATION = 300;
    private MovePhotoBean mStartBean;
    private MovePhotoBean mEndBean;

    public MovePhotoView(Context context) {
        super(context);
    }

    public MovePhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public static int getX(View view) {
        int[] arr = new int[2];
        view.getLocationInWindow(arr);
        return arr[0];
    }

    public static int getY(View view) {
        int[] arr = new int[2];
        view.getLocationInWindow(arr);
        return arr[1];
    }

    public void setStartPosition(int x, int y, int width, int height, float radio) {
        mStartBean = new MovePhotoBean(x, y, width, height, radio);
    }

    public void setEndPosition(int x, int y, int width, int height, float radio) {
        mEndBean = new MovePhotoBean(x, y, width, height, radio);
    }


    public void start() {
        if (mStartBean == null || mEndBean == null) return;

        int startX = mStartBean.getX();
        int startY = mStartBean.getY();

        int startWidth = mStartBean.getWidth();
        int startHeight = mStartBean.getHeight();

        float startRadio = mStartBean.getRadio();


        int endX = mEndBean.getX();
        int endY = mEndBean.getY();

        int endWidth = mEndBean.getWidth();
        int endHeight = mEndBean.getHeight();

        float endRadio = mEndBean.getRadio();

        AnimatorAgent build = AnimatorHelper.build();

        ObjectAnimator widthObj = ObjectAnimator.ofInt(this, WIDTH, startWidth, endWidth);
        widthObj.setDuration(ANIMATION_DURATION);

        ObjectAnimator heightObj = ObjectAnimator.ofInt(this, HEIGHT, startHeight, endHeight);
        heightObj.setDuration(ANIMATION_DURATION);

        ValueAnimator radioAnimation = ValueAnimator.ofFloat(startRadio, endRadio);
        radioAnimation.addUpdateListener(new RadioValueAnimationListener());
        radioAnimation.setDuration(ANIMATION_DURATION);


        AnimatorAction radio = build.obj(this, radioAnimation);
        AnimatorAction width = build.obj(this, widthObj);
        AnimatorAction height = build.obj(this, heightObj);
        AnimatorAction moveX = build.moveToAbsX(this, startX, endX, ANIMATION_DURATION);
        AnimatorAction moveY = build.moveToAbsY(this, startY, endY, ANIMATION_DURATION);

        moveX.with(moveY.with(width.with(height.with(radio))));
        radio.setAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (getAnimationStatus() != null) {
                    getAnimationStatus().start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (getAnimationStatus() != null) {
                    getAnimationStatus().end();
                }
            }
        });
        moveX.start();

    }

    class RadioValueAnimationListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float r = (float) animation.getAnimatedValue();
            Log.e("onAnimationUpdate", r + "");
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(r);
            MovePhotoView.this.getHierarchy().setRoundingParams(roundingParams);

        }
    }

    Property<View, Integer> WIDTH = new IntProperty<View>("width") {

        @Override
        public Integer get(View object) {


            return object.getWidth();
        }

        @Override
        public void setValue(View object, int value) {
            ViewGroup.LayoutParams par = object.getLayoutParams();
            par.width = value;
            object.setLayoutParams(par);
        }
    };

    Property<View, Integer> HEIGHT = new IntProperty<View>("height") {

        @Override
        public Integer get(View object) {
            return object.getHeight();
        }

        @Override
        public void setValue(View object, int value) {
            ViewGroup.LayoutParams par = object.getLayoutParams();
            par.height = value;
            object.setLayoutParams(par);
        }
    };

    public void setAnimationStatus(AnimationStatus mAnimationStatus) {
        this.mAnimationStatus = mAnimationStatus;
    }

    public AnimationStatus getAnimationStatus() {
        return mAnimationStatus;
    }

    private AnimationStatus mAnimationStatus;

    public static class AnimationStatus {
        public void start() {
        }


        public void end() {
        }

    }


}
