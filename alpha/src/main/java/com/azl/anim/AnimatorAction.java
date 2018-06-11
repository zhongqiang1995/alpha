package com.azl.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;

import java.util.List;

/**
 * Created by zhong on 2017/2/9.
 */

public abstract class AnimatorAction {

    protected ActionType mActionType;
    protected Animator mAnimator;
    protected List<AnimatorAction> mAnimatorList;
    protected long mDelay;
    protected Animator.AnimatorListener mListener;
    protected TimeInterpolator mInterpolator;

    public abstract AnimatorAction with(AnimatorAction animator);

    public abstract AnimatorAction after(AnimatorAction animator);


    public abstract AnimatorAction after(long delay);

    public abstract AnimatorAction before(AnimatorAction animator);

    public abstract void start();

    public abstract void pause();

    public abstract void resume();

    public abstract boolean isPaused();

    public abstract void cancel();

    public abstract void end();

    public abstract boolean isRunning();

    public abstract boolean isStarted();


    public abstract void setAnimatorListener(Animator.AnimatorListener listener);

    public abstract void removeListenerAnimator(Animator.AnimatorListener listener);

    public abstract void removeAllListener();

    public abstract void setInterpolator(TimeInterpolator mInterpolator);


    private AnimatorSet mAnimatorSet;

    public void setAnimatorSet(AnimatorSet mAnimatorSet) {
        this.mAnimatorSet = mAnimatorSet;
    }

    public AnimatorSet getAnimatorSet() {
        if (mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
        }
        return mAnimatorSet;
    }

    public Animator groupStart() {
        AnimatorSet set = getAnimatorSet();
        AnimatorSet.Builder build = set.play(mAnimator);
        if (mAnimatorList != null) {

            for (AnimatorAction entity : mAnimatorList) {
                switch (entity.mActionType) {
                    case WITH:
                        build.with(entity.mAnimator);

                        break;
                    case BEFORE:
                        build.after(entity.mAnimator);
                        break;
                    case AFTER:
                        build.before(entity.mAnimator);
                        break;
                    case DELAY:
                        build.after(entity.mDelay);
                        break;
                    case PLAY:
                        break;
                }
                entity.setAnimatorSet(set);
                entity.groupStart();
            }
        }
        return set;
    }

    enum ActionType {
        WITH, AFTER, BEFORE, PLAY, DELAY
    }
}
