package com.azl.view.grid.select;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.azl.util.FileUriUtil;
import com.azl.util.ScreenUtil;
import com.azl.view.grid.image.decoration.GridImageItemDecoration;
import com.azl.view.grid.select.adapter.GridSelectFileViewAdapter;
import com.azl.view.grid.select.entity.GridAddEntity;
import com.azl.view.grid.select.entity.GridSelectEntity;
import com.azl.view.grid.select.util.GridSelectViewResultCodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhong on 2018/3/7.
 */

public class GridSelectFileView extends RecyclerView {

    public static final String OPEN_TYPE_IMAGE = "image/*";
    public static final String OPEN_TYPE_AUDIO = "audio/*";
    public static final String OPEN_TYPE_VIDEO = "video/*";
    public static final String OPEN_TYPE_ALL = "*/*";


    private int mColumn = 3;//一列的数量
    private int mMaxCount = 9;//最多数量
    private int mColumnSpace;

    public int mResultCode;


    private GridSelectFileViewAdapter mAdapter;//设配器
    private GridImageItemDecoration mDecoration;
    private GridLayoutManager mGridLayoutManager;


    public GridSelectFileView(Context context) {
        this(context, null);
    }

    public GridSelectFileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        mResultCode = GridSelectViewResultCodeUtil.getResultCode();
        mColumnSpace = ScreenUtil.dip2px(getContext(), ScreenUtil.dip2px(getContext(), 5));
        mDecoration = new GridImageItemDecoration(mColumnSpace, mColumn);
        mGridLayoutManager = new GridLayoutManager(getContext(), mColumn);
        mAdapter = new GridSelectFileViewAdapter(getContext(), mMaxCount, mResultCode, mColumn);
        List<Object> list = new ArrayList<>();
        list.add(new GridAddEntity());


        setNestedScrollingEnabled(false);
        setAdapter(mAdapter);
        addItemDecoration(mDecoration);
        setLayoutManager(mGridLayoutManager);
        mAdapter.refresh(list);
    }

    public void onActivityForResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == mResultCode) {
            if (data != null) {
                Uri uri = data.getData();
                String path = FileUriUtil.getPath(getContext(), uri);
                if (path == null) {
                    return;
                }
                mAdapter.addObj(path);
            }
        }
    }

    public void setOpenType(String type) {
        if (mAdapter != null) {
            mAdapter.setOpenType(type);
        }
    }

    public void setMaxCount(int maxCount) {
        if (maxCount <= 0) return;
        this.mMaxCount = maxCount;
        mAdapter.setMaxCount(mMaxCount);
    }

    public void setColumn(int column) {
        this.mColumn = column;
        mDecoration.setSize(mColumnSpace, mColumn);
        mGridLayoutManager.setSpanCount(column);
        mAdapter.setColumn(mColumn);
        mAdapter.notifyDataSetChanged();
    }

    public void setAddImageResId(int resId) {
        mAdapter.setAddImageResId(resId);
    }

    public void setPlaceholderImageRedId(int resId) {
        mAdapter.setPlaceHolderImageResId(resId);
    }


    public void addData(List<String> list) {
        mAdapter.addData(list);
    }

    public void setData(List<String> list) {
        mAdapter.setData(list);
    }

    public void remove(int position) {
        mAdapter.remove(position);
    }

    public void remove(int start, int end) {
        mAdapter.removeRange(start, end);
    }

    public List<String> getSelectData() {
        List<Object> listData = mAdapter.getListData();
        List<String> list = new ArrayList<>();
        if (listData == null) return new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            Object obj = listData.get(i);
            if (obj instanceof GridSelectEntity) {
                GridSelectEntity entity = (GridSelectEntity) obj;
                if (entity.getPath() != null) {
                    list.add(entity.getPath());
                }

            }
        }
        return list;
    }
}
