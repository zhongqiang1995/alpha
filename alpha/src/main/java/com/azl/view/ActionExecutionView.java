package com.azl.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by zhongq on 2016/11/8.
 */

public abstract class ActionExecutionView extends LinearLayout implements NestedScrollingParent {
    private static final String TAG = "ActionExecutionView";
    private NestedScrollingParentHelper parentHelper;
    /**
     * 记录是否禁止下拉
     */
    private boolean mBanDropDown;
    /**
     * 记录是否禁止上拉
     */
    private boolean mBanPullUp;

    public boolean getBanPullUp() {
        return mBanPullUp;
    }

    public boolean getBanDropDown() {
        return mBanDropDown;
    }

    public ActionExecutionView(Context context) {
        super(context);
        init();
    }

    private void init() {
        topView = prepareTopView();
        bottomView = prepareBottomView();
        if (topView != null) {
            addView(topView);
        } else {
            setBanPullUp(true);
        }
        if (bottomView != null) {
            addView(bottomView);
        } else {
            setBanDropDown(true);
        }
        parentHelper = new NestedScrollingParentHelper(this);
    }

    public View getBottomView() {
        return bottomView;
    }

    public View getTopView() {
        return topView;
    }

    public void setBanDropDown(boolean isBanDropDown) {
        this.mBanDropDown = isBanDropDown;
    }

    public void setBanPullUp(boolean isBanPullUp) {
        this.mBanPullUp = isBanPullUp;
    }

    public ActionExecutionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnScrollStatusListener(OnScrollStatusListener mScrollStatusListener) {
        this.mScrollStatusListener = mScrollStatusListener;
    }

    public void setOnScrollStatusChangeListener(OnScrollStatusChangeListener onScrollChangeListener) {
        this.mScrollChangeListener = onScrollChangeListener;
    }


    /**
     * 这个变量主要控制滑动头部显示和滑动尾部显示时的速率，最大值为1，最小值为0.1。
     * 此值越小，滑动越慢。
     */
    private float mScrollRate = 0.6f;

    public void setScrollRate(float scrollRate) {
        if (scrollRate <= 0f) {
            scrollRate = 0.1f;
        } else if (scrollRate > 1f) {
            scrollRate = 1f;
        }
        this.mScrollRate = scrollRate;
    }

    public float getScrollRate() {
        return mScrollRate;
    }

    /**
     * 还原上拉下拉状态
     */
    public void resetStatus() {
        restoreLocation();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop();
        int count = getChildCount();
        if (count == 0) {
            return;
        }
        if (count > 3) {
            throw new IllegalArgumentException("The view cannot be more than one");
        }
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView == topView) {
                childView.layout((getMeasuredWidth() / 2) - (topView.getMeasuredWidth() / 2),
                        -topView.getMeasuredHeight(),
                        ((getMeasuredWidth() / 2) - (topView.getMeasuredWidth() / 2)) + childView.getMeasuredWidth(),
                        childView.getMeasuredHeight());
            } else if (childView == bottomView) {
                childView.layout((getMeasuredWidth() / 2) - (bottomView.getMeasuredWidth() / 2),
                        getMeasuredHeight(),
                        ((getMeasuredWidth() / 2) - (bottomView.getMeasuredWidth() / 2)) + childView.getMeasuredWidth(),
                        getMeasuredHeight() + childView.getMeasuredHeight());
            } else {
                contentView = childView;

                childView.layout(0 + paddingLeft, 0 + paddingTop, getMeasuredWidth() - paddingRight, getMeasuredHeight() - paddingTop);
            }
        }
        isLayout = true;
        if (isD) {
            setCompleteDropDownStatus();
            isD = false;
        }
        if (isU) {
            setCompletePullUpStatus();
            isU = false;
        }
    }

    private boolean isLayout;
    private  View topView;
    private  View bottomView;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
//
        int measureWidth = 0;
        int measureHeight = 0;
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int childViewHeight = 0;
        int childViewWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            childViewHeight += childView.getMeasuredHeight();
            childViewWidth = childView.getMeasuredWidth() > childViewWidth ? childView.getMeasuredWidth() : childViewWidth;
        }

        if (MeasureSpec.EXACTLY == widthMode) {
            measureWidth = widthSize;
        } else {
            measureWidth = childViewWidth;
        }

        if (MeasureSpec.EXACTLY == heightMode) {
            measureHeight = heightSize;
        } else {
            measureHeight = childViewHeight;
        }
//        this.measure(MeasureSpec.makeMeasureSpec())
        setMeasuredDimension(measureWidth, measureHeight);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    View contentView;
    ValueAnimator mValueAnimator;

    /**
     * 还原scrollY状态的时间
     */
    private int mRestScrollTime = 400;

    /**
     * 判断当前的容器有无滑动，如果scrollY>0 或 scrollY<0, 还原scrollY为0
     */
    public void restoreLocation() {
        restoreLocation(0);
    }

    public void restoreLocation(long delayed) {
        if (getScrollY() == 0) return;
        mValueAnimator = ValueAnimator.ofInt(getScrollY(), 0);
        mValueAnimator.setDuration(mRestScrollTime);
        mValueAnimator.setStartDelay(delayed);
        if (mAnimatorEndAnimatorListener == null) {
            mAnimatorEndAnimatorListener = new AnimatorEndAnimatorListener();
        }
        mValueAnimator.addUpdateListener(mScrollToAnimatorUpdateListener);
        mValueAnimator.addListener(mAnimatorEndAnimatorListener);
        mValueAnimator.start();
    }

    private AnimatorEndAnimatorListener mAnimatorEndAnimatorListener;

    class AnimatorEndAnimatorListener extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            mValueAnimator = null;
            action = ACTION_DEFAULT;
            resetView(topView, bottomView);
            mIsDropDown = false;
            mIsPullUp = false;
        }
    }

    private ValueAnimator.AnimatorUpdateListener mScrollToAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int animatorValue = (int) animation.getAnimatedValue();
            scrollTo(0, animatorValue);
        }
    };


    /**
     * 当子视图调用 startNestedScroll(View, int) 后调用该方法。返回 true 表示响应子视图的滚动。
     * 实现这个方法来声明支持嵌套滚动，如果返回 true，那么这个视图将要配合子视图嵌套滚动。当嵌套滚动结束时会调用到 onStopNestedScroll(View)。
     *
     * @param child            可滚动的子视图
     * @param target           NestedScrollingParent 的直接可滚动的视图，一般情况就是 child
     * @param nestedScrollAxes 包含 ViewCompat#SCROLL_AXIS_HORIZONTAL, ViewCompat#SCROLL_AXIS_VERTICAL 或者两个值都有。
     * @return 返回 true 表示响应子视图的滚动。
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
        return true;
    }

    /**
     * 如果 onStartNestedScroll 返回 true ，然后走该方法，这个方法里可以做一些初始化。
     */
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        parentHelper.onNestedScrollAccepted(child, target, axes);
    }

    /**
     * 这个方法表示子视图正在滚动，并且把滚动距离回调用到该方法，前提是 onStartNestedScroll 返回了 true。
     *
     * @param target       滚动的子视图
     * @param dxConsumed   手指产生的触摸距离中，子视图消耗的x方向的距离
     * @param dyConsumed   手指产生的触摸距离中，子视图消耗的y方向的距离 ，如果 onNestedPreScroll 中 dy = 20， consumed[0] = 8，那么 dy = 12
     * @param dxUnconsumed 手指产生的触摸距离中，未被子视图消耗的x方向的距离
     * @param dyUnconsumed 手指产生的触摸距离中，未被子视图消耗的y方向的距离
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (judgeAction()) return;
        if (isBanMove(dyUnconsumed)) return;
        if (dyUnconsumed != 0) {
            int sum = dyUnconsumed - dxConsumed;
            sum *= getScrollRate();

            int toY = getScrollY() + sum;
            if (toY > getMaxBottomHeight()) {
                toY = getMaxBottomHeight();
            }
            scrollTo(0, toY);
        }
    }

    private boolean isBanMove(int dyUnconsumed) {
        if (mBanDropDown && dyUnconsumed < 0) return true;
        if (mBanPullUp && dyUnconsumed > 0) return true;
        return false;
    }

    /**
     * 子视图开始滚动前会调用这个方法。这时候父布局（也就是当前的 NestedScrollingParent 的实现类）可以通过这个方法来配合子视图同时处理滚动事件。
     *
     * @param target   滚动的子视图
     * @param dx       绝对值为手指在x方向滑动的距离，dx<0 表示手指在屏幕向右滑动
     * @param dy       绝对值为手指在y方向滑动的距离，dy<0 表示手指在屏幕向下滑动
     * @param consumed 一个数组，值用来表示父布局消耗了多少距离，未消耗前为[0,0], 如果父布局想处理滚动事件，就可以在这个方法的实现中为consumed[0]，consumed[1]赋值。
     *                 分别表示x和y方向消耗的距离。如父布局想在竖直方向（y）完全拦截子视图，那么让 consumed[1] = dy，就把手指产生的触摸事件给拦截了，子视图便响应不到触摸事件了 。
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (judgeAction()) return;
        if (getScrollY() != 0) {
            if (dy > 0 && getScrollY() < 0) {
                //向上
                int sumScroll = getScrollY() + dy;
                scrollTo(0, sumScroll > 0 ? 0 : sumScroll);
                if (sumScroll > 0) {
                    consumed[1] = sumScroll;
                } else {
                    consumed[1] = dy;
                }
            } else if (dy < 0 && getScrollY() > 0) {
                //向下
                int sumScroll = getScrollY() + dy;
                scrollTo(0, sumScroll < 0 ? 0 : sumScroll);
                if (getScrollY() < 0) {
                    consumed[1] = sumScroll;
                } else {
                    consumed[1] = dy;
                }
            }
        }
    }

    protected boolean judgeAction() {
        if (action == ACTION_PULL_UP || action == ACTION_DROP_DOWN) return true;
        return false;
    }

    /**
     * 响应嵌套滚动结束
     * 当一个嵌套滚动结束后（如MotionEvent#ACTION_UP， MotionEvent#ACTION_CANCEL）会调用该方法，在这里可有做一些收尾工作，比如变量重置
     */
    @Override
    public void onStopNestedScroll(View child) {
        parentHelper.onStopNestedScroll(child);
        up();
    }

    protected void up() {
        if (getScrollY() == 0) return;
        if (getScrollY() > getTriggerPullUpHeight() && action != ACTION_PULL_UP) {
            //上拉
            setCompletePullUpStatus();
        } else if (getScrollY() < getTriggerDropDownHeight() && action != ACTION_DROP_DOWN) {
            //下拉
            setCompleteDropDownStatus();
        } else {
            if (action != ACTION_DROP_DOWN && action != ACTION_PULL_UP) {
                restoreLocation();
                action = ACTION_DEFAULT;
            }
        }
    }

    /**
     * 判断当前是不是显示出头部的状态
     *
     * @return
     */
    public boolean getIsDropDown() {
        return mIsDropDown;
    }

    /**
     * 判断当前是不是显示出尾部的状态
     *
     * @return
     */
    public boolean getIsPullUp() {
        return mIsPullUp;
    }

    private boolean mIsDropDown;
    private boolean mIsPullUp;


    /**
     * 当触发下拉个上拉事件的时候，设置当前scrollY到头部显示和尾部显示的时间
     */
    private static final int MOVE_MAX_BOTH_ENDS_TIME = 100;

    /**
     * 触发事件监听
     *
     * @param action
     * @param delayed
     */
    private synchronized void implementationListener(int action, int delayed) {
        if (mScrollStatusListener == null) return;
        if (mListenerRunnable == null) {
            mListenerRunnable = new ListenerRunnable();
        }
        mListenerRunnable.action = action;
        postDelayed(mListenerRunnable, delayed);
    }

    private ListenerRunnable mListenerRunnable;

    class ListenerRunnable implements Runnable {
        int action;

        @Override
        public void run() {
            if (mScrollStatusListener == null) return;
            if (action == ACTION_DROP_DOWN) {
                mScrollStatusListener.dropDown();
            } else if (action == ACTION_PULL_UP) {
                mScrollStatusListener.pullTop();
            }
        }
    }

    private int getTriggerDropDownHeight() {
        return getTopOffset() == 0 ? -getMaxBottomHeight() : -getMaxBottomHeight();
    }

    private int getTriggerPullUpHeight() {
        return getBottomOffset() == 0 ? bottomView.getMeasuredHeight() - (bottomView.getMeasuredHeight() / 3) : getMaxTopHeight();
    }

    protected int getMaxBottomHeight() {
        return bottomView == null ? 0 : bottomView.getMeasuredHeight();
    }

    private int getMaxTopHeight() {
        return topView == null ? 0 : topView.getMeasuredHeight();
    }

    protected int action = ACTION_DEFAULT;
    public static final int ACTION_DROP_DOWN = 1;
    public static final int ACTION_PULL_UP = 2;
    public static final int ACTION_DEFAULT = 3;

    /**
     * 子视图fling 时回调，父布局可以选择监听子视图的 fling。
     * true 表示父布局处理 fling，false表示父布局监听子视图的fling
     *
     * @param target    View that initiated the nested scroll
     * @param velocityX Horizontal velocity in pixels per second
     * @param velocityY Vertical velocity in pixels per second
     * @param consumed  true 表示子视图处理了fling
     */
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    /**
     * 手指在屏幕快速滑触发Fling前回调，如果前面 onNestedPreScroll 中父布局消耗了事件，那么这个也会被触发
     * 返回true表示父布局完全处理 fling 事件
     *
     * @param target    滚动的子视图
     * @param velocityX x方向的速度（px/s）
     * @param velocityY y方向的速度
     * @return true if this parent consumed the fling ahead of the target view
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    /**
     * 返回当前 NestedScrollingParent 的滚动方向，
     *
     * @return
     * @see ViewCompat#SCROLL_AXIS_HORIZONTAL
     * @see ViewCompat#SCROLL_AXIS_VERTICAL
     * @see ViewCompat#SCROLL_AXIS_NONE
     */
    @Override
    public int getNestedScrollAxes() {
        return super.getNestedScrollAxes();
    }


    /**
     * 实现这个方法返回下拉的时候的偏移量，默认为bottomView的1/3高度
     *
     * @return
     */
    protected int getTopOffset() {
        return 0;
    }

    /**
     * 实现这个方法返回下拉的时候的偏移量，默认为topView的1/3高度
     *
     * @return
     */
    protected int getBottomOffset() {
        return 0;
    }

    /**
     * 事件监听对象
     */
    private OnScrollStatusListener mScrollStatusListener;

    //滚动监听对象
    private OnScrollStatusChangeListener mScrollChangeListener;

    public interface OnScrollStatusListener {
        void dropDown();

        void pullTop();
    }

    public interface OnScrollStatusChangeListener {
        void onScrollChange(int y);
    }

    /**
     * 实现这个方法返回上拉底部的要显示View，不需要可以null
     *
     * @return bottomView
     */
    protected abstract View prepareBottomView();

    /**
     * 实现这个方法返回下拉顶端的要显示View，不需要可以null
     *
     * @return bottomView
     */
    protected abstract View prepareTopView();

    /**
     * 此方法可以还原topView和bottomView的状态
     *
     * @param topView
     * @param bottomView
     */
    protected abstract void resetView(View topView, View bottomView);


    /**
     * 下拉距离比较小的时候执行
     *
     * @param topView
     */
    protected abstract void startDropDown(View topView);

    /**
     * 下拉距离比较大的时候执行
     *
     * @param topView
     */
    protected abstract void dropDownHalfway(View topView);

    /**
     * 完成下拉状态时执行此方法
     *
     * @param topView
     */
    protected abstract void completeDropDown(View topView);

    /**
     * 上拉距离比较小的时候执行
     *
     * @param topView
     */
    protected abstract void startPullUp(View topView);

    /**
     * 上拉距离比较大的时候执行
     *
     * @param topView
     */
    protected abstract void pullUpHalfway(View topView);

    /**
     * 完成上拉状态时执行此方法
     *
     * @param bottomView
     */
    protected abstract void completePullUp(View bottomView);

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollChangeListener != null) {
            mScrollChangeListener.onScrollChange(t);
        }
        int action;
        if (t > 0) {
            action = ACTION_PULL_UP;
        } else if (t < 0) {
            action = ACTION_DROP_DOWN;
        } else {
            action = ACTION_DEFAULT;
        }

        if (action == ACTION_PULL_UP && !getIsPullUp()) {
            if (t > getTriggerPullUpHeight()) {
                pullUpHalfway(bottomView);
            } else if (action == ACTION_PULL_UP) {
                startPullUp(bottomView);
            }
        }
        if (action == ACTION_DROP_DOWN && !getIsDropDown()) {
            if (t < getTriggerDropDownHeight()) {
                dropDownHalfway(topView);
            } else if (action == ACTION_DROP_DOWN) {
                startDropDown(topView);
            }
        }
    }

    /**
     * 设置View为头部显示状态
     */
    public void setCompleteDropDownStatus() {
        if (mBanDropDown) return;
        if (!isLayout) {
            isD = true;
            isU = false;
            return;
        }
        action = ACTION_DROP_DOWN;
        completeDropDown(topView);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(getScrollY(), -getMaxTopHeight());
        valueAnimator.setDuration(MOVE_MAX_BOTH_ENDS_TIME);
        valueAnimator.addUpdateListener(mScrollToAnimatorUpdateListener);
        implementationListener(action, MOVE_MAX_BOTH_ENDS_TIME);
        mIsDropDown = true;
        valueAnimator.start();
    }

    private boolean isD;
    private boolean isU;

    /**
     * 设置View为尾部显示状态
     */
    public void setCompletePullUpStatus() {
        if (mBanPullUp) return;
        if (!isLayout) {
            isU = true;
            isD = false;
            return;
        }

        action = ACTION_PULL_UP;
        completePullUp(bottomView);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(getScrollY(), getMaxBottomHeight());
        valueAnimator.setDuration(MOVE_MAX_BOTH_ENDS_TIME);
        valueAnimator.addUpdateListener(mScrollToAnimatorUpdateListener);
        implementationListener(action, MOVE_MAX_BOTH_ENDS_TIME);
        mIsPullUp = true;
        valueAnimator.start();
    }


    private int downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (contentView instanceof NestedScrollingChild || getBanDropDown() && getBanPullUp() || judgeAction()) {
            return super.onTouchEvent(event);
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            actionDown(event);
        } else if (action == MotionEvent.ACTION_MOVE) {
            actionMove(event);
        } else if (action == MotionEvent.ACTION_UP) {
            actionUp();
        }
        return true;
    }

    private void actionUp() {
        up();
    }

    private void actionMove(MotionEvent event) {
        int moveY = (int) event.getRawY();
        int dis = downY - moveY;
        if (dis > 0) {
            //上

            if (isBanMove(dis) && getScrollY() >= 0) {
                downY = moveY;
                return;
            }
        } else {
            //下

            if (isBanMove(dis) && getScrollY() <= 0) {
                downY = moveY;
                return;
            }
        }

        dis *= getScrollRate();
        int toY = getScrollY() + dis;
        if (toY > getMaxBottomHeight()) {
            toY = getMaxBottomHeight();
        }
        scrollTo(0, toY);
        downY = moveY;
    }

    private void actionDown(MotionEvent event) {
        downY = (int) event.getRawY();
    }


}
