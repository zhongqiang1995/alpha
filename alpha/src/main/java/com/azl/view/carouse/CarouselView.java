package com.azl.view.carouse;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.azl.util.ScreenUtil;
import com.azl.view.carouse.scroller.FixedSpeedScroller;
import com.example.zhlib.R;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhong on 2017/8/17.
 */

public class CarouselView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private static final String TAG = "CarouselView";
    private ViewPager mViewPager;
    private List<Object> mListData;
    private MPagerAdapter mAdapter;
    private float mChangeRange = 0.3f;//变化的比例
    private float mBasisRange = 0.7f;//最小的比例
    private View mChildView;
    private RadioGroup mTabLayout;
    private float mDensity;
    private boolean mIsRunCarouse;//记录是否正在轮播
    private boolean mIsAutoCarouse = true;//是否要自动轮播
    private Timer mTimer;
    private long mCarouseTime = 4000;//轮播的时间间隔
    private int lastValue = -1;//记录上一次滑动的positionOffsetPixels值
    private OnSelectListener mOnSelectListener;

    private List<View> mApplyList = new ArrayList<>();
    private List<View> mCacheList = new ArrayList<>();

    public CarouselView(@NonNull Context context) {
        this(context, null);
    }

    public CarouselView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setClipChildren(false);
        mListData = new ArrayList<>();
        mDensity = getContext().getResources().getDisplayMetrics().density;
        viewPagerInit();
    }

    public void setChangeRange(float f) {
        if (f > 1) {
            f = 1;
        }
        if (f < 0) {
            f = 0;
        }
        mChangeRange = f;
        mBasisRange = 1 - f;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (mBasisRange == 1) {
            mViewPager.setPageMargin(0);
            params.setMargins(0, 0, 0, 0);
        } else {
            mViewPager.setPageMargin(ScreenUtil.dip2px(getContext(), 5));
            int leftRightMarign = ScreenUtil.dip2px(getContext(), 40);
            params.setMargins(leftRightMarign, 0, leftRightMarign, 0);
        }
        mViewPager.setLayoutParams(params);
    }

    private boolean isScale() {
        MarginLayoutParams par = (MarginLayoutParams) mViewPager.getLayoutParams();
        if (par == null || par.leftMargin == 0) {
            return false;
        }
        return true;
    }


    public void setCarouseData(final List<Object> urls) {


        if (isFirst) {
            isFirst = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCarouseData(urls);
                }
            }, 200);
        } else {
            stopCarouse();
            mListData.clear();
            mListData.addAll(urls);
            mAdapter.notifyDataSetChanged();
            setTabLab(mListData.size());
        }
//        if (!mListData.isEmpty()) {
//            mViewPager.setCurrentItem(20, false);
//        }
    }

    private boolean isFirst = true;

    private void viewPagerInit() {
        mChildView = LayoutInflater.from(getContext()).inflate(R.layout.layout_carouse_child, null, false);
        mViewPager = mChildView.findViewById(R.id.viewPager);
        mTabLayout = mChildView.findViewById(R.id.tabLayout);
        mViewPager.setPageMargin(ScreenUtil.dip2px(getContext(), 5));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);
        try {//设置滚动时间
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(),
                    new AccelerateInterpolator());
            field.set(mViewPager, scroller);
            scroller.setScrollDuration(500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAdapter = new MPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        addView(mChildView);
    }


    public void setOnSelectListener(OnSelectListener mOnSelectListener) {
        this.mOnSelectListener = mOnSelectListener;
    }

    public OnSelectListener getOnSelectListener() {
        return mOnSelectListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (!isScale()) return;
        int currentPosition = mViewPager.getCurrentItem();//当前被选择的
        if (positionOffset != 0) {
            if (lastValue > positionOffsetPixels) {
                //右滑
                for (int i = 0; i < mApplyList.size(); i++) {
                    View view = mApplyList.get(i);
                    MPagerAdapter.OnClickItem item = (MPagerAdapter.OnClickItem) view.getTag();
                    int index = item.position;
                    ScaleLayout scaleLayout = (ScaleLayout) view;
                    if (index == currentPosition) {
                        //当前选择的item 变小
                        if (position != currentPosition) {
                            float fl = positionOffset;
                            float ff = mChangeRange * fl;
                            float f = ff + mBasisRange;
                            scaleLayout.scaleRight(f);
                        } else {
                            float fl = 1.0f - positionOffset;
                            float ff = mChangeRange * fl;
                            float f = ff + mBasisRange;
                            scaleLayout.scaleLeft(f);
                        }

                    } else if (index == currentPosition - 1.0f) {
                        //当前选择右边的item 变大
                        if (position != currentPosition) {
                            float ff = mChangeRange * (1.0f - positionOffset);
                            scaleLayout.scaleLeft(ff + mBasisRange);
                        }
                    } else if (index == currentPosition + 1.0f) {
                        if (position == currentPosition) {
                            float ff = mChangeRange * positionOffset;
                            scaleLayout.scaleRight(ff + mBasisRange);
                        }
                    } else {
                        if (index > currentPosition) {
                            scaleLayout.scaleRight(mBasisRange);
                        } else {
                            scaleLayout.scaleLeft(mBasisRange);
                        }
                    }
                }
            } else if (lastValue < positionOffsetPixels) {
                //左滑
                for (int i = 0; i < mApplyList.size(); i++) {
                    View view = mApplyList.get(i);
                    MPagerAdapter.OnClickItem item = (MPagerAdapter.OnClickItem) view.getTag();
                    int index = item.position;
                    ScaleLayout scaleLayout = (ScaleLayout) view;
                    if (index == currentPosition) {
                        if (position == currentPosition) {
                            //当前选择的item 变小
                            float fl = 1.0f - positionOffset;
                            float ff = mChangeRange * fl;
                            scaleLayout.scaleLeft(ff + mBasisRange);
                        } else {
                            float fl = positionOffset;
                            float ff = mChangeRange * fl;
                            float f = ff + mBasisRange;
                            scaleLayout.scaleRight(f);
                        }

                    } else if (index == currentPosition + 1.0f) {
                        //当前选择右边的item 变大
                        if (position == currentPosition) {
                            float ff = mChangeRange * positionOffset;
                            scaleLayout.scaleRight(ff + mBasisRange);
                        }
                    } else if (index == currentPosition - 1.0f) {
                        if (position != currentPosition) {
                            float ff = mChangeRange * (1.0f - positionOffset);
                            float f = ff + mBasisRange;
                            scaleLayout.scaleLeft(f);
                        }
                    } else {
                        if (index < currentPosition) {
                            scaleLayout.scaleLeft(mBasisRange);
                        } else {
                            scaleLayout.scaleRight(mBasisRange);
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < mApplyList.size(); i++) {
                View view = mApplyList.get(i);
                ScaleLayout layout = (ScaleLayout) view;
                MPagerAdapter.OnClickItem item = (MPagerAdapter.OnClickItem) view.getTag();
                int index = item.position;
                if (index > currentPosition) {
                    layout.scaleRight(mBasisRange);
                } else if (index < currentPosition) {
                    layout.scaleLeft(mBasisRange);
                }
            }
        }
        lastValue = positionOffsetPixels;


    }

    /**
     * 开始轮播
     */
    public void startCarouse() {
        if (mIsRunCarouse || !mIsAutoCarouse) return;
        if (mListData == null || mListData.isEmpty()) return;
        Log.i(TAG, "startCarouse");
        mIsRunCarouse = true;
        mTimer = new Timer();
        mTimer.schedule(new CarouseTimeTask(), mCarouseTime, mCarouseTime);
    }

    /**
     * 停止轮播
     */
    public void stopCarouse() {
        if (!mIsRunCarouse) return;
        Log.i(TAG, "stopCourse");
        mIsRunCarouse = false;
        mTimer.cancel();
        resetCurrentStatus();
    }

    public void resetCurrentStatus() {
        if (mAdapter != null) {
            mAdapter.resetCurrentViewStatus();
        }
    }

    /**
     * 跳到下一页
     */
    private void nextPage() {
        int currIndex = mViewPager.getCurrentItem();
        currIndex++;
        if (currIndex >= mAdapter.getCount()) {
            currIndex = 0;
        }
        mViewPager.setCurrentItem(currIndex);
    }


    class CarouseTimeTask extends TimerTask {
        @Override
        public void run() {
            if (mAdapter.getCount() > 0 && mIsAutoCarouse) {
                mHandler.sendEmptyMessage(0);
            } else {
                stopCarouse();
            }
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            nextPage();
        }
    };

    @Override
    public void onPageSelected(int position) {
        int index = position % mTabLayout.getChildCount();
        RadioButton ra = (RadioButton) mTabLayout.getChildAt(index);
        ra.setChecked(true);
        if (getOnSelectListener() != null) {
            getOnSelectListener().onSelect(index, mListData.get(index));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private List<RadioButton> mListRadio = new ArrayList<>();

    private void setTabLab(int tab) {
        if (tab > 0) {
            startCarouse();
        }
        if (tab > mTabLayout.getChildCount()) {
            while (tab > mTabLayout.getChildCount()) {
                RadioButton ra = getTabView();
                mTabLayout.addView(ra);
            }
        } else if (mTabLayout.getChildCount() > tab) {
            while (mTabLayout.getChildCount() > tab) {
                RadioButton ra = (RadioButton) mTabLayout.getChildAt(0);
                mTabLayout.removeView(ra);
                mListRadio.add(ra);

            }
        }

        int index = 0;
        int position = mViewPager.getCurrentItem();
        if (position != 0 && mListData.size() != 0) {
            index = position % mListData.size();
        }
        if (mTabLayout.getChildCount() > 0) {
            RadioButton ra = (RadioButton) mTabLayout.getChildAt(index);
            ra.setChecked(true);
        }
    }

    private RadioButton getTabView() {
        if (mListRadio.size() > 0) {
            RadioButton ra = mListRadio.get(0);
            if (ra.getParent() != null) {
                ViewGroup parent = (ViewGroup) ra.getParent();
                parent.removeView(ra);
            }
            return ra;
        } else {
            RadioButton ra = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.layout_page_tab, null);
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams((int) mDensity * 7, (int) mDensity * 7);
            layoutParams.setMargins(0, 0, (int) mDensity * 5, 0);
            ra.setLayoutParams(layoutParams);
            ra.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            return ra;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_DOWN) {
            stopCarouse();
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int ww = ScreenUtil.getScreenWidth(getContext());
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            setMeasuredDimension(0, 0);
            return;
        }

        heightSize = widthSize / 16;
        heightSize *= 9;
        heightSize += getPaddingTop();
        heightSize += getPaddingBottom();
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            startCarouse();
        }
        return super.dispatchTouchEvent(ev);
    }

    class MPagerAdapter extends PagerAdapter {

        public void resetCurrentViewStatus() {
            if (!isScale()) return;
            int position = mViewPager.getCurrentItem();
            for (int i = 0; i < mApplyList.size(); i++) {
                View view = mApplyList.get(i);
                OnClickItem onClickItem = (OnClickItem) view.getTag();
                if (onClickItem.position == position) {
                    ScaleLayout layout = (ScaleLayout) view;
                    layout.resetScale();
                } else if (onClickItem.position > position) {
                    ScaleLayout layout = (ScaleLayout) view;
                    layout.scaleRight(mBasisRange);
                } else {
                    ScaleLayout layout = (ScaleLayout) view;
                    layout.scaleLeft(mBasisRange);
                }
            }
        }

        @Override
        public int getCount() {
            if (mListData.isEmpty()) {
                return 0;
            }
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
            if (isScale()) {
                ScaleLayout layout = (ScaleLayout) view;
                layout.resetScale();
            }
            mCacheList.add(view);
            mApplyList.remove(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int index = position;
            if (position != 0 && mListData.size() != 0) {
                index = position % mListData.size();
            }
            Object url = mListData.get(index);


            View view;
            SimpleDraweeView simpleDraweeView;
            OnClickItem click = null;
            if (mCacheList.isEmpty()) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.view_carousel_item, null, false);
                FrameLayout cardView = view.findViewById(R.id.cardView);
                simpleDraweeView = view.findViewById(R.id.imgPhoto);
                setCard(cardView, simpleDraweeView);
                click = new OnClickItem();
                view.setOnClickListener(click);
                view.setTag(click);
            } else {
                view = mCacheList.remove(0);
                click = (OnClickItem) view.getTag();
                simpleDraweeView = view.findViewById(R.id.imgPhoto);
            }
            click.index = index;
            click.position = position;
            if (isScale()) {
                ScaleLayout layout = (ScaleLayout) view;
                if (position > mViewPager.getCurrentItem()) {
                    layout.scaleRight(mBasisRange);
                } else if (index < mViewPager.getCurrentItem()) {
                    layout.scaleLeft(mBasisRange);
                } else {
                    layout.resetScale();
                }
            }
            if (url instanceof String) {
                String u = (String) url;
                simpleDraweeView.setImageURI(u);

            } else if (url instanceof Integer) {
                simpleDraweeView.setImageResource((Integer) url);
            } else {
                throw new RuntimeException("轮播控件接受到的参数类型不对");
            }
            mApplyList.add(view);
            container.addView(view);
            return view;
        }

        private void setCard(FrameLayout cardView, SimpleDraweeView simpleDraweeView) {
            if (isScale()) {
                RoundingParams roundingParams = simpleDraweeView.getHierarchy().getRoundingParams();
                roundingParams.setCornersRadius(ScreenUtil.dip2px(getContext(), 10));
                simpleDraweeView.getHierarchy().setRoundingParams(roundingParams);


                FrameLayout.LayoutParams par = (LayoutParams) cardView.getLayoutParams();
                int margin = ScreenUtil.dip2px(getContext(), 5);
                par.setMargins(margin, margin, margin, margin);
                cardView.setLayoutParams(par);
            } else {
                //设置圆角
                RoundingParams roundingParams = simpleDraweeView.getHierarchy().getRoundingParams();
                roundingParams.setCornersRadius(0);
                simpleDraweeView.getHierarchy().setRoundingParams(roundingParams);

                FrameLayout.LayoutParams par = (LayoutParams) cardView.getLayoutParams();
                par.setMargins(0, 0, 0, 0);
                cardView.setLayoutParams(par);
            }
        }

        class OnClickItem implements OnClickListener {
            int index;
            int position;

            @Override
            public void onClick(View view) {
                if (mOnSelectListener != null) {
                    mOnSelectListener.onClickCarouse(index);
                }
            }
        }
    }


    public interface OnSelectListener {
        void onSelect(int position, Object url);

        void onClickCarouse(int position);
    }

}
