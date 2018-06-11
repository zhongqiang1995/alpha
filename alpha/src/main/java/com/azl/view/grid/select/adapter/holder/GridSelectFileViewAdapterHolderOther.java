package com.azl.view.grid.select.adapter.holder;

import android.text.TextUtils;
import android.widget.TextView;

import com.azl.view.grid.select.GridSelectActionView;
import com.azl.view.grid.select.adapter.GridSelectFileViewAdapter;
import com.azl.view.grid.select.entity.GridSelectEntity;
import com.azl.view.helper.adapter.multiple.holder.ItemHolder;
import com.azl.view.helper.holder.CommonHolder;
import com.example.zhlib.R;

import java.io.File;

/**
 * Created by zhong on 2018/3/7.
 */

public class GridSelectFileViewAdapterHolderOther extends ItemHolder<GridSelectFileViewAdapter, GridSelectEntity> {

    private GridSelectActionView mActionView;
    private TextView mTv;

    private MOnClickDeleteListener mOnClickDeleteListener;

    public GridSelectFileViewAdapterHolderOther(int type) {
        super(type);

    }

    @Override
    public void initViewHolder(CommonHolder ch) {
        super.initViewHolder(ch);

        mActionView = (GridSelectActionView) ch.getItemView();
        mTv = ch.findViewById(R.id.tvName);
        mOnClickDeleteListener = new MOnClickDeleteListener();
        mActionView.setClickDeleteListener(mOnClickDeleteListener);
    }

    @Override
    public void setItemView(GridSelectFileViewAdapter adapter, GridSelectEntity gridSelectEntity, CommonHolder ch, int position) {
        mOnClickDeleteListener.setData(position, gridSelectEntity, adapter);
        String path = gridSelectEntity.getPath() == null ? "" : gridSelectEntity.getPath();
        File file = new File(path);
        if (file != null) {
            String name = TextUtils.isEmpty(file.getName()) ? "none" : file.getName();
            mTv.setText(name);
        } else {
            mTv.setText("none");
        }

    }

    @Override
    public boolean isSelectItem(GridSelectFileViewAdapter adapter, int position, Object item) {
        if (item instanceof GridSelectEntity) {
            GridSelectEntity entity = (GridSelectEntity) item;
            if (!entity.isImage()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.alpha_item_grid_view_other;
    }

    class MOnClickDeleteListener implements GridSelectActionView.OnClickDeleteListener {
        int mPosition;
        GridSelectEntity mEntity;
        GridSelectFileViewAdapter mAdapter;

        public void setData(int position, GridSelectEntity entity, GridSelectFileViewAdapter adapter) {
            this.mPosition = position;
            this.mEntity = entity;
            this.mAdapter = adapter;
        }

        @Override
        public void onDelete() {
            mAdapter.remove(mPosition);
        }
    }
}
