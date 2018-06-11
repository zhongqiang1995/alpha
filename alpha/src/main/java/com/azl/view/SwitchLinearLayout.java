package com.azl.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.zhlib.R;


/**
 * Created by zhongq on 2016/7/12.
 */
public class SwitchLinearLayout extends LinearLayout {

    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = 2;
    private ViewDragHelper mDragHelper;
    //可以滑动的边缘方向
    private int mDirection;
    //滑动的子View
    private View mDragView;
    //是否触摸view是否可以滑动
    private boolean mIsTouchContent;
    //滑动偏移值 触发监听 默认是当前view的二分之一距离
    private int mOffsetY;
    //滑动放开view的时候是否自动返回开始位置
    private boolean mIsAutoScroll=true;
    //是否可以触摸边缘滑动
    private boolean mIsDragEdge=true;

    public SwitchLinearLayout(Context context) {
        this(context, null);
    }

    public SwitchLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setmIsDragEdge(boolean mIsDragEdge) {
        this.mIsDragEdge = mIsDragEdge;
    }

    public boolean getIsDragEdge(){
        return mIsDragEdge;
    }

    public void setIsAutoScroll(boolean mIsAutoScroll) {
        this.mIsAutoScroll = mIsAutoScroll;
    }
    public boolean getIsAutoScroll(){
        return mIsAutoScroll;
    }

    public void setIsTouchContent(boolean mIsTouchEdge) {
        this.mIsTouchContent = mIsTouchEdge;
    }
    public boolean getIsTouchContent(){
        return mIsTouchContent;
    }


    private void init() {
        setBackgroundColor(Color.parseColor("#c0000000"));
        mDragHelper = ViewDragHelper.create(this, 1.0f, new MCall());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        mDirection = DIRECTION_LEFT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mOffsetY == 0) {
            mOffsetY = getMeasuredWidth() / 2;
        }
        if (getChildCount() > 0&&mDragView==null) {
            mDragView = getChildAt(0);
            mLeft=mDragView.getLeft();
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }else if(action==MotionEvent.ACTION_DOWN){
//            if(mDragView!=null){
//                mLeft=mDragView.getLeft();
//            }
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }
    private int mLeft;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }





    /**
     * 设置移动的view
     *
     * @param mDragView
     */
    public void setDragView(View mDragView) {
        this.mDragView = mDragView;
        mLeft=this.mDragView.getLeft();
    }

    /**
     * 设置偏移量
     */
    public void setOffsetY(int offsetY) {
        this.mOffsetY = offsetY;
    }


    /**
     * 设置滑动的方向
     * DIRECTION_LEFT
     * DIRECTION_RIGHT
     *
     * @param direction
     */
    public void setDirection(int direction) {
        mDirection = direction;
        if (mDirection == DIRECTION_RIGHT) {
            mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
        } else if (mDirection == DIRECTION_LEFT) {
            mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        }
    }


    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    class MCall extends ViewDragHelper.Callback {
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if(mFinalCall!=null) {
                if (left == SwitchLinearLayout.this.getWidth() || left == -SwitchLinearLayout.this.getWidth()) {
                    mFinalCall.arriveoffsetY();
                }
                mFinalCall.positionChanged(left, top);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if(mIsAutoScroll){
                if (mDirection == DIRECTION_LEFT) {
                    if (releasedChild.getLeft()-mLeft > mOffsetY) {
                        mDragHelper.settleCapturedViewAt(SwitchLinearLayout.this.getWidth(),mDragView.getTop());
                    } else {
                        mDragHelper.settleCapturedViewAt(mLeft, mDragView.getTop());
                    }
                } else if (mDirection == DIRECTION_RIGHT) {
                    if (mLeft-releasedChild.getLeft() < mOffsetY) {
                        mDragHelper.settleCapturedViewAt(mLeft, mDragView.getTop());
                    } else {
                        mDragHelper.settleCapturedViewAt(-SwitchLinearLayout.this.getWidth(),mDragView.getTop());
                    }
                }

                invalidate();
            }
        }


        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            if(mIsDragEdge){
                mDragHelper.captureChildView(mDragView, pointerId);
            }
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (mIsTouchContent) {
                return child == mDragView;
            } else {
                return false;
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
            final int bottomBound = getHeight() - child.getHeight();
            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            return mDragView.getTop();
        }
    }


    public void setOnFinalCall(  MFinalCall mFinalCall ){
        this.mFinalCall=mFinalCall;
    }
    //默认回调 触发监听关闭activity
    MFinalCall mFinalCall = new MFinalCall() {
        @Override
        public void arriveoffsetY() {
            try {
                if (getContext() instanceof Activity) {
                    Activity activity = (Activity) getContext();
                    activity.overridePendingTransition(R.anim.slide_right_in,R.anim.silde_right_out_none);
                    activity.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void positionChanged(int left, int top) {
            if(mDirection==DIRECTION_RIGHT){
                int value=SwitchLinearLayout.this.getWidth()-Math.abs(left);
                float f=(value*1.0f)/SwitchLinearLayout.this.getWidth();
                Log.e("positionChanged:",""+f);
                getBackground().setAlpha((int) (f*255));
            }else if(mDirection==DIRECTION_LEFT){
                int value=SwitchLinearLayout.this.getWidth()-left;
                float f=(value*1.0f)/SwitchLinearLayout.this.getWidth();
                getBackground().setAlpha((int) (f*255));
//                SwitchLinearLayout.this.setAlpha();
            }

        }
    };

    public interface MFinalCall {
        void arriveoffsetY();

        void positionChanged(int left, int top);
    }
}
