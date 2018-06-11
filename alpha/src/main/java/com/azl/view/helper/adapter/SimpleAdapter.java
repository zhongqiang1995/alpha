package com.azl.view.helper.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.azl.util.ObjectValueUtil;
import com.azl.view.helper.holder.CommonHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhong on 2017/5/18.
 */

public abstract class SimpleAdapter<Bean, Item> extends CommonAdapter<Bean> {
    private List<Item> mListData;
    private ObjectValueUtil mValueUtil;

    public SimpleAdapter(Context context) {
        super(context);
        mListData = new ArrayList<>();
        mValueUtil = ObjectValueUtil.getInstance();
    }

    @Override
    public void onBindViewHolder(CommonHolder holder, int position) {
        Item t = mListData.get(position);
        int itemType = getItemViewType(position);
        bindViewHolder(holder, position, t, itemType);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public void call(Bean obj) {
    }

    @Override
    public void refresh(Bean obj) {
        if (obj == null) return;
        call(obj);
        List<Item> list = (List<Item>) mValueUtil.getValueObject(obj, getItemDataPath());
        if (list == null) {
            list = new ArrayList<>();
        }
        Message message = new Message();
        message.obj = list;
        mHandle.sendMessageDelayed(message, 200);
    }

    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            List<Item> list = (List<Item>) msg.obj;
            int oldCount=mListData.size();
            mListData.clear();
            notifyItemRangeRemoved(0,oldCount);
            mListData.addAll(list);
            notifyItemRangeChanged(0,mListData.size());
//            notifyItemRangeInserted(0,mListData.size());
            notifyDataSetChanged();
        }
    };

    @Override
    public void next(Bean obj) {
        if (obj == null) return;
        call(obj);
        List<Item> list = (List<Item>) mValueUtil.getValueObject(obj, getItemDataPath());
        if (list == null || list.isEmpty()) {
            return;
        }
        int oldPosition = mListData.size();
        mListData.addAll(list);
        int count = list.size();
        if (count + oldPosition > mListData.size()) {
            count = mListData.size() - oldPosition;
        }
        notifyItemRangeInserted(oldPosition < 0 ? 0 : oldPosition, count);
    }

    @Override
    public abstract int getLayoutId(int itemType);

    public abstract void bindViewHolder(CommonHolder holder, int position, Item object, int type);

    public abstract String getItemDataPath();


    protected List<Item> getListData() {
        return mListData;
    }

}
