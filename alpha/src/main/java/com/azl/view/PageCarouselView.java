package com.azl.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Scroller;

import com.azl.view.animation.ZoomOutPageTransformer;
import com.azl.view.helper.adapter.PageCarouseAdapter;
import com.example.zhlib.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhong on 2017/6/1.
 * 页面轮播
 */

public class PageCarouselView extends FrameLayout implements ViewPager.OnPageChangeListener {
    private static final String TAG = "PageCarouselView";
    private ViewPager mViewPager;
    private PageCarouseAdapter mAdapter;
    private Timer mTimer;//定时器
    private boolean mIsRunCarouse;//记录是否正在轮播
    private boolean mIsAutoCarouse;//是否要自动轮播
    private long mCarouseTime = 4000;//轮播的时间间隔
    private boolean mIsTabVisibility;//是否显示页面指示器
    private HorizontalScrollView mScrollView;
    private RadioGroup mTabLayout;
    private float mDensity;

    public PageCarouselView(@NonNull Context context) {
        this(context, null);
    }

    public PageCarouselView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PageCarouselView);
            mIsAutoCarouse = array.getBoolean(R.styleable.PageCarouselView_mp_auto_carouse, true);
            mCarouseTime = (long) array.getFloat(R.styleable.PageCarouselView_mp_carouse_time, mCarouseTime);
            mIsTabVisibility = array.getBoolean(R.styleable.PageCarouselView_mp_tab_visibility, true);
            array.recycle();
        }
        mDensity = getContext().getResources().getDisplayMetrics().density;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        initView(inflater);
    }

    private void initView(LayoutInflater inflater) {
        mAdapter = new PageCarouseAdapter();
        View view = inflater.inflate(R.layout.layout_page_carouse, null);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mScrollView = (HorizontalScrollView) view.findViewById(R.id.tabScroll);
        mTabLayout = (RadioGroup) view.findViewById(R.id.tabLayout);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        addView(view);
    }

    /**
     * 清除动画
     */
    public void clearPageTransformer() {
        mViewPager.setPageTransformer(true, null);
    }

    /**
     * 设置默认动画
     */
    public void setDefaultPageTransformer() {
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    /**
     * 设置动画
     */
    public void setPageTransformer(ViewPager.PageTransformer pageTransformer) {
        mViewPager.setPageTransformer(true, pageTransformer);
    }

    /**
     * 设置页面切换的时间
     *
     * @param time
     */
    public void setScrollerTime(int time) {
        FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), new LinearInterpolator());
        scroller.setTime(time);
        controlViewPagerSpeed(scroller);
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_UP) {
            startCarouse();
        }
        return super.dispatchTouchEvent(ev);
    }
    /**
     * 是否自动切换
     *
     * @param mIsAutoCarouse
     */
    public void setAutoCarouse(boolean mIsAutoCarouse) {
        this.mIsAutoCarouse = mIsAutoCarouse;
        if (this.mIsAutoCarouse) {
            startCarouse();
        } else {
            stopCarouse();
        }
    }



    public void setImageUrlData(String[] arr) {
        if (arr == null) return;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        List<View> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            String url = arr[i];
            SimpleDraweeView drView = (SimpleDraweeView) inflater.inflate(R.layout.layout_page_carouse_item, null);
            if (url != null) {
                drView.setImageURI(Uri.parse(url));
            } else {
                continue;
            }
            list.add(drView);
        }
        setViewData(list);
    }

    public void setImageResIds(int[] arr) {
        if (arr == null) return;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        List<View> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            int id = arr[i];
            SimpleDraweeView drView = (SimpleDraweeView) inflater.inflate(R.layout.layout_page_carouse_item, null);
            drView.setImageResource(id);
            list.add(drView);
        }
        setViewData(list);
    }


    public void setViewData(List<View> list) {
        mAdapter.setData(list);
        setTab(list == null ? 0 : list.size());
        startCarouse();
    }


    /**
     * 开始轮播
     */
    private void startCarouse() {
        Log.i(TAG, "startCarouse");
        if (mIsRunCarouse || !mIsAutoCarouse) return;
        mIsRunCarouse = true;
        mTimer = new Timer();
        mTimer.schedule(new CarouseTimeTask(), mCarouseTime, mCarouseTime);
    }

    /**
     * 停止轮播
     */
    private void stopCarouse() {
        Log.i(TAG, "stopCourse");
        if (!mIsRunCarouse) return;
        mIsRunCarouse = false;
        mTimer.cancel();
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

    private List<RadioButton> mListRadio = new ArrayList<>();

    private void setTab(int tab) {
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
        if (mTabLayout.getChildCount() > 0) {
            RadioButton ra = (RadioButton) mTabLayout.getChildAt(0);
            ra.setChecked(true);
        }
        if (mIsTabVisibility) {
            mScrollView.setVisibility(View.VISIBLE);
        } else {
            mScrollView.setVisibility(View.GONE);
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


    private void controlViewPagerSpeed(Scroller scroller) {
        try {
            Field mField;

            mField = mViewPager.getClass().getDeclaredField("mScroller");
            mField.setAccessible(true);
            mField.set(mViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int index = position % mTabLayout.getChildCount();
        RadioButton ra = (RadioButton) mTabLayout.getChildAt(index);
        ra.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.
                    makeMeasureSpec((int) (200 * mDensity), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    class FixedSpeedScroller extends Scroller {
        private int time = 200;

        public void setTime(int time) {
            this.time = time;
        }

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, time);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, time);
        }

    }
}


