package com.dy.alp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.azl.view.carouse.CarouselView;
import com.dy.alp.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        CarouselView carouse = (CarouselView) findViewById(R.id.carouse);
//        carouse.setChangeRange(0);
        List<Object> mListData = new ArrayList<>();
        mListData.add("https://fs.kuxiao.cn/eM7r=1==.png");
        mListData.add("https://fs.kuxiao.cn/eM7r=1==.png");
        mListData.add("https://fs.kuxiao.cn/eM7r=1==.png");
        carouse.setChangeRange(0);
        carouse.setCarouseData(mListData);
    }
}
