package com.azl.view.helper.adapter.multiple;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azl.view.helper.adapter.multiple.diff.DefaultDiffCallBack;
import com.azl.view.helper.adapter.multiple.holder.ItemCommonHolder;
import com.azl.view.helper.adapter.multiple.holder.ItemHolder;
import com.example.zhlib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhong on 2018/1/16.
 */

public abstract class BaseMultipleTypeAdapter extends RecyclerView.Adapter<ItemCommonHolder> {


    private Context mContext;
    private boolean mIsFirstGetItemViewType = true;
    private List<Object> mListData;

    public BaseMultipleTypeAdapter(Context context) {
        this.mContext = context;
        this.mListData = new ArrayList<>();
    }



    public List<Object> getListData() {
        return mListData;
    }

    public void setListData(List<Object> mListData) {
        this.mListData = mListData;
    }

    public void diffInsertItem(int start, Object bean) {
        List<Object> newList = new ArrayList<>();
        newList.addAll(getListData());
        start = start > newList.size() - 1 ? mListData.size() : start;
        newList.add(start, bean);
        notifyList(newList);
    }

    public void diffInsertItems(int start, List<Object> list) {
        List<Object> newList = new ArrayList<>();
        newList.addAll(getListData());
        start = start > newList.size() - 1 ? mListData.size() : start;
        newList.addAll(start, list);
        notifyList(newList);
    }


    public void diffRemoveItem(int position) {
        diffRemoveItems(position, 1);
    }

    public void diffRemoveItems(int start, int count) {

        List<Object> newList = new ArrayList<>();
        newList.addAll(getListData());
        while (count > 0 && !newList.isEmpty()) {
            count--;
            if (start > newList.size() - 1) {
                return;
            }
            newList.remove(start);
        }
        notifyList(newList);
    }

    public void refresh(List... listArr) {
        List<Object> newList = new ArrayList<>();
        for (int i = 0; i < listArr.length; i++) {
            newList.addAll(listArr[i]);
        }
        notifyList(newList);

    }

    public void next(List... listArr) {
        List<Object> newList = new ArrayList<>();
        for (int i = 0; i < listArr.length; i++) {
            newList.addAll(listArr[i]);
        }
        newList.addAll(0, getListData());
        notifyList(newList);
    }


    private void notifyList(List<Object> newList) {
        DiffUtil.DiffResult d = getDiffUtil(newList, getListData());
        d.dispatchUpdatesTo(this);
        setListData(newList);
    }

    protected DiffUtil.DiffResult getDiffUtil(List<Object> newList, List<Object> oldList) {
        return DiffUtil.calculateDiff(new DefaultDiffCallBack(newList, oldList), true);
    }

    public void call(Object... objs) {
    }

    @Override
    public ItemCommonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHolder ih = null;
        for (int i = 0; i < getItemHolder().length; i++) {
            ItemHolder item = getItemHolder()[i];
            if (item.getItemType() == viewType) {
                ih = item;
                break;
            }
        }
        View itemView = LayoutInflater.from(getContext()).inflate(ih.getItemLayoutId(), parent, false);
        ItemCommonHolder ch = new ItemCommonHolder(this, itemView, ih);
        return ch;
    }


    @Override
    public void onBindViewHolder(ItemCommonHolder holder, int position) {
        holder.setView(this, getListData().get(position), position);
    }


    @Override
    public int getItemCount() {
        return getListData().size();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemViewType(int position) {

        ItemHolder[] holderArr = getItemHolder();
        if (holderArr == null) {
            throw new RuntimeException("item types cannot be empty");
        }
        if (holderArr.length == 0) {
            throw new RuntimeException("item type length is 0");
        }

        judgmentItemType(holderArr);

        for (int i = 0; i < holderArr.length; i++) {
            ItemHolder itemHolder = holderArr[i];
            if (itemHolder.isSelectItem(this, position, getListData().get(position))) {
                return itemHolder.getItemType();
            }
        }
        return super.getItemViewType(position);
    }

    /**
     * 判断类型是否合法，
     */
    private void judgmentItemType(ItemHolder[] holderArr) {
        if (mIsFirstGetItemViewType) {
            mIsFirstGetItemViewType = false;
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < holderArr.length; i++) {
                ItemHolder itemHolder = holderArr[i];
                int type = itemHolder.getItemType();
                if (list.contains(type)) {
                    throw new RuntimeException("type repeat:" + type);
                }
                list.add(type);
            }
        }
    }

    public abstract ItemHolder[] getItemHolder();

}
