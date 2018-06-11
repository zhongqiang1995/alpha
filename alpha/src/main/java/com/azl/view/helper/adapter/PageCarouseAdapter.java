package com.azl.view.helper.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhong on 2017/6/1.
 */

public class PageCarouseAdapter extends PagerAdapter {
    private List<View> mList;

    public PageCarouseAdapter() {
        mList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        int count;
        if(mList.size()==0||mList.size()==1){
            count=mList.size();
        }else{
            count=Integer.MAX_VALUE;
        }
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position % mList.size();

        View indexView = mList.get(index);
        if (indexView.getParent() != null) {
            ViewGroup group = (ViewGroup) indexView.getParent();
            group.removeView(indexView);
        }
        container.addView(indexView);
        return indexView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public void setData(List<View> list) {
        if (list != null) {
            this.mList = list;
        } else {
            this.mList.clear();
        }
        notifyDataSetChanged();
    }
}
