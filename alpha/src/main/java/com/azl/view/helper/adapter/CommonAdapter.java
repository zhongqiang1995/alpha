package com.azl.view.helper.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azl.view.helper.holder.CommonHolder;

/**
 * Created by zhong on 2017/5/18.
 */

public abstract class CommonAdapter<Bean> extends RecyclerView.Adapter<CommonHolder> {
    private Context mContext;

    public CommonAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public CommonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(getLayoutId(viewType), parent, false);

        return new CommonHolder(view);

    }

    @Override
    public abstract void onBindViewHolder(CommonHolder holder, int position);

    public abstract int getLayoutId(int itemType);


    public void refresh(Bean item) {
    }

    ;

    public void next(Bean item) {
    }

    ;

    public String getItemDataPath() {
        return null;
    }

    public Context getContext() {
        return mContext;
    }

    public String getString(int id) {
        return getContext().getString(id);
    }

    public int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    ;
}
