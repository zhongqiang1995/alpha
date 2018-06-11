package com.azl.view.grid.select.adapter.holder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.azl.bean.PermissionPackageBean;
import com.azl.handle.action.HandleMsg;
import com.azl.helper.AKXMarkList;
import com.azl.view.grid.select.GridSelectActionView;
import com.azl.view.grid.select.GridSelectFileView;
import com.azl.view.grid.select.adapter.GridSelectFileViewAdapter;
import com.azl.view.grid.select.entity.GridAddEntity;
import com.azl.view.grid.select.entity.GridSelectEntity;
import com.azl.view.helper.adapter.multiple.holder.ItemHolder;
import com.azl.view.helper.holder.CommonHolder;
import com.example.zhlib.R;

/**
 * Created by zhong on 2018/3/7.
 */

public class GridSelectFileViewAdapterHolderAdd extends ItemHolder<GridSelectFileViewAdapter, GridAddEntity> implements View.OnClickListener {
    private GridSelectActionView mActionView;
    private OnClickAdd mClickAdd;
    private ImageView mImgAdd;
    private int mImgAddDrawableResId;
    private int mResultCode;

    public GridSelectFileViewAdapterHolderAdd(int type, int mAddDrawable, int resultCode) {
        super(type);
        this.mResultCode = resultCode;
        this.mImgAddDrawableResId = mAddDrawable;
    }

    @Override
    public void initViewHolder(CommonHolder ch) {
        super.initViewHolder(ch);
        mClickAdd = new OnClickAdd();
        mActionView = (GridSelectActionView) ch.getItemView();
        mImgAdd = ch.findViewById(R.id.imgAddPhoto);
        mActionView.hideDelete();
        mActionView.setOnClickListener(mClickAdd);
        mImgAdd.setImageResource(mImgAddDrawableResId);
    }

    @Override
    public void setItemView(GridSelectFileViewAdapter adapter, GridAddEntity gridAddEntity, CommonHolder ch, int position) {
        mClickAdd.setAdapter(adapter);
    }

    @Override
    public boolean isSelectItem(GridSelectFileViewAdapter adapter, int position, Object item) {
        return item instanceof GridAddEntity;
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.alpha_item_grid_view_add;
    }


    class OnClickAdd implements View.OnClickListener {
        GridSelectFileViewAdapter mAdapter;


        public void setAdapter(GridSelectFileViewAdapter mAdapter) {
            this.mAdapter = mAdapter;
        }

        @Override
        public void onClick(View v) {

            if (checkPermissions(v.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(mAdapter.getOpenType());//无类型限制
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                Context context = v.getContext();
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    activity.startActivityForResult(intent, mResultCode);
                }
            } else {
                HandleMsg.handleMark(AKXMarkList.MARK_PERMISSIONS,
                        new PermissionPackageBean(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}));
            }

        }
    }

    protected boolean checkPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            String[] var2 = permissions;
            int var3 = permissions.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                String permission = var2[var4];
                if (ContextCompat.checkSelfPermission(context, permission) != 0) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public void onClick(View v) {

    }
}
