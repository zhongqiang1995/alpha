package com.azl.view.helper.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhong on 2017/5/18.
 */

public class CommonHolder extends RecyclerView.ViewHolder {
    private Map<Integer, View> mMap;
    private View itemView;

    public View getItemView() {
        return itemView;
    }

    public CommonHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        mMap = new HashMap<>();
    }

    public <T> T findViewById(int id) {
        View targetView = mMap.get(id);
        if (targetView == null) {
            targetView = this.itemView.findViewById(id);
            mMap.put(id, targetView);
        }
        return (T) targetView;
    }
}
