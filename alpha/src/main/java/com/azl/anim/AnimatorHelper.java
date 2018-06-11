package com.azl.anim;

/**
 * Created by zhong on 2017/2/10.
 */

public class AnimatorHelper {
    public static AnimatorAgent build() {
        return AnimatorAgent.factor(PropertyAnimator.newInstance());
    }
}
