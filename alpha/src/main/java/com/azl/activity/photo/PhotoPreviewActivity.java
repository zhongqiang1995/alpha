package com.azl.activity.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.azl.activity.photo.adapter.PhotoPreviewActivityViewPagerAdapter;
import com.azl.base.StatusActivity;
import com.azl.util.ScreenUtil;
import com.example.zhlib.R;


public class PhotoPreviewActivity extends StatusActivity implements ViewPager.OnPageChangeListener {


    private static final String VALUE_POSITION = "valuePosition";
    private static final String VALUE_LIST = "valueList";
    private static final String VALUE_SAVE_PATH = "savePath";

    private ViewPager mViewPager;
    private RadioGroup mTabLayout;

    private Object[] mDataList;
    private int mPosition;
    private String mSavePath;

    /**
     * @param list     显示的图片集合
     * @param position 默认显示指定的图片页数
     * @param savePath 保存图片时候的路径
     * @return
     */
    public static Intent getJumpIntent(Context context, int position, String savePath, Object[] list) {
        Intent intent = new Intent(context, PhotoPreviewActivity.class);
        intent.putExtra(VALUE_POSITION, position);
        intent.putExtra(VALUE_LIST, list);
        intent.putExtra(VALUE_SAVE_PATH, savePath);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.alpha_activity_photo_preview);
        remoteData();
        initView();
        initListener();
        initViewPager();
    }


    private void remoteData() {
        mSavePath = getIntent().getStringExtra(VALUE_SAVE_PATH);
        mDataList = (Object[]) getIntent().getSerializableExtra(VALUE_LIST);
        mPosition = getIntent().getIntExtra(VALUE_POSITION, 0);
        if (TextUtils.isEmpty(mSavePath)) {
            mSavePath = Environment.getExternalStorageDirectory() + "kuxiao/";
        }
        if (mPosition >= mDataList.length) {
            mPosition = mDataList.length - 1;
            if (mPosition < 0) {
                mPosition = 0;
            }
        }
    }


    private void initViewPager() {
        PhotoPreviewActivityViewPagerAdapter adapter = new PhotoPreviewActivityViewPagerAdapter(mDataList, this, mSavePath);
        mViewPager.setAdapter(adapter);
        setTabLab(mDataList.length);
        if (mDataList.length > 0) {
            mViewPager.setCurrentItem(mPosition);
        }
    }

    private void initView() {
        mViewPager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.tabLayout);

    }

    private void initListener() {
        mViewPager.addOnPageChangeListener(this);
    }

    private void setTabLab(int tab) {

        if (tab > mTabLayout.getChildCount()) {
            while (tab > mTabLayout.getChildCount()) {
                int width = ScreenUtil.dip2px(this, 5);
                RadioButton ra = (RadioButton) LayoutInflater.from(this).inflate(R.layout.layout_page_tab, null);
                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(width, width);
                layoutParams.setMargins(0, 0, width, 0);
                ra.setLayoutParams(layoutParams);
                ra.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                mTabLayout.addView(ra);
            }
        } else if (mTabLayout.getChildCount() > tab) {
            while (mTabLayout.getChildCount() > tab) {
                RadioButton ra = (RadioButton) mTabLayout.getChildAt(0);
                mTabLayout.removeView(ra);
            }
        }

        int position = mViewPager.getCurrentItem();

        if (mTabLayout.getChildCount() > 0) {
            RadioButton ra = (RadioButton) mTabLayout.getChildAt(position);
            ra.setChecked(true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //切换图片的时候切换指示器
        if (position < mTabLayout.getChildCount()) {
            RadioButton rb = (RadioButton) mTabLayout.getChildAt(position);
            rb.setChecked(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
