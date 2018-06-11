package com.azl.anim;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;

/**
 * Created by zhong on 2017/2/9.
 */

public class AnimatorControlEntity extends AnimatorAction {


    protected AnimatorControlEntity(Animator animator) {
        this(animator, ActionType.PLAY);
    }

    protected AnimatorControlEntity(Animator animator, ActionType type) {
        this(animator, type, 0);
    }

    protected AnimatorControlEntity(Animator animator, ActionType type, long delay) {
        if (animator == null) throw new NullPointerException();
        this.mAnimator = animator;
        this.mActionType = type;
        this.mDelay = delay;
    }

    public AnimatorControlEntity(ActionType type, long delayTime) {
        this.mDelay = delayTime;
        this.mActionType = type;
    }


    private void addToList(AnimatorAction animator, ActionType type) {
        animator.mActionType = type;
        addToList(animator);
    }

    private void addToList(AnimatorAction animator) {
        if (mAnimatorList == null) {
            mAnimatorList = new ArrayList<>();
        }
        mAnimatorList.add(animator);
    }

    @Override
    public AnimatorAction with(AnimatorAction animator) {
        addToList(animator, ActionType.WITH);
        return this;
    }

    @Override
    public AnimatorAction after(AnimatorAction animator) {
        addToList(animator, ActionType.AFTER);
        return this;
    }

    @Override
    public AnimatorAction after(long delay) {
        AnimatorControlEntity entity = new AnimatorControlEntity(ActionType.DELAY, delay);
        addToList(entity);
        return this;
    }

    @Override
    public AnimatorAction before(AnimatorAction animator) {
        addToList(animator, ActionType.BEFORE);
        return this;
    }

    private Animator mStartAnimator;

    @Override
    public void start() {
        if (mAnimatorList != null && !mAnimatorList.isEmpty()) {
            mStartAnimator = groupStart();
        } else {
            mStartAnimator = mAnimator;
        }
        mStartAnimator.start();
    }




    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pause() {
        if (mStartAnimator == null) return;
        mStartAnimator.pause();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void resume() {
        if (mStartAnimator == null) return;
        mStartAnimator.resume();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean isPaused() {
        return mStartAnimator == null ? false : mStartAnimator.isPaused();
    }

    public void cancel() {
        if (mStartAnimator == null) return;
        mStartAnimator.cancel();

    }

    public void end() {
        if (mStartAnimator == null) return;
        mStartAnimator.end();
    }

    public boolean isRunning() {
        return mStartAnimator == null ? false : mStartAnimator.isRunning();
    }

    public boolean isStarted() {

        return mStartAnimator == null ? false : mStartAnimator.isStarted();
    }

    public void setAnimatorListener(AnimatorListener listener) {
        mAnimator.addListener(listener);
    }

    public void removeListenerAnimator(AnimatorListener listener) {
        mAnimator.removeListener(listener);
    }

    public void removeAllListener() {
        mAnimator.removeAllListeners();
    }

    public void setInterpolator(TimeInterpolator mInterpolator) {
        mAnimator.setInterpolator(mInterpolator);
    }
}
